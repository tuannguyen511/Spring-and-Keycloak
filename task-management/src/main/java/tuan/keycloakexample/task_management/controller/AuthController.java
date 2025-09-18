package tuan.keycloakexample.task_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tuan.keycloakexample.task_management.dto.LoginRequest;
import tuan.keycloakexample.task_management.dto.LogoutRequest;
import tuan.keycloakexample.task_management.dto.RefreshRequest;
import tuan.keycloakexample.task_management.dto.TokenResponse;
import tuan.keycloakexample.task_management.exception.AuthenticationException;
import tuan.keycloakexample.task_management.service.KeycloakService;


/**
 * @author cps
 **/
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = keycloakService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        TokenResponse token = keycloakService.refresh(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request) {
        keycloakService.logout(request);
    }

}
