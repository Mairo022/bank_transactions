package com.playtech.assignment.entities;
import java.math.BigDecimal;

public class User {
    public final String id;
    public final String username;
    public BigDecimal balance;
    public final String country;
    public boolean frozen;
    public BigDecimal depositMin;
    public BigDecimal depositMax;
    public BigDecimal withdrawMin;
    public BigDecimal withdrawMax;

    public User(String id, String username, BigDecimal balance, String country, boolean frozen,
                BigDecimal depositMin, BigDecimal depositMax, BigDecimal withdrawMin, BigDecimal withdrawMax
    ) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.country = country;
        this.frozen = frozen;
        this.depositMin = depositMin;
        this.depositMax = depositMax;
        this.withdrawMin = withdrawMin;
        this.withdrawMax = withdrawMax;
    }

    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        BigDecimal newBalance = this.balance.add(amount);

        if (newBalance.toString().length() <= 20) {
            this.balance = newBalance;
        }
    }
}
