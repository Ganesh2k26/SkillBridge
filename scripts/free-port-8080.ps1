# Stops processes listening on port 8080 (leftover SkillBridge / Java instances)
$connections = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if (-not $connections) {
    Write-Host "Port 8080 is free."
    exit 0
}
$pids = $connections | Select-Object -ExpandProperty OwningProcess -Unique
foreach ($pid in $pids) {
    $name = (Get-Process -Id $pid -ErrorAction SilentlyContinue).ProcessName
    Write-Host "Stopping $name (PID $pid) on port 8080..."
    Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
}
Write-Host "Done. Port 8080 should be free now."
