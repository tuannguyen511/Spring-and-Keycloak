package tuan.keycloakexample.task_management.exception;

/**
 * @author cps
 **/
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}

