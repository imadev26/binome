$ErrorActionPreference = "Stop"

Write-Host "🛑 Arrêt de tous les services Docker..." -ForegroundColor Yellow
docker-compose -f monitoring/docker-compose.yml down --remove-orphans

Write-Host "🧹 Nettoyage des volumes obsolètes (optionnel)..." -ForegroundColor Gray
# docker volume prune -f 

Write-Host "🚀 Démarrage de la Base de Données (PostgreSQL)..." -ForegroundColor Cyan
docker-compose -f monitoring/docker-compose.yml up -d postgres
Write-Host "   Attente de 10 secondes pour l'initialisation de la DB..."
Start-Sleep -Seconds 10

Write-Host "🚀 Démarrage de la Monitoring Stack (InfluxDB, Grafana)..." -ForegroundColor Cyan
docker-compose -f monitoring/docker-compose.yml up -d influxdb grafana
Write-Host "   Attente de 10 secondes..."
Start-Sleep -Seconds 10
Write-Host "   ✅ Grafana est accessible sur http://localhost:3000" -ForegroundColor Green

Write-Host "🚀 Démarrage des Applications (Variants A, C, D)..." -ForegroundColor Cyan
docker-compose -f monitoring/docker-compose.yml up -d variant-a variant-c variant-d

Write-Host "⏳ Attente de 30 secondes pour le démarrage des applications Spring/Jersey..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Health Checks
$endpoints = @(
    @{ Name="Variant A (Jersey)"; Url="http://localhost:8080/items" },
    @{ Name="Variant C (Spring MVC)"; Url="http://localhost:8082/items" },
    @{ Name="Variant D (Spring Data)"; Url="http://localhost:8083/items" }
)

foreach ($ep in $endpoints) {
    try {
        $response = Invoke-WebRequest -Uri $ep.Url -Method Head -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "   ✅ $($ep.Name) est EN LIGNE" -ForegroundColor Green
        }
    } catch {
        Write-Host "   ❌ $($ep.Name) est HORS LIGNE ou ERREUR" -ForegroundColor Red
    }
}

Write-Host "`n🎉 Infrastructure prête ! Vous pouvez lancer le benchmark." -ForegroundColor Cyan
