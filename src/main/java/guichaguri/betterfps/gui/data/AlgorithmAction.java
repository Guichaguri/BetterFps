package guichaguri.betterfps.gui.data;

import guichaguri.betterfps.BetterFpsConfig.AlgorithmType;
import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.gui.GuiConfigOption;
import guichaguri.betterfps.tweaker.BetterFpsTweaker;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;

/**
 * @author Guilherme Chaguri
 */
public class AlgorithmAction implements Runnable {
    private final GuiConfigOption<AlgorithmType> button;
    private Thread thread;
    private Process process;

    public AlgorithmAction(GuiConfigOption<AlgorithmType> button) {
        this.button = button;
    }

    private String getJavaDir() {
        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator;
        if((Util.getOSType() == EnumOS.WINDOWS) && (new File(path + "javaw.exe").isFile())) {
            return path + "javaw.exe";
        }
        return path + "java";
    }

    private AlgorithmType parseAlgorithm(String algorithm) {
        if(algorithm == null) {
            return null;
        } else if(algorithm.equals("java")) {
            return AlgorithmType.JAVA;
        } else if(algorithm.equals("rivens")) {
            return AlgorithmType.RIVENS;
        } else if(algorithm.equals("rivens-full")) {
            return AlgorithmType.RIVENS_FULL;
        } else if(algorithm.equals("rivens-half")) {
            return AlgorithmType.RIVENS_HALF;
        } else if(algorithm.equals("libgdx")) {
            return AlgorithmType.LIBGDX;
        } else if(algorithm.equals("taylors")) {
            return AlgorithmType.TAYLORS;
        } else if(algorithm.equals("vanilla")) {
            return AlgorithmType.VANILLA;
        }
        return null;
    }

    @Override
    public void run() {
        if(process != null && thread != null && thread.isAlive()) return;
        BetterFpsHelper.LOG.info("Benchmarking algorithms...");
        button.setTitle(I18n.format("betterfps.installer.algorithm.working"));

        List<String> args = new ArrayList<String>();
        args.add(getJavaDir());
        args.add("-cp");
        args.add(BetterFpsTweaker.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        args.add("guichaguri.betterfps.installer.GuiAlgorithmTester");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    process.waitFor();

                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = in.readLine();

                    BetterFpsHelper.LOG.info("Benchmark is done.");
                    button.setValue(parseAlgorithm(line));
                    button.updateTitle();
                    process.destroy();
                    process = null;
                    thread = null;
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            process = new ProcessBuilder(args).start();
            thread.start();
        } catch(IOException ex) {
            BetterFpsHelper.LOG.error("Couldn't launch the benchmark", ex);
        }
    }

}
