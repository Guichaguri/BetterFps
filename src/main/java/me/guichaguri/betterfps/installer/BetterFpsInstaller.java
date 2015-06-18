package me.guichaguri.betterfps.installer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.*;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsInstaller extends JFrame implements ActionListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch(Exception ex) {}

            BetterFpsInstaller installer = new BetterFpsInstaller();
            installer.setVisible(true);
            }
        });
    }

    // Constants
    private final String installerDesc = "<html>This is the installer for <strong>BetterFps</strong><br>" +
                                         "If you are using Forge, you just need to drop this file in the mods folder<br>" +
                                         "It's recommended closing the Minecraft Launcher before installing.</html>";
    private final String modUrl = "http://minecraft.curseforge.com/mc-mods/229876-betterfps";

    // Action Commands for the buttons
    private final String INSTALL = "install";
    private final String PAGE = "page";
    private final String CALC_ALGORITHM = "calc_algorithm";
    private final String CHANGE_FILE = "change_file";

    // Frame components
    private JTextField installLocation;
    private JFileChooser fc;

    public BetterFpsInstaller() {
        setTitle("BetterFps Installer");
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 5;
        c.ipady = 5;
        c.insets = new Insets(5, 5, 5, 5);

        JLabel title = new JLabel("BetterFps Installer");
        title.setFont(title.getFont().deriveFont(32F));
        add(title, c);

        c.gridy = 1;
        JLabel desc = new JLabel(installerDesc);
        add(desc, c);

        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        installLocation = new JTextField(12);
        installLocation.setText(InstanceInstaller.getSuggestedMinecraftFolder().getAbsolutePath());
        add(installLocation, c);

        c.gridx = 1;
        c.fill = GridBagConstraints.NONE;
        JButton choose = new JButton("...");
        choose.setActionCommand(CHANGE_FILE);
        choose.addActionListener(this);
        add(choose, c);

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select the Minecraft Installation folder (.minecraft)");

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        JButton install = new JButton("Install");
        install.setActionCommand(INSTALL);
        install.addActionListener(this);
        add(install, c);

        c.gridy = 4;
        JButton testAlgorithms = new JButton("Test Algorithms");
        testAlgorithms.setToolTipText("Test all algorithm to see which is faster");
        testAlgorithms.setActionCommand(CALC_ALGORITHM);
        testAlgorithms.addActionListener(this);
        add(testAlgorithms, c);

        c.gridy = 5;
        JButton page = new JButton("Official Page");
        page.setActionCommand(PAGE);
        page.addActionListener(this);
        add(page, c);

        setSize(450, 325);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if(cmd.equals(INSTALL)) {
            File file = new File(installLocation.getText());
            if((!file.exists()) || (!file.isDirectory())) {
                JOptionPane.showMessageDialog(this, "The install location is invalid.",
                                                "Oops!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            InstanceInstaller.install(file);
        } else if(cmd.equals(PAGE)) {
            try {
                Desktop.getDesktop().browse(new URI(modUrl));
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, modUrl, "URL", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if(cmd.equals(CHANGE_FILE)) {
            int val = fc.showDialog(this, "Select");
            if(val == JFileChooser.APPROVE_OPTION) {
                installLocation.setText(fc.getSelectedFile().getAbsolutePath());
            }
        } else if(cmd.equals(CALC_ALGORITHM)) {
            HashMap<String, Float> results = InstanceInstaller.testAlgorithms();
            createAlgorithmTestWindow(results);
        }
    }

    private void createAlgorithmTestWindow(HashMap<String, Float> results) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Test Results");
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridLayout(0, 2, 5, 0));
        for(Entry<String, Float> e : results.entrySet()) {
            resultsPanel.add(new JLabel(e.getKey(), JLabel.RIGHT));
            resultsPanel.add(new JLabel((float)Math.round(e.getValue() * 100) / 100 + "ms", JLabel.LEFT));
        }
        dialog.add(resultsPanel, c);
        JButton changeAlgorithm = new JButton("Change Algorithm");
        changeAlgorithm.setToolTipText("This will change the config file setting the best algorithm for you.");
        dialog.add(changeAlgorithm, c);
        JButton close = new JButton("Close");
        dialog.add(close, c);
        Dimension d = dialog.getPreferredSize();
        dialog.setSize(new Dimension(d.width + 50, d.height + 50));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static class InstallerVersionSelector extends JDialog {
        JComboBox versions;
        public InstallerVersionSelector() {
            setTitle("Select a Version");
            setLayout(new FlowLayout());

            versions = new JComboBox();
            versions.addItem("1.8-Optifine");

            add(versions);

            pack();
        }
    }

}
