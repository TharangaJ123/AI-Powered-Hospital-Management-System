# Hospital Management System - Startup Script
# This script starts all services and the web client in the correct order.

Write-Host 'Starting Discovery Service (Eureka)...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd infrastructure/discovery-service; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow
Start-Sleep -Seconds 15

Write-Host 'Starting API Gateway...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd infrastructure/api-gateway; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow
Start-Sleep -Seconds 10

Write-Host 'Starting User Management...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd services/user-management; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow

Write-Host 'Starting Doctor Management...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd services/doctor-management; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow

Write-Host 'Starting Appointment Service...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd services/appointment-service; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow

Write-Host 'Starting Telemedicine Service...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd services/telemedicine-service; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow

Write-Host 'Starting AI Symptom Service...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd services/ai-symptom-service; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow

Write-Host 'Starting Contact Service...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd services/contact-service; .\mvnw spring-boot:run -Dspring-boot.run.jvmArguments=\"-Xmx256m -Xms256m\"' -NoNewWindow

Write-Host 'Starting Web Client...' -ForegroundColor Cyan
Start-Process powershell -ArgumentList '-Command cd web-client; npm run dev -- --host' -NoNewWindow

Write-Host 'All services, the contact service, and the web client have been initiated!' -ForegroundColor Green
Write-Host 'Please wait about 60 seconds for everything to register with Eureka.' -ForegroundColor Yellow
Write-Host 'Access Eureka Dashboard at: http://localhost:8761' -ForegroundColor Gray
