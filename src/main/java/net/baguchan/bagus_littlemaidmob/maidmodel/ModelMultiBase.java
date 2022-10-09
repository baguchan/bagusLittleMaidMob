package net.baguchan.bagus_littlemaidmob.maidmodel;

import net.baguchan.bagus_littlemaidmob.entity.MultiModelEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Pose;

/**
 * マルチモデル用の基本クラス、これを継承していればマルチモデルとして使用できる。
 * 新しく書き換えたため、一部おかしな点もあるかも
 * 継承クラスではなくなったため、直接的な互換性はない。
 */
public abstract class ModelMultiBase<T extends MultiModelEntity> extends ModelBase<T>{

    public ModelPart root;

    public void init(ModelPart root){
        this.initModel(root);
    }

    protected void initModel(ModelPart root){
        this.root = root.getChild("root");
    }

    @Override
    public ModelPart root() {
        return root;
    }

    public abstract LayerDefinition createBodyLayer(float size);


    /**
     * モデル指定詞に依らずに使用するテクスチャパック名。
     * 一つのテクスチャに複数のモデルを割り当てる時に使う。
     *
     * @return
     */
    public String getUsingTexture() {
        return null;
    }
    @Override
    public float getInnerArmorSize() {
        return getArmorModelsSize()[0];
    }

    @Override
    public float getOuterArmorSize() {
        return getArmorModelsSize()[1];
    }

    public float getEyeHeight(T caps) {
        return this.getEyeHeight(caps, Pose.STANDING);
    }

    @Override
    public float getEyeHeight(T caps, Pose pose) {
        return getHeight(caps, pose) * 0.85F;
    }

    /**
     * 身長
     */
    @Deprecated
    public abstract float getHeight();

    /**
     * 身長
     */
    public float getHeight(T pEntityCaps) {
        return getHeight();
    }

    /**
     * 身長
     */
    @Override
    public float getHeight(T pEntityCaps, Pose pose) {
        if (pose == Pose.FALL_FLYING || pose == Pose.SWIMMING || pose == Pose.SPIN_ATTACK) {
            return Math.min(getHeight(pEntityCaps), getWidth(pEntityCaps, pose));
        } else if (pose == Pose.SLEEPING || pose == Pose.DYING) {
            return 0.2f;
        } else if (pose == Pose.CROUCHING) {
            return Math.max(0.2f, getHeight(pEntityCaps) - 0.3f);
        }
        return getHeight(pEntityCaps);
    }

    /**
     * 横幅
     */
    @Deprecated
    public abstract float getWidth();

    /**
     * 横幅
     */
    public float getWidth(T pEntityCaps) {
        return getWidth();
    }

    /**
     * 横幅
     */
    public float getWidth(T pEntityCaps, Pose pose) {
        if (pose == Pose.SLEEPING || pose == Pose.DYING) {
            return 0.2f;
        }
        return getWidth();
    }

    /**
     * モデルのYオフセット
     */
    @Deprecated
    public abstract float getyOffset();

    /**
     * モデルのYオフセット
     */
    public float getyOffset(T pEntityCaps) {
        return getyOffset();
    }

    /**
     * 上に乗せる時のオフセット高
     */
    @Deprecated
    public abstract float getMountedYOffset();

    /**
     * 上に乗せる時のオフセット高
     */
    public float getMountedYOffset(T pEntityCaps) {
        return getMountedYOffset();
    }

    /**
     * ロープの取り付け位置調整用
     *
     * @return
     */
    public float getLeashOffset(T pEntityCaps) {
        return 0.4F;
    }

    /**
     * アイテムを持っているときに手を前に出すかどうか。
     */
    @Deprecated
    public boolean isItemHolder() {
        return false;
    }

    /**
     * アイテムを持っているときに手を前に出すかどうか。
     */
    public boolean isItemHolder(T pEntityCaps) {
        return isItemHolder();
    }

    /**
     * 表示すべきすべての部品
     */
    public void showAllParts() {
    }

    /**
     * 表示すべきすべての部品
     */
    public void showAllParts(T pEntityCaps) {
        showAllParts();
    }

    /**
     * 部位ごとの装甲表示。
     *
     * @param parts 3:頭部。
     *              2:胴部。
     *              1:脚部
     *              0:足部
     * @param index 0:inner
     *              1:outer
     * @return 戻り値は基本 -1
     */
    public int showArmorParts(int parts, int index) {
        return -1;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}