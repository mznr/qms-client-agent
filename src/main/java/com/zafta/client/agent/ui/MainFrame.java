package com.zafta.client.agent.ui;

import com.zafta.client.agent.MyGuiApp;
import com.zafta.client.agent.config.YamlConfigService;
import com.zafta.client.agent.services.ClientServiceRunner;
import com.zafta.client.agent.system.ConsoleRedirect;
import com.zafta.client.agent.system.SingleInstanceLock;
import com.zafta.client.agent.ui.dialogs.ConfigDialog;
import com.zafta.client.agent.ui.dialogs.DialogUtils;
import com.zafta.client.agent.ui.dialogs.HelpDialog;
import org.springframework.format.datetime.DateFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.prefs.Preferences;

public class MainFrame {

    private final JFrame frame = new JFrame("نرم افزار فراخوان باجه");
    private final JTextArea logArea = new JTextArea();
    private TrayManager trayManager;

    private JCheckBox showLogCheck;
    private JScrollPane logScroll;

    private static final String CONFIG_PASSWORD = "AryaZ@";

    public MainFrame() {
        initUI();
        installTray();
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            splash.close();
        }
        startServices();
    }

    private void initUI() {
        restoreWindowSize();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);

        Image logo = loadLogo();
        frame.setIconImage(logo);



        JButton configBtn = new JButton(loadIcon("settings.png", 18, 18));
        configBtn.setText("تنظیمات");
        configBtn.setToolTipText("تنظیمات");

        JButton helpBtn = new JButton(loadIcon("help.png", 18, 18));
        helpBtn.setText("راهنمای کلیدها");
        helpBtn.setToolTipText("راهنما");


        JButton exitBtn = new JButton(loadIcon("exit.png", 18, 18));
        exitBtn.setText("خروج از برنامه");
        exitBtn.setToolTipText("خروج");



        configBtn.setMnemonic(KeyEvent.VK_S); // Alt+S
        helpBtn.setMnemonic(KeyEvent.VK_H);   // Alt+H
        exitBtn.setMnemonic(KeyEvent.VK_X);



        logScroll = new JScrollPane(logArea);

        JButton clearLogBtn = new JButton(loadIcon("erase.png", 16, 16));
        clearLogBtn.setText("پاکسازی لاگ");
        clearLogBtn.setToolTipText("پاک کردن لاگ");
        clearLogBtn.addActionListener(e -> logArea.setText(""));

        showLogCheck = new JCheckBox("نمایش لاگ", true);
        showLogCheck.addActionListener(e -> toggleLogVisibility());


        helpBtn.addActionListener(e -> {
            configBtn.setEnabled(false);
            helpBtn.setEnabled(false);

            new HelpDialog(frame).setVisible(true);

            configBtn.setEnabled(true);
            helpBtn.setEnabled(true);
        });


        exitBtn.addActionListener(e -> {
            if (DialogUtils.confirm(frame, "خروج", "آیا نسبت به خروج از نرم افزار فراخوان باجه اطمینان دارید؟")) {
                SingleInstanceLock.release();
                System.exit(0);
            }
        });


        YamlConfigService yamlConfigService = new YamlConfigService();


        configBtn.addActionListener(e -> {
            int verificationResult = verifyConfigPassword();
            if (verificationResult == -1) {
                DialogUtils.error(frame, "رمز عبور اشتباه است");
                return;
            }else if (verificationResult == -2) {
                DialogUtils.error(frame, "وارد کردن رمز عبور الزامی است");
                return;
            }
            else if(verificationResult == 0){
                return;
            }

            configBtn.setEnabled(false);
            helpBtn.setEnabled(false);

            new ConfigDialog(
                    frame,
                    yamlConfigService,
                    updated -> {
                        AppNotifier.notify(
                            "ذخیره",
                            "تنظیمات ذخیره شد",
                            TrayIcon.MessageType.INFO
                    );
                        DialogUtils.info(
                                frame,
                                "برای اعمال تنظیمات لطفاً برنامه را یکبار بسته و مجدداً اجرا نمایید."
                        );
                    }
            ).setVisible(true);

            configBtn.setEnabled(true);
            helpBtn.setEnabled(true);
        });


        //frame.add(new JScrollPane(logArea), BorderLayout.CENTER);
        frame.add(createLogPanel(), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.add(showLogCheck);
        bottomPanel.add(clearLogBtn);
        bottomPanel.add(new JSeparator(SwingConstants.VERTICAL));

        bottomPanel.add(configBtn);
        bottomPanel.add(helpBtn);
        bottomPanel.add(exitBtn);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        //frame.add(exit, BorderLayout.SOUTH);

        frame.setSize(750, 300);
        positionBottomRight();
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                persistWindowSize();
                if (trayManager != null) {
                    trayManager.showMessage(
                            "سامانه نوبت دهی شرکت زفتا",
                            "نرم افزار باجه در پس زمینه در حال اجراست",
                            TrayIcon.MessageType.INFO
                    );
                }
            }
        });

        frame.getRootPane().registerKeyboardAction(
                e -> helpBtn.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void positionBottomRight() {
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

        int x = bounds.x + bounds.width - insets.right - frame.getWidth();
        int y = bounds.y + bounds.height - insets.bottom - frame.getHeight();

        frame.setLocation(x, y);
    }

    private void installTray() {
        trayManager = new TrayManager(frame);
        trayManager.install();

        AppNotifier.register(notification ->
                trayManager.showMessage(
                        notification.title(),
                        notification.message(),
                        notification.type()
                )
        );

        trayManager.showMessage(
                "سامانه نوبت دهی شرکت زفتا",
                "نرم افزار باجه در حال اجراست",
                TrayIcon.MessageType.INFO
        );
    }

    private void restoreWindowSize() {
        Preferences p = Preferences.userNodeForPackage(MainFrame.class);
        int w = p.getInt("win.w", 750);
        int h = p.getInt("win.h", 300);
        frame.setSize(w, h);
    }

    private void persistWindowSize() {
        Preferences p = Preferences.userNodeForPackage(MainFrame.class);
        p.putInt("win.w", frame.getWidth());
        p.putInt("win.h", frame.getHeight());
    }

    private void startServices() {
        new ConsoleRedirect().redirect(logArea);
        new ClientServiceRunner().start();
    }

    public void hideFrame() {
        frame.setVisible(false);
    }

    public void showFrame() {
        frame.setVisible(true);
    }

    public void showTrayMessage(String title, String message, TrayIcon.MessageType type) {
        if (trayManager != null) {
            trayManager.showMessage(title, message, type);
        }
    }

    private Image loadLogo() {
        URL iconUrl = MyGuiApp.class.getResource("/zafta_logo.png");

        if (iconUrl == null) {
            System.err.println("Window icon not found: zafta_logo.png");
            return null;
        }

        return new ImageIcon(iconUrl).getImage();

    }


    private static ImageIcon loadIcon(String fileName, int w, int h) {
        URL location = MyGuiApp.class.getResource("/" + fileName);
        if (location == null) {
            System.err.println("Icon not found: /" + fileName);
            return null;
        }

        ImageIcon icon = new ImageIcon(location);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }


    private JPanel createLogPanel() {
        logScroll = new JScrollPane(logArea);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logScroll, BorderLayout.CENTER);

        return panel;
    }

    private void toggleLogVisibility() {
        logScroll.setVisible(showLogCheck.isSelected());
        frame.revalidate();
        frame.repaint();
    }


    private int verifyConfigPassword() {
        JPasswordField passwordField = new JPasswordField(15);

        passwordField.requestFocusInWindow();


        // Save default echo char
        char defaultEcho = passwordField.getEchoChar();

        JCheckBox showPassword = new JCheckBox("نمایش رمز عبور");
        showPassword.setFont(showPassword.getFont().deriveFont(Font.PLAIN, 12f));

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0); // show text
            } else {
                passwordField.setEchoChar(defaultEcho); // hide text
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));


        panel.add(Box.createVerticalStrut(8));
        panel.add(showPassword);

        panel.add(passwordField);


        JLabel label = new JLabel("رمز عبور تنظیمات:");
        label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(label);


        UIManager.put("OptionPane.buttonOrientation", SwingConstants.CENTER);

        Object[] options = {"تأیید", "انصراف"};

        int result = JOptionPane.showOptionDialog(
                frame,
                panel,
                "احراز هویت",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (result != 0) { // not "تأیید"
            return 0;
        }

        String entered = new String(passwordField.getPassword());

        if(entered.isEmpty()){
            return -2;
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter usFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

        String formattedToday = today.format(usFormatter);

        if ((CONFIG_PASSWORD + formattedToday).equals(entered)) {
            return 1;
        }

        return -1;
    }

   /* private int verifyConfigPassword() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.requestFocusInWindow();
        passwordField.setToolTipText("رمز عبور را وارد نمایید");

        Object[] options = { "تأیید", "انصراف" };

        int result = JOptionPane.showOptionDialog(
                frame,
                passwordField,
                "رمز عبور تنظیمات",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0] // default selected button
        );

        // user clicked "انصراف" or closed dialog
        if (result != 0) {
            return 0;
        }

        String entered = new String(passwordField.getPassword());

        LocalDate today = LocalDate.now();
        DateTimeFormatter usFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

        String formattedToday = today.format(usFormatter);

        if ((CONFIG_PASSWORD + formattedToday).equals(entered)) {
            return 1;
        }

        return -1;
    }*/

    /*private int verifyConfigPassword() {
        JPasswordField passwordField = new JPasswordField();

        int result = JOptionPane.showConfirmDialog(
                frame,
                passwordField,
                "رمز عبور تنظیمات",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return 0;
        }

        String entered = new String(passwordField.getPassword());

        LocalDate today = LocalDate.now();

        DateTimeFormatter usFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

        String formattedToday = today.format(usFormatter);

        if((CONFIG_PASSWORD+ formattedToday).equals(entered)){
            return 1;
        }
        return -1;
    }*/
}