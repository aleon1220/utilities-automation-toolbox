param (
    [string]$from,
    [string[]]$seconds
)

if (-not $from -and -not $seconds) {
    Write-Host ""
    Write-Host "Extract Frames From Video" -ForegroundColor Cyan
    Write-Host "=========================" -ForegroundColor Cyan
    Write-Host "This script extracts specific frames from a video file based on a list of seconds."
    Write-Host "The frames will be saved in the same directory as the source video."
    Write-Host ""
    Write-Host "Usage Example:" -ForegroundColor Yellow
    Write-Host '.\extract-frames-from-video.ps1 -from "C:\path\to\video.mp4" -seconds 1,2,3' -ForegroundColor White
    Write-Host ""
    exit 0
}

try {
    if (-not $from) {
        throw "The '-from' parameter is required. Provide the path to the video."
    }
    if (-not $seconds) {
        throw "The '-seconds' parameter is required. Provide a comma-separated list of seconds."
    }

    if (-not (Test-Path -Path $from -PathType Leaf)) {
        throw "The specified video file does not exist: $from"
    }
    
    if (-not (Get-Command "ffmpeg" -ErrorAction SilentlyContinue)) {
        throw "ffmpeg is not installed or not available in the system PATH."
    }

    $videoFile = Get-Item -Path $from
    $videoDir = $videoFile.DirectoryName
    $videoBaseName = $videoFile.BaseName
    
    $secondsArray = $seconds | ForEach-Object { $_ -split ',' } | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne '' }

    foreach ($sec in $secondsArray) {
        if (-not [int]::TryParse($sec, [ref]$null)) {
            Write-Warning "Skipping invalid second format: '$sec'"
            continue
        }

        $outputFilename = "$videoBaseName-frame-$sec.jpg"
        $outputPath = Join-Path -Path $videoDir -ChildPath $outputFilename
        
        Write-Host "Extracting frame at second $sec to $outputPath..." -ForegroundColor Cyan

        $ffmpegArgs = @(
            "-ss", $sec,
            "-i", $from,
            "-frames:v", "1",
            "-q:v", "2",
            "-y",
            $outputPath
        )

        $process = Start-Process -FilePath "ffmpeg" -ArgumentList $ffmpegArgs -NoNewWindow -Wait -PassThru -RedirectStandardError "$videoDir\ffmpeg_error.log"

        if ($process.ExitCode -ne 0) {
            $errorOutput = Get-Content "$videoDir\ffmpeg_error.log" -Raw
            throw "FFmpeg failed at second $sec with exit code $($process.ExitCode). Error details: $errorOutput"
        }
        
        Remove-Item "$videoDir\ffmpeg_error.log" -ErrorAction SilentlyContinue
    }

    Write-Host "Extraction completed successfully." -ForegroundColor Green
} catch {
    Write-Error "Execution Failed: $_"
}
