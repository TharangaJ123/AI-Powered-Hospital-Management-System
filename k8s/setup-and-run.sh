#!/bin/bash

echo "=== AI-Powered Hospital Management System - Kubernetes Setup ==="

# Step 1: Check if Kubernetes is running
echo "Step 1: Checking Kubernetes cluster status..."
if ! kubectl cluster-info &> /dev/null; then
    echo "ERROR: Kubernetes cluster is not running!"
    echo "Please start your Kubernetes cluster first:"
    echo "- For Docker Desktop: Enable Kubernetes in settings"
    echo "- For Minikube: run 'minikube start'"
    echo "- For other clusters: ensure kubectl is configured"
    exit 1
fi

echo "Kubernetes cluster is running!"

# Step 2: Build Docker images (if needed)
echo ""
echo "Step 2: Building Docker images..."
echo "Note: Make sure you're in the project root directory"

# Build all service images
services=("discovery-service" "api-gateway" "user-management" "appointment-service" "doctor-management" "ai-symptom-service" "notification-service" "contact-service" "telemedicine-service" "review-service" "payment-service" "web-client")

for service in "${services[@]}"; do
    echo "Building $service..."
    # Uncomment the line below if you need to build images
    # docker build -t $service:latest .
done

# Step 3: Apply Kubernetes configurations
echo ""
echo "Step 3: Deploying to Kubernetes..."

echo "Applying configuration and secrets..."
kubectl apply -f hospital-config.yaml

echo "Deploying database..."
kubectl apply -f database.yaml

echo "Waiting for database to be ready..."
kubectl wait --for=condition=ready pod -l app=mysql --timeout=300s

echo "Deploying core backend services..."
kubectl apply -f services/core-backend-services.yaml

echo "Waiting for core services to be ready..."
kubectl wait --for=condition=ready pod -l app=discovery-service --timeout=300s
kubectl wait --for=condition=ready pod -l app=api-gateway --timeout=300s

echo "Deploying support backend services..."
kubectl apply -f services/support-backend-services.yaml

echo "Deploying other backend services..."
kubectl apply -f services/other-backend-services.yaml

echo "Deploying frontend..."
kubectl apply -f web-client.yaml

# Step 4: Wait for all deployments to be ready
echo ""
echo "Step 4: Waiting for all deployments to be ready..."
kubectl wait --for=condition=available deployment --all --timeout=600s

# Step 5: Show pod status
echo ""
echo "Step 5: Checking pod status..."
kubectl get pods

# Step 6: Start port forwarding
echo ""
echo "Step 6: Starting port forwarding..."

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

echo ""
echo "=== DEPLOYMENT COMPLETE ==="
echo ""
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
