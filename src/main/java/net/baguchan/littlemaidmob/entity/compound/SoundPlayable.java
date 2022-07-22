package net.baguchan.littlemaidmob.entity.compound;

import net.baguchan.littlemaidmob.resource.holder.ConfigHolder;
public interface SoundPlayable {

    void play(String soundName);

    void setConfigHolder(ConfigHolder configHolder);

    ConfigHolder getConfigHolder();

}
