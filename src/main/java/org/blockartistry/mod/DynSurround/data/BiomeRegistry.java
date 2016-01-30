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
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.Module;
import org.blockartistry.mod.DynSurround.data.BiomeConfig.SoundRecord;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.MyUtils;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.biome.BiomeGenBase;

public final class BiomeRegistry {

	private static int reloadCount = 0;
	private static final TIntObjectHashMap<Entry> registry = new TIntObjectHashMap<Entry>();

	public static final BiomeGenBase UNDERGROUND = new FakeBiome(-1, "Underground");
	public static final BiomeGenBase INSIDE = new FakeBiome(-2, "Inside");
	public static final BiomeGenBase UNDERWATER = new FakeBiome(-3, "Underwater");

	public static final BiomeSound WATER_DRIP = new BiomeSound(Module.MOD_ID + ":waterdrops", 0.1F, 1.0F);

	// This is for cases when the biome coming in doesn't make sense
	// and should default to something to avoid crap.
	private static final BiomeGenBase WTF = new FakeBiome(-256, "(FooBar)");

	public static final class BiomeSound {
		public final String sound;
		public final String conditions;
		private final Pattern pattern;
		public final float volume;
		public final float pitch;
		public final int weight;
		public final boolean isSpot;

		protected BiomeSound(final String sound, final float volume, final float pitch) {
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
			this.conditions = ".*";
			this.pattern = null;
			this.weight = 0;
			this.isSpot = true;
		}

		protected BiomeSound(final SoundRecord record) {
			this.sound = record.sound;
			this.conditions = StringUtils.isEmpty(record.conditions) ? ".*" : record.conditions;
			this.volume = record.volume == null ? 1.0F : record.volume.floatValue();
			this.pitch = record.pitch == null ? 1.0F : record.pitch.floatValue();
			this.pattern = Pattern.compile(this.conditions);
			this.weight = record.weight == null ? 10 : record.weight.intValue();
			this.isSpot = record.spotSound != null && record.spotSound.booleanValue();
		}

		public boolean matches(final String conditions) {
			return pattern.matcher(conditions).matches();
		}

		@Override
		public boolean equals(final Object anObj) {
			if (this == anObj)
				return true;
			if (!(anObj instanceof BiomeSound))
				return false;
			final BiomeSound s = (BiomeSound) anObj;
			return this.volume == s.volume && this.pitch == s.pitch && this.sound.equals(s.sound)
					&& this.conditions.equals(s.conditions);
		}

		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append('[').append(this.sound);
			if (!StringUtils.isEmpty(this.conditions))
				builder.append('(').append(this.conditions).append(')');
			builder.append(", v:").append(this.volume);
			builder.append(", p:").append(this.pitch);
			if (this.isSpot)
				builder.append(", w:").append(this.weight);
			builder.append(']');
			return builder.toString();
		}
	}

	private static class Entry {

		public final BiomeGenBase biome;
		public boolean hasPrecipitation;
		public boolean hasDust;
		public boolean hasAurora;
		public boolean hasFog;

		public Color dustColor;
		public Color fogColor;
		public float fogDensity;

		public List<BiomeSound> sounds;

		public int spotSoundChance;
		public List<BiomeSound> spotSounds;

		public Entry(final BiomeGenBase biome) {
			this.biome = biome;
			this.hasPrecipitation = biome.canSpawnLightningBolt() || biome.getEnableSnow();
			this.sounds = new ArrayList<BiomeSound>();
			this.spotSounds = new ArrayList<BiomeSound>();
			this.spotSoundChance = 600;
		}

		public BiomeSound findMatch(final String conditions) {
			for (final BiomeSound sound : this.sounds)
				if (sound.matches(conditions))
					return sound;
			return null;
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
				for (final BiomeSound sound : this.sounds)
					builder.append(sound.toString()).append(',');
				builder.append(']');
			}

			if (!this.spotSounds.isEmpty()) {
				builder.append("; spot sound chance:").append(this.spotSoundChance);
				builder.append(" spot sounds [");
				for (final BiomeSound sound : this.spotSounds)
					builder.append(sound.toString()).append(',');
				builder.append(']');
			}
			return builder.toString();
		}
	}

	public static int getReloadCount() {
		return reloadCount;
	}

	public static String resolveName(final BiomeGenBase biome) {
		if (biome == null)
			return "(Bad Biome)";
		if (StringUtils.isEmpty(biome.biomeName))
			return new StringBuilder().append('#').append(biome.biomeID).toString();
		return biome.biomeName;
	}

	public static void initialize() {

		reloadCount++;
		registry.clear();

		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		for (int i = 0; i < biomeArray.length; i++)
			if (biomeArray[i] != null) {
				registry.put(biomeArray[i].biomeID, new Entry(biomeArray[i]));
			}

		// Add our fake biomes
		registry.put(UNDERGROUND.biomeID, new Entry(UNDERGROUND));
		registry.put(INSIDE.biomeID, new Entry(INSIDE));
		registry.put(UNDERWATER.biomeID, new Entry(UNDERWATER));
		registry.put(WTF.biomeID, new Entry(WTF));

		processConfig();

		ModLog.info("*** BIOME REGISTRY ***");
		for (final Entry entry : registry.valueCollection())
			ModLog.info(entry.toString());
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

	public static BiomeSound getSound(final BiomeGenBase biome, final String conditions) {
		return get(biome).findMatch(conditions);
	}

	public static BiomeSound getSpotSound(final BiomeGenBase biome, final String conditions, final Random random) {
		final Entry e = get(biome);
		if (e == null || e.spotSounds.isEmpty() || random.nextInt(e.spotSoundChance) != 0)
			return null;

		int totalWeight = 0;
		final List<BiomeSound> candidates = new ArrayList<BiomeSound>();
		for (final BiomeSound s : e.spotSounds)
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

		final String[] configFiles = ModOptions.getBiomeConfigFiles();
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

	private static void process(final BiomeConfig config) {
		for (final BiomeConfig.Entry entry : config.entries) {
			final Pattern pattern = Pattern.compile(entry.biomeName);
			for (final Entry biomeEntry : registry.valueCollection()) {
				final Matcher m = pattern.matcher(resolveName(biomeEntry.biome));
				if (m.matches()) {
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
						biomeEntry.sounds = new ArrayList<BiomeSound>();
						biomeEntry.spotSounds = new ArrayList<BiomeSound>();
					}

					if (entry.spotSoundChance != null)
						biomeEntry.spotSoundChance = entry.spotSoundChance.intValue();

					for (final SoundRecord sr : entry.sounds) {
						final BiomeSound s = new BiomeSound(sr);
						if (sr.spotSound != null && sr.spotSound.booleanValue())
							biomeEntry.spotSounds.add(s);
						else
							biomeEntry.sounds.add(s);
					}
				}
			}
		}
	}
}
