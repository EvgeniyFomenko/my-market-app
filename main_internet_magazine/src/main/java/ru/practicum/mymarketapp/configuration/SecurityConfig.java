package ru.practicum.mymarketapp.configuration;

import java.net.URI;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import org.springframework.web.reactive.result.view.RequestDataValueProcessor;
import ru.practicum.mymarketapp.repository.UserRepository;
import ru.practicum.mymarketapp.repository.UserRoleRepository;
import ru.practicum.mymarketapp.service.JpaReactiveUserDetailsManager;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    // Защищаем пароли шифрованием
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSessionServerCsrfTokenRepository csrfTokenRepository() {
        return new WebSessionServerCsrfTokenRepository();
    }

//    @Bean
//    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
//        return new JpaReactiveUserDetailsManager(userRepository, userRoleRepository);
//    }

    // Настраиваем поведение при выходе
    @Bean
    public RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        // При выходе перенаправляем его на домашнюю страницу
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));
        return logoutSuccessHandler;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler) {
        http
//                .csrf(csrf ->csrf
//                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//                        .csrfTokenRequestHandler(serverCsrfTokenRequestAttributeHandler())
//                )
                .csrf((csrf) -> csrf.disable()
                )
                // Явно разрешаем доступ к /login и / для всех
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/", "/login", "/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/**").permitAll()
                        .anyExchange().authenticated()
                )
                // Настраиваем форму логина
                .formLogin(form -> form
                        // URL страницы логина
                        .loginPage("/login")
                        .authenticationSuccessHandler(
                                // В случае успешного логина, перенаправляем на /
                                new RedirectServerAuthenticationSuccessHandler("/")
                        )
                )
                // Настраиваем обработку при выходе
                .logout(logout -> logout
                        // URL страницы выхода
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(redirectServerLogoutSuccessHandler)
                )
                // OAuth2 Client для WebClient
                .oauth2Client(withDefaults());

        return http.build();
    }

    @Bean
    public XorServerCsrfTokenRequestAttributeHandler serverCsrfTokenRequestAttributeHandler() {
        XorServerCsrfTokenRequestAttributeHandler serverCsrfTokenRequestAttributeHandler = new XorServerCsrfTokenRequestAttributeHandler();
        serverCsrfTokenRequestAttributeHandler.setTokenFromMultipartDataEnabled(true);

        return serverCsrfTokenRequestAttributeHandler;
    }

    @Bean
    public CsrfRequestDataValueProcessor csrfRequestDataValueProcessor(){
      return   new CsrfRequestDataValueProcessor();
    }
}

