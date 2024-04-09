package com.playtech.assignment;

import com.playtech.assignment.entities.BinMapping;
import com.playtech.assignment.entities.Event;
import com.playtech.assignment.entities.Transaction;
import com.playtech.assignment.entities.User;
import com.playtech.assignment.enums.TransactionType;
import com.playtech.assignment.utils.DataLoader;
import com.playtech.assignment.utils.DataWriter;
import com.playtech.assignment.validations.TransactionValidation;
import com.playtech.assignment.validations.UserValidation;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class TransactionProcessorSample {
    public static void main(final String[] args) throws IOException {
        List<User> users = DataLoader.readUsers(Paths.get(args[0]));
        List<Transaction> transactions = DataLoader.readTransactions(Paths.get(args[1]));
        List<BinMapping> binMappings = DataLoader.readBinMappings(Paths.get(args[2]));

        List<Event> events = TransactionProcessorSample.processTransactions(users, transactions, binMappings);

        DataWriter.writeBalances(Paths.get(args[3]), users);
        DataWriter.writeEvents(Paths.get(args[4]), events);
    }

    private static List<Event> processTransactions(final List<User> users, final List<Transaction> transactions, final List<BinMapping> binMappings) {
        ArrayList<Event> events = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            try {
                Transaction transaction = transactions.get(i);
                List<Transaction> previousTransactions = transactions.subList(0, i);

                User user = findUser(users, transaction.userID);

                String status = Event.STATUS_DECLINED;
                String message;

                if (!TransactionValidation.isIdUnique(previousTransactions, transaction.id)) {
                    message = TransactionValidation.getMessage();
                }
                else if (!UserValidation.userExists(user, transaction.userID) ||
                        UserValidation.isFrozen(user)
                ) {
                    message = UserValidation.getMessage();
                }
                else if (!TransactionValidation.validMethod(transaction, user, binMappings) ||
                        !TransactionValidation.validType(transaction, user, previousTransactions, events)
                ) {
                    message = TransactionValidation.getMessage();
                }
                else if (!UserValidation.isAccountOwner(previousTransactions, events, transaction)) {
                    message = UserValidation.getMessage();
                }
                else {
                    handleBalanceChange(transaction.amount, transaction.type, user);

                    status = Event.STATUS_APPROVED;
                    message = "OK";
                }

                events.add(new Event(transaction.id, status, message));
            } catch (Exception e) {
                try {
                    System.out.print("Error, i=" + i);
                    System.out.print(", transactionID=" + transactions.get(i).id);
                } finally {
                    System.out.print("\n");
                }
            }
        }

        return events;
    }

    private static User findUser(List<User> users, String userID) {
        return users
                .stream()
                .filter(u -> u.id.equals(userID))
                .findFirst()
                .orElse(null);
    }

    private static void handleBalanceChange(final BigDecimal amount, final TransactionType type, final User user) {
        if (type.equals(TransactionType.DEPOSIT)) {
            user.deposit(amount);
        }

        if (type.equals(TransactionType.WITHDRAW)) {
            user.withdraw(amount);
        }
    }
}
