package net.baguchan.littlemaidmob.maidmodel;


import net.baguchan.littlemaidmob.entity.MultiModelEntity;

public class ModelLittleMaid_Orign<T extends MultiModelEntity> extends ModelLittleMaidBase<T> {

	/**
	 * コンストラクタは全て継承させること
	 */
	public ModelLittleMaid_Orign() {
	}

	@Override
	public String getUsingTexture() {
		return "default";
	}
}
