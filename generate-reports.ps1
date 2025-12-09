$jmeterPath = "jmeter"
$resultsDir = "jmeter/results"
$reportDir = "jmeter/report-dashboard"

# Create output directory
if (Test-Path $reportDir) { Remove-Item $reportDir -Recurse -Force }
New-Item -ItemType Directory -Force -Path $reportDir | Out-Null

# Combine all valid JTLs into one file for the global report? 
# Using specific per-scenario report is better for the user tables.

Function Generate-Report($jtlFile, $outputFolder) {
    if (Test-Path $jtlFile) {
        Write-Host "Generating report for $jtlFile..." -ForegroundColor Cyan
        # Increase Heap for large JTL parsing
        $env:HEAP="-Xms1g -Xmx4g"
        # Quote paths to handle spaces
        $cmd = "jmeter -g `"$jtlFile`" -o `"$outputFolder`""
        Invoke-Expression $cmd
    } else {
        Write-Host "Skipping missing file: $jtlFile" -ForegroundColor Red
    }
}

# Generate individual reports for each variant/scenario to easily pick numbers
$files = Get-ChildItem "$resultsDir/*.jtl"
foreach ($file in $files) {
    $name = $file.BaseName
    Generate-Report $file.FullName "$reportDir/$name"
}

Write-Host "Reports generated in $reportDir" -ForegroundColor Green
