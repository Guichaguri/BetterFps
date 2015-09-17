package me.guichaguri.betterfps.installer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import me.guichaguri.betterfps.BetterFpsHelper;
import me.guichaguri.betterfps.math.JavaMath;
import me.guichaguri.betterfps.math.LibGDXMath;
import me.guichaguri.betterfps.math.RivensFullMath;
import me.guichaguri.betterfps.math.RivensHalfMath;
import me.guichaguri.betterfps.math.RivensMath;
import me.guichaguri.betterfps.math.TaylorMath;

/**
 * @author Guilherme Chaguri
 */
public class AlgorithmTester extends JFrame implements ActionListener {

    private static final Class[] algorithms = new Class[]{
            JavaMath.class, VanillaMath.class, TaylorMath.class, LibGDXMath.class,
            RivensMath.class, RivensFullMath.class, RivensHalfMath.class
    };

    public static HashMap<String, Long> testAlgorithms() {
        HashMap<String, Long> results = new HashMap<String, Long>();
        for(Class algorithm : algorithms) {
            try {
                Method sin = algorithm.getDeclaredMethod("sin", float.class);
                Method cos = algorithm.getDeclaredMethod("cos", float.class);

                long startTime = System.nanoTime();
                for(int i = 0; i < 360 * 1000; i++) {
                    float angle = (float)i / 1000F;
                    sin.invoke(null, angle);
                    cos.invoke(null, angle);
                }
                long endTime = System.nanoTime();

                String name = algorithm.getSimpleName();
                for(Entry<String, String> e : BetterFpsHelper.helpers.entrySet()) {
                    if(e.getValue().equals(name)) {
                        name = e.getKey();
                        break;
                    }
                }
                results.put(name, endTime - startTime);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return results;
    }

    private static AlgorithmTester INSTANCE = null;
    public static void open(Component c, File mcDir, String calcAction, ActionListener listener) {
        if(INSTANCE != null) INSTANCE.setVisible(false);
        INSTANCE = new AlgorithmTester(mcDir, testAlgorithms(), calcAction, listener);
        INSTANCE.setLocationRelativeTo(c);
        INSTANCE.setVisible(true);
        INSTANCE.requestFocusInWindow();
    }

    private final String CHANGE_ALGORITHM = "tester_change_algorithm";
    private final String CLOSE_TESTER = "close_tester";

    private final String DESCRIPTION = "<html><center>We recommend testing a few times<br>" +
                                        "to confirm which is the best algorithm<br>" +
                                        "<small>[Notice that this tester is still in development]</small></center></html>";

    private File mcDir;
    private String bestAlgorithm = null;
    private String bestAlgorithmName = null;

    public AlgorithmTester(final File mcDir, HashMap<String, Long> results, String CALC_ALGORITHM, ActionListener calcAction) {
        this.mcDir = mcDir;

        setTitle("Test Results");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        add(new JLabel(DESCRIPTION, JLabel.CENTER), c);
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridLayout(0, 2, 5, 0));

        long bestAlgorithmTime = 0;
        for(Entry<String, Long> e : results.entrySet()) {
            String algorithm = e.getKey();
            long v = e.getValue();
            String displayName = algorithm;
            if(BetterFpsHelper.displayHelpers.containsKey(algorithm)) {
                displayName = BetterFpsHelper.displayHelpers.get(algorithm);
            }
            if((v < bestAlgorithmTime) || (bestAlgorithm == null)) {
                bestAlgorithmTime = v;
                bestAlgorithm = algorithm;
                bestAlgorithmName = displayName;
            }
            resultsPanel.add(new JLabel(displayName, JLabel.RIGHT));
            resultsPanel.add(new JLabel((float)Math.round(((float)v / 1000000) * 100) / 100 + "ms", JLabel.LEFT));
        }
        add(resultsPanel, c);
        JButton changeAlgorithm = new JButton("Change Algorithm to " + bestAlgorithmName);
        changeAlgorithm.setToolTipText("This will choose the best algorithm and change the config file for you.");
        changeAlgorithm.setActionCommand(CHANGE_ALGORITHM);
        changeAlgorithm.addActionListener(this);
        add(changeAlgorithm, c);
        JButton calcAgain = new JButton("Test Again");
        if((CALC_ALGORITHM == null) || (calcAction == null)) {
            calcAgain.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    AlgorithmTester.open(null, mcDir, null, null);
                }
            });
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            calcAgain.setActionCommand(CALC_ALGORITHM);
            calcAgain.addActionListener(calcAction);
        }
        add(calcAgain, c);
        JButton close = new JButton("Close");
        close.setActionCommand(CLOSE_TESTER);
        close.addActionListener(this);
        add(close, c);
        Dimension d = getPreferredSize();
        setSize(new Dimension(d.width + 50, d.height + 50));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if(cmd.equals(CLOSE_TESTER)) {
            setVisible(false);
            if(getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) System.exit(0);
        } else if(cmd.equals(CHANGE_ALGORITHM)) {
            if((!mcDir.exists()) || (!mcDir.isDirectory())) {
                JOptionPane.showMessageDialog(this, "The install location is invalid.",
                                                "Oops!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            BetterFpsHelper.MCDIR = mcDir;
            // TODO FIX!
            //BetterFpsHelper.loadConfig();
            //BetterFpsHelper.CONFIG.setProperty("algorithm", bestAlgorithm);
            //BetterFpsHelper.saveConfig();
            setVisible(false);
            System.out.println(bestAlgorithm);
            JOptionPane.showMessageDialog(this, "The algorithm was set to " + bestAlgorithmName + ".\n\n" +
                                        "Note: If the game is started, you have to restart it to take effect",
                                        "Done!", JOptionPane.INFORMATION_MESSAGE);

            if(getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) System.exit(0);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible) {
            INSTANCE = null;
        }
    }


    private static class VanillaMath {
        private static final float[] SIN_TABLE = new float[65536];
        static {
            for (int i = 0; i < 65536; i++) {
                SIN_TABLE[i] = (float)Math.sin((double)i * Math.PI * 2.0D / 65536.0D);
            }
        }

        public static float sin(float val) {
            return SIN_TABLE[(int)(val * 10430.378F) & 65535];
        }

        public static float cos(float val) {
            return SIN_TABLE[(int)(val * 10430.378F + 16384.0F) & 65535];
        }
    }

}
