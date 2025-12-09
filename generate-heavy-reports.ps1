$ErrorActionPreference = "Continue"

# Configuration
$jmeterPath = "jmeter"
$resultsDir = "jmeter/results"
$reportBaseDir = "jmeter/final-reports"
$env:HEAP="-Xms1g -Xmx4g" 

Write-Host "Generating HeavyBody Reports..." -ForegroundColor Cyan

# Filter only HeavyBody JTLs
$jtlFiles = Get-ChildItem -Path $resultsDir -Filter "*HeavyBody.jtl"

if ($jtlFiles.Count -eq 0) {
    Write-Error "No *HeavyBody.jtl files found!"
    exit
}

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

Write-Host "`nHeavyBody Reports Generated." -ForegroundColor Yellow
