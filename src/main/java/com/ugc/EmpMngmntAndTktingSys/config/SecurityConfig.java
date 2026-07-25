package com.ugc.EmpMngmntAndTktingSys.config;

import com.ugc.EmpMngmntAndTktingSys.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        System.out.println("SecurityConfig Loaded - Redis PermitAll");
        httpSecurity
                .csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/empNtkt/register","/empNtkt/register/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/redis/**").permitAll()
                        .requestMatchers("/kafka/**").permitAll()
                        .requestMatchers("/empNtkt/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/empNtkt/manager/**")
                        .hasAnyRole("ADMIN","MANAGER")

                        .requestMatchers("/empNtkt/employee/**")
                        .hasAnyRole("ADMIN","MANAGER","EMP")

                        .requestMatchers("/tickets/employee/**")
                        .hasAnyRole("ADMIN", "MANAGER", "EMP")

                        .requestMatchers("/tickets/manager/**")
                        .hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

}
