# Docker Compose Configuration

Local development and testing environment setup using Docker Compose.

## Overview

This Docker Compose configuration provides a complete local development environment with PostgreSQL, Redis, Kafka, all microservices, frontend, and monitoring stack.

## Services

### Databases

- **PostgreSQL 15**: Main relational database (Port 5432)
- **Redis 7**: Cache and session store (Port 6379)

### Message Broker

- **Kafka 7.4**: Event streaming (Port 9092)
- **Zookeeper**: Kafka coordination (Port 2181)

### Microservices

- **Order Service**: Port 8080
- **Payment Service**: Port 8082
- **Inventory Service**: Port 8083
- **Notification Service**: Port 8084

### Frontend

- **Next.js Application**: Port 3000

### Supporting Services

- **MailHog**: Email testing (Port 1025 SMTP, 8025 Web UI)
- **Prometheus**: Metrics collection (Port 9090)
- **Grafana**: Metrics visualization (Port 3001)

## Getting Started

### Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- Minimum 8GB RAM
- Minimum 20GB free disk space

### Start Services

```bash
docker-compose up -d
docker-compose logs -f
docker-compose down
```

## Access Points

- Frontend: http://localhost:3000
- Order Service: http://localhost:8080
- Grafana: http://localhost:3001 (admin/admin123)
- MailHog: http://localhost:8025
- Prometheus: http://localhost:9090

## Database Setup

Database is initialized automatically from `./scripts/init-db.sql`

Connect to PostgreSQL:

```bash
docker-compose exec postgres psql -U ecommerce_user -d ecommerce
```

## Kafka Topics

- `order-events` - Order creation and status
- `payment-events` - Payment transactions
- `inventory-events` - Stock level changes

## Monitoring

### Grafana

- URL: http://localhost:3001
- Username: admin
- Password: admin123

### Application Health

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

## Troubleshooting

| Issue                       | Solution                                        |
| --------------------------- | ----------------------------------------------- |
| Port already in use         | Change port mapping or kill process             |
| Service won't start         | Check logs: `docker-compose logs service-name`  |
| Database connection refused | Ensure postgres is healthy: `docker-compose ps` |

## Common Commands

```bash
docker-compose build
docker-compose down -v
docker stats
```

## References

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [PostgreSQL Docker](https://hub.docker.com/_/postgres)
