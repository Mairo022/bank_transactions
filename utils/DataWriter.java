package com.playtech.assignment.utils;

import com.playtech.assignment.entities.Event;
import com.playtech.assignment.entities.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DataWriter {
    public static void writeBalances(final Path filePath, final List<User> users) throws IOException {
        File file = new File(filePath.toUri());

        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        try (final FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            writer.append("user_id,balance\n");
            for (final var user : users) {
                writer.append(user.id).append(",").append(user.balance.toString()).append("\n");
            }
        }
    }

    public static void writeEvents(final Path filePath, final List<Event> events) throws IOException {
        File file = new File(filePath.toUri());

        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        try (final FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            writer.append("transaction_id,status,message\n");
            for (final var event : events) {
                writer.append(event.transactionId).append(",").append(event.status).append(",").append(event.message).append("\n");
            }
        }
    }
}
