package com.ugc.EmpMngmntAndTktingSys.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomerUserDetailService customerUserDetailService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomerUserDetailService customerUserDetailService) {
        this.jwtService = jwtService;
        this.customerUserDetailService = customerUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("========================================");
        System.out.println("Incoming Request : " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header : " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer Token Found");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        System.out.println("JWT : " + jwt);

        try {

            String username = jwtService.extractUsername(jwt);
            System.out.println("Extracted Username : " + username);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        customerUserDetailService.loadUserByUsername(username);

                System.out.println("User Loaded : " + userDetails.getUsername());
                System.out.println("Authorities : " + userDetails.getAuthorities());

                boolean valid = jwtService.isTokenValid(jwt, userDetails);
                System.out.println("Token Valid : " + valid);

                if (valid) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);

                    System.out.println("Authentication Stored In Security Context");
                } else {
                    System.out.println("Token Invalid");
                }
            }

        } catch (Exception e) {
            System.out.println("JWT FILTER EXCEPTION");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}