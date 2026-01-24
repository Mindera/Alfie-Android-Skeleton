#!/usr/bin/env python3
"""
Generate weekly AI metrics report for GitHub repository.

This script analyzes pull requests to measure AI development productivity:
- Time from issue creation to PR opening
- Time from PR open to merge
- Time to first successful CI run
- Human interaction metrics
- AI vs human development patterns
"""

import os
import sys
import subprocess
from datetime import datetime, timedelta, timezone
from typing import Dict, List, Optional, Tuple
import json

try:
    from github import Github, GithubException, Auth
    import pandas as pd
except ImportError as e:
    print(f"Error importing required modules: {e}")
    print("Installing required packages...")
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "PyGithub", "pandas"])
    from github import Github, GithubException, Auth
    import pandas as pd


class AIMetricsAnalyzer:
    """Analyzes GitHub repository metrics for AI development productivity."""
    
    def __init__(self, token: str, repo_owner: str, repo_name: str):
        self.github = Github(auth=Auth.Token(token))
        self.repo = self.github.get_repo(f"{repo_owner}/{repo_name}")
        self.metrics_data = []
        # Time threshold (hours) for considering a commit as a response to a comment
        self.comment_response_threshold_hours = 24
    
    def _is_bot_user(self, username: str) -> bool:
        """Check if a username belongs to a bot or AI service."""
        if not username:
            return False
        username_lower = username.lower()
        return any(keyword in username_lower for keyword in ['copilot', 'bot', 'ai', 'github-actions'])
        
    def analyze_pr(self, pr) -> Dict:
        """Analyze a single pull request and extract metrics."""
        try:
            metrics = {
                'pr_number': pr.number,
                'pr_title': pr.title,
                'pr_url': pr.html_url,
                'author': pr.user.login,
                'created_at': pr.created_at,
                'merged_at': pr.merged_at,
                'closed_at': pr.closed_at,
                'is_merged': pr.merged,
                'is_draft': pr.draft if hasattr(pr, 'draft') else False,
            }
            
            # Detect if PR was created by AI (copilot, bot, etc.)
            author_login = pr.user.login.lower()
            metrics['is_ai_created'] = self._is_bot_user(pr.user.login)
            
            # Check if linked to an issue
            issue_number = self._extract_issue_number(pr.body or "")
            metrics['linked_issue'] = issue_number
            
            if issue_number:
                try:
                    issue = self.repo.get_issue(issue_number)
                    metrics['issue_created_at'] = issue.created_at
                    metrics['issue_to_pr_time'] = (pr.created_at - issue.created_at).total_seconds() / 3600  # hours
                except Exception:
                    metrics['issue_created_at'] = None
                    metrics['issue_to_pr_time'] = None
            else:
                metrics['issue_created_at'] = None
                metrics['issue_to_pr_time'] = None
            
            # Calculate time to merge
            if pr.merged_at:
                metrics['pr_to_merge_time'] = (pr.merged_at - pr.created_at).total_seconds() / 3600  # hours
                metrics['total_lifecycle_time'] = metrics['pr_to_merge_time']
                if metrics['issue_to_pr_time']:
                    metrics['total_lifecycle_time'] += metrics['issue_to_pr_time']
            else:
                metrics['pr_to_merge_time'] = None
                metrics['total_lifecycle_time'] = None
            
            # Count commits
            metrics['commit_count'] = pr.commits
            
            # Count review comments
            metrics['review_comments_count'] = pr.review_comments
            metrics['comments_count'] = pr.comments
            metrics['total_comments'] = metrics['review_comments_count'] + metrics['comments_count']
            
            # Count reviews
            reviews = list(pr.get_reviews())
            metrics['review_count'] = len(reviews)
            
            # Count unique reviewers
            reviewers = set(review.user.login for review in reviews if review.user)
            metrics['unique_reviewers'] = len(reviewers)
            
            # Analyze workflow runs for CI metrics
            ci_metrics = self._analyze_ci_runs(pr)
            metrics.update(ci_metrics)
            
            # Count files changed
            metrics['files_changed'] = pr.changed_files
            metrics['additions'] = pr.additions
            metrics['deletions'] = pr.deletions
            
            # Detect human interactions
            human_interactions = self._count_human_interactions(pr, reviews)
            metrics['human_interactions'] = human_interactions
            
            # NEW METRICS: Check if AI PR has human commits
            metrics['has_human_commits'] = self._has_human_commits_on_ai_pr(pr, metrics['is_ai_created'])
            
            # NEW METRICS: Count AI vs Human comments
            comment_breakdown = self._analyze_comment_breakdown(pr)
            metrics.update(comment_breakdown)
            
            # NEW METRICS: Detect comments that led to changes
            comments_with_changes = self._count_comments_with_changes(pr)
            metrics['comments_that_led_to_changes'] = comments_with_changes
            
            return metrics
            
        except Exception as e:
            print(f"Error analyzing PR #{pr.number}: {e}")
            return None
    
    def _extract_issue_number(self, text: str) -> Optional[int]:
        """Extract issue number from PR body."""
        import re
        # Look for patterns like "Fixes #123", "Closes #456", etc.
        patterns = [
            r'(?:fix|fixes|fixed|close|closes|closed|resolve|resolves|resolved)\s+#(\d+)',
            r'#(\d+)'
        ]
        for pattern in patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                return int(match.group(1))
        return None
    
    def _analyze_ci_runs(self, pr) -> Dict:
        """Analyze CI workflow runs for the PR."""
        ci_metrics = {
            'first_ci_start': None,
            'first_ci_success': None,
            'time_to_first_success': None,
            'ci_run_count': 0,
            'ci_success_count': 0,
            'ci_failure_count': 0,
            'first_attempt_success': False,
        }
        
        try:
            # Get commits in the PR
            commits = list(pr.get_commits())
            if not commits:
                return ci_metrics
            
            # Get workflow runs for commits in this PR
            all_runs = []
            for commit in commits:
                try:
                    runs = self.repo.get_commit(commit.sha).get_check_runs()
                    all_runs.extend(list(runs))
                except Exception:
                    continue
            
            if not all_runs:
                return ci_metrics
            
            # Sort by creation time, filtering out runs without started_at
            all_runs = [run for run in all_runs if run.started_at]
            all_runs.sort(key=lambda x: x.started_at)
            
            # Track CI metrics
            ci_metrics['ci_run_count'] = len(all_runs)
            first_success_index = None
            
            for index, run in enumerate(all_runs):
                if not run.started_at:
                    continue
                    
                if ci_metrics['first_ci_start'] is None:
                    ci_metrics['first_ci_start'] = run.started_at
                
                if run.conclusion == 'success':
                    ci_metrics['ci_success_count'] += 1
                    if ci_metrics['first_ci_success'] is None:
                        ci_metrics['first_ci_success'] = run.completed_at
                        first_success_index = index
                        if ci_metrics['first_ci_start']:
                            time_delta = (run.completed_at - pr.created_at).total_seconds() / 3600
                            ci_metrics['time_to_first_success'] = time_delta
                elif run.conclusion in ['failure', 'cancelled', 'timed_out']:
                    ci_metrics['ci_failure_count'] += 1
            
            # Check if first CI run was successful
            if first_success_index == 0:
                ci_metrics['first_attempt_success'] = True
                    
        except Exception as e:
            print(f"Error analyzing CI runs: {e}")
        
        return ci_metrics
    
    def _count_human_interactions(self, pr, reviews) -> int:
        """Count human interactions on the PR."""
        interactions = 0
        
        # Count reviews by humans (not bots)
        for review in reviews:
            if review.user and not self._is_bot_user(review.user.login):
                interactions += 1
        
        # Count comments by humans
        try:
            for comment in pr.get_issue_comments():
                if comment.user and not self._is_bot_user(comment.user.login):
                    interactions += 1
        except Exception:
            pass
        
        # Count review comments by humans
        try:
            for comment in pr.get_review_comments():
                if comment.user and not self._is_bot_user(comment.user.login):
                    interactions += 1
        except Exception:
            pass
        
        return interactions
    
    def _has_human_commits_on_ai_pr(self, pr, is_ai_created: bool) -> bool:
        """Check if an AI-created PR has commits from humans."""
        if not is_ai_created:
            return False
        
        try:
            commits = list(pr.get_commits())
            pr_author = pr.user.login.lower()
            
            for commit in commits:
                if commit.author and commit.author.login:
                    commit_author = commit.author.login.lower()
                    # Check if commit author is different from PR author and is not a bot
                    if commit_author != pr_author and not self._is_bot_user(commit.author.login):
                        return True
            return False
        except Exception as e:
            print(f"Error checking human commits: {e}")
            return False
    
    def _analyze_comment_breakdown(self, pr) -> Dict:
        """Analyze comments breakdown by AI vs Human."""
        breakdown = {
            'ai_comments_count': 0,
            'human_comments_count': 0,
        }
        
        try:
            # Count issue comments
            for comment in pr.get_issue_comments():
                if comment.user:
                    if self._is_bot_user(comment.user.login):
                        breakdown['ai_comments_count'] += 1
                    else:
                        breakdown['human_comments_count'] += 1
            
            # Count review comments
            for comment in pr.get_review_comments():
                if comment.user:
                    if self._is_bot_user(comment.user.login):
                        breakdown['ai_comments_count'] += 1
                    else:
                        breakdown['human_comments_count'] += 1
        except Exception as e:
            print(f"Error analyzing comment breakdown: {e}")
        
        return breakdown
    
    def _count_comments_with_changes(self, pr) -> int:
        """
        Count comments that led to changes (commits after comment).
        
        A comment is considered to have led to a change if there's a commit
        within self.comment_response_threshold_hours (default 24 hours) after it.
        """
        comments_with_changes = 0
        
        try:
            # Get all commits with timestamps
            commits = list(pr.get_commits())
            commit_times = [(commit.commit.author.date, commit.sha) for commit in commits 
                           if commit.commit.author and commit.commit.author.date]
            
            # Get all comments (both issue and review comments) with timestamps
            all_comments = []
            
            # Issue comments
            for comment in pr.get_issue_comments():
                if comment.user and not self._is_bot_user(comment.user.login):
                    all_comments.append(comment.created_at)
            
            # Review comments  
            for comment in pr.get_review_comments():
                if comment.user and not self._is_bot_user(comment.user.login):
                    all_comments.append(comment.created_at)
            
            # For each comment, check if there's a commit after it
            for comment_time in all_comments:
                for commit_time, _ in commit_times:
                    # If there's a commit within threshold hours after the comment, consider it a change
                    time_diff = (commit_time - comment_time).total_seconds() / 3600  # hours
                    if 0 < time_diff <= self.comment_response_threshold_hours:
                        comments_with_changes += 1
                        break  # Count each comment only once
                        
        except Exception as e:
            print(f"Error counting comments with changes: {e}")
        
        return comments_with_changes
    
    def collect_weekly_metrics(self, days_back: int = 7) -> List[Dict]:
        """Collect metrics for PRs from the last N days."""
        since_date = datetime.now(timezone.utc) - timedelta(days=days_back)
        
        print(f"Collecting metrics for PRs since {since_date.strftime('%Y-%m-%d')}...")
        
        # Get all closed/merged PRs in the time range
        pulls = self.repo.get_pulls(state='all', sort='updated', direction='desc')
        
        analyzed_count = 0
        for pr in pulls:
            # Only analyze PRs updated in the time range
            if pr.updated_at < since_date:
                break
            
            # Only analyze merged PRs for meaningful metrics
            if pr.merged:
                metrics = self.analyze_pr(pr)
                if metrics:
                    self.metrics_data.append(metrics)
                    analyzed_count += 1
                    print(f"Analyzed PR #{pr.number}: {pr.title}")
        
        print(f"\nAnalyzed {analyzed_count} PRs")
        return self.metrics_data
    
    def generate_report(self) -> str:
        """Generate a markdown report from collected metrics."""
        if not self.metrics_data:
            return self._generate_empty_report()
        
        df = pd.DataFrame(self.metrics_data)
        
        report = []
        report.append("# 📊 Weekly AI Development Metrics Report\n")
        report.append(f"**Report Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M UTC')}\n")
        report.append(f"**Period:** Last 7 days\n")
        report.append(f"**Total PRs Analyzed:** {len(df)}\n")
        report.append("\n---\n")
        
        # Summary statistics
        report.append("## 📈 Executive Summary\n")
        
        ai_prs = df[df['is_ai_created'] == True]
        human_prs = df[df['is_ai_created'] == False]
        
        report.append(f"- **Total Merged PRs:** {len(df)}\n")
        report.append(f"- **AI-Created PRs:** {len(ai_prs)} ({len(ai_prs)/len(df)*100:.1f}%)\n")
        report.append(f"- **Human-Created PRs:** {len(human_prs)} ({len(human_prs)/len(df)*100:.1f}%)\n")
        report.append("\n")
        
        # Time to merge metrics
        report.append("## ⏱️ Time to Merge Metrics\n")
        report.append("\n### Issue to PR to Merge Lifecycle\n")
        
        prs_with_issues = df[df['linked_issue'].notna()]
        if len(prs_with_issues) > 0:
            report.append(f"- **PRs Linked to Issues:** {len(prs_with_issues)}\n")
            report.append(f"- **Avg Issue → PR Time:** {prs_with_issues['issue_to_pr_time'].mean():.2f} hours\n")
            report.append(f"- **Avg PR → Merge Time:** {prs_with_issues['pr_to_merge_time'].mean():.2f} hours\n")
            report.append(f"- **Avg Total Lifecycle:** {prs_with_issues['total_lifecycle_time'].mean():.2f} hours\n")
        else:
            report.append("- No PRs linked to issues in this period\n")
        
        report.append("\n### All PRs (Issue-Linked and Direct)\n")
        report.append(f"- **Avg Time to Merge:** {df['pr_to_merge_time'].mean():.2f} hours\n")
        report.append(f"- **Median Time to Merge:** {df['pr_to_merge_time'].median():.2f} hours\n")
        report.append(f"- **Min Time to Merge:** {df['pr_to_merge_time'].min():.2f} hours\n")
        report.append(f"- **Max Time to Merge:** {df['pr_to_merge_time'].max():.2f} hours\n")
        report.append("\n")
        
        # CI metrics
        report.append("## 🚀 CI/CD Performance\n")
        
        prs_with_ci = df[df['first_ci_success'].notna()]
        if len(prs_with_ci) > 0:
            report.append(f"- **PRs with CI Data:** {len(prs_with_ci)}\n")
            report.append(f"- **Avg Time to First CI Success:** {prs_with_ci['time_to_first_success'].mean():.2f} hours\n")
            report.append(f"- **First Attempt Success Rate:** {df['first_attempt_success'].sum()/len(df)*100:.1f}%\n")
            report.append(f"- **Avg CI Runs per PR:** {df['ci_run_count'].mean():.2f}\n")
            
            # Safe division for CI success rate
            total_ci_runs = df['ci_success_count'].sum() + df['ci_failure_count'].sum()
            if total_ci_runs > 0:
                success_rate = df['ci_success_count'].sum() / total_ci_runs * 100
                report.append(f"- **CI Success Rate:** {success_rate:.1f}%\n")
            else:
                report.append(f"- **CI Success Rate:** N/A (no completed CI runs)\n")
        else:
            report.append("- No CI data available for this period\n")
        report.append("\n")
        
        # Human interaction metrics
        report.append("## 👥 Human Interaction Metrics\n")
        report.append(f"- **Avg Comments per PR:** {df['total_comments'].mean():.2f}\n")
        report.append(f"- **Avg Review Comments:** {df['review_comments_count'].mean():.2f}\n")
        report.append(f"- **Avg Reviews per PR:** {df['review_count'].mean():.2f}\n")
        report.append(f"- **Avg Unique Reviewers:** {df['unique_reviewers'].mean():.2f}\n")
        report.append(f"- **Avg Human Interactions:** {df['human_interactions'].mean():.2f}\n")
        report.append(f"- **Avg Commits per PR:** {df['commit_count'].mean():.2f}\n")
        report.append("\n")
        
        # AI vs Human comparison
        if len(ai_prs) > 0 and len(human_prs) > 0:
            report.append("## 🤖 AI vs Human Performance Comparison\n")
            report.append("\n### Time to Merge\n")
            report.append(f"- **AI PRs Avg:** {ai_prs['pr_to_merge_time'].mean():.2f} hours\n")
            report.append(f"- **Human PRs Avg:** {human_prs['pr_to_merge_time'].mean():.2f} hours\n")
            report.append(f"- **Difference:** {((ai_prs['pr_to_merge_time'].mean() - human_prs['pr_to_merge_time'].mean()) / human_prs['pr_to_merge_time'].mean() * 100):.1f}%\n")
            
            report.append("\n### Human Interactions Required\n")
            report.append(f"- **AI PRs Avg:** {ai_prs['human_interactions'].mean():.2f}\n")
            report.append(f"- **Human PRs Avg:** {human_prs['human_interactions'].mean():.2f}\n")
            
            report.append("\n### Code Change Size\n")
            report.append(f"- **AI PRs Avg Lines Added:** {ai_prs['additions'].mean():.0f}\n")
            report.append(f"- **Human PRs Avg Lines Added:** {human_prs['additions'].mean():.0f}\n")
            report.append(f"- **AI PRs Avg Lines Changed:** {(ai_prs['additions'].mean() + ai_prs['deletions'].mean()):.0f}\n")
            report.append(f"- **Human PRs Avg Lines Changed:** {(human_prs['additions'].mean() + human_prs['deletions'].mean()):.0f}\n")
            
            # NEW: AI PRs with Human Commits
            report.append("\n### AI PRs with Human Commits\n")
            ai_prs_with_human_commits = ai_prs[ai_prs['has_human_commits'] == True]
            report.append(f"- **AI PRs with Human Commits:** {len(ai_prs_with_human_commits)} ({len(ai_prs_with_human_commits)/len(ai_prs)*100:.1f}%)\n")
            
            # NEW: CI Failures by PR Type
            report.append("\n### CI Failures Comparison\n")
            ai_total_failures = ai_prs['ci_failure_count'].sum()
            human_total_failures = human_prs['ci_failure_count'].sum()
            report.append(f"- **AI PRs Total CI Failures:** {ai_total_failures:.0f}\n")
            report.append(f"- **Human PRs Total CI Failures:** {human_total_failures:.0f}\n")
            report.append(f"- **AI PRs Avg CI Failures:** {ai_prs['ci_failure_count'].mean():.2f}\n")
            report.append(f"- **Human PRs Avg CI Failures:** {human_prs['ci_failure_count'].mean():.2f}\n")
            
            # NEW: Comments Breakdown
            report.append("\n### Comments Breakdown\n")
            report.append(f"- **Avg AI Comments on AI PRs:** {ai_prs['ai_comments_count'].mean():.2f}\n")
            report.append(f"- **Avg Human Comments on AI PRs:** {ai_prs['human_comments_count'].mean():.2f}\n")
            report.append(f"- **Avg AI Comments on Human PRs:** {human_prs['ai_comments_count'].mean():.2f}\n")
            report.append(f"- **Avg Human Comments on Human PRs:** {human_prs['human_comments_count'].mean():.2f}\n")
            
            # NEW: Comments Leading to Changes
            report.append("\n### Comments That Led to Changes\n")
            report.append(f"- **AI PRs with Comments Leading to Changes:** {ai_prs['comments_that_led_to_changes'].sum():.0f}\n")
            report.append(f"- **AI PRs Avg Comments→Changes:** {ai_prs['comments_that_led_to_changes'].mean():.2f}\n")
            report.append(f"- **Human PRs with Comments Leading to Changes:** {human_prs['comments_that_led_to_changes'].sum():.0f}\n")
            report.append(f"- **Human PRs Avg Comments→Changes:** {human_prs['comments_that_led_to_changes'].mean():.2f}\n")
            report.append("\n")
        
        # Code change metrics
        report.append("## 📝 Code Change Metrics\n")
        report.append(f"- **Avg Files Changed:** {df['files_changed'].mean():.2f}\n")
        report.append(f"- **Avg Lines Added:** {df['additions'].mean():.0f}\n")
        report.append(f"- **Avg Lines Deleted:** {df['deletions'].mean():.0f}\n")
        report.append(f"- **Avg Total Lines Changed:** {(df['additions'].mean() + df['deletions'].mean()):.0f}\n")
        report.append("\n")
        
        # Detailed PR list
        report.append("## 📋 Detailed PR List\n")
        report.append("\n| PR | Title | Author | Type | Time to Merge (hrs) | Comments | Reviews | CI Success |\n")
        report.append("|---|---|---|---|---|---|---|---|\n")
        
        for _, row in df.iterrows():
            pr_type = "🤖 AI" if row['is_ai_created'] else "👤 Human"
            ci_status = "✅" if row['first_attempt_success'] else "⚠️"
            report.append(f"| [#{row['pr_number']}]({row['pr_url']}) | {row['pr_title'][:50]} | {row['author']} | {pr_type} | {row['pr_to_merge_time']:.1f} | {row['total_comments']} | {row['review_count']} | {ci_status} |\n")
        
        report.append("\n")
        
        # Insights and recommendations
        report.append("## 💡 Insights & Recommendations\n")
        
        if len(ai_prs) > 0 and len(human_prs) > 0:
            ai_faster = ai_prs['pr_to_merge_time'].mean() < human_prs['pr_to_merge_time'].mean()
            if ai_faster:
                improvement = ((human_prs['pr_to_merge_time'].mean() - ai_prs['pr_to_merge_time'].mean()) / human_prs['pr_to_merge_time'].mean() * 100)
                report.append(f"- ✅ **AI-created PRs are {improvement:.1f}% faster to merge** than human-created PRs\n")
            else:
                slowdown = ((ai_prs['pr_to_merge_time'].mean() - human_prs['pr_to_merge_time'].mean()) / human_prs['pr_to_merge_time'].mean() * 100)
                report.append(f"- ⚠️ **AI-created PRs take {slowdown:.1f}% longer to merge** than human-created PRs\n")
        
        first_success_rate = df['first_attempt_success'].sum() / len(df) * 100
        if first_success_rate < 80:
            report.append(f"- ⚠️ **First attempt CI success rate is {first_success_rate:.1f}%** - consider improving code quality checks\n")
        else:
            report.append(f"- ✅ **High first attempt CI success rate** ({first_success_rate:.1f}%)\n")
        
        avg_interactions = df['human_interactions'].mean()
        if avg_interactions > 10:
            report.append(f"- ⚠️ **High average human interactions** ({avg_interactions:.1f}) - PRs may need more clarity or smaller scope\n")
        elif avg_interactions < 3:
            report.append(f"- ✅ **Low human interaction needed** ({avg_interactions:.1f}) - efficient review process\n")
        
        report.append("\n")
        
        # Footer
        report.append("---\n")
        report.append("\n*This report was automatically generated by the AI Metrics GitHub Action*\n")
        
        return ''.join(report)
    
    def _generate_empty_report(self) -> str:
        """Generate a report when no data is available."""
        report = []
        report.append("# 📊 Weekly AI Development Metrics Report\n")
        report.append(f"**Report Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M UTC')}\n")
        report.append(f"**Period:** Last 7 days\n")
        report.append("\n---\n")
        report.append("\n## No Data Available\n")
        report.append("\nNo merged pull requests were found in the specified time period.\n")
        report.append("\n---\n")
        report.append("\n*This report was automatically generated by the AI Metrics GitHub Action*\n")
        return ''.join(report)
    
    def save_report(self, filename: str = "metrics/weekly-report.md"):
        """Save the generated report to a file."""
        os.makedirs(os.path.dirname(filename), exist_ok=True)
        
        report_content = self.generate_report()
        with open(filename, 'w') as f:
            f.write(report_content)
        
        print(f"\nReport saved to {filename}")
        return filename


def main():
    """Main entry point for the metrics analyzer."""
    # Get configuration from environment
    token = os.environ.get('GITHUB_TOKEN')
    repo_owner = os.environ.get('REPO_OWNER')
    repo_name = os.environ.get('REPO_NAME')
    
    if not all([token, repo_owner, repo_name]):
        print("Error: Required environment variables not set")
        print("Required: GITHUB_TOKEN, REPO_OWNER, REPO_NAME")
        sys.exit(1)
    
    try:
        # Create analyzer and collect metrics
        analyzer = AIMetricsAnalyzer(token, repo_owner, repo_name)
        analyzer.collect_weekly_metrics(days_back=7)
        
        # Generate and save report
        analyzer.save_report()
        
        print("\n✅ Metrics report generated successfully!")
        
    except GithubException as e:
        print(f"GitHub API Error: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == '__main__':
    main()
