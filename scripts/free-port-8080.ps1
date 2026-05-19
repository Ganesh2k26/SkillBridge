# Stops processes listening on port 8080 (leftover SkillBridge / Java instances)
$connections = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if (-not $connections) {
    Write-Host "Port 8080 is free."
    exit 0
}
$processIds = $connections | Select-Object -ExpandProperty OwningProcess -Unique
foreach ($procId in $processIds) {
    $name = (Get-Process -Id $procId -ErrorAction SilentlyContinue).ProcessName
    Write-Host "Stopping $name (PID $procId) on port 8080..."
    Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 1
Write-Host "Done. Port 8080 should be free now."
