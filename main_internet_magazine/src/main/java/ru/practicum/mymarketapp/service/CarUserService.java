package ru.practicum.mymarketapp.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.mymarketapp.entity.CartUser;
import ru.practicum.mymarketapp.entity.Order;
import ru.practicum.mymarketapp.repository.CartUserRepository;
import ru.practicum.mymarketapp.repository.UserRepository;

@Service
public class CarUserService {
    private final CartUserRepository cartUserRepository;
    private final UserRepository userRepository;

    public CarUserService(CartUserRepository cartUserRepository, UserRepository userRepository) {
        this.cartUserRepository = cartUserRepository;
        this.userRepository = userRepository;
    }

    public Mono<CartUser> save(CartUser cartUser) {
        return cartUserRepository.save(cartUser);
    }

    public Flux<CartUser> getCartUserByUserId(Long id) {
        return cartUserRepository.findCartUserByUserId(id);
    }

    public Flux<CartUser> getCartUserByUserLogin(String login) {
       return userRepository.getUserByLogin(login).flux().flatMap(user -> cartUserRepository.findCartUserByUserId(user.getId())).switchIfEmpty(Flux.empty());
    }

    public Mono<CartUser> setOrder(String userLogin, Order order) {
       return cartUserRepository.findByCartId(order).switchIfEmpty(
               userRepository.getUserByLogin(userLogin).map(user-> {
                   CartUser cartUser = new CartUser();
                   cartUser.setCartId(order.getId());
                   cartUser.setUserId(user.getId());
                   return cartUser;
               }).flatMap(cartUserRepository::save)
       );
    }

}
