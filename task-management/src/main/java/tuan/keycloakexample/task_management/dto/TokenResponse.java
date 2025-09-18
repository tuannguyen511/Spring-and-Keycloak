package tuan.keycloakexample.task_management.dto;

/**
 * @author cps
 **/
public record TokenResponse(String access_token, String refresh_token, String token_type, long expires_in) {
}
