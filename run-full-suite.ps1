$ErrorActionPreference = "Stop"

# Configuration
$jmeterPath = "jmeter" # Assumes jmeter is in PATH
$scenarios = @(
    @{ Name="1-ReadHeavy"; File="jmeter/test-plans/1-read-heavy.jmx"; Duration=1800 },
    @{ Name="2-JoinFilter"; File="jmeter/test-plans/2-join-filter.jmx"; Duration=1800 },
    @{ Name="3-MixedOps"; File="jmeter/test-plans/3-mixed-operations.jmx"; Duration=1800 },
    @{ Name="4-HeavyBody"; File="jmeter/test-plans/4-heavy-body.jmx"; Duration=1800 }
)

$variants = @(
    @{ Name="VariantA"; Url="http://localhost:8080"; Label="A-Jersey" },
    @{ Name="VariantC"; Url="http://localhost:8082"; Label="C-SpringMVC" },
    @{ Name="VariantD"; Url="http://localhost:8083"; Label="D-SpringData" }
)

# Ensure results directory exists
New-Item -ItemType Directory -Force -Path "jmeter/results" | Out-Null

Write-Host "Starting Full Benchmark Suite (4 Scenarios x 3 Variants concurrently)" -ForegroundColor Cyan
Write-Host "Total estimated time: $((($scenarios.Count * 30) + ($scenarios.Count * 1))) minutes" -ForegroundColor Yellow

foreach ($scenario in $scenarios) {
    Write-Host "`n----------------------------------------------------------------" -ForegroundColor Green
    Write-Host "Starting Scenario: $($scenario.Name)" -ForegroundColor Green
    Write-Host "----------------------------------------------------------------"
    
    $jobs = @()

    foreach ($variant in $variants) {
        $resultFile = "jmeter/results/$($variant.Label)-$($scenario.Name).jtl"
        $logFile = "jmeter/results/$($variant.Label)-$($scenario.Name).log"
        
        # Remove old results
        if (Test-Path $resultFile) { Remove-Item $resultFile }

        $cmdArgs = @(
            "-n",
            "-t", $scenario.File,
            "-JBASE_URL=$($variant.Url)",
            "-Jduration=$($scenario.Duration)",
            "-l", $resultFile,
            "-j", $logFile
        )

        Write-Host "Launching $($variant.Label) [$($scenario.Name)]..." -ForegroundColor Cyan
        
        $job = Start-Process -FilePath "jmeter" -ArgumentList $cmdArgs -PassThru -NoNewWindow
        $jobs += $job
    }

    Write-Host "All variants running for $($scenario.Name). Waiting $($scenario.Duration) seconds..." -ForegroundColor Yellow
    
    # Wait for all jobs in this scenario to complete
    $jobs | ForEach-Object { $_.WaitForExit() }
    
    Write-Host "Scenario $($scenario.Name) completed." -ForegroundColor Green
    Write-Host "Cooling down for 60 seconds..."
    Start-Sleep -Seconds 60
}

Write-Host "`nAll Benchmarks Completed Successfully!" -ForegroundColor Green
