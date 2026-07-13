package ru.practicum.mymarketapp.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.User;
import ru.practicum.mymarketapp.entity.UserRole;
import ru.practicum.mymarketapp.repository.UserRepository;
import ru.practicum.mymarketapp.repository.UserRoleRepository;

import java.util.stream.Collectors;

@Component
public class JpaReactiveUserDetailsManager implements ReactiveUserDetailsService {
    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;

    public JpaReactiveUserDetailsManager(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<User> user = userRepository.getUserByLogin(username);
        return user.flatMap(e-> {
            Flux<UserRole> roles = userRoleRepository.findUserRoleByUserId(e.getId());
            return roles.collectList().zipWith(Mono.just(e));
        }).map(tuples -> new org.springframework.security.core.userdetails.User(
                tuples.getT2().getLogin(),  tuples.getT2().getPassword(),tuples.getT1().stream().map(e-> new SimpleGrantedAuthority(e.getRole())).collect(Collectors.toList())));
    }
}
