package sh.jfm.springbootdemos.securitymetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of Spring Boot's AuditEventRepository that combines:
 * - In-memory storage of audit events (inherited from InMemoryAuditEventRepository)
 * - Metrics collection for authentication/authorization events
 * <p>
 * This repository automatically captures Spring Security events like successful/failed
 * authentication attempts and authorization failures, making them available for:
 * - Monitoring through Micrometer metrics
 * - Runtime inspection through Spring Boot Actuator endpoints
 *
 * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/auditing.html">Actuator Auditing</a>
 */
@Component
public class MetricsAuditEventRepository extends InMemoryAuditEventRepository {
    private static final Logger logger = LoggerFactory.getLogger(MetricsAuditEventRepository.class);
    private final MeterRegistry meterRegistry;

    public MetricsAuditEventRepository(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    private static String getUrl(AuditEvent event) {
        if (event.getData().get("details") instanceof WebAuthenticationDetailsWithUrl authDetails) {
            return authDetails.getUrl();
        }
        return "N/A";
    }

    /**
     * Processes incoming security audit events by:
     * 1. Logging the event details
     * 2. Storing in memory (through parent class)
     * 3. Updating relevant metrics counters
     *
     * @param event The security audit event from Spring Security
     */
    @Override
    public void add(AuditEvent event) {
        super.add(event);
        countEvent(event);
    }

    private void countEvent(AuditEvent event) {
        switch (event.getType()) {
            case "AUTHENTICATION_FAILURE", "AUTHORIZATION_FAILURE" -> countFailureEvent(event);
            case "AUTHENTICATION_SUCCESS" -> countSuccessEvent(event);
            default -> logger.warn("Unknown audit event type: {}", event.getType());
        }
    }

    private void countSuccessEvent(AuditEvent event) {
        Counter.builder("customevent.audit.authentications.success")
                .description("Number of successful authentication audit events")
                .tags("user", event.getPrincipal())
                .tags("url", getUrl(event))
                .tags("type", event.getType())
                .register(meterRegistry)
                .increment();
    }

    private void countFailureEvent(AuditEvent event) {
        Counter.builder("customevent.audit.authentications.failure")
                .description("Number of failed authentication audit events")
                .tags("user", event.getPrincipal())
                .tags("url", getUrl(event))
                .tags("type", event.getType())
                .register(meterRegistry)
                .increment();
    }
}
