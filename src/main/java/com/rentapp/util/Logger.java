package com.rentapp.util;

import com.rentapp.gui.scene.LoginCtrl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Logger {
    private static final String PACKAGE_PREFIX = "com.rentapp";
    private static final Lock lock = new ReentrantLock();

    public static void logExToFile(Exception ex) {
        lock.lock();
        String currentDay = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fn = currentDay + "_error.log";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fn, true))) {
            writer.println("At: " + LocalDateTime.now() + " Logged user: " + LoginCtrl.getLoggedUser());
            printFilteredStackTrace(writer, ex);
            writer.println("=======================================\n");
        } catch (IOException e) {
            System.err.println("[" + LocalDateTime.now() + "] Failed to write to log file: " + e.getMessage());
            ex.printStackTrace(System.err);
        }
        finally {
            lock.unlock();
        }
    }

    private static void printFilteredStackTrace(PrintWriter writer, Throwable throwable) {
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            writer.println(t == throwable ? t : "Caused by: " + t);
            for (StackTraceElement element : t.getStackTrace()) {
                if (element.getClassName().startsWith(PACKAGE_PREFIX)) {
                    writer.println("\tat " + element);
                }
            }
        }
    }
}