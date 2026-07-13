package ru.practicum.pay.server;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
public class PayController extends ru.practicum.pay.server.DefaultApi {
    public PayController(PayService payService) {
        this.payService = payService;
    }

    private final PayService payService;

    @PreAuthorize("hasAuthority('SERVICE')")
    @PostMapping("/toPay")
    public Mono<Balance> toPayPost(
            @Parameter(name = "Quantity", description = "стоимость вещи", required = true) @Valid @RequestBody Quantity quantity
    ) {
        return Mono.just(quantity).doOnNext(e -> payService.changeBalance(e.getQuantity())).flatMap(
                e -> {
                    Balance balance = new Balance();
                    balance.setBalance(payService.getBalance());
                    return Mono.just(balance);
                }
        );
    }

    @PreAuthorize("hasAuthority('SERVICE')")
    @GetMapping("/getBalance")
    public Mono<Balance> getBalanceGet() {
        Balance balance = new Balance();
        balance.setBalance(payService.getBalance());
        return Mono.just(balance);
    }
}
