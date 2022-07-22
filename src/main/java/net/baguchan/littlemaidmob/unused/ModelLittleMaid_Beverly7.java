package net.baguchan.littlemaidmob.unused;

import net.baguchan.littlemaidmob.entity.LittleMaidBaseEntity;
import net.baguchan.littlemaidmob.maidmodel.ModelLittleMaidBase;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Random;

/**
 * ���֐߃��f��
 * �g��2.25�u���b�N��
 */
public class ModelLittleMaid_Beverly7<T extends LittleMaidBaseEntity> extends ModelLittleMaidBase<T> {
    public ModelLittleMaid_Beverly7(){
    }
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

    *//**
     * �R���X�g���N�^�͑S�Čp�������邱��
     *//*
    public ModelLittleMaid_Beverly7(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public float getHeight() {
        return 1.99F;
    }

    @Override
    public float getWidth() {
        return 0.5F;
    }

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
