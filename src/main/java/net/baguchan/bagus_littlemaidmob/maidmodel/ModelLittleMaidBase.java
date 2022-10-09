package net.baguchan.bagus_littlemaidmob.maidmodel;

import net.baguchan.bagus_littlemaidmob.client.animation.MaidAnimation;
import net.baguchan.bagus_littlemaidmob.entity.LittleMaidBaseEntity;
import net.baguchan.bagus_littlemaidmob.entity.MultiModelEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * LMM用に最適化
 */
public abstract class ModelLittleMaidBase<T extends MultiModelEntity> extends ModelMultiBase<T> {

	//fields
	public ModelPart torso;
	public ModelPart neck;
	public ModelPart head;
	public ModelPart right_hand;
	public ModelPart left_hand;
	public ModelPart body;
	public ModelPart pelvic;
	public ModelPart right_leg;
	public ModelPart left_leg;
	public ModelPart skirt;

	//x.1.0での追加
	public float roll;
	public float leaningPitch;

	@Override
	public void initModel(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("head");
		this.body = this.root.getChild("body");
		this.skirt = this.body.getChild("skirt");
		this.left_leg = this.root.getChild("left_leg");
		this.right_leg = this.root.getChild("right_leg");
		this.left_hand = this.root.getChild("left_hand");
		this.right_hand = this.root.getChild("right_hand");
	}
	//x.1.0での追加ここまで

    @Override
    public LayerDefinition createBodyLayer(float size) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 19).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(size)), PartPose.offset(-1.5F, -10.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(size)), PartPose.offset(0.0F, -15.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(32, 8).mirror().addBox(-3.0F, 0.0F, -2.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(size)).mirror(false), PartPose.offset(0.0F, -15.0F, 0.0F));

        PartDefinition skirt = body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-3.75F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(size)).mirror(false), PartPose.offset(0.0F, 5.0F, 0.0F));

		PartDefinition left_hand = root.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(size)), PartPose.offset(2.0F, -14.0F, 0.0F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 19).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(size)).mirror(false), PartPose.offset(1.5F, -10.0F, 0.0F));

		PartDefinition right_hand = root.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(size)), PartPose.offset(-3.0F, -14.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public float[] getArmorModelsSize() {
        return new float[] {0.1F, 0.5F};
    }

    @Override
    public float getHeight() {
        return 1.35F;
    }

    @Override
    public float getWidth() {
        return 0.5F;
    }

    @Override
    public float getyOffset() {
        return getHeight() * 0.9F;
    }

    @Override
    public float getMountedYOffset() {
        return 0.35F;
    }

    public float lerp(float delta, float a, float b) {
        return delta * (b - a) + a;
    }

    @Override
    public void showAllParts() {
		// 表示制限を解除してすべての部品を表示
		head.visible = true;
		body.visible = true;
		skirt.visible = true;
		right_hand.visible = true;
		left_hand.visible = true;
		right_leg.visible = true;
		left_leg.visible = true;
	}

    @Override
    public int showArmorParts(int parts, int index) {
		// 鎧の表示用
		boolean f;
		// 兜
		f = parts == 3;
		head.visible = f;
		// 鎧
		f = parts == 2;
		body.visible = f;
		right_hand.visible = f;
		left_hand.visible = f;
		// 脚甲
		f = parts == 1;
		skirt.visible = f;
		// 臑当
		f = parts == 0;
		right_leg.visible = f;
		left_leg.visible = f;

		return -1;
	}
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.right_hand.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		this.left_hand.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		if (entity instanceof LittleMaidBaseEntity && LittleMaidBaseEntity.MoveState.get(((LittleMaidBaseEntity) entity).getMovingState()) == LittleMaidBaseEntity.MoveState.WAITING) {
			this.right_hand.yRot = -0.4F;
			this.left_hand.yRot = 0.4F;
			this.right_hand.xRot = -0.8F;
			this.left_hand.xRot = -0.8F;
		} else {
			this.right_hand.yRot = 0;
			this.left_hand.yRot = 0;
		}
		if (entity instanceof LittleMaidBaseEntity) {
			this.animate(((LittleMaidBaseEntity) entity).eyeBlinkAnimation, MaidAnimation.EYE_BLINK, ageInTicks);
		}
	}
}
