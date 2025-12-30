# Kubernetes Deployment Configuration

Production-grade Kubernetes manifests for deploying the e-commerce platform.

## Overview

This directory contains Kubernetes YAML configurations for:

- Namespace and RBAC setup
- ConfigMaps and Secrets
- Deployments with rolling updates
- Services and Ingress
- Horizontal Pod Autoscaling
- Storage and Persistent Volumes

## Structure

```
k8s/
├── namespaces/
├── secrets/
├── configmaps/
├── deployments/
├── services/
├── ingress/
├── hpa/
└── storage/
```

## Prerequisites

- Kubernetes 1.24+
- kubectl CLI
- Storage class provisioner
- Ingress controller (nginx-ingress)

## Quick Start

### 1. Create Namespace

```bash
kubectl apply -f namespaces/ecommerce-prod.yaml
```

### 2. Create Secrets

```bash
kubectl create secret generic database-credentials \
  --from-literal=username=ecommerce_user \
  --from-literal=password=your-secure-password \
  -n ecommerce-prod
```

### 3. Deploy Services

```bash
kubectl apply -f deployments/
kubectl apply -f services/
```

### 4. Setup Autoscaling

```bash
kubectl apply -f hpa/
```

## Deployment

```bash
# Validate manifests
kubectl apply -f . --dry-run=client

# Apply all
kubectl apply -f .

# Check status
kubectl get pods -n ecommerce-prod
kubectl get services -n ecommerce-prod
```

## Operations

### Scaling

```bash
kubectl scale deployment order-service --replicas=10 -n ecommerce-prod
```

### Rolling Updates

```bash
kubectl set image deployment/order-service \
  order-service=ecommerce/order-service:1.0.1 \
  -n ecommerce-prod
```

### Monitoring

```bash
kubectl get pods -n ecommerce-prod
kubectl describe pod <pod-name> -n ecommerce-prod
kubectl logs deployment/order-service -n ecommerce-prod
```

## Best Practices

### Resource Management

Always set resource requests and limits for containers.

### Health Checks

Implement liveness and readiness probes.

### Security

- Run as non-root user
- Use Pod Security Policies
- Encrypt secrets at rest
- Implement Network Policies

## Troubleshooting

### Pod won't start

```bash
kubectl describe pod <pod-name> -n ecommerce-prod
kubectl logs <pod-name> -n ecommerce-prod
```

### Service unreachable

```bash
kubectl get endpoints -n ecommerce-prod
```

## Cleanup

```bash
kubectl delete namespace ecommerce-prod
```

## References

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
