#!/bin/bash

echo "Setting up port forwarding for all services..."

# Function to run port forwarding in background
port_forward() {
    echo "Port forwarding $1 -> $2:$3"
    kubectl port-forward svc/$1 $2:$3 &
}

# Core Services
port_forward "discovery-service" 8761 8761
port_forward "api-gateway" 30099 8099
port_forward "user-management" 8081 8081
port_forward "appointment-service" 8083 8083

# Support Services
port_forward "ai-symptom-service" 8089 8089
port_forward "notification-service" 8085 8085
port_forward "contact-service" 8087 8087

# Other Services
port_forward "doctor-management" 8082 8082
port_forward "telemedicine-service" 8084 8084
port_forward "review-service" 8088 8088
port_forward "payment-service" 8086 8086

# Frontend
port_forward "web-client" 30173 80

echo "All port forwarding commands started!"
echo "Access URLs:"
echo "- Frontend: http://localhost:30173"
echo "- API Gateway: http://localhost:30099"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- User Management: http://localhost:8081"
echo "- Appointment Service: http://localhost:8083"
echo "- Doctor Management: http://localhost:8082"
echo "- AI Symptom Service: http://localhost:8089"
echo "- Notification Service: http://localhost:8085"
echo "- Contact Service: http://localhost:8087"
echo "- Telemedicine Service: http://localhost:8084"
echo "- Review Service: http://localhost:8088"
echo "- Payment Service: http://localhost:8086"

echo ""
echo "Press Ctrl+C to stop all port forwarding"
wait
