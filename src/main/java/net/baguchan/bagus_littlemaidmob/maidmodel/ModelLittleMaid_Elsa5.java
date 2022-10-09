package net.baguchan.bagus_littlemaidmob.maidmodel;

import net.baguchan.bagus_littlemaidmob.entity.MultiModelEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ModelLittleMaid_Elsa5<T extends MultiModelEntity> extends ModelLittleMaidBase<T> {

	//added fields
	public ModelPart right_eye;
	public ModelPart left_eye;
	public ModelPart hat;

	//TODO Sorry!
	/*public ModelPart Ponytail;
	public ModelPart BunchR;
	public ModelPart BunchL;
	public ModelPart hemSkirt;*/
	@Override
	public void initModel(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("head");
		this.hat = this.head.getChild("hat");
		this.right_eye = this.head.getChild("right_eye");
		this.left_eye = this.head.getChild("left_eye");
		this.body = this.root.getChild("body");
		this.skirt = this.body.getChild("skirt");
		this.left_leg = this.root.getChild("left_leg");
		this.right_leg = this.root.getChild("right_leg");
		this.left_hand = this.root.getChild("left_hand");
		this.right_hand = this.root.getChild("right_hand");
	}

	@Override
	public LayerDefinition createBodyLayer(float size) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 29).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F + size)), PartPose.offset(-1.5F, -11.0F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 29).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F + size)).mirror(false), PartPose.offset(1.5F, -11.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -9.0F, -2.0F, 6.0F, 9.0F, 4.0F, new CubeDeformation(0.0F + size)), PartPose.offset(0.0F, -11.0F, 0.0F));

		PartDefinition skirt = body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(36, 40).addBox(-4.0F, 0.0F, -3.25F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.0F + size)), PartPose.offset(0.0F, -4.0F, -0.25F));

		PartDefinition skirt_2 = skirt.addOrReplaceChild("skirt_2", CubeListBuilder.create().texOffs(34, 50).addBox(-4.0F, -0.5F, -4.0F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F + size)), PartPose.offset(0.0F, 4.5F, 0.25F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F + size)), PartPose.offset(0.0F, -20.0F, 0.0F));

		PartDefinition right_eye = head.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(1, 4).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F + size)), PartPose.offset(-2.0F, -1.0F, -4.05F));

		PartDefinition left_eye = head.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(3, 4).mirror().addBox(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F + size)).mirror(false), PartPose.offset(2.0F, -1.0F, -4.05F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.01F + size)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_hand = root.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(20, 24).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F + size)), PartPose.offset(-3.0F, -19.0F, 0.0F));

		PartDefinition left_hand = root.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(28, 24).addBox(0.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F + size)), PartPose.offset(3.0F, -19.0F, 0.0F));

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
		/*if (0 > mh_sin(f3 * 0.05F) + mh_sin(f3 * 0.13F) + mh_sin(f3 * 0.7F) + 2.55F) {
			right_eye.visible = false;
			left_eye.visible = false;
		} else {
			right_eye.visible = true;
			left_eye.visible = true;
		}*/
	}
}
