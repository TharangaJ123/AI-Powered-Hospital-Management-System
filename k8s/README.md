# Kubernetes Deployment Guide

## Prerequisites
- Docker Desktop with **Kubernetes enabled** (Settings → Kubernetes → Enable Kubernetes)
- `kubectl` configured (should work automatically with Docker Desktop)
- All Docker images built: `docker-compose build` from the project root

## Verify Setup
```bash
kubectl config current-context
# Should show: docker-desktop
```

## Deploy Everything (in order)

### Step 1: Apply Config & Secrets
```bash
kubectl apply -f k8s/00-config.yaml
```

### Step 2: Deploy Infrastructure (Eureka + API Gateway)
```bash
kubectl apply -f k8s/01-infrastructure.yaml
```
Wait for discovery-service to be ready:
```bash
kubectl wait --for=condition=ready pod -l app=discovery-service --timeout=120s
```

### Step 3: Deploy Backend Services
```bash
kubectl apply -f k8s/02-backend-services.yaml
kubectl apply -f k8s/03-support-services.yaml
```

### Step 4: Deploy Frontend
```bash
kubectl apply -f k8s/04-web-client.yaml
```

### Step 5: Verify All Pods Are Running
```bash
kubectl get pods
```
All pods should show `Running` status within 1-2 minutes.

## Accessing the Application

| Service         | URL                        |
|----------------|---------------------------|
| **Frontend**   | http://localhost:30173     |
| **API Gateway**| http://localhost:30099     |

To view the Eureka Dashboard:
```bash
kubectl port-forward svc/discovery-service 8761:8761
```
Then visit: http://localhost:8761

## Troubleshooting

### Check pod logs
```bash
kubectl logs <pod-name>
```

### Restart a deployment
```bash
kubectl rollout restart deployment <deployment-name>
```

### Delete everything and start over
```bash
kubectl delete -f k8s/04-web-client.yaml
kubectl delete -f k8s/03-support-services.yaml
kubectl delete -f k8s/02-backend-services.yaml
kubectl delete -f k8s/01-infrastructure.yaml
kubectl delete -f k8s/00-config.yaml
```

## Service Port Reference

| Service              | Container Port |
|---------------------|---------------|
| discovery-service    | 8761          |
| api-gateway          | 8099          |
| user-management      | 8081          |
| doctor-management    | 8082          |
| appointment-service  | 8083          |
| payment-service      | 8084          |
| notification-service | 8085          |
| review-service       | 8086          |
| contact-service      | 8087          |
| telemedicine-service | 8088          |
| ai-symptom-service   | 8089          |
| web-client           | 80            |
