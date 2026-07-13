package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.UserRole;
import ru.practicum.mymarketapp.repository.UserRoleRepository;
@Service
public class UserRoleService {
    private UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public Flux<UserRole> getRolesByUserId(Long userId) {
        return userRoleRepository.findUserRoleByUserId(userId);
    }

    public Mono<UserRole> saveUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }
}
