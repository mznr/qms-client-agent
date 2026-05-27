package com.zafta.client.agent.ui;

import com.zafta.client.agent.system.SingleInstanceLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class TrayManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrayManager.class);

    private TrayIcon trayIcon;
    private final JFrame frame;

    public TrayManager(JFrame frame) {
        this.frame = frame;
    }

    public void install() {
        if (!SystemTray.isSupported()) {
            LOGGER.warn("System tray not supported");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            PopupMenu menu = new PopupMenu();

            MenuItem openItem = new MenuItem("Show");
            MenuItem exitItem = new MenuItem("Exit");

            openItem.addActionListener(e -> restoreFrame());
            exitItem.addActionListener(e -> exitApplication());

            menu.add(openItem);
            menu.add(exitItem);

            Image image = ImageIO.read(
                    Objects.requireNonNull(getClass().getResource("/zafta_logo.png"))
            );

            trayIcon = new TrayIcon(image, "Zafta QMS System", menu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("سامانه فراخوان باجه شرکت زفتا");

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        restoreFrame();
                    }
                }
            });

            tray.add(trayIcon);

        } catch (AWTException | IOException e) {
            LOGGER.error("Tray install failed", e);
        }
    }

    public void showMessage(String title, String message, TrayIcon.MessageType type) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, type);
        }
    }

    private void restoreFrame() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            frame.setState(Frame.NORMAL);
            frame.toFront();
        });
    }

    private void exitApplication() {
        showMessage(
                "سامانه نوبت دهی شرکت زفتا",
                "سرویس نرم افزار باجه متوقف شد",
                TrayIcon.MessageType.INFO
        );

        SingleInstanceLock.release();
        System.exit(0);
    }
}
