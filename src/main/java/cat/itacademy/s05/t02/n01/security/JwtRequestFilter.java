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
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/swagger-ui.html"),
                new AntPathRequestMatcher("/api-docs/**")
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

        // Si la petición es para una URL pública, la pasamos directamente sin procesar JWT
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
                // Asumimos que jwtUtil.extractRole() devuelve el rol como "ROLE_USER", "ROLE_ADMIN", etc.
                // o "USER", "ADMIN" y luego se le añade el prefijo.
                // Sé consistente con lo que generateToken() guarda y extractRole() devuelve.
                role = jwtUtil.extractRole(jwt);
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired for URI {}: {}", request.getRequestURI(), e.getMessage());
                // No envíes respuesta aquí, deja que el AuthenticationEntryPoint lo maneje
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
            // Si confías en el token (firma y expiración validadas por jwtUtil.extractUsername/extractRole)
            // puedes crear la autenticación directamente con el rol del token.
            // Para una validación más estricta (ej. verificar si el usuario aún existe o si sus roles han cambiado en la BD),
            // deberías cargar UserDetails aquí.

            // Ejemplo de carga desde UserDetails (más seguro si los roles pueden cambiar o el usuario ser deshabilitado):
            // UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // if (jwtUtil.validateToken(jwt, userDetails)) { // Necesitarías un método validateToken que también compare con UserDetails
            //    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            //    UsernamePasswordAuthenticationToken authenticationToken =
            //            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            //    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //    log.debug("User '{}' authenticated from JWT and UserDetails.", username);
            // }

            // Ejemplo simplificado confiando en el rol del token (asegúrate que jwtUtil.extractRole es fiable):
            // Y que el rol ya tiene el prefijo "ROLE_" o lo añades.
            // Si extractRole devuelve "USER", necesitarías "ROLE_" + role.
            // Si extractRole devuelve "ROLE_USER", entonces solo role.
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role); // Asume que 'role' ya es "ROLE_X"
            Collection<? extends GrantedAuthority> authorities = Collections.singletonList(authority);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("User '{}' with role '{}' authenticated from JWT.", username, role);
        }

        filterChain.doFilter(request, response);
    }
}
