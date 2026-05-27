package com.zafta.client.agent.ui;


import javax.swing.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public final class UIInitializer {

    private UIInitializer() {}

    public static void init(boolean dark) {
        applyLookAndFeel(dark);
        applyPersianUIFont();
        applyEnglishUIFont();
    }


    private static void applyPersianUIFont() {

        String[] persianKeys = {
                "Label.font",
                "Button.font",
                "ToggleButton.font",
                "RadioButton.font",
                "CheckBox.font",
                "Menu.font",
                "MenuItem.font",
                "PopupMenu.font",
                "ToolTip.font",
                "TitledBorder.font",
                "TabbedPane.font",
                "OptionPane.font",
                "ComboBox.font"
        };

        for (String key : persianKeys) {
            UIManager.put(key, AppFonts.PERSIAN);
        }
    }

    private static void applyEnglishUIFont() {

        UIManager.put("TextArea.font", AppFonts.MONO);
        UIManager.put("TextPane.font", AppFonts.MONO);
        UIManager.put("EditorPane.font", AppFonts.MONO);
        UIManager.put("Table.font", AppFonts.ENGLISH);
        UIManager.put("List.font", AppFonts.ENGLISH);
    }

    private static void applyLookAndFeel(boolean dark) {
        try {
            /*UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );*/
            if (dark) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
        } catch (Exception ignored) {}
    }
}
