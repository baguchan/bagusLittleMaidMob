package net.baguchan.bagus_littlemaidmob.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.baguchan.bagus_littlemaidmob.entity.MultiModelEntity;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NullModel<T extends MultiModelEntity & IHasMultiModel> extends ModelBase<T> {
    private T entity;

    public NullModel() {
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {

    }

    @Override
    public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {

    }

    @Override
    public ModelPart root() {
        return null;
    }

    @Override
    public float[] getArmorModelsSize() {
        return new float[0];
    }

    @Override
    public float getInnerArmorSize() {
        return 0;
    }

    @Override
    public float getOuterArmorSize() {
        return 0;
    }

    @Override
    public float getWidth(T caps, Pose pose) {
        return 0;
    }

    @Override
    public float getHeight(T caps, Pose pose) {
        return 0;
    }

    @Override
    public float getEyeHeight(T caps, Pose pose) {
        return 0;
    }

    @Override
    public float getyOffset(T caps) {
        return 0;
    }

    @Override
    public float getMountedYOffset(T caps) {
        return 0;
    }

    @Override
    public float getLeashOffset(T caps) {
        return 0;
    }

    @Override
    public void showAllParts(T caps) {

    }

    @Override
    public int showArmorParts(int parts, int index) {
        return 0;
    }
}