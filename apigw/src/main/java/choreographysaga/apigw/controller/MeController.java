package choreographysaga.apigw.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal OidcUser user) {
        return Map.of(
                "id", user.getSubject(),
                "username", user.getPreferredUsername(),
                "email", user.getEmail(),
                "name", user.getFullName(),
                "roles", user.getClaimAsMap("realm_access").get("roles"));
    }
}