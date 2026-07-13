package ru.practicum.mymarketapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartUser;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.entity.User;
import ru.practicum.mymarketapp.repository.UserRepository;
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public Mono<User> save(User user) {
                String password = user.getPassword();
                password = passwordEncoder.encode(password);
                user.setPassword(password);
        return  userRepository.save(user);
    }


    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

}
