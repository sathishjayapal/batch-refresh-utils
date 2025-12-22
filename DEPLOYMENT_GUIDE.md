# Deployment Guide - Batch Job Scheduler

## Pre-Deployment Checklist

### 1. Database Setup
- [ ] PostgreSQL database created
- [ ] Database connection credentials configured
- [ ] Flyway migrations will run automatically on startup

### 2. Environment Configuration

Create environment-specific configuration files or set environment variables:

```bash
# Database Configuration
export JDBC_DATABASE_URL=jdbc:postgresql://your-db-host:5432/aws-refresh
export JDBC_DATABASE_USERNAME=your-username
export JDBC_DATABASE_PASSWORD=your-password

# Optional: Scheduler Configuration
export BATCH_JOB_ENABLE_SCHEDULING=true
export BATCH_JOB_SCHEDULER_POOL_SIZE=20
export BATCH_JOB_EXECUTOR_POOL_SIZE=10
```

### 3. Build the Application

```bash
# Clean build
./mvnw clean package -DskipTests

# With tests
./mvnw clean package
```

### 4. Run the Application

#### Development
```bash
java -Dspring.profiles.active=local -jar target/aws-refresh-0.0.1-SNAPSHOT.jar
```

#### Production
```bash
java -Dspring.profiles.active=production \
  -DJDBC_DATABASE_URL=jdbc:postgresql://prod-db:5432/aws-refresh \
  -DJDBC_DATABASE_USERNAME=prod_user \
  -DJDBC_DATABASE_PASSWORD=secure_password \
  -jar target/aws-refresh-0.0.1-SNAPSHOT.jar
```

## Docker Deployment

### Build Docker Image
```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=batch-refresh-utils:latest
```

### Run with Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: aws-refresh
      POSTGRES_USER: psqladmin
      POSTGRES_PASSWORD: psqladminpas$
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    image: batch-refresh-utils:latest
    depends_on:
      - postgres
    environment:
      SPRING_PROFILES_ACTIVE: production
      JDBC_DATABASE_URL: jdbc:postgresql://postgres:5432/aws-refresh
      JDBC_DATABASE_USERNAME: psqladmin
      JDBC_DATABASE_PASSWORD: psqladminpas$
      BATCH_JOB_ENABLE_SCHEDULING: "true"
    ports:
      - "8080:8080"
    restart: unless-stopped

volumes:
  postgres_data:
```

Run:
```bash
docker-compose up -d
```

## Kubernetes Deployment

### ConfigMap
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: batch-refresh-config
data:
  application.yml: |
    spring:
      profiles:
        active: production
    batch:
      job:
        enable-scheduling: true
        scheduler-pool-size: 20
        executor-pool-size: 10
```

### Secret
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: batch-refresh-secrets
type: Opaque
stringData:
  jdbc-url: jdbc:postgresql://postgres-service:5432/aws-refresh
  jdbc-username: psqladmin
  jdbc-password: your-secure-password
```

### Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: batch-refresh-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: batch-refresh
  template:
    metadata:
      labels:
        app: batch-refresh
    spec:
      containers:
      - name: batch-refresh
        image: batch-refresh-utils:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: JDBC_DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: batch-refresh-secrets
              key: jdbc-url
        - name: JDBC_DATABASE_USERNAME
          valueFrom:
            secretKeyRef:
              name: batch-refresh-secrets
              key: jdbc-username
        - name: JDBC_DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: batch-refresh-secrets
              key: jdbc-password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
```

### Service
```yaml
apiVersion: v1
kind: Service
metadata:
  name: batch-refresh-service
spec:
  selector:
    app: batch-refresh
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

## Post-Deployment Verification

### 1. Health Check
```bash
curl http://your-host:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

### 2. Scheduler Status
```bash
curl http://your-host:8080/api/batch-scheduler/status
```

### 3. Create Test Job
```bash
curl -X POST http://your-host:8080/api/batchJobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Job",
    "description": "Test scheduled job",
    "scheduleCron": "0 */5 * * * ?",
    "isActive": true,
    "createdAt": "2024-12-21T10:00:00Z",
    "updatedAt": "2024-12-21T10:00:00Z"
  }'
```

### 4. Verify Job Scheduled
Check logs for:
```
Scheduled job: Test Job (ID: 10001) with cron: 0 */5 * * * ?
```

### 5. Manual Execution Test
```bash
curl -X POST http://your-host:8080/api/batch-scheduler/jobs/10001/execute
```

## Monitoring

### Metrics Endpoints
- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`

### Prometheus Integration
If using Prometheus, add to `prometheus.yml`:
```yaml
scrape_configs:
  - job_name: 'batch-refresh'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['your-host:8080']
```

### Log Monitoring
Logs are written to:
- Console (stdout)
- File: `/var/log/batch-refresh/application.log` (production profile)

Configure log aggregation (ELK, Splunk, etc.) to collect from:
```
/var/log/batch-refresh/*.log
```

## Troubleshooting

### Application Won't Start
1. Check database connectivity
2. Verify Flyway migrations completed
3. Check logs for errors
4. Verify Java 21 is installed

### Jobs Not Scheduling
1. Check `batch.job.enable-scheduling=true`
2. Verify jobs have valid cron expressions
3. Check jobs are active (`is_active=true`)
4. Review scheduler logs

### Database Connection Issues
1. Verify database is running
2. Check connection string format
3. Verify credentials
4. Check network connectivity
5. Review Hikari connection pool settings

### Performance Issues
1. Adjust thread pool sizes
2. Review database indexes
3. Check job execution times
4. Monitor memory usage
5. Review concurrent job count

## Scaling Considerations

### Horizontal Scaling
- Multiple instances can run simultaneously
- Each instance will schedule jobs independently
- Use database locks if job execution must be singleton
- Consider distributed scheduling (Quartz with JDBC store)

### Vertical Scaling
Adjust resources based on:
- Number of concurrent jobs
- Job execution time
- Database connection pool size
- Thread pool sizes

## Security Recommendations

1. **API Security**: Add Spring Security for authentication
2. **Database**: Use encrypted connections (SSL)
3. **Secrets**: Use secret management (Vault, AWS Secrets Manager)
4. **Network**: Deploy behind firewall/VPN
5. **Monitoring**: Restrict actuator endpoints

## Backup and Recovery

### Database Backup
```bash
pg_dump -h your-db-host -U your-user aws-refresh > backup.sql
```

### Restore
```bash
psql -h your-db-host -U your-user aws-refresh < backup.sql
```

### Job Configuration Export
```bash
curl http://your-host:8080/api/batchJobs > jobs-backup.json
```

## Maintenance

### Update Application
1. Build new version
2. Stop application gracefully
3. Deploy new version
4. Verify health checks
5. Monitor logs

### Database Migrations
Flyway runs automatically on startup. To run manually:
```bash
./mvnw flyway:migrate
```

### Clean Old Job Runs
Add periodic cleanup job or manual cleanup:
```sql
DELETE FROM batch_job_run 
WHERE created_at < NOW() - INTERVAL '90 days';
```
