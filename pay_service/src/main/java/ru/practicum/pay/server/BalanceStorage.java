package ru.practicum.pay.server;

import org.springframework.stereotype.Component;


import java.math.BigDecimal;
@Component
public class BalanceStorage {
    public BalanceStorage() {
        balance = BigDecimal.valueOf((int)(Math.random() * ((1000 - 200) + 1)) + 200);
    }

    private BigDecimal balance;
    public String getBalance() {
        return balance.toString();
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
