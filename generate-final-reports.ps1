$ErrorActionPreference = "Continue"

# Configuration
$jmeterPath = "jmeter"
$resultsDir = "jmeter/results"
$reportBaseDir = "jmeter/final-reports"
$env:HEAP="-Xms1g -Xmx4g" 

# Create base report directory
if (Test-Path $reportBaseDir) { Remove-Item $reportBaseDir -Recurse -Force }
New-Item -ItemType Directory -Force -Path $reportBaseDir | Out-Null

# Get all JTL files
$jtlFiles = Get-ChildItem -Path $resultsDir -Filter "*.jtl"

Write-Host "Generating HTML reports for $($jtlFiles.Count) files..." -ForegroundColor Cyan

foreach ($file in $jtlFiles) {
    $reportName = $file.BaseName
    $outputDir = "$reportBaseDir\$reportName"
    
    Write-Host "   Processing: $($file.Name)..." -NoNewline
    
    # Clean output dir
    if (Test-Path $outputDir) { Remove-Item $outputDir -Recurse -Force }
    New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
    
    # Run JMeter Report Generator
    $cmd = "jmeter -g `"$($file.FullName)`" -o `"$outputDir`""
    
    try {
        Invoke-Expression $cmd | Out-Null
        Write-Host " OK" -ForegroundColor Green
    } catch {
        Write-Host " FAIL" -ForegroundColor Red
        Write-Error $_
    }
}

Write-Host "`nAll reports generated in: $reportBaseDir" -ForegroundColor Yellow
