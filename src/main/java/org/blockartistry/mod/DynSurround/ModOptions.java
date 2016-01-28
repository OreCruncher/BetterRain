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

	protected static final String CATEGORY_JETS = "jets";
	protected static final String CONFIG_FIREJETS_ENABLED = "Firejets Enabled";
	protected static boolean enableFireJets = true;
	protected static final String CONFIG_FIREJET_CHANCE = "Firejet Spawn Chance";
	protected static int fireJetsSpawnChance = 1800;
	protected static final String CONFIG_BUBBLEJETS_ENABLED = "Bubblejets Enabled";
	protected static boolean enableBubbleJets = true;
	protected static final String CONFIG_BUBBLEJETS_CHANCE = "Bubblejet Spawn Chance";
	protected static int bubbleJetSpawnChance = 1800;
	protected static final String CONFIG_STEAMJETS_ENABLED = "Steamjets Enabled";
	protected static boolean enableSteamJets = true;
	protected static final String CONFIG_STEAMJETS_CHANCE = "Streamjets Chance";
	protected static int steamJetSpawnChance = 10;
	protected static final String CONFIG_DUSTJETS_ENABLED = "Dustjets Enabled";
	protected static boolean enableDustJets = true;
	protected static final String CONFIG_DUSTJETS_CHANCE = "Dustjets Chance";
	protected static int dustJetSpawnChance = 500;

	protected static final String CATEGORY_BIOMES = "biomes";
	protected static final String CONFIG_BIOME_CONFIG_FILES = "Config Files";
	protected static String[] biomeConfigFiles = {};

	protected static final String CATEGORY_DIMENSIONS = "dimensions";
	protected static final String CONFIG_DIMENSION_CONFIG_FILES = "Config Files";
	protected static String[] dimensionConfigFiles = {};

	protected static final String CATEGORY_SOUND = "sound";
	protected static final String CONFIG_ENABLE_BIOME_SOUNDS = "Enable Biome Sounds";
	protected static boolean enableBiomeSounds = true;
	protected static final String CONFIG_SOUND_ENABLED = "Enabled";
	protected static final String COMMENT_SOUND_ENABLED = "Enable client side sound effect";
	protected static final String CONFIG_SOUND_CHANCE = "Chance";
	protected static final String COMMENT_SOUND_CHANCE = "1-in-N chance for sound spawn";
	protected static final String CONFIG_SOUND_SCALING_FACTOR = "Scaling Factor";
	protected static final String COMMENT_SOUND_SCALING_FACTOR = "Factor to apply to sound volume";
	protected static final String CATEGORY_SOUND_ICE_CRACK = "sound.Ice Cracking";
	protected static boolean enableIceCrackSound = true;
	protected static int iceCrackSoundChance = 10000;
	protected static float iceCrackScaleFactor = 1.0F;
	protected static final String CATEGORY_SOUND_FROGS = "sound.Frog Croaks";
	protected static boolean enableFrogCroakSound = true;
	protected static int frogCroakSoundChance = 25;
	protected static float frogCroakScaleFactor = 1.0F;
	protected static final String CATEGORY_SOUND_REDSTONE_ORE = "sound.Redstone Ore";
	protected static boolean enableRedstoneOreSound = true;
	protected static int redstoneOreSoundChance = 100;
	protected static float redstoneOreScaleFactor = 1.0F;
	protected static final String CATEGORY_SOUND_SOULSAND = "sound.Soul Sand";
	protected static boolean enableSoulSandSound = true;
	protected static int soulSandSoundChance = 8000;
	protected static float soulSandScaleFactor = 1.0F;

	protected static final String CATEGORY_PLAYER = "player";
	protected static final String CONFIG_SUPPRESS_POTION_PARTICLES = "Suppress Potion Particles";
	protected static boolean suppressPotionParticles = false;

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

		// CATEGORY: Player
		comment = "Suppress player's potion particles from rendering";
		suppressPotionParticles = config.getBoolean(CONFIG_SUPPRESS_POTION_PARTICLES, CATEGORY_PLAYER,
				suppressPotionParticles, comment);

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

		// CATEGORY: Jets
		comment = "Enable firejet spawn on lava blocks";
		enableFireJets = config.getBoolean(CONFIG_FIREJETS_ENABLED, CATEGORY_JETS, enableFireJets, comment);

		comment = "1-in-N chance per random tick a firejet will spawn on lava blocks";
		fireJetsSpawnChance = config.getInt(CONFIG_FIREJET_CHANCE, CATEGORY_JETS, fireJetsSpawnChance, 10,
				Integer.MAX_VALUE, comment);

		comment = "Enable bubblejet spawn in water";
		enableBubbleJets = config.getBoolean(CONFIG_BUBBLEJETS_ENABLED, CATEGORY_JETS, enableBubbleJets, comment);

		comment = "1-in-N chance per random tick a bubblejet will spawn in water";
		bubbleJetSpawnChance = config.getInt(CONFIG_BUBBLEJETS_CHANCE, CATEGORY_JETS, bubbleJetSpawnChance, 10,
				Integer.MAX_VALUE, comment);

		comment = "Enable steamjets around water/lava";
		enableSteamJets = config.getBoolean(CONFIG_STEAMJETS_ENABLED, CATEGORY_JETS, enableSteamJets, comment);

		comment = "1-in-N chance per random tick a streamjet will spawn";
		steamJetSpawnChance = config.getInt(CONFIG_STEAMJETS_CHANCE, CATEGORY_JETS, steamJetSpawnChance, 10,
				Integer.MAX_VALUE, comment);

		comment = "Enable dustjets at the bottom of blocks";
		enableDustJets = config.getBoolean(CONFIG_DUSTJETS_ENABLED, CATEGORY_JETS, enableDustJets, comment);

		comment = "1-in-N chance per random tick a dustjet will spawn";
		dustJetSpawnChance = config.getInt(CONFIG_DUSTJETS_CHANCE, CATEGORY_JETS, dustJetSpawnChance, 10,
				Integer.MAX_VALUE, comment);

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

		// CATEGORY: Sound
		comment = "Enable biome sounds";
		enableBiomeSounds = config.getBoolean(CONFIG_ENABLE_BIOME_SOUNDS, CATEGORY_SOUND, enableBiomeSounds, comment);

		enableIceCrackSound = config.getBoolean(CONFIG_SOUND_ENABLED, CATEGORY_SOUND_ICE_CRACK, enableIceCrackSound,
				COMMENT_SOUND_ENABLED);
		iceCrackSoundChance = config.getInt(CONFIG_SOUND_CHANCE, CATEGORY_SOUND_ICE_CRACK, iceCrackSoundChance, 1,
				Integer.MAX_VALUE, COMMENT_SOUND_CHANCE);
		iceCrackScaleFactor = config.getFloat(CONFIG_SOUND_SCALING_FACTOR, CATEGORY_SOUND_ICE_CRACK,
				iceCrackScaleFactor, 0.0F, 5.0F, COMMENT_SOUND_SCALING_FACTOR);

		enableFrogCroakSound = config.getBoolean(CONFIG_SOUND_ENABLED, CATEGORY_SOUND_FROGS, enableFrogCroakSound,
				COMMENT_SOUND_ENABLED);
		frogCroakSoundChance = config.getInt(CONFIG_SOUND_CHANCE, CATEGORY_SOUND_FROGS, frogCroakSoundChance, 1,
				Integer.MAX_VALUE, COMMENT_SOUND_CHANCE);
		frogCroakScaleFactor = config.getFloat(CONFIG_SOUND_SCALING_FACTOR, CATEGORY_SOUND_FROGS, frogCroakScaleFactor,
				0.0F, 5.0F, COMMENT_SOUND_SCALING_FACTOR);

		enableRedstoneOreSound = config.getBoolean(CONFIG_SOUND_ENABLED, CATEGORY_SOUND_REDSTONE_ORE,
				enableRedstoneOreSound, COMMENT_SOUND_ENABLED);
		redstoneOreSoundChance = config.getInt(CONFIG_SOUND_CHANCE, CATEGORY_SOUND_REDSTONE_ORE, redstoneOreSoundChance,
				1, Integer.MAX_VALUE, COMMENT_SOUND_CHANCE);
		redstoneOreScaleFactor = config.getFloat(CONFIG_SOUND_SCALING_FACTOR, CATEGORY_SOUND_REDSTONE_ORE,
				redstoneOreScaleFactor, 0.0F, 5.0F, COMMENT_SOUND_SCALING_FACTOR);

		enableSoulSandSound = config.getBoolean(CONFIG_SOUND_ENABLED, CATEGORY_SOUND_SOULSAND, enableSoulSandSound,
				COMMENT_SOUND_ENABLED);
		soulSandSoundChance = config.getInt(CONFIG_SOUND_CHANCE, CATEGORY_SOUND_SOULSAND, soulSandSoundChance, 1,
				Integer.MAX_VALUE, COMMENT_SOUND_CHANCE);
		soulSandScaleFactor = config.getFloat(CONFIG_SOUND_SCALING_FACTOR, CATEGORY_SOUND_SOULSAND, soulSandScaleFactor,
				0.0F, 5.0F, COMMENT_SOUND_SCALING_FACTOR);
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

	public static boolean getEnableSteamJets() {
		return enableSteamJets;
	}

	public static int getSteamJetSpawnChance() {
		return steamJetSpawnChance;
	}

	public static boolean getEnableDustJets() {
		return enableDustJets;
	}

	public static int getDustJetSpawnChance() {
		return dustJetSpawnChance;
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

	public static boolean getEnableBiomeSounds() {
		return enableBiomeSounds;
	}

	// Block sounds
	public static boolean getEnableIceCrackSound() {
		return enableIceCrackSound;
	}

	public static int getIceCrackSoundChance() {
		return iceCrackSoundChance;
	}

	public static float getIceCrackScaleFactor() {
		return iceCrackScaleFactor;
	}

	public static boolean getEnableFrogCroakSound() {
		return enableFrogCroakSound;
	}

	public static int getFrogCroakSoundChance() {
		return frogCroakSoundChance;
	}

	public static float getFrogCroakScaleFactor() {
		return frogCroakScaleFactor;
	}

	public static boolean getEnableRedstoneOreSound() {
		return enableRedstoneOreSound;
	}

	public static int getRedstoneOreSoundChance() {
		return redstoneOreSoundChance;
	}

	public static float getRedstoneOreScaleFactor() {
		return redstoneOreScaleFactor;
	}

	public static boolean getEnableSoulSandSound() {
		return enableSoulSandSound;
	}

	public static int getSoulSandSoundChance() {
		return soulSandSoundChance;
	}

	public static float getSoulSandScaleFactor() {
		return soulSandScaleFactor;
	}

	public static boolean getSuppressPotionParticleEffect() {
		return suppressPotionParticles;
	}
}