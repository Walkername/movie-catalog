package ru.walkername.movie_catalog.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.walkername.movie_catalog.services.TokenService;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashMap;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Autowired
    public JWTFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println(authHeader);
        if (authHeader != null && authHeader.length() == 6) {
            authHeader = authHeader + " ";
        }

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (token.isBlank()) {
                setResponse(response, request, "JWT token was not found");
                return;
            } else {
                try {
                    DecodedJWT jwt = tokenService.validateToken(token);
                    String role = jwt.getClaim("role").asString();
                    String username = jwt.getClaim("username").asString();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            "",
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                    );

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JWTVerificationException e) {
                    setResponse(response, request, "Invalid JWT token");
                    return;
                }

            }
        }

        filterChain.doFilter(request, response);
    }

    private void setResponse(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        LinkedHashMap<String, String> responseMap = new LinkedHashMap<>();
        responseMap.put("timestamp", now.toString());
        responseMap.put("status", String.valueOf(response.getStatus()));
        responseMap.put("error", message);
        responseMap.put("path", request.getRequestURI());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseMap);

        response.getWriter().write(jsonResponse);
    }

}
