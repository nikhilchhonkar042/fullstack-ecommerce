# Infrastructure Scripts

Utility scripts for deployment, maintenance, and operational tasks.

## Overview

This directory contains helper scripts for:

- Database initialization and schema setup
- Infrastructure deployment automation
- Backup and restore operations
- Health checks and monitoring
- Performance optimization

## Script Categories

### Database Scripts

#### init-db.sql

Initializes PostgreSQL database schema and initial data.

**Usage:**

```bash
psql -h localhost -U ecommerce_user -d ecommerce -f init-db.sql
```

**Contents:**

- Create tables (orders, order_items, payments, inventory)
- Create indexes for performance
- Create functions for common operations
- Insert initial seed data

### Deployment Scripts

#### deploy.sh

Automated deployment script for production environments.

**Usage:**

```bash
./deploy.sh --environment=prod --version=1.0.0
./deploy.sh --environment=prod --version=1.0.0 --dry-run
```

**Options:**

- `--environment`: Target environment (dev, staging, prod)
- `--version`: Application version to deploy
- `--auto-rollback`: Automatically rollback on failure
- `--dry-run`: Preview changes without applying

### Backup and Restore Scripts

#### backup-database.sh

Creates database backups and stores them securely.

**Usage:**

```bash
./backup-database.sh --database=ecommerce --output=/backups/
./backup-database.sh --all --compress
```

**Features:**

- Full and incremental backups
- Compression with gzip
- Encryption support
- Retention policies

#### restore-database.sh

Restores database from backup file.

**Usage:**

```bash
./restore-database.sh --backup=/backups/ecommerce-2024-01-15.sql.gz
```

### Health Check Scripts

#### health-check.sh

Monitors service health and sends alerts.

**Usage:**

```bash
./health-check.sh --services=all
./health-check.sh --services=order-service,payment-service
./health-check.sh --monitor --interval=60
```

**Checks:**

- Database connectivity
- Service availability
- Pod health (Kubernetes)
- Memory usage
- Disk space

## Maintenance Tasks

### Regular Maintenance

```bash
# Weekly
./scripts/analyze-database.sh
./scripts/vacuum-database.sh
./scripts/reindex-database.sh

# Monthly
./scripts/backup-database.sh --verify
./scripts/update-statistics.sh
```

### Performance Optimization

```bash
./scripts/analyze-slow-queries.sh
./scripts/recommend-indexes.sh
```

## Backup Strategy

### Backup Schedule

- Daily Incremental: 02:00 AM UTC
- Weekly Full: Sunday 01:00 AM UTC
- Monthly Snapshots: 1st of month 00:00 AM UTC

### Retention

- Daily: 7 days
- Weekly: 30 days
- Monthly: 1 year

### Restore Procedure

1. Verify backup integrity
2. Stop all services
3. Restore database
4. Run verification queries
5. Start services
6. Monitor for errors

## Configuration Files

### prometheus.yml

Prometheus metrics collection configuration.

### Grafana Dashboards

Pre-built dashboards for monitoring:

- System metrics (CPU, memory, disk)
- Application metrics
- Business metrics

## Security

- Scripts run with minimal required privileges
- Sensitive data uses environment variables
- Backup files are encrypted
- Access logs are maintained
- Regular security audits

## Contributing

When adding new scripts:

1. Add detailed comments
2. Include error handling
3. Add logging
4. Document usage
5. Test thoroughly

## References

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Bash Scripting Guide](https://www.gnu.org/software/bash/manual/)
