package com.tplite.core_banking.module.auth.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends GenericFilterBean {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.trim().regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            log.debug("JWT header missing for path={}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.trim().substring(BEARER_PREFIX.length()).trim();

        try {
            String email = jwtService.extractUsername(token);
            Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
            if (email != null && shouldAuthenticate(currentAuthentication)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("JWT token is invalid for path={}, email={}", request.getRequestURI(), email);
                }
            } else {
                log.debug("JWT authentication skipped for path={}, email={}, currentAuthentication={}",
                        request.getRequestURI(),
                        email,
                        currentAuthentication);
            }
        } catch (RuntimeException ex) {
            log.warn("JWT authentication failed for path={}: {}", request.getRequestURI(), ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldAuthenticate(Authentication currentAuthentication) {
        return currentAuthentication == null
                || !currentAuthentication.isAuthenticated()
                || currentAuthentication instanceof AnonymousAuthenticationToken;
    }
}
