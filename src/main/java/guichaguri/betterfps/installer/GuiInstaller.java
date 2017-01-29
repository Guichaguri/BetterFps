package guichaguri.betterfps.installer;

import guichaguri.betterfps.BetterFps;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * @author Guilherme Chaguri
 */
public class GuiInstaller extends JFrame implements ActionListener {

    private final String INSTALL = "install";
    private final String EXTRACT = "extract";
    private final String TEST_ALGORITHMS = "algorithm_test";
    private final String DOWNLOADS = "downloads";
    private final String ISSUES = "issues";

    private final String ISSUES_URL = "https://github.com/Guichaguri/BetterFps/issues";

    public GuiInstaller() {
        setTitle(BetterFpsInstaller.i18n("betterfps.installer.title"));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        content.add(createLabel("betterfps.installer.versions", BetterFps.VERSION, BetterFps.MC_VERSION));

        JLabel title = new JLabel("BetterFps");
        title.setFont(title.getFont().deriveFont(32F));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        content.add(title);

        for(int i = 1; i <= 3; i++) {
            content.add(createLabel("betterfps.installer.note." + i));
        }

        JPanel actions = new JPanel();
        actions.setLayout(new GridLayout(1, 2, 10, 0));
        actions.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        actions.add(createButton("betterfps.installer.button.install", INSTALL));
        actions.add(createButton("betterfps.installer.button.extract", EXTRACT));
        content.add(actions);

        JPanel extras = new JPanel();
        extras.setLayout(new GridLayout(1, 3, 10, 0));
        extras.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        extras.add(createButton("betterfps.installer.button.algorithm", TEST_ALGORITHMS));
        extras.add(createButton("betterfps.installer.button.downloads", DOWNLOADS));
        extras.add(createButton("betterfps.installer.button.issues", ISSUES));
        content.add(extras);

        add(content);
        pack();
        setSize(getPreferredSize());
        setLocationRelativeTo(null);
    }

    private JLabel createLabel(String key, Object ... data) {
        JLabel label = new JLabel(BetterFpsInstaller.i18n(key, data));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        label.setFont(label.getFont().deriveFont(12F));
        return label;
    }

    private JButton createButton(String key, String action) {
        String txt = BetterFpsInstaller.i18n(key);
        JButton button = new JButton(txt);
        button.setActionCommand(action);
        button.addActionListener(this);
        button.setFont(button.getFont().deriveFont(12F));
        button.setPreferredSize(new Dimension(150, 30));
        button.setToolTipText(txt);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();

        if(action.equals(INSTALL)) {

            GuiInstallOptions options = new GuiInstallOptions(this);
            options.setVisible(true);

        } else if(action.equals(EXTRACT)) {

            extractMod();

        } else if(action.equals(TEST_ALGORITHMS)) {

            GuiAlgorithmTester tester = new GuiAlgorithmTester(this);
            tester.setVisible(true);

        } else if(action.equals(DOWNLOADS)) {

            openURL(BetterFps.URL);

        } else if(action.equals(ISSUES)) {

            openURL(ISSUES_URL);

        }
    }

    private void extractMod() {
        JFileChooser chooser = new JFileChooser(BetterFpsInstaller.getSuggestedMinecraftFolder());
        chooser.setDialogTitle(BetterFpsInstaller.i18n("betterfps.installer.button.extract"));
        chooser.setSelectedFile(new File(String.format("BetterFps-%s.jar", BetterFps.VERSION)));
        int r = chooser.showSaveDialog(this);

        if(r == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                long time = System.nanoTime();
                BetterFpsInstaller.copyMod(f);
                time = System.nanoTime() - time;

                String title = BetterFpsInstaller.i18n("betterfps.installer.title");
                String msg = BetterFpsInstaller.i18n("betterfps.installer.done", TimeUnit.NANOSECONDS.toMillis(time));
                JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);

            } catch(IOException ex) {
                ex.printStackTrace();

                String msg = "An error ocurred while extracting: %s\nSorry for the inconvenience.";
                msg = String.format(msg, ex.getClass().getSimpleName());
                JOptionPane.showMessageDialog(this, msg, "Oops!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openURL(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } catch(Exception ex) {}
    }
}
