# Start SkillBridge backend (frees port 8080 first)
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
& "$PSScriptRoot\free-port-8080.ps1"
Set-Location "$root\backend"
Write-Host "`nStarting SkillBridge backend on http://localhost:8080 ...`n"
mvn spring-boot:run
