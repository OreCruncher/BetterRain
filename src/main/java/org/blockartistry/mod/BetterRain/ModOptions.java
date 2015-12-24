/*
 * This file is part of BetterRain, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.BetterRain;

import org.blockartistry.mod.BetterRain.util.ElementRule;
import org.blockartistry.mod.BetterRain.util.MyUtils;
import org.blockartistry.mod.BetterRain.util.ElementRule.Rule;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Configuration;

public final class ModOptions {

	private ModOptions() {
	}

	protected static final String CATEGORY_LOGGING_CONTROL = "logging";
	protected static final String CONFIG_ENABLE_DEBUG_LOGGING = "Enable Debug Logging";
	protected static boolean enableDebugLogging = false;

	protected static final String CONFIG_ENABLE_ONLINE_VERSION_CHECK = "Enable Online Version Check";
	protected static boolean enableVersionChecking = true;

	protected static final String CATEGORY_RAIN = "rain";
	protected static final String CONFIG_RAIN_VOLUME = "Sound Level";
	protected static float soundLevel = 1.0F;
	protected static final String CONFIG_ALWAYS_OVERRIDE_SOUND = "Always Override Sound";
	protected static boolean alwaysOverrideSound = true;
	protected static final String CONFIG_ALLOW_DESERT_DUST = "Desert Dust";
	protected static boolean allowDesertDust = true;

	protected static final String CATEGORY_GENERAL = "general";
	protected static final String CONFIG_DIMENSION_LIST = "Dimensions";
	protected static int[] dimensions = {};
	protected static final String CONFIG_DIMENSION_BLACKLIST = "Black List";
	protected static boolean dimensionListAsBlacklist = true;
	protected static final String CONFIG_MIN_RAIN_STRENGTH = "Default Minimum Rain Strength";
	protected static float defaultMinRainStrength = 0.0F;
	protected static final String CONFIG_MAX_RAIN_STRENGTH = "Default Maximum Rain Strength";
	protected static float defaultMaxRainStrength = 1.0F;
	
	protected static final String CATEGORTY_OVERRIDE = "biomes.override";
	protected static final String CONFIG_DUST_BIOMES = "Dust";
	protected static String dustBiomes = "";
	protected static final String CONFIG_PRECIPITATION_BIOMES = "Precipitation";
	protected static String precipitationBiomes = "";
	protected static final String CONFIG_NONE_BIOMES = "None";
	protected static String noneBiomes = "";
	
	protected static final String CATEGORY_AURORA = "aurora";
	protected static final String CONFIG_AURORA_ENABLED = "Enabled";
	protected static boolean auroraEnable = true;
	protected static final String CONFIG_Y_PLAYER_RELATIVE = "Height Player Relative";
	protected static boolean auroraHeightPlayerRelative = true;
	protected static final String CONFIG_ALLOW_MULTIPLES = "Allow Multiples";
	protected static boolean auroraAllowMultiples = true;
	protected static final String CONFIG_AURORA_BIOME_LIST = "Affected Biomes";
	protected static String auroraAffectedBiomes = "";
	protected static final String CONFIG_AURORA_ANIMATE = "Animate";
	protected static boolean auroraAnimate = true;
	
	public static void load(final Configuration config) {

		// CATEGORY: Logging
		String comment = "Enables/disables debug logging of the mod";
		enableDebugLogging = config.getBoolean(CONFIG_ENABLE_DEBUG_LOGGING, CATEGORY_LOGGING_CONTROL,
				enableDebugLogging, comment);

		comment = "Enables/disables online version checking";
		enableVersionChecking = config.getBoolean(CONFIG_ENABLE_ONLINE_VERSION_CHECK, CATEGORY_LOGGING_CONTROL,
				enableVersionChecking, comment);

		// CATEGORY: Rain
		comment = "Factor to apply to rain sound level to adjust";
		soundLevel = config.getFloat(CONFIG_RAIN_VOLUME, CATEGORY_RAIN, soundLevel, 0.0F, 1.0F, comment);

		comment = "Always override Vanilla rain sound even when dimension is blacklisted";
		alwaysOverrideSound = config.getBoolean(CONFIG_ALWAYS_OVERRIDE_SOUND, CATEGORY_RAIN, alwaysOverrideSound,
				comment);

		comment = "Allow desert dust when raining";
		allowDesertDust = config.getBoolean(CONFIG_ALLOW_DESERT_DUST, CATEGORY_RAIN, allowDesertDust,
				comment);

		// CATEGORY: General
		comment = "Comma separated dimension ID list";
		String temp = config.getString(CONFIG_DIMENSION_LIST, CATEGORY_GENERAL, "1,-1", comment);
		dimensions = MyUtils.splitToInts(temp, ',');

		comment = "Treat dimension ID list as a black list";
		dimensionListAsBlacklist = config.getBoolean(CONFIG_DIMENSION_BLACKLIST, CATEGORY_GENERAL,
				dimensionListAsBlacklist, comment);

		comment = "Default minimum rain strength for a dimension";
		defaultMinRainStrength = MathHelper.clamp_float(config.getFloat(CONFIG_MIN_RAIN_STRENGTH, CATEGORY_GENERAL,
				defaultMinRainStrength, 0.0F, 1.0F, comment), 0.0F, 1.0F);

		comment = "Default maximum rain strength for a dimension";
		defaultMaxRainStrength = MathHelper.clamp_float(config.getFloat(CONFIG_MAX_RAIN_STRENGTH, CATEGORY_GENERAL,
				defaultMaxRainStrength, 0.0F, 1.0F, comment), defaultMinRainStrength, 1.0F);
		
		// CATEGORY: Biome Overrides
		comment = "Comma separated biome names to apply dust weather effect";
		dustBiomes = config.getString(CONFIG_DUST_BIOMES, CATEGORTY_OVERRIDE, dustBiomes, comment);

		comment = "Comma separated biome names to apply rain/snow weather effect";
		precipitationBiomes = config.getString(CONFIG_PRECIPITATION_BIOMES, CATEGORTY_OVERRIDE, precipitationBiomes, comment);

		comment = "Comma separated biome names to apply NO weather effect";
		noneBiomes = config.getString(CONFIG_NONE_BIOMES, CATEGORTY_OVERRIDE, noneBiomes, comment);
		
		// CATEGORY: Aurora
		comment = "Whether to enable Aurora processing on server/client";
		auroraEnable = config.getBoolean(CONFIG_AURORA_ENABLED, CATEGORY_AURORA,
				auroraEnable, comment);

		comment = "true to keep the aurora at a height above player; false to fix it to an altitude";
		auroraHeightPlayerRelative = config.getBoolean(CONFIG_Y_PLAYER_RELATIVE, CATEGORY_AURORA,
				auroraHeightPlayerRelative, comment);

		comment = "Allow Auroras with multiple bands";
		auroraAllowMultiples = config.getBoolean(CONFIG_ALLOW_MULTIPLES, CATEGORY_AURORA,
				auroraAllowMultiples, comment);

		comment = "Comma separated biome names where Auroras can occur";
		auroraAffectedBiomes = config.getString(CONFIG_AURORA_BIOME_LIST, CATEGORY_AURORA, auroraAffectedBiomes, comment);

		comment = "Animate Aurora";
		auroraAnimate = config.getBoolean(CONFIG_AURORA_ANIMATE, CATEGORY_AURORA,
				auroraAnimate, comment);
	}

	public static boolean getEnableDebugLogging() {
		return enableDebugLogging;
	}

	public static boolean getOnlineVersionChecking() {
		return enableVersionChecking;
	}

	public static float getSoundLevel() {
		return soundLevel;
	}

	public static boolean getAlwaysOverrideSound() {
		return alwaysOverrideSound;
	}
	
	public static boolean getAllowDesertDust() {
		return allowDesertDust;
	}

	public static int[] getDimensionList() {
		return dimensions;
	}

	public static boolean getDimensionListAsBlacklist() {
		return dimensionListAsBlacklist;
	}

	public static ElementRule getDimensionRule() {
		return new ElementRule(dimensionListAsBlacklist ? Rule.MUST_NOT_BE_IN : Rule.MUST_BE_IN, dimensions);
	}

	public static float getDefaultMinRainIntensity() {
		return defaultMinRainStrength;
	}

	public static float getDefaultMaxRainIntensity() {
		return defaultMaxRainStrength;
	}
	
	public static String getDustBiomes() {
		return dustBiomes;
	}
	
	public static String getPrecipitationBiomes() {
		return precipitationBiomes;
	}
	
	public static String getNoneBiomes() {
		return noneBiomes;
	}
	
	public static boolean getAuroraHeightPlayerRelative() {
		return auroraHeightPlayerRelative;
	}

	public static boolean getAuroraAllowMultiples() {
		return auroraAllowMultiples;
	}

	public static String getAuroraAffectedBiomes() {
		return auroraAffectedBiomes;
	}
	
	public static boolean getAuroraEnable() {
		return auroraEnable;
	}
	
	public static boolean getAuroraAnimate() {
		return auroraAnimate;
	}
}
