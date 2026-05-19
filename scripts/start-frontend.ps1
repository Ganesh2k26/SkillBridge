# Start SkillBridge frontend (Vite dev server)
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location "$root\frontend"
Write-Host "`nStarting SkillBridge frontend ...`n"
npm run dev
