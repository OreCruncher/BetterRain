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

package org.blockartistry.mod.DynSurround.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.Module;
import org.blockartistry.mod.DynSurround.client.sound.SoundEffect;
import org.blockartistry.mod.DynSurround.client.sound.SoundEffect.SoundType;
import org.blockartistry.mod.DynSurround.data.config.BiomeConfig;
import org.blockartistry.mod.DynSurround.data.config.SoundConfig;
import org.blockartistry.mod.DynSurround.event.RegistryReloadEvent;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.MyUtils;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

public final class BiomeRegistry {

	private static final TIntObjectHashMap<Entry> registry = new TIntObjectHashMap<Entry>();
	private static final Map<String, String> biomeAliases = new HashMap<String, String>();

	public static final BiomeGenBase UNDERGROUND = new FakeBiome(-1, "Underground");
	public static final BiomeGenBase PLAYER = new FakeBiome(-2, "Player");
	public static final BiomeGenBase UNDERWATER = new FakeBiome(-3, "Underwater");
	public static final BiomeGenBase UNDEROCEAN = new FakeBiome(-4, "UnderOCN");
	public static final BiomeGenBase UNDERDEEPOCEAN = new FakeBiome(-5, "UnderDOCN");
	public static final BiomeGenBase UNDERRIVER = new FakeBiome(-6, "UnderRVR");
	public static final BiomeGenBase OUTERSPACE = new FakeBiome(-7, "OuterSpace");
	public static final BiomeGenBase CLOUDS = new FakeBiome(-8, "Clouds");

	public static final SoundEffect WATER_DRIP = new SoundEffect(Module.MOD_ID + ":waterdrops");

	// This is for cases when the biome coming in doesn't make sense
	// and should default to something to avoid crap.
	private static final BiomeGenBase WTF = new FakeBiome(-256, "(FooBar)");

	private static class Entry {

		public final BiomeGenBase biome;
		public boolean hasPrecipitation;
		public boolean hasDust;
		public boolean hasAurora;
		public boolean hasFog;

		public Color dustColor;
		public Color fogColor;
		public float fogDensity;

		public List<SoundEffect> sounds;

		public int spotSoundChance;
		public List<SoundEffect> spotSounds;

		public Entry(final BiomeGenBase biome) {
			this.biome = biome;
			this.hasPrecipitation = biome.canSpawnLightningBolt() || biome.getEnableSnow();
			this.sounds = new ArrayList<SoundEffect>();
			this.spotSounds = new ArrayList<SoundEffect>();
			this.spotSoundChance = 1200;
		}

		public SoundEffect findSoundMatch(final String conditions) {
			for (final SoundEffect sound : this.sounds)
				if (sound.matches(conditions))
					return sound;
			return null;
		}

		public List<SoundEffect> findSoundMatches(final String conditions) {
			final List<SoundEffect> results = new ArrayList<SoundEffect>();
			for (final SoundEffect sound : this.sounds)
				if (sound.matches(conditions))
					results.add(sound);
			return results;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append(String.format("Biome %d [%s]:", this.biome.biomeID, resolveName(this.biome)));
			if (this.hasPrecipitation)
				builder.append(" PRECIPITATION");
			if (this.hasDust)
				builder.append(" DUST");
			if (this.hasAurora)
				builder.append(" AURORA");
			if (this.hasFog)
				builder.append(" FOG");
			if (!this.hasPrecipitation && !this.hasDust && !this.hasAurora && !this.hasFog)
				builder.append(" NONE");
			if (this.dustColor != null)
				builder.append(" dustColor:").append(this.dustColor.toString());
			if (this.fogColor != null) {
				builder.append(" fogColor:").append(this.fogColor.toString());
				builder.append(" fogDensity:").append(this.fogDensity);
			}

			if (!this.sounds.isEmpty()) {
				builder.append("; sounds [");
				for (final SoundEffect sound : this.sounds)
					builder.append(sound.toString()).append(',');
				builder.append(']');
			}

			if (!this.spotSounds.isEmpty()) {
				builder.append("; spot sound chance:").append(this.spotSoundChance);
				builder.append(" spot sounds [");
				for (final SoundEffect sound : this.spotSounds)
					builder.append(sound.toString()).append(',');
				builder.append(']');
			}
			return builder.toString();
		}
	}

	public static String resolveName(final BiomeGenBase biome) {
		if (biome == null)
			return "(Bad Biome)";
		if (StringUtils.isEmpty(biome.biomeName))
			return new StringBuilder().append('#').append(biome.biomeID).toString();
		return biome.biomeName;
	}

	public static void initialize() {

		biomeAliases.clear();
		for (final String entry : ModOptions.biomeAliases) {
			final String[] parts = StringUtils.split(entry, "=");
			if (parts.length == 2) {
				biomeAliases.put(parts[0], parts[1]);
			}
		}

		registry.clear();

		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		for (int i = 0; i < biomeArray.length; i++)
			if (biomeArray[i] != null) {
				registry.put(biomeArray[i].biomeID, new Entry(biomeArray[i]));
			}

		// Add our fake biomes
		registry.put(UNDERGROUND.biomeID, new Entry(UNDERGROUND));
		registry.put(UNDERWATER.biomeID, new Entry(UNDERWATER));
		registry.put(UNDEROCEAN.biomeID, new Entry(UNDEROCEAN));
		registry.put(UNDERDEEPOCEAN.biomeID, new Entry(UNDERDEEPOCEAN));
		registry.put(UNDERRIVER.biomeID, new Entry(UNDERRIVER));
		registry.put(OUTERSPACE.biomeID, new Entry(OUTERSPACE));
		registry.put(CLOUDS.biomeID, new Entry(CLOUDS));
		registry.put(PLAYER.biomeID, new Entry(PLAYER));
		registry.put(WTF.biomeID, new Entry(WTF));

		processConfig();

		if (ModOptions.enableDebugLogging) {
			ModLog.info("*** BIOME REGISTRY ***");
			for (final Entry entry : registry.valueCollection())
				ModLog.info(entry.toString());
		}

		// Free memory because we no longer need
		biomeAliases.clear();
		
		MinecraftForge.EVENT_BUS.post(new RegistryReloadEvent.Biome());
	}

	private static Entry get(final BiomeGenBase biome) {
		Entry entry = registry.get(biome == null ? WTF.biomeID : biome.biomeID);
		if (entry == null) {
			ModLog.warn("Biome [%s] was not detected during initial scan! Reloading config...", resolveName(biome));
			initialize();
			entry = registry.get(biome.biomeID);
			if (entry == null) {
				ModLog.warn("Still can't find biome [%s]! Explicitly adding at defaults", resolveName(biome));
				entry = new Entry(biome);
				registry.put(biome.biomeID, entry);
			}
		}
		return entry;
	}

	public static boolean hasDust(final BiomeGenBase biome) {
		return get(biome).hasDust;
	}

	public static boolean hasPrecipitation(final BiomeGenBase biome) {
		return get(biome).hasPrecipitation;
	}

	public static boolean hasAurora(final BiomeGenBase biome) {
		return get(biome).hasAurora;
	}

	public static boolean hasFog(final BiomeGenBase biome) {
		return get(biome).hasFog;
	}

	public static Color getDustColor(final BiomeGenBase biome) {
		return get(biome).dustColor;
	}

	public static Color getFogColor(final BiomeGenBase biome) {
		return get(biome).fogColor;
	}

	public static float getFogDensity(final BiomeGenBase biome) {
		return get(biome).fogDensity;
	}

	public static SoundEffect getSound(final BiomeGenBase biome, final String conditions) {
		return get(biome).findSoundMatch(conditions);
	}

	public static List<SoundEffect> getSounds(final BiomeGenBase biome, final String conditions) {
		return get(biome).findSoundMatches(conditions);
	}

	public static SoundEffect getSpotSound(final BiomeGenBase biome, final String conditions, final Random random) {
		final Entry e = get(biome);
		if (e == null || e.spotSounds.isEmpty() || random.nextInt(e.spotSoundChance) != 0)
			return null;

		int totalWeight = 0;
		final List<SoundEffect> candidates = new ArrayList<SoundEffect>();
		for (final SoundEffect s : e.spotSounds)
			if (s.matches(conditions)) {
				candidates.add(s);
				totalWeight += s.weight;
			}
		if (totalWeight <= 0)
			return null;

		if (candidates.size() == 1)
			return candidates.get(0);

		int targetWeight = random.nextInt(totalWeight);
		int i = 0;
		for (i = candidates.size(); (targetWeight -= candidates.get(i - 1).weight) >= 0; i--)
			;

		return candidates.get(i - 1);
	}

	private static void processConfig() {
		try {
			process(BiomeConfig.load(Module.MOD_ID));
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final String[] configFiles = ModOptions.biomeConfigFiles;
		for (final String file : configFiles) {
			final File theFile = new File(Module.dataDirectory(), file);
			if (theFile.exists()) {
				try {
					final BiomeConfig config = BiomeConfig.load(theFile);
					if (config != null)
						process(config);
					else
						ModLog.warn("Unable to process biome config file " + file);
				} catch (final Exception ex) {
					ModLog.error("Unable to process biome config file " + file, ex);
				}
			} else {
				ModLog.warn("Could not locate biome config file [%s]", file);
			}
		}
	}

	final static boolean isBiomeMatch(final BiomeConfig.Entry entry, final String biomeName) {
		if (Pattern.matches(entry.biomeName, biomeName))
			return true;
		final String alias = biomeAliases.get(biomeName);
		return alias == null ? false : Pattern.matches(entry.biomeName, alias);
	}

	private static void process(final BiomeConfig config) {
		for (final BiomeConfig.Entry entry : config.entries) {
			for (final Entry biomeEntry : registry.valueCollection()) {
				if (isBiomeMatch(entry, resolveName(biomeEntry.biome))) {
					if (entry.hasPrecipitation != null)
						biomeEntry.hasPrecipitation = entry.hasPrecipitation.booleanValue();
					if (entry.hasAurora != null)
						biomeEntry.hasAurora = entry.hasAurora.booleanValue();
					if (entry.hasDust != null)
						biomeEntry.hasDust = entry.hasDust.booleanValue();
					if (entry.hasFog != null)
						biomeEntry.hasFog = entry.hasFog.booleanValue();
					if (entry.fogDensity != null)
						biomeEntry.fogDensity = entry.fogDensity.floatValue();
					if (entry.fogColor != null) {
						final int[] rgb = MyUtils.splitToInts(entry.fogColor, ',');
						if (rgb.length == 3)
							biomeEntry.fogColor = new Color(rgb[0], rgb[1], rgb[2]);
					}
					if (entry.dustColor != null) {
						final int[] rgb = MyUtils.splitToInts(entry.dustColor, ',');
						if (rgb.length == 3)
							biomeEntry.dustColor = new Color(rgb[0], rgb[1], rgb[2]);
					}
					if (entry.soundReset != null && entry.soundReset.booleanValue()) {
						biomeEntry.sounds = new ArrayList<SoundEffect>();
						biomeEntry.spotSounds = new ArrayList<SoundEffect>();
					}

					if (entry.spotSoundChance != null)
						biomeEntry.spotSoundChance = entry.spotSoundChance.intValue();

					for (final SoundConfig sr : entry.sounds) {
						if (SoundRegistry.isSoundBlocked(sr.sound))
							continue;
						final SoundEffect s = new SoundEffect(sr);
						if (s.type == SoundType.SPOT)
							biomeEntry.spotSounds.add(s);
						else
							biomeEntry.sounds.add(s);
					}
				}
			}
		}
	}
}
