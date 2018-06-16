package app.authorization;

import app.config.ApplicationConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication success handler that, upon login, provides a JSON object
 * containing the user's token.
 */
public class JWTAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final String KEY = ApplicationConfig.getJWTKey();

    /**
     * Upon logging in, send a JSON string containing the user's JWT web
     * token.
     *
     * @param request        HTTP request
     * @param response       HTTP response to send
     * @param authentication User authentication
     * @throws IOException Throws IOException when response cannot be written
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"token\": \"%s\"}", token));
    }
}
