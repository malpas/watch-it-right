package app.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    PasswordEncoder encoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/api/login", "/api/register", "/rss/*").permitAll()
                    .antMatchers("/**/static/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter,
                        BasicAuthenticationFilter.class)
                .csrf()
                    .disable()
                .cors()
                    .and()
                .exceptionHandling()
                    .and()
                .formLogin()
                    .loginProcessingUrl("/api/login")
                .successHandler(new JWTAuthenticationSuccessHandler())
                .failureHandler(new JWTAuthenticationFailureHandler())
                .and()
                .logout()
                    .permitAll()
                .and()
                .headers()
                    .defaultsDisabled()
                    .cacheControl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }
}
