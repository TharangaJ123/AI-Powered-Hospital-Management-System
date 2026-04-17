# Kubernetes Deployment Instructions

This directory contains the Kubernetes manifests for the AI-Powered Hospital Management System.

## Prerequisites
- Kubernetes cluster (Minikube, Docker Desktop K8s, etc.)
- `kubectl` configured to your cluster

## Deployment Steps

1. **Apply Configuration & Secrets:**
   ```bash
   kubectl apply -f hospital-config.yaml
   ```

2. **Deploy Database:**
   ```bash
   kubectl apply -f database.yaml
   ```

3. **Deploy Backend Services:**
   ```bash
   kubectl apply -f services/core-backend-services.yaml
   ```
   Wait for Eureka and Gateway to be ready, then:
   ```bash
   kubectl apply -f services/support-backend-services.yaml
   kubectl apply -f services/other-backend-services.yaml
   ```

4. **Deploy Frontend:**
   ```bash
   kubectl apply -f web-client.yaml
   ```

## Accessing the Application

- **API Gateway:** `http://localhost:30099`
- **Web Client (Frontend):` `http://localhost:30173`
- **Eureka Dashboard:** `kubectl port-forward svc/discovery-service 8761:8761` then visit `http://localhost:8761`

## Note on Images
Ensure you build your Docker images with names matching those in the manifests (e.g., `user-management:latest`). If using Minikube, run `eval $(minikube docker-env)` before building.
