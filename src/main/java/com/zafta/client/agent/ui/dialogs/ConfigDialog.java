package com.zafta.client.agent.ui.dialogs;

import com.zafta.client.agent.config.ApplicationSettings;
import com.zafta.client.agent.config.YamlConfigService;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.function.Consumer;

public class ConfigDialog extends JDialog {

    private final JTextField counterIdField = new JTextField();
    private final JTextField serverUrlField = new JTextField();

    private final YamlConfigService configService;
    private final Consumer<ApplicationSettings> onSave;

    public ConfigDialog(
            JFrame parent,
            YamlConfigService configService,
            Consumer<ApplicationSettings> onSave
    ) {
        super(parent, "تنظیمات سامانه", true);
        this.configService = configService;
        this.onSave = onSave;

        initUI();
        loadConfig();
    }

    private void initUI() {
        setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Counter ID
        c.gridx = 0; c.gridy = 0;
        counterIdField.setColumns(20);
        form.add(counterIdField, c);


        c.gridx = 1;
        form.add(new JLabel("شناسه باجه:"), c);

        // Server URL
        c.gridx = 0; c.gridy = 1;
        serverUrlField.setColumns(20);
        form.add(serverUrlField, c);

        c.gridx = 1;
        form.add(new JLabel("آدرس سرور:"), c);

        JButton save = new JButton("ذخیره");
        JButton cancel = new JButton("انصراف");

        save.addActionListener(e -> saveConfig());
        cancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(save);
        buttons.add(cancel);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(form, BorderLayout.CENTER);
        main.add(buttons, BorderLayout.SOUTH);

        setContentPane(main);
        pack();
        setLocationRelativeTo(getParent());
        setAlwaysOnTop(true);
    }

    private void loadConfig() {
        ApplicationSettings settings = configService.load();
        if (settings == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "خطا در بارگذاری تنظیمات",
                    "خطا",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        counterIdField.setText(settings.getCounterId());
        serverUrlField.setText(extractHost(settings.getServerUrl()));
    }

    private void saveConfig() {
        String counterId = counterIdField.getText().trim();
        String serverUrl = serverUrlField.getText().trim();

        if (counterId.isEmpty() || serverUrl.isEmpty()) {
            DialogUtils.error(this, "تمام فیلدها الزامی هستند");
            return;
        }

        ApplicationSettings oldSettings = configService.load();
        String fullUrl =
                "http://" + serverUrl + ":" + extractPort(oldSettings.getServerUrl());

        /*if (counterId.equals(oldSettings.getCounterId())
                && fullUrl.equals(oldSettings.getServerUrl())) {
            return;
        }*/

        boolean confirmed = DialogUtils.confirm(
                this,
                "تأیید تغییرات",
                "آیا نسبت به اعمال تنظیمات جدید بر روی نرم افزار اطمینان دارید؟"
        );

        if (!confirmed) {
            return;
        }


        configService.save(counterId, fullUrl);

        ApplicationSettings updated = new ApplicationSettings();
        updated.setCounterId(counterId);
        updated.setServerUrl(fullUrl);

        if (onSave != null) {
            onSave.accept(updated);
        }

        dispose();
    }

    private String extractHost(String fullUrl) {
        if (fullUrl == null) return "";
        try {
            URI uri = new URI(fullUrl);
            return uri.getHost();
        } catch (Exception e) {
            return "";
        }
    }

    private int extractPort(String fullUrl) {
        try {
            URI uri = new URI(fullUrl);
            return uri.getPort() > 0 ? uri.getPort() : 6600;
        } catch (Exception e) {
            return 6600;
        }
    }
}
