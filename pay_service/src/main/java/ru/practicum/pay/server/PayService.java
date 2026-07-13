package ru.practicum.pay.server;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
@Component
public class PayService {
    public PayService(BalanceStorage balanceStorage) {
        this.balanceStorage = balanceStorage;
    }

    private final BalanceStorage balanceStorage;
    public String getBalance() {
        return balanceStorage.getBalance();
    }

    public void changeBalance(String amount) {
        BigDecimal decrease = new BigDecimal(amount);
        BigDecimal balance = new BigDecimal(balanceStorage.getBalance());
        balance = balance.subtract(decrease);
        if  (balance.compareTo(BigDecimal.ZERO) <= 0) {
           throw new RuntimeException("Баланс равен нулю или ниже");
        }
        balanceStorage.setBalance(balance);
    }
}
