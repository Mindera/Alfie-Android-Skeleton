#!/usr/bin/env python3
"""
PR Metrics Collection Script

This script collects and analyzes pull request metrics from a GitHub repository,
including authors, reviewers, review activity, comments, and pipeline failures.
"""

import os
import sys
import json
from datetime import datetime, timedelta
from collections import defaultdict
from typing import Dict, List, Set, Optional
import requests

# Configuration
GITHUB_API_URL = "https://api.github.com"
GITHUB_REPOSITORY = os.environ.get("GITHUB_REPOSITORY", "")
REPO_OWNER = os.environ.get("GITHUB_REPOSITORY_OWNER", "")
REPO_NAME = GITHUB_REPOSITORY.split("/")[-1] if "/" in GITHUB_REPOSITORY else ""
GITHUB_TOKEN = os.environ.get("GH_TOKEN", "")
LOOKBACK_DAYS = int(os.environ.get("LOOKBACK_DAYS", "30"))

# Headers for API requests
headers = {
    "Authorization": f"Bearer {GITHUB_TOKEN}",
    "Accept": "application/vnd.github+json",
    "X-GitHub-Api-Version": "2022-11-28"
}

class PRMetrics:
    def __init__(self):
        self.pr_authors: Set[str] = set()
        self.reviewers: Set[str] = set()
        self.reviewer_activity: Dict[str, int] = defaultdict(int)
        self.reviewer_comments: Dict[str, int] = defaultdict(int)
        self.reviewer_code_change_comments: Dict[str, int] = defaultdict(int)
        self.review_times: List[float] = []
        self.approved_then_failed: List[Dict] = []
        self.all_team_members: Set[str] = set()
        self.prs_analyzed: int = 0
        
    def fetch_paginated(self, url: str, params: Optional[Dict] = None) -> List[Dict]:
        """Fetch all pages of a paginated API endpoint"""
        results = []
        page = 1
        
        while True:
            current_params = params.copy() if params else {}
            current_params['page'] = page
            current_params['per_page'] = 100
            
            response = requests.get(url, headers=headers, params=current_params)
            
            if response.status_code != 200:
                print(f"Error fetching {url}: {response.status_code}")
                print(f"Response: {response.text}")
                break
                
            data = response.json()
            
            if not data:
                break
                
            results.extend(data)
            page += 1
            
            # Check if there are more pages
            if 'Link' not in response.headers or 'rel="next"' not in response.headers['Link']:
                break
                
        return results
    
    def get_all_contributors(self):
        """Get all contributors to identify team members"""
        url = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}/contributors"
        contributors = self.fetch_paginated(url)
        
        for contributor in contributors:
            self.all_team_members.add(contributor['login'])
    
    def analyze_prs(self):
        """Analyze all PRs within the lookback period"""
        cutoff_date = datetime.now() - timedelta(days=LOOKBACK_DAYS)
        
        # Fetch all PRs (both open and closed)
        url = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}/pulls"
        
        for state in ['open', 'closed']:
            params = {
                'state': state,
                'sort': 'updated',
                'direction': 'desc'
            }
            
            prs = self.fetch_paginated(url, params)
            
            for pr in prs:
                # Check if PR is within our lookback period
                pr_updated = datetime.strptime(pr['updated_at'], '%Y-%m-%dT%H:%M:%SZ')
                
                if pr_updated < cutoff_date:
                    continue
                
                self.prs_analyzed += 1
                
                # Track PR author
                self.pr_authors.add(pr['user']['login'])
                
                # Analyze reviews
                self.analyze_pr_reviews(pr['number'], pr['created_at'])
                
                # Check if PR was approved then failed
                self.check_approved_then_failed(pr['number'], pr.get('merged', False))
    
    def is_code_change_comment(self, comment_body: str) -> bool:
        """Determine if a comment suggests code changes"""
        if not comment_body:
            return False
        
        comment_lower = comment_body.lower()
        
        # Keywords that suggest code changes
        code_change_keywords = [
            'change', 'fix', 'update', 'modify', 'refactor',
            'should', 'could', 'consider', 'suggest',
            'instead', 'better', 'improve', 'replace',
            'add', 'remove', 'delete', 'rename',
            'use', 'try', 'recommend', 'prefer'
        ]
        
        # Check for code change keywords
        return any(keyword in comment_lower for keyword in code_change_keywords)
    
    def analyze_pr_reviews(self, pr_number: int, pr_created_at: str):
        """Analyze reviews for a specific PR"""
        url = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}/pulls/{pr_number}/reviews"
        reviews = self.fetch_paginated(url)
        
        pr_created = datetime.strptime(pr_created_at, '%Y-%m-%dT%H:%M:%SZ')
        first_review_time = None
        
        for review in reviews:
            reviewer = review['user']['login']
            self.reviewers.add(reviewer)
            self.reviewer_activity[reviewer] += 1
            
            # Track time to first review
            if first_review_time is None:
                review_time = datetime.strptime(review['submitted_at'], '%Y-%m-%dT%H:%M:%SZ')
                time_to_review = (review_time - pr_created).total_seconds() / 3600  # hours
                self.review_times.append(time_to_review)
                first_review_time = review_time
        
        # Count review comments
        comments_url = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}/pulls/{pr_number}/comments"
        comments = self.fetch_paginated(comments_url)
        
        for comment in comments:
            commenter = comment['user']['login']
            self.reviewer_comments[commenter] += 1
            
            # Check if comment implies code changes
            if self.is_code_change_comment(comment.get('body', '')):
                self.reviewer_code_change_comments[commenter] += 1
    
    def check_approved_then_failed(self, pr_number: int, is_merged: bool):
        """Check if PR was approved but then failed pipeline"""
        # Get reviews
        reviews_url = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}/pulls/{pr_number}/reviews"
        reviews = self.fetch_paginated(reviews_url)
        
        # Check if PR was approved
        approved = any(review['state'] == 'APPROVED' for review in reviews)
        
        if not approved:
            return
        
        # Get check runs
        pr_url = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}/pulls/{pr_number}"
        pr_response = requests.get(pr_url, headers=headers)
        
        if pr_response.status_code != 200:
            return
            
        pr_data = pr_response.json()
        head_sha = pr_data['head']['sha']
        
        # Get check runs for the PR
        checks_url = f"{GITHUB_API_URL}/repos/{REPO_OWNER}/{REPO_NAME}/commits/{head_sha}/check-runs"
        checks_response = requests.get(checks_url, headers=headers)
        
        if checks_response.status_code != 200:
            return
            
        checks_data = checks_response.json()
        
        # Check if any checks failed
        if 'check_runs' in checks_data:
            failed_checks = [
                check for check in checks_data['check_runs']
                if check['conclusion'] in ['failure', 'cancelled', 'timed_out']
            ]
            
            if failed_checks and not is_merged:
                self.approved_then_failed.append({
                    'pr_number': pr_number,
                    'failed_checks': [check['name'] for check in failed_checks]
                })
    
    def generate_report(self) -> str:
        """Generate a formatted metrics report"""
        report = []
        report.append("=" * 80)
        report.append("PR METRICS REPORT")
        report.append(f"Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S UTC')}")
        report.append(f"Period: Last {LOOKBACK_DAYS} days")
        report.append(f"Repository: {REPO_OWNER}/{REPO_NAME}")
        report.append("=" * 80)
        report.append("")
        
        # Summary
        report.append("## SUMMARY")
        report.append(f"- PRs Analyzed: {self.prs_analyzed}")
        report.append(f"- Unique PR Authors: {len(self.pr_authors)}")
        report.append(f"- Unique Reviewers: {len(self.reviewers)}")
        report.append(f"- Total Reviews: {sum(self.reviewer_activity.values())}")
        report.append(f"- Total Review Comments: {sum(self.reviewer_comments.values())}")
        report.append("")
        
        # PR Authors
        report.append("## PR AUTHORS")
        report.append(f"Total: {len(self.pr_authors)}")
        for author in sorted(self.pr_authors):
            report.append(f"  - {author}")
        report.append("")
        
        # Active Reviewers - Combined table format
        report.append("## REVIEWER ACTIVITY")
        report.append("")
        
        # Collect all reviewer data
        all_reviewers = set(self.reviewer_activity.keys()) | set(self.reviewer_comments.keys())
        reviewer_data = []
        
        for reviewer in all_reviewers:
            reviews = self.reviewer_activity.get(reviewer, 0)
            comments = self.reviewer_comments.get(reviewer, 0)
            code_change_comments = self.reviewer_code_change_comments.get(reviewer, 0)
            reviewer_data.append((reviewer, reviews, comments, code_change_comments))
        
        # Sort by number of reviews (descending)
        reviewer_data.sort(key=lambda x: x[1], reverse=True)
        
        # Create table
        if reviewer_data:
            # Table header
            report.append("| Reviewer | Reviews | Comments | Comments Implying Code Changes |")
            report.append("|----------|---------|----------|--------------------------------|")
            
            # Table rows
            for reviewer, reviews, comments, code_change_comments in reviewer_data:
                report.append(f"| {reviewer} | {reviews} | {comments} | {code_change_comments} |")
        else:
            report.append("No reviewers found in the analysis period.")
        
        report.append("")
        
        # Average Review Time
        if self.review_times:
            avg_review_time = sum(self.review_times) / len(self.review_times)
            report.append("## AVERAGE REVIEW TIME")
            report.append(f"Average time to first review: {avg_review_time:.2f} hours")
            report.append(f"Median time to first review: {sorted(self.review_times)[len(self.review_times)//2]:.2f} hours")
            report.append("")
        
        # Approved then Failed
        report.append("## APPROVED PRs THAT FAILED PIPELINE")
        if self.approved_then_failed:
            report.append(f"Total: {len(self.approved_then_failed)}")
            for item in self.approved_then_failed:
                report.append(f"  - PR #{item['pr_number']}")
                report.append(f"    Failed checks: {', '.join(item['failed_checks'])}")
        else:
            report.append("None found!")
        report.append("")
        
        # Inactive Reviewers
        report.append("## INACTIVE REVIEWERS")
        report.append("Team members who have not reviewed code:")
        inactive = self.all_team_members - self.reviewers
        if inactive:
            for member in sorted(inactive):
                report.append(f"  - {member}")
        else:
            report.append("All team members are actively reviewing!")
        report.append("")
        
        report.append("=" * 80)
        
        return "\n".join(report)

def main():
    """Main entry point"""
    if not GITHUB_TOKEN:
        print("ERROR: GITHUB_TOKEN environment variable is required")
        sys.exit(1)
    
    if not REPO_OWNER or not REPO_NAME:
        print("ERROR: Could not determine repository owner/name")
        sys.exit(1)
    
    print(f"Collecting PR metrics for {REPO_OWNER}/{REPO_NAME}...")
    print(f"Lookback period: {LOOKBACK_DAYS} days")
    print("")
    
    metrics = PRMetrics()
    
    # Get all team members
    print("Fetching team members...")
    metrics.get_all_contributors()
    print(f"Found {len(metrics.all_team_members)} contributors")
    
    # Analyze PRs
    print("Analyzing PRs...")
    metrics.analyze_prs()
    
    # Generate report
    print("")
    print("Generating report...")
    report = metrics.generate_report()
    
    # Print to console
    print(report)
    
    # Save to file
    output_file = os.environ.get("OUTPUT_FILE", "pr-metrics-report.txt")
    with open(output_file, 'w') as f:
        f.write(report)
    
    print(f"\nReport saved to: {output_file}")

if __name__ == "__main__":
    main()
