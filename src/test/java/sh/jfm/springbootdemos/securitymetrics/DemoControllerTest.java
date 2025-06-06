package sh.jfm.springbootdemos.securitymetrics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// Simple tests for ensuring Spring Security is configured correctly and doesn't regress.
@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static String basicAuth(@SuppressWarnings("SameParameterValue") String usernameAndPassword) {
        return "Basic " + Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
    }

    @ParameterizedTest
    @CsvSource({
            "/secure/1,Hello from secured endpoint 1!",
            "/secure/2,Hello from secured endpoint 2!"
    })
    @WithMockUser
    void securedEndpoints_WithAuthenticatedUser_ReturnsSuccess(String url, String message) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/secure/1", "/secure/2"})
    void securedEndpoints_WithUnauthenticatedUser_ReturnsUnauthorized(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/secure/1", "/secure/2"})
    void securedEndpoints_WithIncorrectCredentials_ReturnsUnauthorized(String url) throws Exception {
        mockMvc.perform(get(url)
                        .header("Authorization", basicAuth("bad-user:bad-password")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get(url)
                        .header("Authorization", basicAuth("chester:bad-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void insecureEndpoint_WithUnauthenticatedUser_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/insecure"))
                .andExpect(status().isOk());
    }
}