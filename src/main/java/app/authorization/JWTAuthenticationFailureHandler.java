package app.authorization;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Provides JSON error response on authentication failure
 */
public class JWTAuthenticationFailureHandler extends
        SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid username or password.\"}");
    }
}
