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

import org.blockartistry.mod.DynSurround.util.ConfigProcessor;
import org.blockartistry.mod.DynSurround.util.ConfigProcessor.Comment;
import org.blockartistry.mod.DynSurround.util.ConfigProcessor.MinMaxFloat;
import org.blockartistry.mod.DynSurround.util.ConfigProcessor.MinMaxInt;
import org.blockartistry.mod.DynSurround.util.ConfigProcessor.Parameter;

import net.minecraftforge.common.config.Configuration;

public final class ModOptions {

	private ModOptions() {
	}

	public static final String CATEGORY_LOGGING_CONTROL = "logging";
	public static final String CONFIG_ENABLE_DEBUG_LOGGING = "Enable Debug Logging";
	public static final String CONFIG_ENABLE_ONLINE_VERSION_CHECK = "Enable Online Version Check";

	@Parameter(category = CATEGORY_LOGGING_CONTROL, property = CONFIG_ENABLE_DEBUG_LOGGING)
	@Comment("Enables/disables debug logging of the mod")
	public static boolean enableDebugLogging = false;
	@Parameter(category = CATEGORY_LOGGING_CONTROL, property = CONFIG_ENABLE_ONLINE_VERSION_CHECK)
	@Comment("Enables/disables online version checking")
	public static boolean enableVersionChecking = true;

	public static final String CATEGORY_RAIN = "rain";
	public static final String CONFIG_RAIN_VOLUME = "Sound Level";
	public static final String CONFIG_ALWAYS_OVERRIDE_SOUND = "Always Override Sound";
	public static final String CONFIG_ALLOW_DESERT_DUST = "Desert Dust";
	public static final String CONFIG_RESET_RAIN_ON_SLEEP = "Reset Rain on Sleep";

	@Parameter(category = CATEGORY_RAIN, property = CONFIG_RAIN_VOLUME)
	@MinMaxFloat(min = 0.0F, max = 1.0F)
	@Comment("Factor to apply to rain sound level to adjust")
	public static float soundLevel = 1.0F;
	@Parameter(category = CATEGORY_RAIN, property = CONFIG_ALWAYS_OVERRIDE_SOUND)
	@Comment("Always override Vanilla rain sound even when dimension is blacklisted")
	public static boolean alwaysOverrideSound = true;
	@Parameter(category = CATEGORY_RAIN, property = CONFIG_ALLOW_DESERT_DUST)
	@Comment("Allow desert dust when raining")
	public static boolean allowDesertDust = true;
	@Parameter(category = CATEGORY_RAIN, property = CONFIG_RESET_RAIN_ON_SLEEP)
	@Comment("Reset rain/thunder when all players sleep")
	public static boolean resetRainOnSleep = true;

	public static final String CATEGORY_FOG = "fog";
	public static final String CONFIG_ALLOW_DESERT_FOG = "Desert Fog";
	public static final String CONFIG_DESERT_FOG_FACTOR = "Desert Fog Factor";
	public static final String CONFIG_ENABLE_ELEVATION_HAZE = "Elevation Haze";
	public static final String CONFIG_ELEVATION_HAZE_FACTOR = "Elevation Haze Factor";
	public static final String CONFIG_ENABLE_BIOME_FOG = "Biome Fog";
	public static final String CONFIG_BIOME_FOG_FACTOR = "Biome Fog Factor";

	@Parameter(category = CATEGORY_FOG, property = CONFIG_ALLOW_DESERT_FOG)
	@Comment("Allow desert fog when raining")
	public static boolean allowDesertFog = true;
	@Parameter(category = CATEGORY_FOG, property = CONFIG_DESERT_FOG_FACTOR)
	@MinMaxFloat(min = 0.0F, max = 5.0F)
	@Comment("Visibility factor to apply to desert fog (higher is thicker)")
	public static float desertFogFactor = 1.0F;
	@Parameter(category = CATEGORY_FOG, property = CONFIG_ENABLE_ELEVATION_HAZE)
	@Comment("Higher the player elevation the more haze that is experienced")
	public static boolean enableElevationHaze = true;
	@Parameter(category = CATEGORY_FOG, property = CONFIG_ELEVATION_HAZE_FACTOR)
	@MinMaxFloat(min = 0.0F, max = 5.0F)
	@Comment("Visibility factor to apply to elevation haze (higher is thicker)")
	public static float elevationHazeFactor = 1.0F;
	@Parameter(category = CATEGORY_FOG, property = CONFIG_ENABLE_BIOME_FOG)
	@Comment("Enable biome specific fog density and color")
	public static boolean enableBiomeFog = true;
	@Parameter(category = CATEGORY_FOG, property = CONFIG_BIOME_FOG_FACTOR)
	@MinMaxFloat(min = 0.0F, max = 5.0F)
	@Comment("Visibility factor to apply to biome fog (higher is thicker)")
	public static float biomeFogFactor = 1.0F;

	public static final String CATEGORY_GENERAL = "general";
	public static final String CONFIG_MIN_RAIN_STRENGTH = "Default Minimum Rain Strength";
	public static final String CONFIG_MAX_RAIN_STRENGTH = "Default Maximum Rain Strength";
	public static final String CONFIG_FX_RANGE = "Special Effect Range";
	public static final String CONFIG_FANCY_CLOUD_HANDLING = "Fancy Cloud Handling";

	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_MIN_RAIN_STRENGTH)
	@MinMaxFloat(min = 0.0F, max = 1.0F)
	@Comment("Default minimum rain strength for a dimension")
	public static float defaultMinRainStrength = 0.0F;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_MAX_RAIN_STRENGTH)
	@MinMaxFloat(min = 0.0F, max = 1.0F)
	@Comment("Default maximum rain strength for a dimension")
	public static float defaultMaxRainStrength = 1.0F;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_FX_RANGE)
	@MinMaxInt(min = 16, max = 32)
	@Comment("Block radius/range around player for special effect application")
	public static int specialEffectRange = 16;
	@Parameter(category = CATEGORY_GENERAL, property = CONFIG_FANCY_CLOUD_HANDLING)
	@Comment("Adjust cloud graphics based on configured cloud height")
	public static boolean enableFancyCloudHandling = true;

	public static final String CATEGORY_AURORA = "aurora";
	public static final String CONFIG_AURORA_ENABLED = "Enabled";
	public static final String CONFIG_Y_PLAYER_RELATIVE = "Height Player Relative";
	public static final String CONFIG_PLAYER_FIXED_HEIGHT = "Player Fixed Height";
	public static final String CONFIG_MULTIPLE_BANDS = "Multiple Bands";
	public static final String CONFIG_AURORA_ANIMATE = "Animate";
	public static final String CONFIG_AURORA_SPAWN_OFFSET = "Spawn Offset";

	@Parameter(category = CATEGORY_AURORA, property = CONFIG_AURORA_ENABLED)
	@Comment("Whether to enable Aurora processing on server/client")
	public static boolean auroraEnable = true;
	@Parameter(category = CATEGORY_AURORA, property = CONFIG_Y_PLAYER_RELATIVE)
	@Comment("true to keep the aurora at a height above player; false to fix it to an altitude")
	public static boolean auroraHeightPlayerRelative = true;
	@Parameter(category = CATEGORY_AURORA, property = CONFIG_PLAYER_FIXED_HEIGHT)
	@MinMaxFloat(min = 16.0F, max = 2048.0F)
	@Comment("Number of blocks to say fixed above player if Aurora is player relative")
	public static float playerFixedHeight = 64.0F;
	@Parameter(category = CATEGORY_AURORA, property = CONFIG_MULTIPLE_BANDS)
	@Comment("Allow Auroras with multiple bands")
	public static boolean auroraMultipleBands = true;
	@Parameter(category = CATEGORY_AURORA, property = CONFIG_AURORA_ANIMATE)
	@Comment("Animate Aurora so it waves")
	public static boolean auroraAnimate = true;
	@Parameter(category = CATEGORY_AURORA, property = CONFIG_AURORA_SPAWN_OFFSET)
	@MinMaxInt(min = 0, max = 200)
	@Comment("Number of blocks north of player location to spawn an aurora")
	public static int auroraSpawnOffset = 150;

	public static final String CATEGORY_BIOMES = "biomes";
	public static final String CONFIG_BIOME_CONFIG_FILES = "Config Files";
	public static final String CONFIG_BIOME_ALIASES = "Biome Alias";

	@Parameter(category = CATEGORY_BIOMES, property = CONFIG_BIOME_CONFIG_FILES)
	@Comment("Configuration files for configuring Biome Registry")
	public static String[] biomeConfigFiles = {};
	@Parameter(category = CATEGORY_BIOMES, property = CONFIG_BIOME_ALIASES)
	@Comment("Biome alias list")
	public static String[] biomeAliases = {};

	public static final String CATEGORY_DIMENSIONS = "dimensions";
	public static final String CONFIG_DIMENSION_CONFIG_FILES = "Config Files";

	@Parameter(category = CATEGORY_DIMENSIONS, property = CONFIG_DIMENSION_CONFIG_FILES)
	@Comment("Configuration files for configuring Dimension Registry")
	public static String[] dimensionConfigFiles = {};

	public static final String CATEGORY_BLOCK = "block";
	public static final String CONFIG_BLOCK_CONFIG_FILES = "Config Files";

	@Parameter(category = CATEGORY_BLOCK, property = CONFIG_BLOCK_CONFIG_FILES)
	@Comment("Configuration files for configuring Block sounds and behavior")
	public static String[] blockConfigFiles = {};

	public static final String CATEGORY_SOUND = "sound";
	public static final String CONFIG_ENABLE_BIOME_SOUNDS = "Enable Biome Sounds";
	public static final String CONFIG_MASTER_SOUND_FACTOR = "Master Sound Scale Factor";
	public static final String CONFIG_AUTO_CONFIG_CHANNELS = "Autoconfigure Channels";
	public static final String CONFIG_NORMAL_CHANNEL_COUNT = "Number Normal Channels";
	public static final String CONFIG_STREAMING_CHANNEL_COUNT = "Number Streaming Channels";
	public static final String CONFIG_ENABLE_JUMP_SOUND = "Jump Sound";
	public static final String CONFIG_ENABLE_SWING_SOUND = "Swing Sound";
	public static final String CONFIG_ENABLE_CRAFTING_SOUND = "Crafting Sound";
	public static final String CONFIG_ENABLE_BOW_PULL_SOUND = "Bow Pull Sound";
	public static final String CONFIG_ENABLE_FOOTSTEPS_SOUND = "Footsteps";
	public static final String CONFIG_FOOTSTEPS_SOUND_FACTOR = "Footsteps Sound Factor";
	public static final String CONFIG_SOUND_CULL_THRESHOLD = "Sound Culling Threshold";
	public static final String CONFIG_CULLED_SOUNDS = "Culled Sounds";
	public static final String CONFIG_BLOCKED_SOUNDS = "Blocked Sounds";

	@Parameter(category = CATEGORY_SOUND, property = CONFIG_ENABLE_BIOME_SOUNDS)
	@Comment("Enable biome background and spot sounds")
	public static boolean enableBiomeSounds = true;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_MASTER_SOUND_FACTOR)
	@MinMaxFloat(min = 0.0F, max = 1.0F)
	@Comment("Master sound scale factor for biome and block sounds")
	public static float masterSoundScaleFactor = 0.5F;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_AUTO_CONFIG_CHANNELS)
	@Comment("Automatically configure sound channels")
	public static boolean autoConfigureChannels = true;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_NORMAL_CHANNEL_COUNT)
	@MinMaxInt(min = 28)
	@Comment("Number of normal sound channels to configure in the sound system (manual)")
	public static int normalSoundChannelCount = 28;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_STREAMING_CHANNEL_COUNT)
	@MinMaxInt(min = 4)
	@Comment("Number of streaming sound channels to configure in the sound system (manual)")
	public static int streamingSoundChannelCount = 4;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_ENABLE_JUMP_SOUND)
	@Comment("Enable sound effect when jumping")
	public static boolean enableJumpSound = true;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_ENABLE_SWING_SOUND)
	@Comment("Enable weapons swing sound effect when attacking")
	public static boolean enableSwingSound = true;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_ENABLE_CRAFTING_SOUND)
	@Comment("Enable sound when item crafted")
	public static boolean enableCraftingSound = true;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_ENABLE_BOW_PULL_SOUND)
	@Comment("Enable sound when bow is pulled")
	public static boolean enableBowPullSound = true;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_ENABLE_FOOTSTEPS_SOUND)
	@Comment("Enable footstep sounds")
	public static boolean enableFootstepSounds = true;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_FOOTSTEPS_SOUND_FACTOR)
	@MinMaxFloat(min = 0.0F, max = 1.0F)
	@Comment("Sound scale factor for footstep sounds")
	public static float footstepsSoundFactor = 0.3F;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_SOUND_CULL_THRESHOLD)
	@MinMaxInt(min = 0)
	@Comment("Ticks between culled sound events (0 to disable culling)")
	public static int soundCullingThreshold = 20;
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_CULLED_SOUNDS)
	@Comment("Sounds to cull from frequent playing")
	public static String[] culledSounds = { "^minecraft:liquid.*", "minecraft:mob.sheep.say",
			"minecraft:mob.chicken.say", "minecraft:mob.cow.say", "minecraft:mob.pig.say" };
	@Parameter(category = CATEGORY_SOUND, property = CONFIG_BLOCKED_SOUNDS)
	@Comment("Sounds to block from playing")
	public static String[] blockedSounds = {};

	public static final String CATEGORY_PLAYER = "player";
	public static final String CONFIG_SUPPRESS_POTION_PARTICLES = "Suppress Potion Particles";
	public static final String CONFIG_ENABLE_POPOFFS = "Damage Popoffs";

	@Parameter(category = CATEGORY_PLAYER, property = CONFIG_SUPPRESS_POTION_PARTICLES)
	@Comment("Suppress player's potion particles from rendering")
	public static boolean suppressPotionParticles = false;
	@Parameter(category = CATEGORY_PLAYER, property = CONFIG_ENABLE_POPOFFS)
	@Comment("Controls display of damage pop-offs when an entity is damaged")
	public static boolean enableDamagePopoffs = true;

	public static final String CATEGORY_POTION_HUD = "player.potion hud";
	public static final String CONFIG_POTION_HUD_ENABLE = "Enable";
	public static final String CONFIG_POTION_HUD_TRANSPARENCY = "Transparency";
	public static final String CONFIG_POTION_HUD_LEFT_OFFSET = "Left Offset";
	public static final String CONFIG_POTION_HUD_TOP_OFFSET = "Top Offset";
	public static final String CONFIG_POTION_HUD_SCALE = "Display Scale";

	@Parameter(category = CATEGORY_POTION_HUD, property = CONFIG_POTION_HUD_ENABLE)
	@Comment("Enable display of potion icons in display")
	public static boolean potionHudEnabled = true;
	@Parameter(category = CATEGORY_POTION_HUD, property = CONFIG_POTION_HUD_TRANSPARENCY)
	@MinMaxFloat(min = 0.0F, max = 1.0F)
	@Comment("Transparency factor for icons (higher more solid)")
	public static float potionHudTransparency = 0.5F;
	@Parameter(category = CATEGORY_POTION_HUD, property = CONFIG_POTION_HUD_LEFT_OFFSET)
	@MinMaxInt(min = 0)
	@Comment("Offset from left side of screen")
	public static int potionHudLeftOffset = 5;
	@Parameter(category = CATEGORY_POTION_HUD, property = CONFIG_POTION_HUD_TOP_OFFSET)
	@MinMaxInt(min = 0)
	@Comment("Offset from top of screen")
	public static int potionHudTopOffset = 5;
	@Parameter(category = CATEGORY_POTION_HUD, property = CONFIG_POTION_HUD_SCALE)
	@MinMaxFloat(min = 0.0F, max = 1.0F)
	@Comment("Size scale of icons (lower is smaller)")
	public static float potionHudScale = 0.5F;

	public static void load(final Configuration config) {

		ConfigProcessor.process(config, ModOptions.class);

		// CATEGORY: Logging
		config.setCategoryRequiresMcRestart(CATEGORY_LOGGING_CONTROL, true);
		config.setCategoryComment(CATEGORY_LOGGING_CONTROL, "Defines how Dynamic Surroundings logging will behave");

		// CATEGORY: Rain
		config.setCategoryRequiresMcRestart(CATEGORY_RAIN, true);
		config.setCategoryComment(CATEGORY_RAIN, "Options that control rain effects in the client");

		// CATEGORY: General
		config.setCategoryRequiresMcRestart(CATEGORY_GENERAL, true);
		config.setCategoryComment(CATEGORY_GENERAL, "Miscellaneous settings");

		// CATEGORY: Player
		config.setCategoryRequiresMcRestart(CATEGORY_PLAYER, true);
		config.setCategoryComment(CATEGORY_PLAYER, "General options for defining sound and effects the player entity");

		// CATEGORY: Aurora
		config.setCategoryRequiresMcRestart(CATEGORY_AURORA, true);
		config.setCategoryComment(CATEGORY_AURORA, "Options that control Aurora behavior and rendering");

		// CATEGORY: Fog
		config.setCategoryRequiresMcRestart(CATEGORY_FOG, true);
		config.setCategoryComment(CATEGORY_FOG, "Options that control the various fog effects in the client");

		// CATEGORY: Biomes
		config.setCategoryRequiresMcRestart(CATEGORY_BIOMES, true);
		config.setCategoryComment(CATEGORY_BIOMES, "Options for controlling biome sound/effects");

		// CATEGORY: Dimensions
		config.setCategoryRequiresMcRestart(CATEGORY_DIMENSIONS, true);
		config.setCategoryComment(CATEGORY_DIMENSIONS,
				"Options for defining per dimension parameters for Dynamic Surroundings");

		// CATEGORY: Block
		config.setCategoryRequiresMcRestart(CATEGORY_BLOCK, true);
		config.setCategoryComment(CATEGORY_BLOCK, "Options for defining block specific sounds/effects");

		// CATEGORY: Sound
		config.setCategoryRequiresMcRestart(CATEGORY_SOUND, true);
		config.setCategoryComment(CATEGORY_SOUND, "General options for defining sound effects");

		// CATEGORY: player.potion hud
		config.setCategoryRequiresMcRestart(CATEGORY_POTION_HUD, true);
		config.setCategoryComment(CATEGORY_POTION_HUD, "Options for the Potion HUD overlay");
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

	public static String[] getBiomeAliases() {
		return biomeAliases;
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

	public static boolean getAutoconfigureSoundChannels() {
		return autoConfigureChannels;
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

	public static boolean getEnableBowPullSound() {
		return enableBowPullSound;
	}

	public static boolean getEnableFootstepSounds() {
		return enableFootstepSounds;
	}

	public static float getFootstepsSoundFactor() {
		return footstepsSoundFactor;
	}

	public static int getSoundCullingThreshold() {
		return soundCullingThreshold;
	}

	public static String[] getCulledSounds() {
		return culledSounds;
	}

	public static String[] getBlockedSounds() {
		return blockedSounds;
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