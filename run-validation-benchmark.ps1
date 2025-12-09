$ErrorActionPreference = "Stop"

# Configuration
$jmeterPath = "jmeter"
$resultsDir = "jmeter/results"
$shortDuration = 300 # 5 minutes per scenario for VALIDATION

# Clean Check
Write-Host "Cleaning old results..." -ForegroundColor Yellow
if (Test-Path "$resultsDir\*.jtl") { Remove-Item "$resultsDir\*.jtl" }
if (Test-Path "$resultsDir\*.log") { Remove-Item "$resultsDir\*.log" }

$scenarios = @(
    @{ Name="1-ReadHeavy"; File="jmeter/test-plans/1-read-heavy.jmx" },
    @{ Name="2-JoinFilter"; File="jmeter/test-plans/2-join-filter.jmx" },
    @{ Name="3-MixedOps"; File="jmeter/test-plans/3-mixed-operations.jmx" },
    @{ Name="4-HeavyBody"; File="jmeter/test-plans/4-heavy-body.jmx" }
)

$variants = @(
    @{ Name="VariantA"; Url="http://localhost:8080"; Label="A-Jersey" },
    @{ Name="VariantC"; Url="http://localhost:8082"; Label="C-SpringMVC" },
    @{ Name="VariantD"; Url="http://localhost:8083"; Label="D-SpringData" }
)

Write-Host "Starting FAST VALIDATION ($($shortDuration)s / scenario)..." -ForegroundColor Cyan

foreach ($scenario in $scenarios) {
    Write-Host "`n-- Scenario: $($scenario.Name)" -ForegroundColor Green
    $jobs = @()

    foreach ($variant in $variants) {
        $resultFile = "$resultsDir/$($variant.Label)-$($scenario.Name).jtl"
        $logFile = "$resultsDir/$($variant.Label)-$($scenario.Name).log"
        
        $cmdArgs = @(
            "-n", "-t", $scenario.File,
            "-JBASE_URL=$($variant.Url)",
            "-Jduration=$shortDuration",
            "-l", $resultFile, "-j", $logFile
        )

        Write-Host "   -> Launching $($variant.Label)..."
        $job = Start-Process -FilePath $jmeterPath -ArgumentList $cmdArgs -PassThru -NoNewWindow
        $jobs += $job
    }

    Write-Host "   Waiting $($shortDuration)s..."
    $jobs | ForEach-Object { $_.WaitForExit() }
}

Write-Host "`nValidation complete. Generating reports..."
./generate-final-reports.ps1
