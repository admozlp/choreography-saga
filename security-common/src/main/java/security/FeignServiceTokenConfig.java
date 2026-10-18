package security;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class FeignServiceTokenConfig {

    @Bean
    RequestInterceptor serviceTokenInterceptor(ClientRegistrationRepository registrations) {
        var clientService = new InMemoryOAuth2AuthorizedClientService(registrations);
        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, clientService);
        var request = OAuth2AuthorizeRequest.withClientRegistrationId("service")
                .principal("service")
                .build();

        return template -> {
            var client = manager.authorize(request);
            if (client == null) throw new IllegalStateException("Servis token'ı alınamadı");
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
        };
    }
}