package net.baguchan.bagus_littlemaidmob.unused;

import net.baguchan.bagus_littlemaidmob.entity.MultiModelEntity;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelLittleMaidBase;

public class ModelLittleMaid_Chloe2<T extends MultiModelEntity> extends ModelLittleMaidBase<T> {
/*
	//added fields
	public ModelPart eyeR;
	public ModelPart eyeL;
	public ModelPart Ponytail;
	public ModelPart BunchR;
	public ModelPart BunchL;
	public ModelPart upperRightArm;
	public ModelPart upperLeftArm;
	public ModelPart upperRightLeg;
	public ModelPart upperLeftLeg;
	public ModelPart hemSkirtR1;
	public ModelPart hemSkirtL1;
	public ModelPart hemSkirtR2;
	public ModelPart hemSkirtL2;
	public ModelPart breastR;
	public ModelPart breastL;
	public ModelPart hipBody;
	protected byte offsetY;
	protected byte headPosY;
	protected byte bodyPosY;
	protected byte legPosY;
	protected Random rand = new Random();
	public ModelLittleMaid_Chloe2(ModelPart modelPart) {
		super(modelPart);
	}


	@Override
	public float getHeight()
	{
		return 1.8F;
	}

	@Override
	public float getWidth()
	{
		return 0.5F;
	}

	*//**
	 * �p������E������
	 *//*
	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float f3 = entityTicksExisted + ageInTicks + entityIdFactor;
		// 目パチ
		if( 0 > mh_sin(f3 * 0.05F) + mh_sin(f3 * 0.13F) + mh_sin(f3 * 0.7F) + 2.55F) {
			eyeR.setVisible(true);
			eyeL.setVisible(true);
		} else {
			eyeR.setVisible(false);
			eyeL.setVisible(false);
		}
	}*/
}
