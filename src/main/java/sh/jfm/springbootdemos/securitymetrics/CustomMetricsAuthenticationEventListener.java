package sh.jfm.springbootdemos.securitymetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/// Listens to Spring Security authentication events and records metrics using Micrometer.
/// This component tracks both successful and failed authentication attempts, providing:
/// - Custom metrics for monitoring authentication success/failure rates
/// - URL tracking for authentication attempts
/// - User-specific authentication metrics
///
/// All metrics are tagged with relevant context information (user, URL) to enable
/// detailed analysis and monitoring through platforms like Prometheus or other
/// monitoring systems that support Micrometer.
///
/// @see <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/events.html">Spring Security Authentication Events</a>
@Component
public class CustomMetricsAuthenticationEventListener {
    private final MeterRegistry meterRegistry;

    /// Creates a new metrics listener with the provided meter registry.
    ///
    /// @param meterRegistry The Micrometer registry used to record authentication metrics
    public CustomMetricsAuthenticationEventListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /// Handles successful authentication events by incrementing a success counter in the metrics registry.
    /// Creates a metric named 'customevent.authentications.success' with the following tags:
    /// - user: The authenticated username
    /// - url: The URL where authentication was attempted
    ///
    /// @param success The authentication success event containing user details
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        Counter.builder("customevent.authentications.success")
                .description("Number of successful authentications")
                .tags("user", success.getAuthentication().getName())
                .tags("url", ((WebAuthenticationDetailsWithUrl) success.getAuthentication().getDetails()).getUrl())
                .register(meterRegistry)
                .increment();
    }

    /// Handles failed authentication events by incrementing a failure counter in the metrics registry.
    /// Creates a metric named 'customevent.authentications.failure' with the following tags:
    /// - user: The username that failed authentication
    /// - url: The URL where authentication was attempted
    /// - message: The detailed failure reason from the security exception
    ///
    /// > Note: Spring Boot does not consider absence of credentials to be an authentication failure,
    /// the Audit approach covers those scenarios
    ///
    /// @param failure The authentication failure event containing error details
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
