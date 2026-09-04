package com.example.roleslz_backend.infra;

import com.example.roleslz_backend.infra.service.SecurityService;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Popula o SecurityContext quando vem um Bearer válido — e só isso.
 *
 * O filtro NÃO recusa requisição: quem decide o que exige autenticação é o
 * authorizeHttpRequests do {@code SecurityConfig}, e quem responde 401 é o
 * authenticationEntryPoint configurado lá. Antes este filtro respondia 403 para header
 * ausente (mesmo em rota pública) e estourava UsernameNotFoundException — virando 500 —
 * quando o token era inválido ou expirado. Como o app só desloga em 401, um token vencido
 * nunca chegava a derrubar a sessão.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final SecurityService securityService;
    private final UserRepository userRepository;

    public SecurityFilter(SecurityService securityService, @Lazy UserRepository userRepository) {
        this.securityService = securityService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);

        if (token != null) {
            String email = securityService.validateToken(token); // "" quando inválido/expirado

            if (email != null && !email.isBlank()) {
                Optional<UserEntity> user = userRepository.findByEmail(email);

                user.ifPresent(u -> SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities())));
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        // startsWith + substring: o replace("Bearer", "") antigo apagava a palavra em
        // qualquer posição, inclusive dentro do próprio token.
        if (header == null || !header.startsWith(PREFIXO)) {
            return null;
        }

        String token = header.substring(PREFIXO.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
