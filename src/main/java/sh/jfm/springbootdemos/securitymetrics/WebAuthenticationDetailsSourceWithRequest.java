package sh.jfm.springbootdemos.securitymetrics;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 * Custom WebAuthenticationDetailsSource that creates WebAuthenticationDetailsWithUrl instances.
 * Part of Spring Security's authentication flow, this class enhances the standard authentication
 * details by including URL information from the request.
 * Used in conjunction with authentication providers to capture additional request context.
 */
public class WebAuthenticationDetailsSourceWithRequest extends WebAuthenticationDetailsSource {

    /**
     * Creates a new WebAuthenticationDetailsWithUrl instance for the given request.
     *
     * @param request the HTTP request to build the authentication details from
     * @return WebAuthenticationDetails containing additional URL information
     */
    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new WebAuthenticationDetailsWithUrl(request);
    }
}