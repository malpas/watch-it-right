package app.authorization;

import app.config.ApplicationConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * A filter that authenticates requests using a JWT bearer token in their
 * request header. Inserting this before the default authentication filter
 * means that users can be authenticated by both state and Authorization header.
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final String KEY = ApplicationConfig.getJWTKey();
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    /**
     * Get the JWT from a request. Used in validating the JWT to prove user
     * authorization
     *
     * @param req HTTP request
     * @return The authorization token from a HTTP request
     */
    private Optional<String> getTokenFromRequest(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            return Optional.of(token);
        }
        return Optional.empty();
    }

    /**
     * Filter function that authorizes uses with a JWT authorization token.
     *
     * @param request     HTTP request
     * @param response    HTTP response to send
     * @param filterChain Chain of filters
     * @throws ServletException When filter cannot be applied
     * @throws IOException      When filter cannot be applied
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        getTokenFromRequest(request).ifPresent(token -> {
            try {
                Jws<Claims> JWT = Jwts.parser()
                        .setSigningKey(KEY)
                        .parseClaimsJws(token);

                String username = JWT.getBody().getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                SecurityContextHolder.getContext().setAuthentication(new
                        UsernamePasswordAuthenticationToken(username, null,
                        userDetails.getAuthorities()));
            } catch (SignatureException e) {
                System.out.println("Someone tried to use invalid token " + token);
            }
        });

        filterChain.doFilter(request, response);
    }
}
