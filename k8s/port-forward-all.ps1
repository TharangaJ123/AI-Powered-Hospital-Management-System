# PowerShell script for port forwarding all services

Write-Host "Setting up port forwarding for all services..."

# Function to start port forwarding job
function Start-PortForward {
    param($Service, $LocalPort, $TargetPort)
    
    Write-Host "Port forwarding $Service -> $LocalPort:$TargetPort"
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

Write-Host "All port forwarding commands started!"
Write-Host ""
Write-Host "Access URLs:"
Write-Host "- Frontend: http://localhost:30173"
Write-Host "- API Gateway: http://localhost:30099"
Write-Host "- Eureka Dashboard: http://localhost:8761"
Write-Host "- User Management: http://localhost:8081"
Write-Host "- Appointment Service: http://localhost:8083"
Write-Host "- Doctor Management: http://localhost:8082"
Write-Host "- AI Symptom Service: http://localhost:8089"
Write-Host "- Notification Service: http://localhost:8085"
Write-Host "- Contact Service: http://localhost:8087"
Write-Host "- Telemedicine Service: http://localhost:8084"
Write-Host "- Review Service: http://localhost:8088"
Write-Host "- Payment Service: http://localhost:8086"

Write-Host ""
Write-Host "Press Ctrl+C to stop all port forwarding"
Write-Host "Run 'Stop-Job -Name *' to stop all background jobs"

# Keep script running
try {
    while ($true) {
        Start-Sleep -Seconds 1
    }
}
finally {
    # Clean up jobs when script stops
    Write-Host "Stopping all port forwarding jobs..."
    Stop-Job -Job $jobs
    Remove-Job -Job $jobs
}
