# PR Metrics System

This directory contains the PR metrics collection system for tracking and analyzing pull request activity in the repository.

## Overview

The PR metrics system automatically generates weekly reports that track:

1. **PR Authors** - All contributors who have opened pull requests
2. **Reviewers** - All team members who have reviewed PRs
3. **Review Activity** - How many reviews each reviewer has performed
4. **Review Comments** - Number of comments each reviewer has left
5. **Average Review Time** - Time from PR creation to first review
6. **Approved Then Failed** - PRs that were approved but failed CI checks
7. **Inactive Reviewers** - Team members who haven't reviewed code recently

## Components

### `pr_metrics.py`

Python script that:
- Connects to GitHub API to fetch PR data
- Analyzes PR reviews, comments, and check runs
- Generates a comprehensive metrics report
- Saves the report to a text file

**Environment Variables:**
- `GITHUB_TOKEN` - GitHub token for API authentication (required)
- `GITHUB_REPOSITORY_OWNER` - Repository owner (required)
- `GITHUB_REPOSITORY` - Full repository name (required)
- `LOOKBACK_DAYS` - Number of days to analyze (default: 30)
- `OUTPUT_FILE` - Output file path (default: pr-metrics-report.txt)

### Workflow: `pr-metrics.yml`

GitHub Actions workflow that:
- Runs weekly on Monday at 9:00 AM UTC
- Can be manually triggered with custom lookback period
- Executes the metrics script
- Uploads the report as an artifact
- Displays summary in GitHub Actions UI

## Usage

### Automatic Weekly Reports

The workflow runs automatically every Monday at 9:00 AM UTC. Reports are uploaded as artifacts and retained for 90 days.

### Manual Execution

You can manually trigger the workflow from the GitHub Actions tab:

1. Go to **Actions** → **PR Metrics Report**
2. Click **Run workflow**
3. (Optional) Specify custom lookback period in days
4. Click **Run workflow**

### Viewing Reports

Reports are available in two ways:

1. **Artifacts**: Download from the workflow run page
   - Go to the workflow run
   - Find "Artifacts" section
   - Download `pr-metrics-report-{run_number}.zip`

2. **Summary**: View directly in the workflow run
   - Go to the workflow run
   - Check the "Summary" section
   - The full report is displayed inline

### Running Locally

You can also run the script locally:

```bash
# Install dependencies
pip install requests

# Set environment variables
export GITHUB_TOKEN="your_github_token"
export GITHUB_REPOSITORY_OWNER="neteinstein"
export GITHUB_REPOSITORY="neteinstein/Alfie-Android-Skeleton"
export LOOKBACK_DAYS=30

# Run the script
python .github/scripts/pr_metrics.py
```

## Report Format

The generated report includes:

```
PR METRICS REPORT
Generated: 2026-01-21 16:21:53 UTC
Period: Last 30 days
Repository: neteinstein/Alfie-Android-Skeleton

## SUMMARY
- PRs Analyzed: X
- Unique PR Authors: X
- Unique Reviewers: X
- Total Reviews: X
- Total Review Comments: X

## PR AUTHORS
Total: X
  - username1
  - username2

## REVIEWER ACTIVITY

| Reviewer | Reviews | Comments | Comments Implying Code Changes |
|----------|---------|----------|--------------------------------|
| reviewer1 | X | X | X |
| reviewer2 | X | X | X |

## AVERAGE REVIEW TIME
Average time to first review: X.XX hours
Median time to first review: X.XX hours

## APPROVED PRs THAT FAILED PIPELINE
Total: X
  - PR #123
    Failed checks: check1, check2

## INACTIVE REVIEWERS
Team members who have not reviewed code:
  - username1
  - username2
```

## Customization

### Changing Schedule

Edit `.github/workflows/pr-metrics.yml`:

```yaml
schedule:
  - cron: '0 9 * * 1'  # Monday at 9:00 AM UTC
```

Common cron patterns:
- Daily: `'0 9 * * *'`
- Weekly (Sunday): `'0 9 * * 0'`
- Monthly (1st): `'0 9 1 * *'`

### Changing Lookback Period

The default lookback period is 30 days. You can change it:

1. **For scheduled runs**: Edit the workflow file and set `LOOKBACK_DAYS`
2. **For manual runs**: Specify when triggering the workflow

### Adding More Metrics

To add new metrics, modify `pr_metrics.py`:

1. Add new data collection in the `PRMetrics` class
2. Update the `generate_report()` method to include new sections
3. Test the changes locally before committing

## Troubleshooting

### Authentication Errors

If you see authentication errors, ensure:
- The workflow has correct permissions (`contents: read`, `pull-requests: read`)
- The repository has actions enabled
- The `GITHUB_TOKEN` has appropriate scopes

### No Data in Report

If the report shows no data:
- Check if PRs exist in the lookback period
- Verify the repository name is correct
- Ensure PRs have reviews and comments
- Try increasing the lookback period

### Script Failures

Check the workflow logs for detailed error messages. Common issues:
- Missing Python dependencies
- API rate limiting (use authenticated requests)
- Network connectivity issues

## Best Practices

1. **Review reports regularly** to identify bottlenecks in the review process
2. **Encourage inactive reviewers** to participate in code reviews
3. **Monitor review times** to ensure timely feedback
4. **Check for approved-then-failed PRs** to improve review quality
5. **Use metrics to improve team processes**, not to judge individual performance

## License

Same as the main repository (MIT License).
# AI Metrics Tracking

This directory contains the automation for generating weekly AI development metrics reports.

## Overview

The AI Metrics system automatically analyzes pull requests to measure development productivity, helping track ROI and efficiency of AI-assisted development.

## Features

### Tracked Metrics

1. **PR Lifecycle Metrics**
   - Time from issue creation to PR opening
   - Time from PR open to first successful CI run
   - Time from PR open to merge
   - Total lifecycle time (issue → PR → merge)

2. **Human Interaction Metrics**
   - Number of review comments
   - Number of manual commits/pushes
   - Number of review rounds
   - Number of unique reviewers
   - Total human interactions per PR

3. **CI/CD Performance**
   - Time to first CI success
   - First attempt success rate
   - Number of CI runs per PR
   - Overall CI success rate

4. **AI vs Human Comparison**
   - Time to merge comparison
   - Human interactions required
   - Code change size differences (lines added/changed)
   - AI PRs with human commits
   - CI failure rates comparison
   - Comments breakdown (AI vs human comments on each PR type)
   - Comments that led to code changes
   - Quality metrics comparison

5. **Code Change Metrics**
   - Files changed
   - Lines added/deleted
   - Total code churn

## Automation

### Workflow Schedule

The metrics report is generated automatically:
- **Schedule**: Every Monday at 9:00 AM UTC
- **Manual Trigger**: Available via GitHub Actions UI

### Report Delivery

Reports are delivered as Pull Requests with:
- Branch: `metrics/ai-report-YYYY-MM-DD`
- Title: `📊 Weekly AI Metrics Report - YYYY-MM-DD`
- Labels: `metrics`, `documentation`, `automated`
- Content: Detailed markdown report in `metrics/weekly-report.md`

## Manual Testing

To test the metrics generation manually:

1. Go to Actions → "Weekly AI Metrics Report"
2. Click "Run workflow"
3. Select the branch to run on
4. Click "Run workflow" button

The workflow will:
1. Analyze PRs from the last 7 days
2. Generate a comprehensive metrics report
3. Create a PR with the report

## Report Structure

Generated reports include:

- **Executive Summary**: High-level statistics
- **Time to Merge Metrics**: Lifecycle timing analysis
- **CI/CD Performance**: Build and test metrics
- **Human Interaction Metrics**: Review and collaboration data
- **AI vs Human Comparison**: Productivity comparison
- **Code Change Metrics**: Code volume statistics
- **Detailed PR List**: Individual PR breakdown
- **Insights & Recommendations**: Automated analysis

## Requirements

The script requires these Python packages:
- `PyGithub`: GitHub API interaction
- `pandas`: Data analysis and aggregation

These are automatically installed by the workflow.

## Environment Variables

The workflow uses these variables:
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions
- `REPO_OWNER`: Repository owner (organization or user)
- `REPO_NAME`: Repository name

## Files

- `.github/workflows/ai-metrics-report.yml`: Workflow definition
- `.github/scripts/generate_ai_metrics.py`: Metrics collection and analysis script
- `metrics/weekly-report.md`: Generated report (created by workflow)

## Customization

### Change Report Frequency

Edit `.github/workflows/ai-metrics-report.yml`:

```yaml
schedule:
  - cron: '0 9 * * 1'  # Every Monday at 9 AM UTC
```

### Change Analysis Period

Edit the script call in the workflow:

```python
analyzer.collect_weekly_metrics(days_back=7)  # Change from 7 to desired days
```

### Add Custom Metrics

Add new analysis methods to the `AIMetricsAnalyzer` class in `generate_ai_metrics.py`.

## Troubleshooting

### No PRs in Report

- Check that PRs were merged in the analysis period
- Verify the workflow has access to repository data
- Check workflow logs for errors

### CI Metrics Missing

- Ensure PRs have workflow runs
- Check that workflow runs are completing
- Verify check runs are properly configured

### Report Not Created

- Check workflow permissions (needs `contents: write`, `pull-requests: write`)
- Verify GitHub token has necessary scopes
- Check workflow logs for specific errors

## Benefits

- **Visibility**: Clear view of development velocity
- **ROI Tracking**: Measure AI development impact
- **Process Improvement**: Identify bottlenecks
- **Quality Monitoring**: Track CI success rates
- **Team Efficiency**: Optimize review processes
