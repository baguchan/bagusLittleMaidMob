package net.baguchan.littlemaidmob.client.util;

import net.baguchan.littlemaidmob.LittleMaidMod;
import net.baguchan.littlemaidmob.entity.compound.IHasMultiModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModelLayerUtils {

	public static ModelLayerLocation setModelLayerID(String id, IHasMultiModel.Layer layer){
		String realID = id + "_" + layer.name().toLowerCase();
		return new ModelLayerLocation(new ResourceLocation(LittleMaidMod.MODID, realID), realID);
	}

	public static float setSize(IHasMultiModel.Layer layer){
		return layer == IHasMultiModel.Layer.SKIN ? 0.0F : 0.15F;
	}
}
