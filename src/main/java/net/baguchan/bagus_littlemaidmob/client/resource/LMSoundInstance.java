package net.baguchan.bagus_littlemaidmob.client.resource;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class LMSoundInstance implements SoundInstance {
    private final WeighedSoundEvents soundSet;
    private final Sound sound;
    private final ResourceLocation id;
    private final SoundSource category;
    private final float volume;
    private final float pitch;
    private final double x;
    private final double y;
    private final double z;

    public LMSoundInstance(WeighedSoundEvents soundSet, SoundSource category,
                           float volume, float pitch, RandomSource random, double x, double y, double z) {
        this.soundSet = soundSet;
        this.sound = soundSet.getSound(random);
        this.id = sound.getLocation();
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public ResourceLocation getLocation() {
        return id;
    }

    @Nullable
    @Override
    public WeighedSoundEvents resolve(SoundManager p_119841_) {
        return soundSet;
    }

    @Override
    public Sound getSound() {
        return this.sound;
    }

    @Override
    public SoundSource getSource() {
        return category;
    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public boolean isRelative() {
        return false;
    }

    @Override
    public int getDelay() {
        return 0;
    }
    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public Attenuation getAttenuation() {
        return Attenuation.LINEAR;
    }
}
