package com.zafta.client.agent.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public final class DialogUtils {

    private DialogUtils() {}


    public static boolean confirm(Component parent, String title, String msg) {
        String[] options = {"بله", "انصراف"};
        int result = JOptionPane.showOptionDialog(
                parent, msg, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
        return result == 0;
    }

    public static void info(Component parent, String msg) {
        String[] options = {"متوجه شدم"};
        JOptionPane.showOptionDialog(
                parent, msg, "توجه",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
        );
    }

    public static void error(Component parent, String msg) {
        String[] options = {"متوجه شدم"};
        JOptionPane.showOptionDialog(
                parent, msg, "خطا",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null, options, options[0]
        );
    }


    /*public static boolean confirm(Component parent, String title, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "توجه",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "خطا",
                JOptionPane.ERROR_MESSAGE);
    }*/
}