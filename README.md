# Spring Boot Security Metrics Demo

## Overview

This repository demonstrates Spring Boot security metrics implementation. It combines
built-in [Spring Boot Actuator metrics](https://docs.spring.io/spring-boot/reference/actuator/metrics.html) 
and custom metrics using [Micrometer](https://micrometer.io/).

## Technology Stack

- [Prometheus](https://prometheus.io/docs/introduction/overview/) - Metrics storage
- [Grafana](https://grafana.com/docs/) - Metrics visualization

For a detailed overview of this technology stack, refer to
the [Spring Boot Observability Demo](https://github.com/JamesMcMahon/spring-boot-demo-observability).

## How to Use this Repo

### Prerequisites

- Java 24 or higher
- Docker (for running Prometheus and Grafana)

### Starting the Application

1. Clone this repository
2. Start the application: `./mvnw spring-boot:run`

> **Note:** Booting the application starts up the full visualization stack automatically from the Docker compose file.

### Generating Sample Traffic

You can generate sample traffic using the provided scripts:

```bash
# Generate authentication events and other endpoint calls
./call-endpoints.sh

# Continuously call endpoints to simulate load on the application
./simulate-load.sh
```

### Viewing Metrics

Once everything is running and traffic has been simulated you can access the metrics through:

- Grafana Dashboards: http://localhost:3000/dashboards
- Prometheus: http://localhost:9090
- Spring Actuator's Prometheus Endpoint: http://localhost:8080/actuator/prometheus

The primary Grafana dashboard is "Security". The other dashboards contain some standard Spring Boot metrics.

The tooltips in the Security dashboard break down each visualization and source where the metrics are coming from.

## Techniques Used For Custom Metrics

### Event Listeners

The `CustomMetricsAuthenticationEventListener` component subscribes to Spring Security authentication events and records
custom success and failure metrics. It tracks authentication attempts by creating custom metrics, tagging them with
contextual information
such as username, URL, and failure reasons.

For more information about the event system,
see [Spring Security Authentication Events](https://docs.spring.io/spring-security/reference/servlet/authentication/events.html).

### Audit Repository

The `CustomMetricsAuditEventRepository` component extends Spring Boot's `InMemoryAuditEventRepository` to store and
track security audit events. It creates custom metrics for authentication successes, authentication failures, and
authorization failures, with each event tagged by user and URL.

For detailed information about auditing,
see [Spring Boot Actuator Auditing](https://docs.spring.io/spring-boot/reference/actuator/auditing.html).