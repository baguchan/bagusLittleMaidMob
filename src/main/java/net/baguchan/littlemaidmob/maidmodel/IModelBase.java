package net.baguchan.littlemaidmob.maidmodel;

/**
 * マルチモデル用識別クラス<br>
 * インターフェースでもいいような気がする。
 *
 */
public interface IModelBase {

	/**
	 * アーマーモデルのサイズを返す。
	 * サイズは内側のものから。
	 */
	public abstract float[] getArmorModelsSize();

}
