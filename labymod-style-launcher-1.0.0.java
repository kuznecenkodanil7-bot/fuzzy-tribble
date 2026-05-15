package ru.wqkcpf.launcher;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class LauncherFrame extends JFrame {
    private final SettingsStore settingsStore;
    private final GameLauncher gameLauncher;
    private final CardLayout contentLayout;
    private final JPanel contentPanel;

    private JTextField usernameField;
    private JTextField javaPathField;
    private JTextField clientJarField;
    private JTextField gameDirField;
    private JTextField ramField;
    private JTextField serverIpField;
    private JTextField extraArgsField;
    private JTextArea consoleArea;
    private JLabel statusLabel;

    public LauncherFrame(SettingsStore settingsStore) {
        this.settingsStore = settingsStore;
        this.gameLauncher = new GameLauncher();
        this.contentLayout = new CardLayout();
        this.contentPanel = new JPanel(contentLayout);

        setTitle("Wqkcpf Launcher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UiTheme.BG);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        contentPanel.setBackground(UiTheme.BG);
        add(contentPanel, BorderLayout.CENTER);

        contentPanel.add(createHomePanel(), "home");
        contentPanel.add(createModsPanel(), "mods");
        contentPanel.add(createSettingsPanel(), "settings");
        contentPanel.add(createConsolePanel(), "console");
        contentLayout.show(contentPanel, "home");

        pack();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(245, 680));
        sidebar.setBackground(new Color(9, 12, 18));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UiTheme.CARD_LIGHT));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(26, 22, 20, 22));

        JLabel logo = UiTheme.title("WQKCPF", 28);
        JLabel subtitle = UiTheme.muted("Laby-style launcher MVP");
        top.add(logo, BorderLayout.NORTH);
        top.add(subtitle, BorderLayout.SOUTH);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 10));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JButton home = navButton("Главная");
        JButton mods = navButton("Моды");
        JButton settings = navButton("Настройки");
        JButton console = navButton("Консоль");

        home.addActionListener(e -> contentLayout.show(contentPanel, "home"));
        mods.addActionListener(e -> contentLayout.show(contentPanel, "mods"));
        settings.addActionListener(e -> contentLayout.show(contentPanel, "settings"));
        console.addActionListener(e -> contentLayout.show(contentPanel, "console"));

        nav.add(home);
        nav.add(mods);
        nav.add(settings);
        nav.add(console);
        sidebar.add(nav, BorderLayout.CENTER);

        statusLabel = UiTheme.muted("Готов к запуску");
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 22, 24, 22));
        bottom.add(statusLabel, BorderLayout.SOUTH);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        UiTheme.ghostButton(button);
        button.setHorizontalAlignment(JButton.LEFT);
        return button;
    }

    private JPanel createHomePanel() {
        JPanel root = pageRoot();
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(32, 34, 18, 34));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);
        titleBox.add(UiTheme.title("Minecraft Client Launcher", 30));
        titleBox.add(UiTheme.muted("Минималистичный лаунчер под свой клиент, модпак или отдельный .jar"));
        header.add(titleBox, BorderLayout.WEST);

        JButton play = new JButton("Играть");
        UiTheme.greenButton(play);
        play.addActionListener(e -> startGame());
        header.add(play, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 18));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 34, 28, 34));

        JPanel card1 = card();
        card1.setLayout(new BorderLayout());
        card1.add(UiTheme.title("Профиль клиента", 22), BorderLayout.NORTH);
        JTextArea text = readonlyArea("Версия: Custom Client\nСтиль: LabyMod-like dark UI\nСборка: Maven + GitHub Actions\nФормат результата: executable .jar\n\nЭтот MVP не ломает Minecraft и не подменяет Microsoft-авторизацию. Он запускает указанный .jar-файл с параметрами, которые ты задашь в настройках.");
        card1.add(text, BorderLayout.CENTER);

        JPanel card2 = card();
        card2.setLayout(new BorderLayout());
        card2.add(UiTheme.title("Новости", 22), BorderLayout.NORTH);
        JTextArea news = readonlyArea("• Добавлена тёмная тема интерфейса\n• Добавлены настройки RAM, ника, папки игры и client.jar\n• Добавлен GitHub workflow для автоматической сборки\n• Добавлена консоль процесса\n\nДальше можно прикрутить авторизацию, загрузку модов, список серверов и автообновление.");
        card2.add(news, BorderLayout.CENTER);

        grid.add(card1);
        grid.add(card2);
        root.add(grid, BorderLayout.CENTER);
        return root;
    }

    private JPanel createModsPanel() {
        JPanel root = pageRoot();
        root.setBorder(BorderFactory.createEmptyBorder(32, 34, 34, 34));

        JPanel card = card();
        card.setLayout(new BorderLayout(0, 14));
        card.add(UiTheme.title("Моды и клиент", 26), BorderLayout.NORTH);
        JTextArea text = readonlyArea("Здесь оставлен экран под будущий менеджер модов.\n\nЧто можно добавить следующим шагом:\n1. Сканирование папки mods.\n2. Включение/отключение модов через переименование .jar.disabled.\n3. Скачивание модов из твоего репозитория.\n4. Проверку совместимости версии клиента.\n5. Автообновление модпака перед запуском.");
        card.add(text, BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private JPanel createSettingsPanel() {
        LauncherSettings settings = settingsStore.load();

        JPanel root = pageRoot();
        root.setBorder(BorderFactory.createEmptyBorder(32, 34, 34, 34));

        JPanel card = card();
        card.setLayout(new BorderLayout(0, 18));
        card.add(UiTheme.title("Настройки запуска", 26), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 0, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        usernameField = field(settings.getUsername());
        javaPathField = field(settings.getJavaPath());
        clientJarField = field(settings.getClientJarPath());
        gameDirField = field(settings.getGameDirectory());
        ramField = field(settings.getRamMb());
        serverIpField = field(settings.getServerIp());
        extraArgsField = field(settings.getExtraArgs());

        addRow(form, gbc, 0, "Ник", usernameField, null);
        addRow(form, gbc, 1, "Java", javaPathField, null);
        addRow(form, gbc, 2, "Client .jar", clientJarField, browseFileButton(clientJarField));
        addRow(form, gbc, 3, "Папка игры", gameDirField, browseDirButton(gameDirField));
        addRow(form, gbc, 4, "RAM, МБ", ramField, null);
        addRow(form, gbc, 5, "Сервер", serverIpField, null);
        addRow(form, gbc, 6, "Extra args", extraArgsField, null);

        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton save = new JButton("Сохранить");
        JButton play = new JButton("Сохранить и играть");
        UiTheme.primaryButton(save);
        UiTheme.greenButton(play);
        save.addActionListener(e -> saveSettings(true));
        play.addActionListener(e -> {
            if (saveSettings(false)) {
                startGame();
            }
        });
        actions.add(save);
        actions.add(play);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private JPanel createConsolePanel() {
        JPanel root = pageRoot();
        root.setBorder(BorderFactory.createEmptyBorder(32, 34, 34, 34));

        JPanel card = card();
        card.setLayout(new BorderLayout(0, 14));
        card.add(UiTheme.title("Консоль", 26), BorderLayout.NORTH);

        consoleArea = readonlyArea("Здесь появится вывод процесса после запуска.\n");
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UiTheme.CARD_LIGHT, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        root.add(card, BorderLayout.CENTER);
        return root;
    }

    private JPanel pageRoot() {
        JPanel root = new JPanel(new BorderLayout());
        UiTheme.applyPanel(root);
        return root;
    }

    private JPanel card() {
        JPanel panel = new JPanel();
        UiTheme.applyCard(panel);
        return panel;
    }

    private JTextField field(String value) {
        JTextField field = new JTextField(value == null ? "" : value);
        UiTheme.styleField(field);
        return field;
    }

    private JTextArea readonlyArea(String value) {
        JTextArea area = new JTextArea(value);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(UiTheme.TEXT);
        area.setBackground(UiTheme.CARD);
        area.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return area;
    }

    private JButton browseFileButton(JTextField target) {
        JButton button = new JButton("Выбрать");
        UiTheme.ghostButton(button);
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                target.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        return button;
    }

    private JButton browseDirButton(JTextField target) {
        JButton button = new JButton("Выбрать");
        UiTheme.ghostButton(button);
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                target.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        return button;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JTextField field, JButton button) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel labelComponent = UiTheme.muted(label);
        form.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        if (button != null) {
            form.add(button, gbc);
        } else {
            JLabel empty = new JLabel(" ");
            form.add(empty, gbc);
        }
    }

    private boolean saveSettings(boolean showMessage) {
        LauncherSettings settings = collectSettings();
        try {
            settingsStore.save(settings);
            statusLabel.setText("Настройки сохранены");
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "Настройки сохранены.");
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка сохранения", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private LauncherSettings collectSettings() {
        LauncherSettings settings = new LauncherSettings();
        settings.setUsername(usernameField == null ? "Player" : usernameField.getText());
        settings.setJavaPath(javaPathField == null ? "java" : javaPathField.getText());
        settings.setClientJarPath(clientJarField == null ? settingsStore.load().getClientJarPath() : clientJarField.getText());
        settings.setGameDirectory(gameDirField == null ? settingsStore.load().getGameDirectory() : gameDirField.getText());
        settings.setRamMb(ramField == null ? "2048" : ramField.getText());
        settings.setServerIp(serverIpField == null ? "" : serverIpField.getText());
        settings.setExtraArgs(extraArgsField == null ? "" : extraArgsField.getText());
        return settings;
    }

    private void startGame() {
        LauncherSettings settings = collectSettings();
        try {
            settingsStore.save(settings);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка сохранения", JOptionPane.ERROR_MESSAGE);
            return;
        }

        contentLayout.show(contentPanel, "console");
        consoleArea.append("\n> Запуск клиента...\n");
        statusLabel.setText("Запуск...");

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    List<String> command = gameLauncher.buildCommand(settings);
                    publish("> Команда: " + String.join(" ", command) + "\n");
                    Process process = gameLauncher.start(settings);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            publish(line + "\n");
                        }
                    }
                    int exitCode = process.waitFor();
                    publish("\n> Процесс завершён. Код: " + exitCode + "\n");
                } catch (Exception ex) {
                    publish("\n> Ошибка: " + ex.getMessage() + "\n");
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    consoleArea.append(chunk);
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                }
            }

            @Override
            protected void done() {
                statusLabel.setText("Готов к запуску");
            }
        };
        worker.execute();
    }
}
