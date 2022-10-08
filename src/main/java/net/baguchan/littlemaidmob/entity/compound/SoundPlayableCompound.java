package net.baguchan.littlemaidmob.entity.compound;

import net.baguchan.littlemaidmob.client.resource.manager.LMSoundManager;
import net.baguchan.littlemaidmob.message.SyncSoundConfigMessage;
import net.baguchan.littlemaidmob.resource.holder.ConfigHolder;
import net.baguchan.littlemaidmob.resource.manager.LMConfigManager;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public class SoundPlayableCompound implements SoundPlayable {
    private final Entity entity;
    private final Supplier<String> packName;
    private ConfigHolder configHolder;

    public SoundPlayableCompound(Entity entity, Supplier<String> packName) {
        this.entity = entity;
        this.packName = packName;
        update();
    }

    public void update() {
        LMConfigManager configManager = LMConfigManager.INSTANCE;
        configHolder = configManager.getTextureSoundConfig(getPackName())
                .orElse(configManager.getAnyConfig());
    }

    public String getPackName() {
        return packName.get();
    }

    public void setConfigHolder(ConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    public ConfigHolder getConfigHolder() {
        return this.configHolder;
    }

    @Override
    public void play(String soundName) {
        //todo 音声周りの仕様を1.19に合わせる
        if (entity.level.isClientSide()) {
            configHolder.getSoundFileName(soundName.toLowerCase())
                    .ifPresent(soundFileName ->
                            LMSoundManager.INSTANCE.play(soundFileName, entity.getSoundSource(),
                                    1F, 1F, entity.getLevel().getRandom(), entity.getX(), entity.getEyeY(), entity.getZ()));
        } else {
            SyncSoundConfigMessage.sendS2CPacket(entity, soundName);
        }
    }

}
