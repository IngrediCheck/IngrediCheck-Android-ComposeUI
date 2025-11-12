#!/usr/bin/env bash

set -euo pipefail

# -----------------------------------------------------------------------------
# configure_branch_protection.sh
#
# Helper script to configure branch protection for the main branch using
# the GitHub CLI (`gh`). This script is idempotent and can be safely re-run.
# -----------------------------------------------------------------------------
#
# Prerequisites:
#   - GitHub CLI installed: https://cli.github.com/
#   - Authenticated via `gh auth login` with repo admin permissions.
#   - `GH_REPO` environment variable exported as "owner/repo", or run the script
#     from within a cloned repository so that `gh` can infer it.
#
# Usage:
#   ./tools/configure_branch_protection.sh
#   GH_REPO=owner/repo ./tools/configure_branch_protection.sh
#
# This script applies the branch protection settings recommended by our CI plan:
#   - Require pull request reviews with at least one approval.
#   - Dismiss stale reviews when new commits are pushed.
#   - Require status checks to pass before merging (build workflow).
#   - Require branches to be up to date before merging.
#   - Disallow force pushes and direct pushes to main.
#
# Customize REQUIRED_CHECKS below if workflow names change.
# -----------------------------------------------------------------------------

BRANCH="main"
REQUIRED_CHECKS=(
  "Build APK / Assemble Release APK"
)

echo "Configuring branch protection for branch: ${BRANCH}"

REQUIRED_CHECKS_JOINED="$(printf '%s\n' "${REQUIRED_CHECKS[@]}")"

PAYLOAD="$(REQUIRED_CHECKS_JOINED="${REQUIRED_CHECKS_JOINED}" python3 <<'PY'
import json
import os

checks = [c for c in os.environ.get("REQUIRED_CHECKS_JOINED", "").splitlines() if c]

payload = {
    "required_status_checks": {
        "strict": True,
        "contexts": checks,
    },
    "enforce_admins": True,
    "required_pull_request_reviews": {
        "dismiss_stale_reviews": True,
        "required_approving_review_count": 1,
    },
    "restrictions": None,
    "allow_force_pushes": False,
    "allow_deletions": False,
    "required_linear_history": True,
    "allow_fork_pushes": False,
    "allow_fork_syncing": True,
    "lock_branch": False,
}

print(json.dumps(payload))
PY
)"

gh api \
  --method PUT \
  -H "Accept: application/vnd.github+json" \
  "/repos/{owner}/{repo}/branches/${BRANCH}/protection" \
  --input - <<<"${PAYLOAD}"

echo "Branch protection applied successfully."

