package guichaguri.betterfps.installer;

import com.eclipsesource.json.JsonObject;
import guichaguri.betterfps.BetterFps;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * @author Guilherme Chaguri
 */
public class GuiInstallOptions extends JDialog implements ActionListener, FocusListener {

    private final String BROWSE = "browse_directories";
    private final String INSTALL = "install";

    private final String DEFAULT_PROFILE = "BetterFps";

    private final JTextField gameDir;
    private final JComboBox versions;
    private final JComboBox profiles;

    private JsonObject launcherProfiles;

    GuiInstallOptions(GuiInstaller installer) {
        setTitle(BetterFpsInstaller.i18n("betterfps.installer.button.install"));
        setResizable(false);
        setModal(true);
        setMinimumSize(new Dimension(300, 200));

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        content.add(createLabel("betterfps.installer.label.game-dir"), c);

        c.gridy++;
        gameDir = new JTextField(BetterFpsInstaller.getSuggestedMinecraftFolder().getAbsolutePath());
        gameDir.addFocusListener(this);
        content.add(gameDir, c);

        c.gridx++;
        JButton browse = new JButton("...");
        browse.setActionCommand(BROWSE);
        browse.addActionListener(this);
        content.add(browse, c);

        c.gridx = 0;
        c.gridy++;
        content.add(Box.createVerticalStrut(10), c);

        c.gridy++;
        content.add(createLabel("betterfps.installer.label.version"), c);

        c.gridy++;
        versions = new JComboBox();
        content.add(versions, c);

        c.gridy++;
        content.add(Box.createVerticalStrut(10), c);

        c.gridy++;
        content.add(createLabel("betterfps.installer.label.profile"), c);

        c.gridy++;
        profiles = new JComboBox();
        content.add(profiles, c);

        c.gridy++;
        content.add(Box.createVerticalStrut(15), c);

        c.gridy++;
        JButton install = new JButton(BetterFpsInstaller.i18n("betterfps.installer.button.install"));
        install.setActionCommand(INSTALL);
        install.addActionListener(this);
        content.add(install, c);

        updateComboBoxes();

        add(content);
        pack();
        setSize(getPreferredSize());
        setLocationRelativeTo(installer);
    }

    private JLabel createLabel(String key) {
        JLabel label = new JLabel(BetterFpsInstaller.i18n(key));
        label.setFont(label.getFont().deriveFont(12F));
        return label;
    }

    private void updateComboBoxes() {
        File dir = new File(gameDir.getText());
        versions.removeAllItems();
        profiles.removeAllItems();

        if(!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File versionsDir = new File(dir, "versions");
        if(versionsDir.exists() && versionsDir.isDirectory()) {
            for(File ver : versionsDir.listFiles()) {
                if(!ver.isDirectory()) continue;
                String name = ver.getName();
                if(!name.startsWith(BetterFps.MC_VERSION)) continue;
                versions.addItem(name);
            }
        }

        profiles.addItem(DEFAULT_PROFILE);
        try {
            launcherProfiles = BetterFpsInstaller.loadProfiles(dir);
            for(String name : BetterFpsInstaller.getProfileNames(launcherProfiles)) {
                if(!name.equalsIgnoreCase(DEFAULT_PROFILE)) profiles.addItem(name);
            }
        } catch(IOException ex) {}
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();

        if(action.equals(BROWSE)) {

            JFileChooser chooser = new JFileChooser(BetterFpsInstaller.getSuggestedMinecraftFolder());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle(BetterFpsInstaller.i18n("betterfps.installer.label.game-dir"));
            int r = chooser.showOpenDialog(this);

            if(r == JFileChooser.APPROVE_OPTION) {
                gameDir.setText(chooser.getSelectedFile().getAbsolutePath());
                updateComboBoxes();
            }

        } else if(action.equals(INSTALL)) {

            if(versions.getSelectedIndex() == -1 || profiles.getSelectedIndex() == -1) {
                String msg = "The version or the profile is missing.";
                JOptionPane.showMessageDialog(this, msg, "Oops!", JOptionPane.WARNING_MESSAGE);
                return;
            }

            File dir = new File(gameDir.getText());
            String version = versions.getSelectedItem().toString();
            String profile = profiles.getSelectedItem().toString();
            BetterFpsInstaller.info("Installing BetterFps in %s using the profile %s...", version, profile);

            try {
                long time = System.nanoTime();
                BetterFpsInstaller.copyLibrary(dir);
                JsonObject versionJson = BetterFpsInstaller.loadVersion(dir, version);

                version = String.format("%s-BetterFps-%s", version, BetterFps.VERSION);
                versionJson = BetterFpsInstaller.generateVersion(versionJson, version);
                BetterFpsInstaller.saveVersion(dir, version, versionJson);

                if(launcherProfiles != null) {
                    BetterFpsInstaller.addProfile(launcherProfiles, profile, version);
                    BetterFpsInstaller.saveProfiles(dir, launcherProfiles);
                }

                BetterFpsInstaller.GAME_DIR = dir;
                BetterFpsInstaller.saveAlgorithm();

                time = System.nanoTime() - time;
                String title = BetterFpsInstaller.i18n("betterfps.installer.title");
                String msg = BetterFpsInstaller.i18n("betterfps.installer.done", TimeUnit.NANOSECONDS.toMillis(time));
                JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);

            } catch(IOException ex) {
                ex.printStackTrace();

                String msg = "An error ocurred while installing: %s\nSorry for the inconvenience.";
                msg = String.format(msg, ex.getClass().getSimpleName());
                JOptionPane.showMessageDialog(this, msg, "Oops!", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    @Override
    public void focusGained(FocusEvent event) {

    }

    @Override
    public void focusLost(FocusEvent event) {
        updateComboBoxes();
    }
}
