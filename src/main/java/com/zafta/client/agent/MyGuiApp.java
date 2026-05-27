package com.zafta.client.agent;

import com.zafta.client.agent.system.SingleInstanceLock;
import com.zafta.client.agent.ui.MainFrame;
import com.zafta.client.agent.ui.UIInitializer;

import javax.swing.*;
import java.awt.*;

public class MyGuiApp {

    public static void main(String[] args) {
        if (SingleInstanceLock.isAlreadyRunning()) {
            JOptionPane.showMessageDialog(
                    null,
                    "یک نسخه از برنامه از قبل در حال اجراست",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }

        UIInitializer.init(false);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.hideFrame();
            //frame.showFrame();
        });
    }
}
