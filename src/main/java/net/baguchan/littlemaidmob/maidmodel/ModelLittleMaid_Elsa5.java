package net.baguchan.littlemaidmob.maidmodel;

import net.baguchan.littlemaidmob.entity.LittleMaidBaseEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.Random;

/**
 * �x�[�V�b�N���f��
 * �g��1.75�u���b�N��
 */
public class ModelLittleMaid_Elsa5<T extends LittleMaidBaseEntity> extends ModelLittleMaidBase<T> {

	//added fields
	public ModelPart eyeR;
	public ModelPart eyeL;
	public ModelPart hat;
	//TODO Sorry!
	/*public ModelPart Ponytail;
	public ModelPart BunchR;
	public ModelPart BunchL;
	public ModelPart hemSkirt;*/
	public ModelLittleMaid_Elsa5() {
	}

	@Override
	public void initModel(ModelPart root) {
		super.initModel(root);
		this.head = this.root.getChild("head");
		this.hat = this.head.getChild("hat");
		this.eyeR = this.head.getChild("eyeR");
		this.eyeL = this.head.getChild("eyeL");
		this.body = this.root.getChild("body");
		this.skirt = this.body.getChild("skirt");
		this.left_leg = this.root.getChild("left_leg");
		this.right_leg = this.root.getChild("right_leg");
		this.left_arm = this.root.getChild("left_arm");
		this.right_arm = this.root.getChild("right_arm");
	}

	@Override
	public LayerDefinition createBodyLayer(float size) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 29).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F + size)), PartPose.offset(-1.5F, -12.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F + size)), PartPose.offset(0.0F, -21.0F, 0.0F));

		PartDefinition eyeL = head.addOrReplaceChild("eyeL", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.1F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F + size)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eyeR = head.addOrReplaceChild("eyeR", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4.0F, -8.0F, -4.1F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F + size)).mirror(false), PartPose.offset(4.0F, 0.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.01F + size)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-3.0F, 0.0F, -2.0F, 6.0F, 9.0F, 4.0F, new CubeDeformation(0.0F + size)).mirror(false), PartPose.offset(0.0F, -21.0F, 0.0F));

		PartDefinition skirt = body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(34, 50).mirror().addBox(-4.0F, -2.25F, -4.0F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F + size)).mirror(false), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(20, 24).addBox(0.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F + size)), PartPose.offset(2.75F, -21.0F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 29).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F + size)).mirror(false), PartPose.offset(1.5F, -12.0F, 0.0F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(28, 24).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F + size)), PartPose.offset(-3.0F, -21.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public float getHeight()
	{
		return 1.75F;
	}

	@Override
	public float getWidth()
	{
		return 0.5F;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float f3 = entity.tickCount + ageInTicks + entity.getId();
		// 目パチ
		if( 0 > mh_sin(f3 * 0.05F) + mh_sin(f3 * 0.13F) + mh_sin(f3 * 0.7F) + 2.55F) {
			eyeR.visible = false;
			eyeL.visible = false;
		} else {
			eyeR.visible = true;
			eyeL.visible = true;
		}

		this.hat.copyFrom(this.head);
	}
}