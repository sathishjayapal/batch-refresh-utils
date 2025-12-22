# Batch Refresh Utils

A production-ready Spring Boot application for managing and scheduling batch jobs with dynamic cron-based scheduling.

## Features

- **Dynamic Cron Scheduling**: Schedule jobs using cron expressions stored in the database
- **REST API**: Full CRUD operations for batch jobs and job runs
- **Web UI**: Thymeleaf-based interface for job management
- **Manual Execution**: Trigger jobs on-demand via API
- **Health Checks**: Spring Boot Actuator integration with custom health indicators
- **Comprehensive Logging**: Structured logging with profile-based configuration
- **Production Ready**: Thread pool management, error handling, metrics, and monitoring

## Quick Start

### Prerequisites
- Java 21
- PostgreSQL database
- Maven 3.6+

### Database Setup

```bash
# Create database
createdb aws-refresh

# Or use Docker
docker run -d \
  --name batch-refresh-postgres \
  -e POSTGRES_DB=aws-refresh \
  -e POSTGRES_USER=psqladmin \
  -e POSTGRES_PASSWORD='psqladminpas$' \
  -p 7433:5432 \
  postgres:15
```

### Running the Application

```bash
# Build
./mvnw clean package

# Run with local profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Or run the JAR
java -Dspring.profiles.active=local -jar target/aws-refresh-0.0.1-SNAPSHOT.jar
```

After starting the application:
- Web UI: `http://localhost:8080`
- API: `http://localhost:8080/api`
- Health Check: `http://localhost:8080/actuator/health`

## Development

Update your local database connection in `application.yml` or create your own `application-local.yml` file to override settings for development.

During development it is recommended to use the profile `local`. In IntelliJ `-Dspring.profiles.active=local` can be added in the VM options of the Run Configuration after enabling this property in "Modify options".

Lombok must be supported by your IDE. For IntelliJ install the Lombok plugin and enable annotation processing - [learn more](https://bootify.io/next-steps/spring-boot-with-lombok.html).

## Build

The application can be built using the following command:

```
mvnw clean package
```

Start your application with the following command - here with the profile `production`:

```
java -Dspring.profiles.active=production -jar ./target/aws-refresh-0.0.1-SNAPSHOT.jar
```

If required, a Docker image can be created with the Spring Boot plugin. Add `SPRING_PROFILES_ACTIVE=production` as environment variable when running the container.

```
mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=me.sathish/aws-refresh
```

## Batch Job Scheduler

The application includes a production-ready cron-based job scheduler. See [BATCH_JOB_SCHEDULER.md](BATCH_JOB_SCHEDULER.md) for detailed documentation including:
- How to create and schedule jobs
- Cron expression examples
- Implementing custom job executors
- API reference
- Monitoring and troubleshooting

### Quick Example

```bash
# Create a scheduled job
curl -X POST http://localhost:8080/api/batchJobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Data Refresh",
    "description": "Refreshes data daily at 2 AM",
    "scheduleCron": "0 0 2 * * ?",
    "isActive": true,
    "createdAt": "2024-12-21T10:00:00Z",
    "updatedAt": "2024-12-21T10:00:00Z"
  }'

# Execute job manually
curl -X POST http://localhost:8080/api/batch-scheduler/jobs/10001/execute

# Check scheduler status
curl http://localhost:8080/api/batch-scheduler/status
```

## Further readings

* [Batch Job Scheduler Documentation](BATCH_JOB_SCHEDULER.md)
* [Maven docs](https://maven.apache.org/guides/index.html)  
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
* [Thymeleaf docs](https://www.thymeleaf.org/documentation.html)  
* [Bootstrap docs](https://getbootstrap.com/docs/5.3/getting-started/introduction/)  
* [Learn Spring Boot with Thymeleaf](https://www.wimdeblauwe.com/books/taming-thymeleaf/)  
