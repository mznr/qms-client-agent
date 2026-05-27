package com.zafta.client.agent.ui;

import javax.swing.*;
import java.awt.TrayIcon;
import java.util.function.Consumer;

public final class AppNotifier {

    private static Consumer<Notification> handler;

    private AppNotifier() {}

    public static void register(Consumer<Notification> h) {
        handler = h;
    }

    public static void notify(String title, String message, TrayIcon.MessageType type) {
        if (handler != null) {
            SwingUtilities.invokeLater(() ->
                    handler.accept(new Notification(title, message, type))
            );
        }
    }
    /*public static void notify(String title, String message, TrayIcon.MessageType type) {
        if (handler != null) {
            handler.accept(new Notification(title, message, type));
        }
    }*/

    public record Notification(String title, String message, TrayIcon.MessageType type) {}
}
