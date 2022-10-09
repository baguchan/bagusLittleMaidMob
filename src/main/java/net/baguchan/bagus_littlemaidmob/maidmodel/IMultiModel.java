package net.baguchan.bagus_littlemaidmob.maidmodel;

import net.baguchan.bagus_littlemaidmob.entity.MultiModelEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
/**
 * マルチモデル用識別インターフェース
 */
public interface IMultiModel<T extends MultiModelEntity> {

/*
    */
/**
     *
     *//*

    void setupTransform(EntityLittleMaid caps, MMMatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta);

    */
/**
     * @param caps         情報を受け取るやつ
     * @param limbAngle    移動量が加算されつづけた値
     * @param limbDistance 1tickの移動量
     * @param tickDelta    tick間の現在フレームの位置する割合
     *//*

    void animateModel(EntityLittleMaid caps, float limbAngle, float limbDistance, float tickDelta);

    */
/**
     * @param caps              情報を受け取るやつ
     * @param limbAngle         移動量が加算されつづけた値
     * @param limbDistance      1tickの移動量
     * @param animationProgress エンティティの存在した期間(tick) + tick間の現在フレームの位置する割合
     * @param headYaw           エンティティが向いている方向
     * @param headPitch         エンティティが向いている方向
     *//*

    void setAngles(EntityLittleMaid caps, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch);

    */
/**
     * 描画
     *//*

    void render(MMRenderContext context);

    */
/**
     * アイテム保持位置の調整
     *//*

    void adjustHandItem(MMMatrixStack matrices, boolean isLeft);
*/

    /**
     * 内側防具サイズ
     */
    float getInnerArmorSize();

    /**
     * 外側防具サイズ
     */
    float getOuterArmorSize();

    /**
     * 横幅
     */
    float getWidth(T caps, Pose pose);

    /**
     * 身長
     */
    float getHeight(T caps, Pose pose);

    /**
     * 目の高さ
     */
    float getEyeHeight(T caps, Pose pose);

    /**
     * モデルのYオフセット
     */
    float getyOffset(T caps);

    /**
     * 上に乗せる時のオフセット高
     */
    float getMountedYOffset(T caps);

    /**
     * ロープの取り付け位置調整用
     */
    float getLeashOffset(T caps);

    /**
     * パーツをすべて表示する
     */
    void showAllParts(T caps);

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
    int showArmorParts(int parts, int index);

    //計算関数

    static float sin(float value) {
        return Mth.sin(value);
    }

    static float cos(float value) {
        return Mth.cos(value);
    }

    static float sqrt(float value) {
        return Mth.sqrt(value);
    }

    static float floor(float value) {
        return Mth.floor(value);
    }

    static float ceil(float value) {
        return Mth.ceil(value);
    }

    static float abs(float value) {
        return Mth.abs(value);
    }

    static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (max < value) return max;
        return value;
    }

    static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

}
