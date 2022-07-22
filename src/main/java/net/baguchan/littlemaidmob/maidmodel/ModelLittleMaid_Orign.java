package net.baguchan.littlemaidmob.maidmodel;


import net.baguchan.littlemaidmob.entity.LittleMaidBaseEntity;
import net.minecraft.client.model.geom.ModelPart;

public class ModelLittleMaid_Orign<T extends LittleMaidBaseEntity> extends ModelLittleMaidBase<T> {

	/**
	 * コンストラクタは全て継承させること
	 */
	public ModelLittleMaid_Orign(){
	}

	@Override
	public String getUsingTexture() {
		return "default";
	}
}
