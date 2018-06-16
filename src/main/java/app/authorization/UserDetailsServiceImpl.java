package app.authorization;

import app.config.ApplicationConfig;
import app.user.User;
import app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * App's implementation of the UserDetailsService. Gets user data from a
 * database rather than memory.
 */
@Configuration
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String adminUsername = ApplicationConfig.getAdminUsername();
        String adminPassword = ApplicationConfig.getAdminPassword();

        if (!username.equals(adminUsername)) {
            userRepository.findFirstByUsername(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("No user called " + username));

        }

        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority("USER"));
                if (username.equals(adminUsername)) {
                    authorities.add(new SimpleGrantedAuthority("ADMIN"));
                }

                return authorities;
            }

            @Override
            public String getPassword() {
                if (username.equals(adminUsername)) {
                    return encoder.encode(adminPassword);
                }
                Optional<User> userOptional = userRepository.findFirstByUsername(username);
                return userOptional.map(User::getPassword).orElse(null);
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
