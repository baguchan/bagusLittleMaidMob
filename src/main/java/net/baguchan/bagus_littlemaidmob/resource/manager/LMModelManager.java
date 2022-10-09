package net.baguchan.bagus_littlemaidmob.resource.manager;

import com.google.common.collect.ImmutableMap;
import net.baguchan.bagus_littlemaidmob.LittleMaidConfig;
import net.baguchan.bagus_littlemaidmob.client.IGetRoot;
import net.baguchan.bagus_littlemaidmob.client.util.ModelLayerUtils;
import net.baguchan.bagus_littlemaidmob.entity.compound.IHasMultiModel;
import net.baguchan.bagus_littlemaidmob.maidmodel.IMultiModel;
import net.baguchan.bagus_littlemaidmob.maidmodel.ModelMultiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LMModelManager {
    public static final LMModelManager INSTANCE = new LMModelManager();
    private static final Logger LOGGER = LogManager.getLogger();
    private IMultiModel defaultModel;
    private final Map<String, ModelHolder> models = new HashMap<>();

    public void addModel(String modelName, Class<? extends ModelMultiBase> modelClass) {
        EntityModelSet entityModelSet = Minecraft.getInstance().getEntityModels();
        try {

            //ごり押しでEntityModelSetのrootをその場で書き換えながらModelPartを書き込む
            Constructor<? extends ModelMultiBase> constructor = modelClass.getConstructor();
            ModelMultiBase skin = constructor.newInstance();
            float size = ModelLayerUtils.setSize(IHasMultiModel.Layer.SKIN);
            ForgeHooksClient.registerLayerDefinition(ModelLayerUtils.setModelLayerID(modelName, IHasMultiModel.Layer.SKIN), () -> skin.createBodyLayer(size));
            ((IGetRoot) entityModelSet).setRoot(ImmutableMap.copyOf(LayerDefinitions.createRoots()));
            skin.init(entityModelSet.bakeLayer(ModelLayerUtils.setModelLayerID(modelName, IHasMultiModel.Layer.SKIN)));

            ModelMultiBase inner = constructor.newInstance();
            float size2 = ModelLayerUtils.setSize(IHasMultiModel.Layer.INNER);

            ForgeHooksClient.registerLayerDefinition(ModelLayerUtils.setModelLayerID(modelName, IHasMultiModel.Layer.INNER), () -> inner.createBodyLayer(size2));
            ((IGetRoot) entityModelSet).setRoot(ImmutableMap.copyOf(LayerDefinitions.createRoots()));
            inner.init(entityModelSet.bakeLayer(ModelLayerUtils.setModelLayerID(modelName, IHasMultiModel.Layer.INNER)));

            ModelMultiBase outer = constructor.newInstance();

            float size3 = ModelLayerUtils.setSize(IHasMultiModel.Layer.OUTER);

            ForgeHooksClient.registerLayerDefinition(ModelLayerUtils.setModelLayerID(modelName, IHasMultiModel.Layer.OUTER), () -> outer.createBodyLayer(size3));
            ((IGetRoot) entityModelSet).setRoot(ImmutableMap.copyOf(LayerDefinitions.createRoots()));
            outer.init(entityModelSet.bakeLayer(ModelLayerUtils.setModelLayerID(modelName, IHasMultiModel.Layer.OUTER)));

            models.put(modelName.toLowerCase(), new ModelHolder(skin, inner, outer));
        } catch (Exception e) {
            LOGGER.debug("インスタンス化に失敗しました。抽象クラスまたは非対応のモデルである可能性があります。 : " + modelClass);
            e.printStackTrace();
            return;
        }
        if (LittleMaidConfig.isDebugMode) {
            LOGGER.debug("Loaded Model : " + modelClass);
        }
    }

    public void addModel(String modelName, IMultiModel skin, IMultiModel inner, IMultiModel outer) {
        models.put(modelName.toLowerCase(), new ModelHolder(skin, inner, outer));
    }

    public boolean hasModel(String modelName) {
        return models.get(modelName.toLowerCase()) != null;
    }

    public Optional<IMultiModel> getModel(String modelName, IHasMultiModel.Layer layer) {
        ModelHolder modelHolder = models.get(modelName.toLowerCase());
        if (modelHolder == null) return Optional.empty();
        IMultiModel model = modelHolder.getModel(layer);
        return Optional.of(model);
    }

    public IMultiModel getOrDefaultModel(String modelName, IHasMultiModel.Layer layer) {
        ModelHolder modelHolder = models.get(modelName.toLowerCase());
        if (modelHolder == null) return getDefaultModel();
        return modelHolder.getModel(layer);
    }

    public void setDefaultModel(IMultiModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    public IMultiModel getDefaultModel() {
        return defaultModel;
    }

    public Map<String, ModelHolder> getModels() {
        return models;
    }

    public static class ModelHolder {
        private final IMultiModel skin;
        private final IMultiModel inner;
        private final IMultiModel outer;

        public ModelHolder(IMultiModel skin, IMultiModel inner, IMultiModel outer) {
            this.skin = skin;
            this.inner = inner;
            this.outer = outer;
            if (skin == null || inner == null || outer == null) {
                throw new IllegalArgumentException("ModelHolderはnull不許容です");
            }
        }

        public IMultiModel getModel(IHasMultiModel.Layer layer) {
            switch (layer) {
                case SKIN:
                    return skin;
                case INNER:
                    return inner;
                case OUTER:
                    return outer;
                default:
                    throw new IllegalStateException();
            }
        }
    }
}
