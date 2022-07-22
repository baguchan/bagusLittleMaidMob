package net.baguchan.littlemaidmob.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

//NM読み込むのに要る
public class LittleServant extends LittleMaidBaseEntity {

    public LittleServant(EntityType<? extends LittleServant> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    public boolean isMale() {
        return true;
    }
}
