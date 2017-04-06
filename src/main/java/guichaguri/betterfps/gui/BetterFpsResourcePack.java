package guichaguri.betterfps.gui;

import com.google.common.collect.ImmutableSet;
import guichaguri.betterfps.tweaker.BetterFpsTweaker;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

/**
 * A resource pack that serves the purpose to provide lang files
 * @author Guilherme Chaguri
 */
public class BetterFpsResourcePack implements IResourcePack {
    private final Set<String> DOMAINS = ImmutableSet.of("betterfps");

    public BetterFpsResourcePack() {

    }

    private String getPath(ResourceLocation location) {
        return String.format("assets/%s/%s", location.getResourceDomain(), location.getResourcePath()).toLowerCase();
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        InputStream stream = BetterFpsTweaker.getResourceStream(getPath(location));

        if(stream == null) throw new FileNotFoundException();

        return stream;
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        return BetterFpsTweaker.getResource(getPath(location)) != null;
    }

    @Override
    public Set<String> getResourceDomains() {
        return DOMAINS;
    }

    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer serializer, String sectionName) throws IOException {
        return null; // No metadata for now
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        throw new FileNotFoundException(); // No image for now
    }

    @Override
    public String getPackName() {
        return "BetterFps";
    }
}
