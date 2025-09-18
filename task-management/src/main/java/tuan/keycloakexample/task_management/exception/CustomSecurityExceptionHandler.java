package tuan.keycloakexample.task_management.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tuan.keycloakexample.task_management.dto.ErrorResponse;

import java.io.IOException;

/**
 * @author cps
 **/
@Component
public class CustomSecurityExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomSecurityExceptionHandler.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access denied: {}", accessDeniedException.getMessage());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        String body = new ObjectMapper().writeValueAsString(
                new ErrorResponse("forbidden","You do not have permission")
        );
        response.getWriter().write(body);
        response.flushBuffer();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("Authentication failed: {}", authException.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        String body = new ObjectMapper().writeValueAsString(
                new ErrorResponse("unauthorized", authException.getMessage())
        );
        response.getWriter().write(body);
        response.flushBuffer();
    }
}
