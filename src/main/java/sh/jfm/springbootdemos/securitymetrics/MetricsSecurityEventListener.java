package sh.jfm.springbootdemos.securitymetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Listens to Spring Security authentication events and records metrics using Micrometer.
 * This component tracks both successful and failed authentication attempts, providing:
 * - Custom metrics for monitoring authentication success/failure rates
 * - URL tracking for authentication attempts
 * <p>
 * Integrates with Spring Boot's metrics system to enable monitoring through
 * platforms like Prometheus or other monitoring systems that support Micrometer.
 *
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/events.html">Spring Security Authentication Events</a>
 */
@Component
public class MetricsSecurityEventListener {
    private final MeterRegistry meterRegistry;

    /**
     * Creates a new metrics listener with the provided meter registry.
     *
     * @param meterRegistry The Micrometer registry used to record authentication metrics
     */
    public MetricsSecurityEventListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Handles successful authentication events by logging the event and incrementing
     * a success counter in the metrics registry. The counter includes tags for
     * the username and the URL where authentication was attempted.
     */
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        Counter.builder("customevent.authentications.success")
                .description("Number of successful authentications")
                .tags("user", success.getAuthentication().getName())
                .tags("url", ((WebAuthenticationDetailsWithUrl) success.getAuthentication().getDetails()).getUrl())
                .register(meterRegistry)
                .increment();
    }

    /**
     * Handles failed authentication events by logging the failure details and incrementing
     * a failure counter in the metrics registry. The counter includes tags for
     * the username, URL, and failure reason.
     */
    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failure) {
        Counter.builder("customevent.authentications.failure")
                .description("Number of failed authentications")
                .tags("user", failure.getAuthentication().getName())
                .tags("url", ((WebAuthenticationDetailsWithUrl) failure.getAuthentication().getDetails()).getUrl())
                .tags("message", failure.getException().getMessage())
                .register(meterRegistry)
                .increment();
    }
}
