package net.baguchan.littlemaidmob.client.resource.manager;
import net.baguchan.littlemaidmob.client.resource.LMSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//todo MixモードとFillモード - リソースパックスクリーン改造
public class LMSoundManager {
    public static final LMSoundManager INSTANCE = new LMSoundManager();
    private final Map<String, WeighedSoundEvents> soundPaths = new HashMap<>();

    public void addSound(String packName, String parentName, String fileName, ResourceLocation location) {
        WeighedSoundEvents soundSet = soundPaths.computeIfAbsent(
                (packName + "." + parentName + "." + fileName).toLowerCase(),
                k -> new WeighedSoundEvents(location, packName + "." + fileName));

        soundSet.addSound(new Sound(location.toString(), (rand) -> 1F, (rand) -> 1F, 1, Sound.Type.FILE,
                false, false, 16) {
            @Override
            public ResourceLocation getLocation() {
                return getPath();
            }
        });
    }

    public Optional<WeighedSoundEvents> getSound(String soundFileLocation) {
        if (soundFileLocation.contains(":")) {
            return Optional.ofNullable(Minecraft.getInstance().getSoundManager()
                    .getSoundEvent(new ResourceLocation(soundFileLocation.toLowerCase())));
        }

        WeighedSoundEvents soundSet = soundPaths.get(soundFileLocation);
        return Optional.ofNullable(soundSet);
    }

    public void play(String soundFileName, SoundSource soundCategory,
                     float volume, float pitch, RandomSource random, double x, double y, double z) {
        getSound(soundFileName).ifPresent(soundSet -> Minecraft.getInstance().getSoundManager()
                .play(new LMSoundInstance(soundSet, soundCategory, volume, pitch, random, x, y, z)));
    }
}
