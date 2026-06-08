package ru.practicum.pay.server;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Controller
public class PayController extends DefaultApi {
    public PayController(PayService payService, ServerWebExchange exchange) {
        this.payService = payService;
        this.exchange = exchange;
    }

    private final PayService payService;
    private final ServerWebExchange exchange;

   public Mono<Balance> toPayPost(
            @Parameter(name = "Quantity", description = "стоимость вещи", required = true) @Valid @RequestBody Quantity quantity,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
       return Mono.just(quantity).doOnNext(e -> payService.changeBalance(e.getQuantity())).flatMap(
                e -> {
                    Balance balance = new Balance();
                    balance.setBalance(payService.getBalance());
                   return Mono.just(balance);
                }
        );
    }

    public Mono<Balance> getBalanceGet( ) {
//        Mono<Void> result = Mono.empty();

//        exchange.getResponse().setStatusCode(HttpStatus.valueOf(200));
//        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
//            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
//                String exampleString = "{ \"balance\" : \""+payService.getBalance()+"\" }";
//                result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
//                break;
                Balance balance = new ru.practicum.pay.server.Balance();
                balance.setBalance(payService.getBalance());
                return Mono.just(balance);
//            }
//        }


//        return result.then(Mono.empty());
    }
}
