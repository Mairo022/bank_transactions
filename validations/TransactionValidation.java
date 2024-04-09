package com.playtech.assignment.validations;

import com.playtech.assignment.entities.BinMapping;
import com.playtech.assignment.entities.Event;
import com.playtech.assignment.entities.Transaction;
import com.playtech.assignment.entities.User;
import com.playtech.assignment.enums.CardType;
import com.playtech.assignment.enums.TransactionMethod;
import com.playtech.assignment.enums.TransactionType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

import static com.playtech.assignment.data.IBanLengths.IbanLengths;

public class TransactionValidation {
    private static String message = null;

    public static boolean isIdUnique(final List<Transaction> previousTransactions, final String transactionId) {
        boolean isUnique = previousTransactions.stream().noneMatch(transaction -> transaction.id.equals(transactionId));

        if (!isUnique) {
            message = "Transaction " + transactionId + " already processed (id non-unique)";
            return false;
        }
        return true;
    }

    public static boolean validMethod(final Transaction transaction, final User user, final List<BinMapping> binMappings) {
        if (transaction.method == TransactionMethod.TRANSFER) {
            String IBanCountryCode = transaction.accountNumber.substring(0, 2);

            if (!isIBanValid(transaction.accountNumber)) {
                message = "Invalid iban " + transaction.accountNumber;
                return false;
            }

            if (!IBanCountryCode.equals(user.country)) {
                message = "Invalid account country " + IBanCountryCode + ", expected " + user.country;
                return false;
            }
        }

        if (transaction.method == TransactionMethod.CARD) {
            BinMapping binMapping = binMappings
                    .stream()
                    .filter(b -> b.rangeTo >= Long.parseLong(transaction.accountNumber.substring(0, 10)))
                    .findFirst()
                    .orElse(null);

            if (binMapping == null) {
                message = "Card " + transaction.accountNumber + " is invalid";
                return false;
            }

            if (transaction.type.equals(TransactionType.DEPOSIT)) {
                if (binMapping.cardType.equals(CardType.CC)) {
                    message = "Only DC cards allowed, got " + binMapping.cardType;
                    return false;
                }
            }

            if (!binMapping.cardType.equals(CardType.CC) && !binMapping.cardType.equals(CardType.DC)) {
                message = "Invalid card type, got " + binMapping.cardType;
                return false;
            }

            String userCountry = new Locale("", user.country).getISO3Country();
            String cardCountry = binMapping.country;

            if (!userCountry.equals(cardCountry)) {
                message = "Invalid country " + cardCountry + ", expected " + user.country + " (" + userCountry + ")";
                return false;
            }
        }

        if (transaction.method != TransactionMethod.CARD && transaction.method != TransactionMethod.TRANSFER) {
            message = "Invalid payment method";
            return false;
        }

        return true;
    }

    public static boolean validType(
            final Transaction transaction,
            final User user,
            final List<Transaction> previousTransactions,
            final List<Event> events
    ) {
        BigDecimal amount = transaction.amount;

        BiFunction<BigDecimal, BigDecimal, Boolean> isUnderLimit = (BigDecimal inputAmount, BigDecimal limit) -> inputAmount.compareTo(limit) < 0;
        BiFunction<BigDecimal, BigDecimal, Boolean> isOverLimit = (BigDecimal inputAmount, BigDecimal limit) -> inputAmount.compareTo(limit) > 0;

        if (transaction.amount.compareTo(BigDecimal.ZERO) <= 0) {
            message = "Invalid payment amount " + transaction.amount;
            return false;
        }

        if (transaction.type.equals(TransactionType.DEPOSIT)) {
            if (isOverLimit.apply(amount, user.depositMax)) {
                message = "Amount " + transaction.amount + " is over the deposit limit of " + user.depositMax;
                return false;
            }

            if (isUnderLimit.apply(amount, user.depositMin)) {
                message = "Amount " + transaction.amount + " is under the deposit limit of " + user.depositMin;
                return false;
            }
        }

        if (transaction.type.equals(TransactionType.WITHDRAW)) {
            if (isOverLimit.apply(amount, user.withdrawMax)) {
                message = "Amount " + transaction.amount + " is over the withdraw limit of " + user.withdrawMax;
                return false;
            }

            if (isUnderLimit.apply(amount, user.withdrawMin)) {
                message = "Amount " + transaction.amount + " is under the withdraw limit of " + user.withdrawMin;
                return false;
            }

            if (isOverLimit.apply(amount, user.balance)) {
                message = "Not enough balance to withdraw " + transaction.amount + " - balance is too low at " + user.balance;
                return false;
            }

            List<Transaction> accountDepositTransactions = previousTransactions
                    .stream()
                    .filter(t -> t.accountNumber.equals(transaction.accountNumber) && t.type == TransactionType.DEPOSIT)
                    .toList();

            boolean isSuccessfullyDeposited = accountDepositTransactions
                    .stream()
                    .anyMatch(t -> events
                            .stream()
                            .anyMatch(e -> e.transactionId.equals(t.id) && e.status.equals(Event.STATUS_APPROVED)));

            if (!isSuccessfullyDeposited) {
                message = "Cannot withdraw with a new account " + transaction.accountNumber;
                return false;
            }
        }

        if (transaction.type != TransactionType.DEPOSIT && transaction.type != TransactionType.WITHDRAW) {
            message = "Invalid transaction type " + transaction.type;
            return false;
        }

        return true;
    }

    public static String getMessage() {
        String messageCopy = message;
        message = null;

        return messageCopy;
    }

    private static boolean isIBanValid(final String IBan) {
        String country = IBan.substring(0, 2);
        int IBanLength = IbanLengths.getOrDefault(country, 0);

        if (IBanLength == 0 || IBan.length() != IBanLength) {
            return false;
        }

        String reArranged = IBan.substring(4) + IBan.substring(0, 4);
        StringBuilder convertedToNumbers = new StringBuilder();

        for (int i = 0; i < IBan.length(); i++) {
            int charValue = (int) reArranged.charAt(i);
            boolean isLetter = 65 <= charValue && charValue <= 90;

            if (isLetter) {
                int convertedValue = charValue - 55;
                convertedToNumbers.append(convertedValue);
                continue;
            }

            convertedToNumbers.append(reArranged.charAt(i));
        }

        BigInteger convertedToDecimal = new BigInteger(convertedToNumbers.toString());
        BigInteger divider = new BigInteger("97");
        String remainder = convertedToDecimal.remainder(divider).toString();

        return remainder.equals("1");
    }
}
