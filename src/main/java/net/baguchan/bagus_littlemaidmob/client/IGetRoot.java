package net.baguchan.bagus_littlemaidmob.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.Map;

public interface IGetRoot {

	Map<ModelLayerLocation, LayerDefinition> getRoot();

	void setRoot(Map<ModelLayerLocation, LayerDefinition> map);
}
