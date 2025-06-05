package sh.jfm.springbootdemos.securitymetrics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple controller providing secured endpoints for basic authentication metrics.
 * Used to demonstrate and test Spring Security authentication flow and logging.
 * <p>
 * Endpoints include simulated random delays to provide more realistic metrics
 * for demonstration purposes.
 */
@RestController
public class DemoController {
    @Value("${endpoint.delay.min}")
    private long minDelayMs;
    @Value("${endpoint.delay.max}")
    private long maxDelayMs;

    private static void simulateDelay(long delayMS) {
        try {
            Thread.sleep(delayMS);
        } catch (InterruptedException e) {
            // https://stackoverflow.com/questions/4906799/why-invoke-thread-currentthread-interrupt-in-a-catch-interruptexception-block
            Thread.currentThread().interrupt();
        }
    }

    @GetMapping("/secure/1")
    public String securedEndpoint1() {
        simulateRandomDelay();
        return "Hello from secured endpoint 1!";
    }

    @GetMapping("/secure/2")
    public String securedEndpoint2() {
        simulateRandomDelay();
        return "Hello from secured endpoint 2!";
    }

    @GetMapping("/insecure")
    public String insecureEndpoint() {
        simulateRandomDelay();
        return "Hello from insecure endpoint!";
    }

    private void simulateRandomDelay() {
        simulateDelay(ThreadLocalRandom.current().nextLong(minDelayMs, maxDelayMs));
    }
}
