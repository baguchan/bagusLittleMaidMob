package net.baguchan.littlemaidmob;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class LittleMaidConfig {

	public static boolean isDebugMode = true;
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static {
		Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Common {
		public final ForgeConfigSpec.BooleanValue isDebugMode;

		public Common(ForgeConfigSpec.Builder builder) {
			isDebugMode = builder
					.translation(LittleMaidMod.MODID + ".config.debugMode")
					.comment("Enable Debug Mode")
					.define("Debug Mode"
							, false);
		}
	}

}