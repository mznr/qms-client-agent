package com.zafta.client.agent.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class HelpDialog extends JDialog {

    public HelpDialog(JFrame parent) {
        super(parent, "راهنمای کلیدهای میانبر", true);
        init();
    }

    private void init() {
        setResizable(false);

        Font tahoma = new Font("Tahoma", Font.PLAIN, 10);

        JPanel grid = new JPanel(new GridLayout(7, 2, 5, 10));

        addRow(grid, "CTRL + Right", "فراخوانی نوبت بعدی:", tahoma);
        addRow(grid, "CTRL + LEFT", "تعداد افراد منتظر در صف:", tahoma);
        addRow(grid, "CTRL + UP", "فراخوانی مجدد اخرین شماره:", tahoma);
        addRow(grid, "CTRL + DOWN", "فعال/غیرفعال انتظار:", tahoma);
        addRow(grid, "ALT + UP", "اعمال تنظیمات نمایشگر:", tahoma);
        addRow(grid, "ALT + DOWN", "فراخوانی نوبت دلخواه:", tahoma);
        addRow(grid, "ALT + Right", "اطلاعات نرم افزار:", tahoma);

        JButton ok = new JButton("متوجه شدم");
        ok.addActionListener(e -> dispose());

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(grid, BorderLayout.CENTER);
        main.add(ok, BorderLayout.SOUTH);

        setContentPane(main);
        setSize(370, 250);
        setLocationRelativeTo(getParent());
        setAlwaysOnTop(true);
    }

    private void addRow(JPanel panel, String left, String right, Font font) {
        JLabel l1 = new JLabel(left);
        l1.setFont(font);
        JLabel l2 = new JLabel(right);
        panel.add(l1);
        panel.add(l2);
    }
}
