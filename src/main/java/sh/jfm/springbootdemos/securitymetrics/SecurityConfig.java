package sh.jfm.springbootdemos.securitymetrics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration class that defines the security rules and authentication setup.
 * This configuration enables web security and sets up:
 * - Basic authentication for endpoints under /secure/**
 * - Public access for all other endpoints
 * - Custom authentication details source for tracking request URLs
 * - In-memory user details for demo purposes
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain with basic authentication and URL-based security rules.
     * - Disables CSRF for demonstration purposes
     * - Requires authentication for /secure/** endpoints
     * - Allows public access to all other endpoints
     * - Uses custom WebAuthenticationDetailsSource for tracking request URLs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/secure/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> httpBasic
                        .authenticationDetailsSource(new WebAuthenticationDetailsSourceWithRequest())
                )
                .build();
    }

    /**
     * Creates an in-memory user details service with demo users.
     * WARNING: Uses deprecated default password encoder - not suitable for production use.
     * Provides two test users with hardcoded credentials for demonstration purposes.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        //noinspection deprecation default password encoder is not safe for production, but this is a demo, so it's okay
        return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder()
                        .username("chester")
                        .password("hardcoded-insecure-password")
                        .build(),
                User.withDefaultPasswordEncoder()
                        .username("harold")
                        .password("hardcoded-insecure-password2")
                        .build()
        );
    }
}
