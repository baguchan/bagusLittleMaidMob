package net.baguchan.bagus_littlemaidmob.entity.compound;

import net.baguchan.bagus_littlemaidmob.resource.holder.ConfigHolder;
public interface SoundPlayable {

    void play(String soundName);

    void setConfigHolder(ConfigHolder configHolder);

    ConfigHolder getConfigHolder();

}
