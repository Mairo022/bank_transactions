package com.playtech.assignment.utils;

import com.playtech.assignment.entities.BinMapping;
import com.playtech.assignment.entities.Transaction;
import com.playtech.assignment.entities.User;
import com.playtech.assignment.enums.CardType;
import com.playtech.assignment.enums.TransactionMethod;
import com.playtech.assignment.enums.TransactionType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.playtech.assignment.utils.BigDecimalCustom.BigDecimalCC;

public class DataLoader {
    public static List<Transaction> readTransactions(final Path filePath) throws IOException {
        List<String> transactionsFile = Files.readAllLines(filePath);
        ArrayList<Transaction> transactions = new ArrayList<>();

        int i = 0;
        for (String line : transactionsFile) {
            if (i == 0) {
                i++;
                continue;
            }

            String[] values = line.split(",");
            String id = values[0];
            String userID = values[1];
            TransactionType type = TransactionType.valueOf(values[2]);
            BigDecimal amount = BigDecimalCC(values[3]);
            TransactionMethod method = TransactionMethod.valueOf(values[4]);
            String accountNumber = values[5];

            transactions.add(new Transaction(id, userID, type, amount, method, accountNumber));
        }

        return transactions;
    }

    public static List<User> readUsers(final Path filePath) throws IOException {
        List<String> usersFile = Files.readAllLines(filePath);
        ArrayList<User> users = new ArrayList<>();

        int i = 0;
        for (String line : usersFile) {
            if (i == 0) {
                i++;
                continue;
            }

            String[] values = line.split(",");
            String userID = values[0];
            String username = values[1];
            BigDecimal balance = BigDecimalCC(values[2]);
            String country = values[3];
            boolean frozen = Boolean.parseBoolean(values[4]);
            BigDecimal depositMin = BigDecimalCC(values[5]);
            BigDecimal depositMax = BigDecimalCC(values[6]);
            BigDecimal withdrawMin = BigDecimalCC(values[7]);
            BigDecimal withdrawMax = BigDecimalCC(values[8]);

            users.add(new User(userID, username, balance, country, frozen, depositMin, depositMax, withdrawMin, withdrawMax));
        }

        return users;
    }

    public static List<BinMapping> readBinMappings(final Path filePath) throws IOException {
        List<String> binsFile = Files.readAllLines(filePath);
        ArrayList<BinMapping> bins = new ArrayList<>();

        int i = 0;
        for (String line : binsFile) {
            if (i == 0) {
                i++;
                continue;
            }

            String[] values = line.split(",");
            String name = values[0];
            long rangeFrom = Long.parseLong(values[1]);
            long rangeTo = Long.parseLong(values[2]);
            CardType cardType = CardType.valueOf(values[3]);
            String country = values[4];

            bins.add(new BinMapping(name, rangeFrom, rangeTo, cardType, country));
        }

        return bins;
    }
}
