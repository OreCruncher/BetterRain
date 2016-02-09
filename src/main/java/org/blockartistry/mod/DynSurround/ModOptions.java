/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.DynSurround;

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
	protected static final String CONFIG_RESET_RAIN_ON_SLEEP = "Reset Rain on Sleep";
	protected static boolean resetRainOnSleep = true;

	protected static final String CATEGORY_FOG = "fog";
	protected static final String CONFIG_ALLOW_DESERT_FOG = "Desert Fog";
	protected static boolean allowDesertFog = true;
	protected static final String CONFIG_DESERT_FOG_FACTOR = "Desert Fog Factor";
	protected static float desertFogFactor = 1.0F;
	protected static final String CONFIG_ENABLE_ELEVATION_HAZE = "Elevation Haze";
	protected static boolean enableElevationHaze = true;
	protected static final String CONFIG_ELEVATION_HAZE_FACTOR = "Elevation Haze Factor";
	protected static float elevationHazeFactor = 1.0F;
	protected static final String CONFIG_ENABLE_BIOME_FOG = "Biome Fog";
	protected static boolean enableBiomeFog = true;
	protected static final String CONFIG_BIOME_FOG_FACTOR = "Biome Fog Factor";
	protected static float biomeFogFactor = 1.0F;

	protected static final String CATEGORY_GENERAL = "general";
	protected static final String CONFIG_MIN_RAIN_STRENGTH = "Default Minimum Rain Strength";
	protected static float defaultMinRainStrength = 0.0F;
	protected static final String CONFIG_MAX_RAIN_STRENGTH = "Default Maximum Rain Strength";
	protected static float defaultMaxRainStrength = 1.0F;
	protected static final String CONFIG_FX_RANGE = "Special Effect Range";
	protected static int specialEffectRange = 16;
	protected static final String CONFIG_FANCY_CLOUD_HANDLING = "Fancy Cloud Handling";
	protected static boolean enableFancyCloudHandling = true;

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
	protected static final String CONFIG_AURORA_SPAWN_OFFSET = "Spawn Offset";
	protected static int auroraSpawnOffset = 150;

	protected static final String CATEGORY_BIOMES = "biomes";
	protected static final String CONFIG_BIOME_CONFIG_FILES = "Config Files";
	protected static String[] biomeConfigFiles = {};

	protected static final String CATEGORY_DIMENSIONS = "dimensions";
	protected static final String CONFIG_DIMENSION_CONFIG_FILES = "Config Files";
	protected static String[] dimensionConfigFiles = {};

	protected static final String CATEGORY_BLOCK = "block";
	protected static final String CONFIG_BLOCK_CONFIG_FILES = "Config Files";
	protected static String[] blockConfigFiles = {};

	protected static final String CATEGORY_SOUND = "sound";
	protected static final String CONFIG_ENABLE_BIOME_SOUNDS = "Enable Biome Sounds";
	protected static boolean enableBiomeSounds = true;
	protected static final String CONFIG_MASTER_SOUND_FACTOR = "Master Sound Scale Factor";
	protected static float masterSoundScaleFactor = 0.5F;
	protected static final String CONFIG_NORMAL_CHANNEL_COUNT = "Number Normal Channels";
	protected static int normalSoundChannelCount = 28;
	protected static final String CONFIG_STREAMING_CHANNEL_COUNT = "Number Streaming Channels";
	protected static int streamingSoundChannelCount = 4;
	protected static final String CONFIG_ENABLE_JUMP_SOUND = "Jump Sound";
	protected static boolean enableJumpSound = true;
	protected static final String CONFIG_ENABLE_SWING_SOUND = "Swing Sound";
	protected static boolean enableSwingSound = true;
	protected static final String CONFIG_ENABLE_CRAFTING_SOUND = "Crafting Sound";
	protected static boolean enableCraftingSound = true;

	protected static final String CATEGORY_PLAYER = "player";
	protected static final String CONFIG_SUPPRESS_POTION_PARTICLES = "Suppress Potion Particles";
	protected static boolean suppressPotionParticles = false;
	protected static final String CONFIG_ENABLE_POPOFFS = "Damage Popoffs";
	protected static boolean enableDamagePopoffs = true;

	protected static final String CATEGORY_POTION_HUD = "player.potion hud";
	protected static final String CONFIG_POTION_HUD_ENABLE = "Enable";
	protected static boolean potionHudEnabled = true;
	protected static final String CONFIG_POTION_HUD_TRANSPARENCY = "Transparency";
	protected static float potionHudTransparency = 0.5F;
	protected static final String CONFIG_POTION_HUD_LEFT_OFFSET = "Left Offset";
	protected static int potionHudLeftOffset = 5;
	protected static final String CONFIG_POTION_HUD_TOP_OFFSET = "Top Offset";
	protected static int potionHudTopOffset = 5;
	protected static final String CONFIG_POTION_HUD_SCALE = "Display Scale";
	protected static float potionHudScale = 0.5F;

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

		comment = "Reset rain/thunder when all players sleep";
		resetRainOnSleep = config.getBoolean(CONFIG_RESET_RAIN_ON_SLEEP, CATEGORY_RAIN, resetRainOnSleep, comment);

		// CATEGORY: General
		comment = "Default minimum rain strength for a dimension";
		defaultMinRainStrength = MathHelper.clamp_float(config.getFloat(CONFIG_MIN_RAIN_STRENGTH, CATEGORY_GENERAL,
				defaultMinRainStrength, 0.0F, 1.0F, comment), 0.0F, 1.0F);

		comment = "Default maximum rain strength for a dimension";
		defaultMaxRainStrength = MathHelper.clamp_float(config.getFloat(CONFIG_MAX_RAIN_STRENGTH, CATEGORY_GENERAL,
				defaultMaxRainStrength, 0.0F, 1.0F, comment), defaultMinRainStrength, 1.0F);

		comment = "Block radius/range around player for special effect application";
		specialEffectRange = config.getInt(CONFIG_FX_RANGE, CATEGORY_GENERAL, specialEffectRange, 8, 32, comment);

		comment = "Adjust cloud graphics based on configured cloud height";
		enableFancyCloudHandling = config.getBoolean(CONFIG_FANCY_CLOUD_HANDLING, CATEGORY_GENERAL,
				enableFancyCloudHandling, comment);

		// CATEGORY: Player
		comment = "Suppress player's potion particles from rendering";
		suppressPotionParticles = config.getBoolean(CONFIG_SUPPRESS_POTION_PARTICLES, CATEGORY_PLAYER,
				suppressPotionParticles, comment);

		comment = "Controls display of damage popoffs when an entity is damaged";
		enableDamagePopoffs = config.getBoolean(CONFIG_ENABLE_POPOFFS, CATEGORY_PLAYER, enableDamagePopoffs, comment);

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

		comment = "Animate Aurora";
		auroraAnimate = config.getBoolean(CONFIG_AURORA_ANIMATE, CATEGORY_AURORA, auroraAnimate, comment);

		comment = "Number of blocks north of player location to spawn an aurora";
		auroraSpawnOffset = config.getInt(CONFIG_AURORA_SPAWN_OFFSET, CATEGORY_AURORA, auroraSpawnOffset, 0, 200,
				comment);

		// CATEGORY: Fog
		comment = "Allow desert fog when raining";
		allowDesertFog = config.getBoolean(CONFIG_ALLOW_DESERT_FOG, CATEGORY_FOG, allowDesertFog, comment);

		comment = "Visibility factor to apply to desert fog (higher is thicker)";
		desertFogFactor = config.getFloat(CONFIG_DESERT_FOG_FACTOR, CATEGORY_FOG, desertFogFactor, 0.0F, 5.0F, comment);

		comment = "Higher the player elevation the more haze that is experienced";
		enableElevationHaze = config.getBoolean(CONFIG_ENABLE_ELEVATION_HAZE, CATEGORY_FOG, enableElevationHaze,
				comment);

		comment = "Visibility factor to apply to elevation haze (higher is thicker)";
		elevationHazeFactor = config.getFloat(CONFIG_ELEVATION_HAZE_FACTOR, CATEGORY_FOG, elevationHazeFactor, 0.0F,
				5.0F, comment);

		comment = "Enable biome specific fog density and color";
		enableBiomeFog = config.getBoolean(CONFIG_ENABLE_BIOME_FOG, CATEGORY_FOG, enableBiomeFog, comment);

		comment = "Visibility factor to apply to biome fog (higher is thicker)";
		biomeFogFactor = config.getFloat(CONFIG_BIOME_FOG_FACTOR, CATEGORY_FOG, biomeFogFactor, 0.0F, 5.0F, comment);

		// CATEGORY: Biomes
		comment = "Configuration files for configuring Biome Registry";
		biomeConfigFiles = config.getStringList(CONFIG_BIOME_CONFIG_FILES, CATEGORY_BIOMES, biomeConfigFiles, comment);

		// CATEGORY: Dimensions
		comment = "Configuration files for configuring Dimension Registry";
		dimensionConfigFiles = config.getStringList(CONFIG_DIMENSION_CONFIG_FILES, CATEGORY_DIMENSIONS,
				dimensionConfigFiles, comment);

		// CATEGORY: Block
		comment = "Configuration files for configuring Block sounds and behavior";
		blockConfigFiles = config.getStringList(CONFIG_BLOCK_CONFIG_FILES, CATEGORY_BLOCK, blockConfigFiles, comment);

		// CATEGORY: Sound
		comment = "Enable biome sounds";
		enableBiomeSounds = config.getBoolean(CONFIG_ENABLE_BIOME_SOUNDS, CATEGORY_SOUND, enableBiomeSounds, comment);

		comment = "Master sound scale factor for biome and block sounds";
		masterSoundScaleFactor = config.getFloat(CONFIG_MASTER_SOUND_FACTOR, CATEGORY_SOUND, masterSoundScaleFactor,
				0.0F, 1.0F, comment);

		comment = "Number of normal sound channels to configure in the sound system";
		normalSoundChannelCount = config.getInt(CONFIG_NORMAL_CHANNEL_COUNT, CATEGORY_SOUND, normalSoundChannelCount,
				28, Integer.MAX_VALUE, comment);

		comment = "Number of streaming sound channels to configure in the sound system";
		streamingSoundChannelCount = config.getInt(CONFIG_STREAMING_CHANNEL_COUNT, CATEGORY_SOUND,
				streamingSoundChannelCount, 4, Integer.MAX_VALUE, comment);

		comment = "Enable sound effect when jumping";
		enableJumpSound = config.getBoolean(CONFIG_ENABLE_JUMP_SOUND, CATEGORY_SOUND, enableJumpSound, comment);

		comment = "Enable weapons swing sound effect when attacking";
		enableSwingSound = config.getBoolean(CONFIG_ENABLE_SWING_SOUND, CATEGORY_SOUND, enableSwingSound, comment);

		comment = "Enable sound when item crafted";
		enableCraftingSound = config.getBoolean(CONFIG_ENABLE_CRAFTING_SOUND, CATEGORY_SOUND, enableCraftingSound, comment);

		// CATEGORY: player.potion hud
		comment = "Enable display of potion icons in display";
		potionHudEnabled = config.getBoolean(CONFIG_POTION_HUD_ENABLE, CATEGORY_POTION_HUD, potionHudEnabled, comment);

		comment = "Transparency factor for icons (higher more solid)";
		potionHudTransparency = config.getFloat(CONFIG_POTION_HUD_TRANSPARENCY, CATEGORY_POTION_HUD,
				potionHudTransparency, 0.0F, 1.0F, comment);

		comment = "Offset from left side of screen";
		potionHudLeftOffset = config.getInt(CONFIG_POTION_HUD_LEFT_OFFSET, CATEGORY_POTION_HUD, potionHudLeftOffset, 0,
				Integer.MAX_VALUE, comment);

		comment = "Offset from top of screen";
		potionHudTopOffset = config.getInt(CONFIG_POTION_HUD_TOP_OFFSET, CATEGORY_POTION_HUD, potionHudTopOffset, 0,
				Integer.MAX_VALUE, comment);

		comment = "Size scale of icons (lower is smaller)";
		potionHudScale = config.getFloat(CONFIG_POTION_HUD_SCALE, CATEGORY_POTION_HUD, potionHudScale, 0.0F, 1.0F,
				comment);

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

	public static boolean getResetRainOnSleep() {
		return resetRainOnSleep;
	}

	public static float getDefaultMinRainIntensity() {
		return defaultMinRainStrength;
	}

	public static float getDefaultMaxRainIntensity() {
		return defaultMaxRainStrength;
	}

	public static int getSpecialEffectRange() {
		return specialEffectRange;
	}

	public static boolean getEnableFancyCloudHandling() {
		return enableFancyCloudHandling;
	}

	public static boolean getAuroraHeightPlayerRelative() {
		return auroraHeightPlayerRelative;
	}

	public static boolean getAuroraMultipleBands() {
		return auroraMultipleBands;
	}

	public static boolean getAuroraEnable() {
		return auroraEnable;
	}

	public static boolean getAuroraAnimate() {
		return auroraAnimate;
	}

	public static int getAuroraSpawnOffset() {
		return auroraSpawnOffset;
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

	public static boolean getEnableBiomeFog() {
		return enableBiomeFog;
	}

	public static float getBiomeFogFactor() {
		return biomeFogFactor;
	}

	public static String[] getBiomeConfigFiles() {
		return biomeConfigFiles;
	}

	public static String[] getDimensionConfigFiles() {
		return dimensionConfigFiles;
	}

	public static String[] getBlockConfigFiles() {
		return blockConfigFiles;
	}

	public static boolean getEnableBiomeSounds() {
		return enableBiomeSounds;
	}

	public static float getMasterSoundScaleFactor() {
		return masterSoundScaleFactor;
	}

	public static int getNumberNormalSoundChannels() {
		return normalSoundChannelCount;
	}

	public static int getNumberStreamingSoundChannels() {
		return streamingSoundChannelCount;
	}
	
	public static boolean getEnableJumpSound() {
		return enableJumpSound;
	}
	
	public static boolean getEnableSwingSound() {
		return enableSwingSound;
	}

	public static boolean getEnableCraftingSound() {
		return enableCraftingSound;
	}

	public static boolean getSuppressPotionParticleEffect() {
		return suppressPotionParticles;
	}

	public static boolean getEnableDamagePopoffs() {
		return enableDamagePopoffs;
	}

	public static boolean getPotionHudEnabled() {
		return potionHudEnabled;
	}

	public static float getPotionHudTransparency() {
		return potionHudTransparency;
	}

	public static int getPotionHudLeftOffset() {
		return potionHudLeftOffset;
	}

	public static int getPotionHudTopOffset() {
		return potionHudTopOffset;
	}

	public static float getPotionHudScale() {
		return potionHudScale;
	}
}