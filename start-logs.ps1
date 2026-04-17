Write-Host 'Starting Discovery Service (Eureka)...'
Start-Process powershell -ArgumentList '-Command cd infrastructure/discovery-service; .\mvnw spring-boot:run > start_log.txt 2>&1'

Write-Host 'Starting API Gateway...'
Start-Process powershell -ArgumentList '-Command cd infrastructure/api-gateway; .\mvnw spring-boot:run > start_log.txt 2>&1'

Write-Host 'Starting User Management...'
Start-Process powershell -ArgumentList '-Command cd services/user-management; .\mvnw spring-boot:run > start_log.txt 2>&1'

Write-Host 'Starting Doctor Management...'
Start-Process powershell -ArgumentList '-Command cd services/doctor-management; .\mvnw spring-boot:run > start_log.txt 2>&1'

Write-Host 'Starting Appointment Service...'
Start-Process powershell -ArgumentList '-Command cd services/appointment-service; .\mvnw spring-boot:run > start_log.txt 2>&1'

Write-Host 'Starting Telemedicine Service...'
Start-Process powershell -ArgumentList '-Command cd services/telemedicine-service; .\mvnw spring-boot:run > start_log.txt 2>&1'

Write-Host 'Starting AI Symptom Service...'
Start-Process powershell -ArgumentList '-Command cd services/ai-symptom-service; .\mvnw spring-boot:run > start_log.txt 2>&1'

Write-Host 'Starting Notification Service...'
Start-Process powershell -ArgumentList '-Command cd services/notification-service; .\mvnw spring-boot:run > start_log.txt 2>&1'
