# centralized Orchestrator repository

## User cases
1. Automatically create a PR at the end of the month for the active repos
2. Generate high level reporting for the GitHub Orgs

### Design Monthly PR

```mermaid
%%{init: { 'theme': 'forest' } }%%
gitGraph
    commit id: "Initial"
    commit id: "Base Repo"
    
    branch "2026-01-january-improvements"
    checkout "2026-01-january-improvements"
    commit id: "Jan improve 1"
    commit id: "Jan Fix 2"
    commit id: "Jan improve 2"
    
    checkout main
    merge "2026-01-january-improvements"
    commit id: "Monthly Merge" type: HIGHLIGHT
    
    branch "yyyy-mm-february-improvements"
    checkout "yyyy-mm-february-improvements"
    commit id: "month improvement 1"
``` 
