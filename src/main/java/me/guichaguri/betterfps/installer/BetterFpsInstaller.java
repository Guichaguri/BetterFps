package me.guichaguri.betterfps.installer;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.List;
import javax.swing.*;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsInstaller extends JFrame implements ActionListener {

    public static void main(String[] args) {
        String tester = System.getProperty("tester", null);
        if(tester != null) {
            JFrame load = new JFrame();
            load.add(new JLabel("Testing each algorithm..."));
            load.setSize(250, 100);
            load.setLocationRelativeTo(null);
            load.setVisible(true);
            load.requestFocusInWindow();
            AlgorithmTester.open(null, new File(tester), null, null);
            load.setVisible(false);
            return;
        }
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

    private JDialog versionDialog = null;

    private JComboBox versionComboBox = null;

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
            List<String> versions = InstanceInstaller.getVersions(file);
            VersionSelector.open(this, file, versions);
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
            File file = new File(installLocation.getText());
            AlgorithmTester.open(this, file, CALC_ALGORITHM, this);
        }
    }

}
