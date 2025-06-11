package sh.jfm.springbootdemos.securitymetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.stereotype.Component;

/// Custom implementation of Spring Boot's AuditEventRepository that provides both
/// audit event storage and security metrics collection capabilities.
///
/// Key features:
/// - Stores authentication and authorization events in memory
/// - Generates Micrometer metrics for security events
/// - Tracks success/failure counts with user and URL context
///
/// Events tracked:
/// - Authentication successes
/// - Authentication failures
/// - Authorization failures
///
/// > Note: Since this demo does not use any traditional authorization, the only usage of Authorization failures is to
/// track access attempts with no username or password.
///
/// All metrics are tagged with relevant context (user, URL, event type).
///
/// @see <a href="https://docs.spring.io/spring-boot/reference/actuator/auditing.html">Spring Boot Actuator Auditing</a>
@Component
public class CustomMetricsAuditEventRepository extends InMemoryAuditEventRepository {
    private static final Logger logger = LoggerFactory.getLogger(CustomMetricsAuditEventRepository.class);
    private final MeterRegistry meterRegistry;

    private static String getUrl(AuditEvent event) {
        if (event.getData().get("details") instanceof WebAuthenticationDetailsWithUrl authDetails) {
            return authDetails.getUrl();
        }
        return "N/A";
    }

    /// Creates a new audit repository with metrics capabilities.
    ///
    /// @param meterRegistry The Micrometer registry used to record security metrics
    public CustomMetricsAuditEventRepository(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /// Processes incoming security audit events by:
    /// 1. Logging the event details
    /// 2. Storing in memory (through parent class)
    /// 3. Updating relevant metrics counters
    ///
    /// @param event The security audit event from Spring Security
    @Override
    public void add(AuditEvent event) {
        super.add(event);
        countEvent(event);
    }

    private void countEvent(AuditEvent event) {
        switch (event.getType()) {
            // AUTHORIZATION_FAILURE is used when no credentials are supplied
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
