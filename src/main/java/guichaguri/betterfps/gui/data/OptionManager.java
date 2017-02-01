package guichaguri.betterfps.gui.data;

import com.mojang.realmsclient.gui.ChatFormatting;
import guichaguri.betterfps.BetterFpsConfig;
import guichaguri.betterfps.BetterFpsConfig.AlgorithmType;
import guichaguri.betterfps.BetterFpsHelper;
import guichaguri.betterfps.gui.GuiConfigOption;
import java.util.List;
import net.minecraft.client.resources.I18n;

/**
 * @author Guilherme Chaguri
 */
public class OptionManager {

    public static void addButtons(List<GuiConfigOption> buttons) {
        BetterFpsConfig config = BetterFpsHelper.getConfig();

        Boolean[] boolMap = new Boolean[]{true, false};

        String[] enabledNames = new String[]{
                I18n.format("betterfps.options.on"),
                I18n.format("betterfps.options.off")
        };
        String[] fancyFast = new String[]{
                I18n.format("betterfps.options.fancy"),
                I18n.format("betterfps.options.fast")
        };

        // Algorithm
        GuiConfigOption<AlgorithmType> algorithm = new GuiConfigOption<AlgorithmType>(0, "betterfps.options.algorithm.title");
        algorithm.add(AlgorithmType.VANILLA, "betterfps.options.algorithm.vanilla");
        algorithm.add(AlgorithmType.RIVENS, "betterfps.options.algorithm.rivens");
        algorithm.add(AlgorithmType.RIVENS_FULL, "betterfps.options.algorithm.rivens-full");
        algorithm.add(AlgorithmType.RIVENS_HALF, "betterfps.options.algorithm.rivens-half");
        algorithm.add(AlgorithmType.TAYLORS, "betterfps.options.algorithm.taylors");
        algorithm.add(AlgorithmType.JAVA, "betterfps.options.algorithm.java");
        algorithm.add(AlgorithmType.RANDOM, "betterfps.options.algorithm.random");
        algorithm.setRestart(true);
        algorithm.setWide(true);
        algorithm.setDefaults(AlgorithmType.VANILLA, AlgorithmType.RIVENS_HALF, config.algorithm);
        algorithm.setDescription(
                I18n.format("betterfps.options.algorithm.desc"),
                ChatFormatting.YELLOW + I18n.format("betterfps.options.algorithm.action")
        );
        algorithm.setShiftClick(new AlgorithmAction(algorithm));
        buttons.add(algorithm);

        // Update Checker
        GuiConfigOption<Boolean> updateChecker = new GuiConfigOption<Boolean>(1, "betterfps.options.updatechecker.title");
        updateChecker.set(boolMap, enabledNames);
        updateChecker.setDefaults(true, true, config.updateChecker);
        updateChecker.setDescription(
                I18n.format("betterfps.options.updatechecker.desc"),
                ChatFormatting.YELLOW + I18n.format("betterfps.options.updatechecker.action")
        );
        updateChecker.setShiftClick(new UpdateCheckAction());
        buttons.add(updateChecker);

        // Pre-allocate memory
        GuiConfigOption<Boolean> allocMemory = new GuiConfigOption<Boolean>(2, "betterfps.options.allocmemory.title");
        allocMemory.set(boolMap, enabledNames);
        allocMemory.setRestart(true);
        allocMemory.setDefaults(true, false, config.preallocateMemory);
        allocMemory.setDescription(I18n.format("betterfps.options.allocmemory.desc"));
        buttons.add(allocMemory);

        // Fog
        GuiConfigOption<Boolean> fog = new GuiConfigOption<Boolean>(3, "betterfps.options.fog.title");
        fog.set(boolMap, fancyFast);
        fog.setRestart(true);
        fog.setDefaults(true, true, config.fog);
        fog.setDescription(I18n.format("betterfps.options.fog.desc"));
        buttons.add(fog);

        // Beacon Beam
        GuiConfigOption<Boolean> beam = new GuiConfigOption<Boolean>(4, "betterfps.options.beaconbeam.title");
        beam.set(boolMap, fancyFast);
        beam.setRestart(true);
        beam.setDefaults(true, true, config.beaconBeam);
        beam.setDescription(I18n.format("betterfps.options.beaconbeam.desc"));
        buttons.add(beam);

        // Hopper Improvement
        GuiConfigOption<Boolean> hopper = new GuiConfigOption<Boolean>(5, "betterfps.options.hopper.title");
        hopper.set(boolMap, enabledNames);
        hopper.setRestart(true);
        hopper.setDefaults(false, true, config.fastHopper);
        hopper.setDescription(I18n.format("betterfps.options.hopper.desc"));
        hopper.setRestart(true);
        buttons.add(hopper);

        // Hopper Improvement
        GuiConfigOption<Boolean> beacon;
        beacon = new GuiConfigOption<Boolean>(6, "betterfps.options.beacon.title");
        beacon.set(boolMap, enabledNames);
        beacon.setRestart(true);
        beacon.setDefaults(false, true, config.fastBeacon);
        beacon.setDescription(I18n.format("betterfps.options.beacon.desc"));
        buttons.add(beacon);
    }

    public static boolean store(List<GuiConfigOption> buttons) {
        BetterFpsConfig config = BetterFpsHelper.getConfig();

        config.algorithm = getButtonValue(buttons, 0, AlgorithmType.class);
        config.updateChecker = getButtonValue(buttons, 1);
        config.preallocateMemory = getButtonValue(buttons, 2);
        config.fog = getButtonValue(buttons, 3);
        config.beaconBeam = getButtonValue(buttons, 4);
        config.fastHopper = getButtonValue(buttons, 5);
        config.fastBeacon = getButtonValue(buttons, 6);

        for(GuiConfigOption button : buttons) {
            if(button.shouldRestart()) return true;
        }
        return false;
    }

    private static boolean getButtonValue(List<GuiConfigOption> buttons, int id) {
        Boolean b = getButtonValue(buttons, id, Boolean.class);
        return b == null ? true : b;
    }

    private static <T extends Object> T getButtonValue(List<GuiConfigOption> buttons, int id, Class<T> type) {
        for(GuiConfigOption button : buttons) {
            if(button.id == id) {
                return (T)button.getValue();
            }
        }
        return null;
    }

}
