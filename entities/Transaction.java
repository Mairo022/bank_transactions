package com.playtech.assignment.entities;

import com.playtech.assignment.enums.TransactionMethod;
import com.playtech.assignment.enums.TransactionType;

import java.math.BigDecimal;

public class Transaction {
    public final String id;
    public final String userID;
    public final TransactionType type;
    public final BigDecimal amount;
    public final TransactionMethod method;
    public final String accountNumber;

    public Transaction(String id, String userID, TransactionType type, BigDecimal amount, TransactionMethod method, String accountNumber) {
        this.id = id;
        this.userID = userID;
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.accountNumber = accountNumber;
    }
}
