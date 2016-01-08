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
	protected static final String CONFIG_ALLOW_DESERT_FOG = "Desert Fog";
	protected static boolean allowDesertFog = true;
	protected static final String CONFIG_DESERT_FOG_FACTOR = "Desert Fog Factor";
	protected static float desertFogFactor = 1.0F;
	protected static final String CONFIG_ENABLE_ELEVATION_HAZE = "Elevation Haze";
	protected static boolean enableElevationHaze = true;
	protected static final String CONFIG_ELEVATION_HAZE_FACTOR = "Elevation Haze Factor";
	protected static float elevationHazeFactor = 1.0F;
	protected static final String CONFIG_ELEVATION_OVERRIDES = "Elevation Overrides";
	protected static String[] elevationOverrides = {};

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
	protected static final String CONFIG_PLAYER_FIXED_HEIGHT = "Player Fixed Height";
	protected static float playerFixedHeight = 64.0F;
	protected static final String CONFIG_MULTIPLE_BANDS = "Multiple Bands";
	protected static boolean auroraMultipleBands = true;
	protected static final String CONFIG_TRIGGER_BIOME_LIST = "Trigger Biomes";
	protected static String auroraTriggerBiomes = "";
	protected static final String CONFIG_AURORA_ANIMATE = "Animate";
	protected static boolean auroraAnimate = true;

	protected static final String CATEGORY_JETS = "jets";
	protected static final String CONFIG_FIREJETS_ENABLED = "Firejets Enabled";
	protected static boolean enableFireJets = true;
	protected static final String CONFIG_FIREJET_CHANCE = "Firejet Spawn Chance";
	protected static int fireJetsSpawnChance = 1800;
	protected static final String CONFIG_BUBBLEJETS_ENABLED = "Bubblejets Enabled";
	protected static boolean enableBubbleJets = true;
	protected static final String CONFIG_BUBBLEJETS_CHANCE = "Bubblejet Spawn Chance";
	protected static int bubbleJetSpawnChance = 1800;

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
		allowDesertDust = config.getBoolean(CONFIG_ALLOW_DESERT_DUST, CATEGORY_RAIN, allowDesertDust, comment);

		comment = "Allow desert fog when raining";
		allowDesertFog = config.getBoolean(CONFIG_ALLOW_DESERT_FOG, CATEGORY_RAIN, allowDesertFog, comment);

		comment = "Visibility factor to apply to desert fog (higher is thicker)";
		desertFogFactor = config.getFloat(CONFIG_DESERT_FOG_FACTOR, CATEGORY_RAIN, desertFogFactor, 0.0F, 5.0F,
				comment);

		comment = "Higher the player elevation the more haze that is experienced";
		enableElevationHaze = config.getBoolean(CONFIG_ENABLE_ELEVATION_HAZE, CATEGORY_RAIN, enableElevationHaze,
				comment);

		comment = "Visibility factor to apply to elevation haze (higher is thicker)";
		elevationHazeFactor = config.getFloat(CONFIG_ELEVATION_HAZE_FACTOR, CATEGORY_RAIN, elevationHazeFactor, 0.0F,
				5.0F, comment);

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

		comment = "Elevation override for dimensions if needed (dimension,sea level,sky height)";
		elevationOverrides = config.getStringList(CONFIG_ELEVATION_OVERRIDES, CATEGORY_GENERAL, elevationOverrides,
				comment);

		// CATEGORY: Biome Overrides
		comment = "Comma separated biome names to apply dust weather effect";
		dustBiomes = config.getString(CONFIG_DUST_BIOMES, CATEGORTY_OVERRIDE, dustBiomes, comment);

		comment = "Comma separated biome names to apply rain/snow weather effect";
		precipitationBiomes = config.getString(CONFIG_PRECIPITATION_BIOMES, CATEGORTY_OVERRIDE, precipitationBiomes,
				comment);

		comment = "Comma separated biome names to apply NO weather effect";
		noneBiomes = config.getString(CONFIG_NONE_BIOMES, CATEGORTY_OVERRIDE, noneBiomes, comment);

		// CATEGORY: Aurora
		comment = "Whether to enable Aurora processing on server/client";
		auroraEnable = config.getBoolean(CONFIG_AURORA_ENABLED, CATEGORY_AURORA, auroraEnable, comment);

		comment = "true to keep the aurora at a height above player; false to fix it to an altitude";
		auroraHeightPlayerRelative = config.getBoolean(CONFIG_Y_PLAYER_RELATIVE, CATEGORY_AURORA,
				auroraHeightPlayerRelative, comment);

		comment = "Number of blocks to say fixed above player if Aurora is player relative";
		playerFixedHeight = config.getFloat(CONFIG_PLAYER_FIXED_HEIGHT, CATEGORY_AURORA, playerFixedHeight, 16.0F,
				2048.0F, comment);

		comment = "Allow Auroras with multiple bands";
		auroraMultipleBands = config.getBoolean(CONFIG_MULTIPLE_BANDS, CATEGORY_AURORA, auroraMultipleBands, comment);

		comment = "Comma separated biome names where Auroras can be triggered";
		auroraTriggerBiomes = config.getString(CONFIG_TRIGGER_BIOME_LIST, CATEGORY_AURORA, auroraTriggerBiomes,
				comment);

		comment = "Animate Aurora";
		auroraAnimate = config.getBoolean(CONFIG_AURORA_ANIMATE, CATEGORY_AURORA, auroraAnimate, comment);

		// CATEGORY: Jets
		comment = "Enable firejet spawn on lava blocks";
		enableFireJets = config.getBoolean(CONFIG_FIREJETS_ENABLED, CATEGORY_JETS, enableFireJets, comment);

		comment = "1-in-N chance per random tick a firejet will spawn on lava blocks";
		fireJetsSpawnChance = config.getInt(CONFIG_FIREJET_CHANCE, CATEGORY_JETS, fireJetsSpawnChance, 300,
				Integer.MAX_VALUE, comment);

		comment = "Enable bubblejet spawn in water";
		enableBubbleJets = config.getBoolean(CONFIG_BUBBLEJETS_ENABLED, CATEGORY_JETS, enableBubbleJets, comment);

		comment = "1-in-N chance per random tick a bubblejet will spawn in water";
		bubbleJetSpawnChance = config.getInt(CONFIG_BUBBLEJETS_CHANCE, CATEGORY_JETS, bubbleJetSpawnChance, 300,
				Integer.MAX_VALUE, comment);
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

	public static boolean getAllowDesertFog() {
		return allowDesertFog;
	}

	public static float getDesertFogFactor() {
		return desertFogFactor;
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

	public static boolean getAuroraMultipleBands() {
		return auroraMultipleBands;
	}

	public static String getAuroraTriggerBiomes() {
		return auroraTriggerBiomes;
	}

	public static boolean getAuroraEnable() {
		return auroraEnable;
	}

	public static boolean getAuroraAnimate() {
		return auroraAnimate;
	}
	
	public static float getPlayerFixedHeight() {
		return playerFixedHeight;
	}

	public static boolean getEnableElevationHaze() {
		return enableElevationHaze;
	}

	public static float getElevationHazeFactor() {
		return elevationHazeFactor;
	}

	public static String[] getElevationOverrides() {
		return elevationOverrides;
	}

	public static boolean getEnableFireJets() {
		return enableFireJets;
	}

	public static int getFireJetsSpawnChance() {
		return fireJetsSpawnChance;
	}
	
	public static boolean getEnableBubbleJets() {
		return enableBubbleJets;
	}
	
	public static int getBubbleJetSpawnChance() {
		return bubbleJetSpawnChance;
	}
}
