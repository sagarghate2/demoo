# Starto V3 - Frontend Runner
# This script starts the Next.js development server.
# Environment variables are loaded from starto-web/.env.local

Write-Host "Starting Starto V3 Frontend..." -ForegroundColor Cyan
Set-Location -Path "$PSScriptRoot\starto-web"

# Install dependencies if node_modules is missing
if (-not (Test-Path "node_modules")) {
    Write-Host "Installing dependencies... this may take a moment." -ForegroundColor Yellow
    npm install
}

npm run dev
