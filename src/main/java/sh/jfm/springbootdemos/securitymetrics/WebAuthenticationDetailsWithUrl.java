package sh.jfm.springbootdemos.securitymetrics;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/// Extends WebAuthenticationDetails to include the request URL in authentication details.
/// Used for enhanced logging and security tracking of authentication attempts.
public class WebAuthenticationDetailsWithUrl extends WebAuthenticationDetails {

    private final String url;

    /// Creates authentication details with URL information from the provided request.
    ///
    /// @param request The HTTP request from which to extract authentication details
    public WebAuthenticationDetailsWithUrl(HttpServletRequest request) {
        super(request);
        this.url = request.getRequestURL().toString();
    }

    /// Returns the URL from which the authentication request originated.
    ///
    /// @return The complete request URL as a string
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "WebAuthenticationDetailsWithUrl{" +
                "remoteAddress='" + getRemoteAddress() + '\'' +
                ", sessionId='" + getSessionId() + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
