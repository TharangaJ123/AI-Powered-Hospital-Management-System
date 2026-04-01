# Hospital Management System - Backend Startup Script
# This script starts all microservices in the correct order.

Write-Host 'Starting Discovery Service (Eureka)...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList 'mvn -f infrastructure/discovery-service/pom.xml spring-boot:run' -NoNewWindow
Start-Sleep -Seconds 15

Write-Host 'Starting API Gateway...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList 'mvn -f infrastructure/api-gateway/pom.xml spring-boot:run' -NoNewWindow
Start-Sleep -Seconds 10

Write-Host 'Starting User Management...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList 'mvn -f services/user-management/pom.xml spring-boot:run' -NoNewWindow

Write-Host 'Starting Doctor Management...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList 'mvn -f services/doctor-management/pom.xml spring-boot:run' -NoNewWindow

Write-Host 'Starting Appointment Service...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList 'mvn -f services/appointment-service/pom.xml spring-boot:run' -NoNewWindow

Write-Host 'Starting Telemedicine Service...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList 'mvn -f services/telemedicine-service/pom.xml spring-boot:run' -NoNewWindow

Write-Host 'Starting AI Symptom Service...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList 'mvn -f services/ai-symptom-service/pom.xml spring-boot:run' -NoNewWindow

Write-Host 'All services have been initiated!' -ForegroundColor Green
Write-Host 'Please wait about 60 seconds for everything to register with Eureka.' -ForegroundColor Yellow
Write-Host 'Access Eureka Dashboard at: http://localhost:8761' -ForegroundColor Gray
