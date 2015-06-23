package me.guichaguri.betterfps.installer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * @author Guilherme Chaguri
 */
public class VersionSelector extends JDialog implements ActionListener {
    private static VersionSelector INSTANCE = null;
    public static void open(Component c, File mcDir, List<String> versionNames) {
        if(INSTANCE != null) INSTANCE.setVisible(false);
        INSTANCE = new VersionSelector(mcDir, versionNames);
        INSTANCE.setLocationRelativeTo(c);
        INSTANCE.setVisible(true);
    }

    private final String INSTALL = "install";

    private File mcDir;

    private JComboBox version;

    private VersionSelector(File mcDir, List<String> versionNames) {
        this.mcDir = mcDir;

        setTitle("Select a Version");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        version = new JComboBox();
        for(String v : versionNames) {
            version.addItem(v);
        }
        add(version, c);

        JButton install = new JButton("Install");
        install.setActionCommand(INSTALL);
        install.addActionListener(this);
        add(install, c);

        Dimension d = getPreferredSize();
        setSize(new Dimension(d.width + 50, d.height + 50));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if(cmd.equals(INSTALL)) {
            String ver = version.getSelectedItem().toString();
            if(ver.toLowerCase().contains("forge")) {
                String[] options = new String[]{"Yes, I don't care", "No, I'll do it correctly", "What?!"};
                int r = JOptionPane.showOptionDialog(this,
                        "Looks like you're using Forge.\n" +
                        "You just need to drop the BetterFps jar file in the mods folder.\n" +
                        "Do you want to continue anyway?",
                        "Forge Version", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[1]);
                if(r == 1) {
                    setVisible(false);
                    return;
                } else if((r == 2) || (r == JOptionPane.CLOSED_OPTION)) {
                    return;
                }
            }
            try {
                InstanceInstaller.install(mcDir, ver);
                setVisible(false);
                JOptionPane.showMessageDialog(this, "BetterFps was successfully installed!",
                                                "Done!", JOptionPane.INFORMATION_MESSAGE);
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "An error has ocurred: " + ex.getClass().getSimpleName() +
                                            "\nTry choosing another version", "Oops!", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible) {
            INSTANCE = null;
        }
    }
}
