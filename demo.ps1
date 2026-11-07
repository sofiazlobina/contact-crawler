# demo.ps1 - Automatic demonstration of Contact Crawler

Write-Host "Starting Contact Crawler..." -ForegroundColor Green

# Set JAVA_HOME if needed
$env:JAVA_HOME = "C:\Users\ru-szlobina\.vscode\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64"

# Start application in background
$process = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "spring-boot:run" -PassThru -NoNewWindow -RedirectStandardOutput "demo_output.log"

Write-Host "Waiting for app to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# Check if app started
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method Get -ErrorAction Stop
    Write-Host "App is running: $health" -ForegroundColor Green
} catch {
    Write-Host "Failed to start app" -ForegroundColor Red
    Stop-Process -Id $process.Id -Force
    exit 1
}

# Create results folder
New-Item -ItemType Directory -Force -Path "demo_results" | Out-Null

# 1. Save Health Check
Write-Host "Saving Health Check..." -ForegroundColor Cyan
Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method Get | Out-File "demo_results/1_health_check.txt"

# 2. Run parser
Write-Host "Running parser on Google..." -ForegroundColor Cyan
$body = '{"urls": ["https://www.google.com/contact/"], "maxDepth": 1}'
$postResult = Invoke-RestMethod -Uri "http://localhost:8080/api/start" -Method Post -Body $body -ContentType "application/json"
Write-Host "POST result: $postResult" -ForegroundColor Green
$postResult | Out-File "demo_results/2_post_request.txt"

# Wait for parsing
Write-Host "Waiting for parsing to complete (20 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# 3. Get results
Write-Host "Getting results..." -ForegroundColor Cyan
$getResult = Invoke-RestMethod -Uri "http://localhost:8080/api/answer" -Method Get | ConvertTo-Json -Depth 5
Write-Host "Contacts found: $($getResult.Length)" -ForegroundColor Green
$getResult | Out-File "demo_results/3_get_response.json"

# 4. Save logs
Write-Host "Saving logs..." -ForegroundColor Cyan
if (Test-Path "crawler_logs.txt") {
    Copy-Item "crawler_logs.txt" "demo_results/4_crawler_logs.txt"
    Write-Host "Logs saved" -ForegroundColor Green
}

# 5. Generate report (simple string concatenation, no here-string)
Write-Host "Generating report..." -ForegroundColor Cyan

$report = "CONTACT CRAWLER DEMO REPORT`n"
$report += "Date: " + (Get-Date -Format "yyyy-MM-dd HH:mm:ss") + "`n`n"
$report += "1. Health Check: " + $health + "`n"
$report += "2. POST request: " + $postResult + "`n"
$report += "3. GET request: " + $getResult.Length + " contacts found`n"

if (Test-Path "demo_results/4_crawler_logs.txt") {
    $report += "4. Logs: Saved`n"
} else {
    $report += "4. Logs: Not available`n"
}

$report += "`n=== RESULTS ===`n"
$report += $getResult + "`n"
$report += "`n=== FILES ===`n"
$report += "- demo_results/1_health_check.txt`n"
$report += "- demo_results/2_post_request.txt`n"
$report += "- demo_results/3_get_response.json`n"
$report += "- demo_results/4_crawler_logs.txt`n"

$report | Out-File "demo_results/REPORT.txt"
Write-Host "Report saved: demo_results/REPORT.txt" -ForegroundColor Green

# Stop application (with safety check)
Write-Host "Stopping application..." -ForegroundColor Yellow
if (Get-Process -Id $process.Id -ErrorAction SilentlyContinue) {
    Stop-Process -Id $process.Id -Force
    Write-Host "Process stopped" -ForegroundColor Green
} else {
    Write-Host "Process already finished" -ForegroundColor Yellow
}

# Also kill any remaining java/mvnw processes on port 8080
Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*8080*" } | Stop-Process -Force -ErrorAction SilentlyContinue

Write-Host "Done! All results in demo_results/" -ForegroundColor Green