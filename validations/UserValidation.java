package com.playtech.assignment.validations;

import com.playtech.assignment.entities.Event;
import com.playtech.assignment.entities.Transaction;
import com.playtech.assignment.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserValidation {
    private static String message = null;

    public static boolean userExists(final User user, String userID) {
        if (user == null) {
            message = "User " + userID + " not found in Users";
            return false;
        }
        return true;
    }

    public static boolean isFrozen(final User user) {
        if (user.frozen) {
            message = "User " + user.id + " is frozen";
            return true;
        }
        return false;
    }

    public static boolean isAccountOwner(
            final List<Transaction> transactions,
            final ArrayList<Event> events,
            final Transaction transaction
    ) {
        List<Transaction> transactionsHistory = transactions
                .stream()
                .filter(t -> t.accountNumber.equals(transaction.accountNumber))
                .toList();

        if (transactionsHistory.isEmpty()) {
            return true;
        }

        for (Transaction transactionOld : transactionsHistory) {
            for (Event event : events) {
                if (transactionOld.id.equals(event.transactionId) && event.status.equals(Event.STATUS_APPROVED)) {
                    boolean isCurrentUser = transactionOld.userID.equals(transaction.userID);

                    if (!isCurrentUser) {
                        message = "Account " + transaction.accountNumber + " is in use by other user";
                    }

                    return isCurrentUser;
                }
            }
        }

        return true;
    }

    public static String getMessage() {
        String messageCopy = message;
        message = null;

        return messageCopy;
    }
}
