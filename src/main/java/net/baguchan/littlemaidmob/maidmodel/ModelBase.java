package net.baguchan.littlemaidmob.maidmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.baguchan.littlemaidmob.entity.LittleMaidBaseEntity;
import net.baguchan.littlemaidmob.entity.MultiModelEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TextureOffsetは死んだ。
public abstract class ModelBase<T extends MultiModelEntity> extends HierarchicalModel<T> implements IModelBase, IMultiModel<T> {

    public static final float PI = (float) Math.PI;

    /*public ModelPart getRandomModelBox(Random par1Random) {
        return lmr;
    }*/


    // Mthトンネル関数群

    public static float mh_sin(float f) {
        return Mth.sin(f);
    }

    public static float mh_cos(float f) {
        return Mth.cos(f);
    }

    public static float mh_sqrt(float f) {
        return Mth.sqrt(f);
    }

    public static float mh_sqrt(double d) {
        return Mth.sqrt((float) d);
    }

    public static int mh_floor(float f) {
        return Mth.floor(f);
    }

    public static int mh_floor(double d) {
        return Mth.floor(d);
    }

    public static long mh_floor_long(double d) {
        return Mth.floor(d);
    }

    public static float mh_abs(float f) {
        return Mth.abs(f);
    }

    public static double mh_abs_max(double d, double d1) {
        return Mth.absMax(d, d1);
    }

    public static boolean mh_stringNullOrLengthZero(String s) {
        return s == null || s.equals("");
    }

    public static int mh_getRandomIntegerInRange(Random random, int minimum, int maximum) {
        return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
    }
}
