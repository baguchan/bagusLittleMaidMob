package net.baguchan.bagus_littlemaidmob.client.util;

import net.baguchan.bagus_littlemaidmob.LittleMaidMod;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModelLayerUtils {

	public static ModelLayerLocation setModelLayerID(String id, IHasMultiModel.Layer layer){
		String realID = id.toLowerCase() + "_" + layer.name().toLowerCase();
		return new ModelLayerLocation(new ResourceLocation(LittleMaidMod.MODID, realID), realID);
	}

	public static float setSize(IHasMultiModel.Layer layer){
		return layer == IHasMultiModel.Layer.SKIN ? 0.0F : 0.15F;
	}
}
