package com.example.cdc.util;

import com.example.cdc.entity.User;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriterUtil {

    public static void writeFullExport(String filePath, List<User> users) throws IOException {

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("id,name,email,created_at,updated_at,is_deleted\n");

            for (User user : users) {
                writer.write(
                        user.getId() + "," +
                        user.getName() + "," +
                        user.getEmail() + "," +
                        user.getCreatedAt() + "," +
                        user.getUpdatedAt() + "," +
                        user.getIsDeleted() + "\n"
                );
            }
        }
    }

    public static void writeDeltaExport(String filePath, List<User> users) throws IOException {

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("operation,id,name,email,created_at,updated_at,is_deleted\n");

            for (User user : users) {

                String operation;

                if (Boolean.TRUE.equals(user.getIsDeleted())) {
                    operation = "DELETE";
                } else if (user.getCreatedAt().equals(user.getUpdatedAt())) {
                    operation = "INSERT";
                } else {
                    operation = "UPDATE";
                }

                writer.write(
                        operation + "," +
                        user.getId() + "," +
                        user.getName() + "," +
                        user.getEmail() + "," +
                        user.getCreatedAt() + "," +
                        user.getUpdatedAt() + "," +
                        user.getIsDeleted() + "\n"
                );
            }
        }
    }
}