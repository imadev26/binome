# Concurrent Benchmark Script
# Runs benchmarks for all 3 variants simultaneously for 30 minutes.

$Duration = 1800 # 30 minutes in seconds

Write-Host "Starting Concurrent Benchmarks for 30 Minutes..."

# Start Variant A Test (Jersey)
Start-Job -ScriptBlock {
    param($dur)
    $args = "-n -t jmeter/test-plans/1-read-heavy.jmx -JBASE_URL=http://localhost:8080 -Jduration=$dur -l jmeter/results/concurrent-a.jtl"
    Write-Host "Starting Variant A..."
    Start-Process jmeter -ArgumentList $args -NoNewWindow
} -ArgumentList $Duration

# Start Variant C Test (Spring MVC)
Start-Job -ScriptBlock {
    param($dur)
    $args = "-n -t jmeter/test-plans/1-read-heavy.jmx -JBASE_URL=http://localhost:8082 -Jduration=$dur -l jmeter/results/concurrent-c.jtl"
    Write-Host "Starting Variant C..."
    Start-Process jmeter -ArgumentList $args -NoNewWindow
} -ArgumentList $Duration

# Start Variant D Test (Spring Data REST)
Start-Job -ScriptBlock {
    param($dur)
    $args = "-n -t jmeter/test-plans/1-read-heavy.jmx -JBASE_URL=http://localhost:8083 -Jduration=$dur -l jmeter/results/concurrent-d.jtl"
    Write-Host "Starting Variant D..."
    Start-Process jmeter -ArgumentList $args -NoNewWindow
} -ArgumentList $Duration

Write-Host "All benchmarks started in background jobs. Monitor progress in Grafana."
Write-Host "http://localhost:3000"
