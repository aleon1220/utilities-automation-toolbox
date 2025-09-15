$outputFileDate = "2025-03-March"
# $outputFileDate = (Get-Date -Format "yyyy-MMMM")
$outputFileName = $outputFileDate + "-merged.md"

# Clear the output file if it already exists
Clear-Content -Path $outputFileName

# Get all .md files in the current directory and loop through them
Get-ChildItem -Path *.md | ForEach-Object {
    # Exclude the output file from the list of files to be merged
    if ($_.Name -ne $outputFileName) {
        # Read the content of the current markdown file
        $content = Get-Content -Path $_.FullName
        # Append the content to the output file, adding a new line between files for readability
        $content | Out-File -FilePath $outputFileName -Append -Encoding UTF8
        # Add a separator to distinguish between the merged files (optional)
        Add-Content -Path $outputFileName -Value "`n---`n" -Encoding UTF8
    }
}

Write-Host "All markdown files have been merged into $outputFileName"