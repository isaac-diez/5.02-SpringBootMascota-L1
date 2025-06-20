package cat.itacademy.s05.t02.n01.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);


    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RequestMatcher publicUrls;


    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;

        this.publicUrls = new OrRequestMatcher(
                new AntPathRequestMatcher("/auth/login"),
                new AntPathRequestMatcher("/user/new"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/swagger-ui.html"),
                new AntPathRequestMatcher("/api-docs/**"),
                new AntPathRequestMatcher("/login.html"),
                new AntPathRequestMatcher("/register.html"),
                new AntPathRequestMatcher("/test")
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JwtRequestFilter: request.getRequestURI() = {}", request.getRequestURI());
        log.info("JwtRequestFilter: request.getServletPath() = {}", request.getServletPath());
        boolean matched = publicUrls.matches(request);
        log.info("JwtRequestFilter: publicUrls.matches(request) = {}", matched);

        if (matched) {
            log.trace("Request for public URL {} - skipping JWT processing.", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (publicUrls.matches(request)) {
            log.trace("Request for public URL {} - skipping JWT processing.", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.trace("Processing JWT for protected URL: {}", request.getRequestURI());
        String username = null;
        String jwt = null;
        String role = null;

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);

                role = jwtUtil.extractRole(jwt);
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired for URI {}: {}", request.getRequestURI(), e.getMessage());

            } catch (UnsupportedJwtException e) {
                log.warn("Unsupported JWT Token for URI {}: {}", request.getRequestURI(), e.getMessage());
            } catch (MalformedJwtException e) {
                log.warn("Malformed JWT Token for URI {}: {}", request.getRequestURI(), e.getMessage());
            } catch (SignatureException e) {
                log.warn("Invalid JWT signature for URI {}: {}", request.getRequestURI(), e.getMessage());
            } catch (IllegalArgumentException e) {
                log.warn("JWT claims string is empty or token is invalid for URI {}: {}", request.getRequestURI(), e.getMessage());
            }
        } else {
            log.trace("Authorization header for URI {} does not begin with Bearer String or is null.", request.getRequestURI());
        }

        if (username != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            Collection<? extends GrantedAuthority> authorities = Collections.singletonList(authority);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("User '{}' with role '{}' authenticated from JWT.", username, role);
        }

        filterChain.doFilter(request, response);
    }
}
