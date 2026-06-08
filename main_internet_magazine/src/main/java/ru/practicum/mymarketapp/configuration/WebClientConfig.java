package ru.practicum.mymarketapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
        ReactiveClientRegistrationRepository clientRegistrations,
        ServerOAuth2AuthorizedClientRepository authorizedClients) {
        // Создаём провайдера OAuth2-авторизованных клиентов, поддерживающего только flow client credentials
        var authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();
        // Создаём стандартный менеджер, который будет использовать переданные репозитории
        var authorizedClientManager = new DefaultReactiveOAuth2AuthorizedClientManager(
            // откуда брать настройки провайдера (client ID, secret, URL и т.д.)
            clientRegistrations,
            // куда кэшировать токены (в WebSession по умолчанию)
            authorizedClients);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    public WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        // Создаём функцию-фильтр для WebClient, которая будет автоматически
        // запрашивать и прикреплять OAuth2-токены к каждому HTTP-запросу
        var oauth2Client = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        // Указываем ID регистрации OAuth2-клиента по умолчанию (должен совпадать с именем в application.yml)
        oauth2Client.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
            // добавляем OAuth2-авторизацию ко всем запросам
            .filter(oauth2Client)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }
}
