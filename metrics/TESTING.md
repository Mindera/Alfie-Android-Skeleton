# AI Metrics Testing Guide

This guide explains how to test the AI Metrics reporting system.

## Quick Test (Manual Trigger)

1. **Navigate to GitHub Actions**
   - Go to the repository on GitHub
   - Click on "Actions" tab
   - Select "Weekly AI Metrics Report" from the workflows list

2. **Trigger Manually**
   - Click "Run workflow" button
   - Select the branch (e.g., `main` or `copilot/add-weekly-ai-metrics-report`)
   - Click "Run workflow" to start

3. **Monitor Execution**
   - Watch the workflow run in real-time
   - Check each step completes successfully:
     - ✓ Checkout code
     - ✓ Set up Python
     - ✓ Install dependencies
     - ✓ Generate metrics report
     - ✓ Create Pull Request

4. **Review Results**
   - After completion, navigate to Pull Requests
   - Look for PR titled: `📊 Weekly AI Metrics Report - YYYY-MM-DD`
   - Review the generated report in `metrics/weekly-report.md`

## Expected Behavior

### First Run (No Data)
If there are no merged PRs in the last 7 days:
- Workflow completes successfully
- Creates a PR with an empty report message
- Report shows "No Data Available"

### With Merged PRs
If there are merged PRs in the last 7 days:
- Workflow analyzes all merged PRs
- Calculates comprehensive metrics
- Generates detailed report with:
  - Executive summary
  - Time to merge metrics
  - CI/CD performance
  - Human interaction metrics
  - AI vs Human comparison
  - Insights and recommendations

## Troubleshooting

### Workflow Fails at "Generate metrics report"

**Possible causes:**
1. Missing environment variables
   - Check workflow has access to `GITHUB_TOKEN`
   - Verify `REPO_OWNER` and `REPO_NAME` are set correctly

2. Python dependencies installation failed
   - Check Python version (should be 3.11)
   - Verify pip can access PyPI

3. GitHub API rate limiting
   - Wait for rate limit to reset
   - Consider running less frequently if hitting limits

**Solution:**
- Review workflow logs
- Check GitHub Actions permissions
- Verify repository settings

### No PR Created

**Possible causes:**
1. Insufficient permissions
   - Workflow needs `contents: write` and `pull-requests: write`
   
2. Branch already exists
   - Delete the existing `metrics/ai-report-YYYY-MM-DD` branch
   
3. No changes detected
   - Report might be identical to previous run

**Solution:**
- Check workflow permissions in Settings → Actions → General
- Delete conflicting branches
- Review workflow logs for specific errors

### Report Shows No Data

**Expected when:**
- No PRs were merged in the last 7 days
- Running on a new repository

**Not expected:**
- There are merged PRs but none are detected

**Solution:**
- Verify PRs are actually merged (not just closed)
- Check PRs are in the main branch or analyzed branches
- Review script logs for errors during PR analysis

## Testing Checklist

Before marking the feature as complete:

- [ ] Workflow appears in Actions tab
- [ ] Can trigger manually via "Run workflow"
- [ ] Workflow completes without errors
- [ ] Python dependencies install correctly
- [ ] Script executes successfully
- [ ] Report is generated in `metrics/weekly-report.md`
- [ ] PR is created automatically
- [ ] PR has correct labels (metrics, documentation, automated)
- [ ] Report contains expected sections
- [ ] Metrics calculations look correct
- [ ] AI vs human comparison works (if applicable)
- [ ] Sample report matches actual report format

## Testing with Historical Data

To test with existing repository data:

1. Ensure the repository has merged PRs in the last 7 days
2. Run the workflow manually
3. Verify the report includes:
   - Correct PR count
   - Accurate time calculations
   - Proper human interaction counts
   - Valid CI metrics (if CI data available)

## Scheduled Run Testing

To verify the scheduled trigger works:

1. Wait for the next Monday at 9:00 AM UTC, or
2. Temporarily modify the cron schedule to trigger sooner:
   ```yaml
   schedule:
     - cron: '*/15 * * * *'  # Every 15 minutes for testing
   ```
3. Revert the cron schedule after testing
4. Monitor that the workflow triggers automatically

## Advanced Testing

### Test Different Scenarios

1. **Repository with no PRs**
   - Expected: Empty report generated

2. **Repository with only AI PRs**
   - Expected: AI metrics shown, human metrics N/A

3. **Repository with only human PRs**
   - Expected: Human metrics shown, AI metrics N/A

4. **Mixed AI and human PRs**
   - Expected: Full comparison report

5. **PRs without CI data**
   - Expected: CI section shows "No CI data available"

6. **PRs without linked issues**
   - Expected: Issue lifecycle section shows "No PRs linked to issues"

### Validate Metrics Accuracy

Manually verify a few calculations:
1. Pick 2-3 recent merged PRs
2. Calculate time to merge manually
3. Count comments and reviews
4. Compare with report values
5. Ensure calculations match

## Performance Testing

For repositories with many PRs:

1. Monitor workflow execution time
2. Check for API rate limiting
3. Verify memory usage stays reasonable
4. Ensure all PRs are processed

Expected performance:
- ~10-30 seconds for repos with <10 PRs/week
- ~1-2 minutes for repos with 20-50 PRs/week
- May need optimization for >100 PRs/week

## Security Testing

Verify:
- [ ] Script doesn't expose sensitive data
- [ ] GitHub token is used securely
- [ ] No credentials in generated reports
- [ ] PR body doesn't include sensitive info

## Next Steps After Testing

1. ✅ Verify workflow works correctly
2. ✅ Review first generated report
3. ✅ Adjust metrics if needed
4. ✅ Document any issues found
5. ✅ Merge PR to enable weekly reporting
