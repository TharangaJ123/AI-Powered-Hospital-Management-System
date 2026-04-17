# PowerShell script for complete Kubernetes setup and deployment

Write-Host "=== AI-Powered Hospital Management System - Kubernetes Setup ===" -ForegroundColor Green

# Step 1: Check if Kubernetes is running
Write-Host "Step 1: Checking Kubernetes cluster status..." -ForegroundColor Yellow
try {
    $clusterInfo = kubectl cluster-info 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Kubernetes cluster is not running!" -ForegroundColor Red
        Write-Host "Please start your Kubernetes cluster first:" -ForegroundColor Yellow
        Write-Host "- For Docker Desktop: Enable Kubernetes in settings" -ForegroundColor Cyan
        Write-Host "- For Minikube: run 'minikube start'" -ForegroundColor Cyan
        Write-Host "- For other clusters: ensure kubectl is configured" -ForegroundColor Cyan
        exit 1
    }
    Write-Host "Kubernetes cluster is running!" -ForegroundColor Green
}
catch {
    Write-Host "ERROR: Cannot connect to Kubernetes cluster!" -ForegroundColor Red
    Write-Host "Please start your Kubernetes cluster first." -ForegroundColor Yellow
    exit 1
}

# Step 2: Build Docker images (if needed)
Write-Host ""
Write-Host "Step 2: Building Docker images..." -ForegroundColor Yellow
Write-Host "Note: Make sure you're in the project root directory" -ForegroundColor Cyan

# List of services
$services = @("discovery-service", "api-gateway", "user-management", "appointment-service", "doctor-management", "ai-symptom-service", "notification-service", "contact-service", "telemedicine-service", "review-service", "payment-service", "web-client")

foreach ($service in $services) {
    Write-Host "Building $service..." -ForegroundColor Cyan
    # Uncomment the line below if you need to build images
    # docker build -t $service`:latest .
}

# Step 3: Apply Kubernetes configurations
Write-Host ""
Write-Host "Step 3: Deploying to Kubernetes..." -ForegroundColor Yellow

Write-Host "Applying configuration and secrets..." -ForegroundColor Cyan
kubectl apply -f hospital-config.yaml

Write-Host "Deploying database..." -ForegroundColor Cyan
kubectl apply -f database.yaml

Write-Host "Waiting for database to be ready..." -ForegroundColor Cyan
kubectl wait --for=condition=ready pod -l app=mysql --timeout=300s

Write-Host "Deploying core backend services..." -ForegroundColor Cyan
kubectl apply -f services/core-backend-services.yaml

Write-Host "Waiting for core services to be ready..." -ForegroundColor Cyan
kubectl wait --for=condition=ready pod -l app=discovery-service --timeout=300s
kubectl wait --for=condition=ready pod -l app=api-gateway --timeout=300s

Write-Host "Deploying support backend services..." -ForegroundColor Cyan
kubectl apply -f services/support-backend-services.yaml

Write-Host "Deploying other backend services..." -ForegroundColor Cyan
kubectl apply -f services/other-backend-services.yaml

Write-Host "Deploying frontend..." -ForegroundColor Cyan
kubectl apply -f web-client.yaml

# Step 4: Wait for all deployments to be ready
Write-Host ""
Write-Host "Step 4: Waiting for all deployments to be ready..." -ForegroundColor Yellow
kubectl wait --for=condition=available deployment --all --timeout=600s

# Step 5: Show pod status
Write-Host ""
Write-Host "Step 5: Checking pod status..." -ForegroundColor Yellow
kubectl get pods

# Step 6: Start port forwarding
Write-Host ""
Write-Host "Step 6: Starting port forwarding..." -ForegroundColor Yellow

# Function to start port forwarding job
function Start-PortForward {
    param($Service, $LocalPort, $TargetPort)
    
    Write-Host "Port forwarding $Service -> $LocalPort`:$TargetPort" -ForegroundColor Cyan
    $job = Start-Job -ScriptBlock {
        param($svc, $local, $target)
        kubectl port-forward svc/$svc $local`:$target
    } -ArgumentList $Service, $LocalPort, $TargetPort
    
    return $job
}

# Start all port forwarding jobs
$jobs = @()

# Core Services
$jobs += Start-PortForward "discovery-service" 8761 8761
$jobs += Start-PortForward "api-gateway" 30099 8099
$jobs += Start-PortForward "user-management" 8081 8081
$jobs += Start-PortForward "appointment-service" 8083 8083

# Support Services
$jobs += Start-PortForward "ai-symptom-service" 8089 8089
$jobs += Start-PortForward "notification-service" 8085 8085
$jobs += Start-PortForward "contact-service" 8087 8087

# Other Services
$jobs += Start-PortForward "doctor-management" 8082 8082
$jobs += Start-PortForward "telemedicine-service" 8084 8084
$jobs += Start-PortForward "review-service" 8088 8088
$jobs += Start-PortForward "payment-service" 8086 8086

# Frontend
$jobs += Start-PortForward "web-client" 30173 80

Write-Host ""
Write-Host "=== DEPLOYMENT COMPLETE ===" -ForegroundColor Green
Write-Host ""
Write-Host "Access URLs:" -ForegroundColor Yellow
Write-Host "- Frontend: http://localhost:30173" -ForegroundColor White
Write-Host "- API Gateway: http://localhost:30099" -ForegroundColor White
Write-Host "- Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "- User Management: http://localhost:8081" -ForegroundColor White
Write-Host "- Appointment Service: http://localhost:8083" -ForegroundColor White
Write-Host "- Doctor Management: http://localhost:8082" -ForegroundColor White
Write-Host "- AI Symptom Service: http://localhost:8089" -ForegroundColor White
Write-Host "- Notification Service: http://localhost:8085" -ForegroundColor White
Write-Host "- Contact Service: http://localhost:8087" -ForegroundColor White
Write-Host "- Telemedicine Service: http://localhost:8084" -ForegroundColor White
Write-Host "- Review Service: http://localhost:8088" -ForegroundColor White
Write-Host "- Payment Service: http://localhost:8086" -ForegroundColor White

Write-Host ""
Write-Host "Press Ctrl+C to stop all port forwarding" -ForegroundColor Yellow
Write-Host "Run 'Stop-Job -Name *' to stop all background jobs" -ForegroundColor Cyan

# Keep script running
try {
    while ($true) {
        Start-Sleep -Seconds 1
    }
}
finally {
    # Clean up jobs when script stops
    Write-Host "Stopping all port forwarding jobs..." -ForegroundColor Yellow
    Stop-Job -Job $jobs
    Remove-Job -Job $jobs
}
