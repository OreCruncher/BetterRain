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

package org.blockartistry.mod.BetterRain.data;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.BetterRain.BetterRain;
import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.util.Color;
import org.blockartistry.mod.BetterRain.util.MyUtils;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.biome.BiomeGenBase;

public final class BiomeRegistry {

	private static int reloadCount = 0;
	private static final TIntObjectHashMap<Entry> registry = new TIntObjectHashMap<Entry>();

	public static final class BiomeSound {
		public final String sound;
		public final float volume;
		public final float pitch;
		public final boolean atDay;
		public final boolean atNight;

		protected BiomeSound(final String sound, final float volume, final float pitch, final boolean day,
				final boolean night) {
			this.sound = sound;
			this.volume = volume;
			this.pitch = pitch;
			this.atDay = day;
			this.atNight = night;
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

		public String sound;
		public float volume;
		public float pitch;
		public boolean atDay;
		public boolean atNight;

		public Entry(final BiomeGenBase biome) {
			this.biome = biome;
			this.hasPrecipitation = biome.canSpawnLightningBolt() || biome.getEnableSnow();

			this.volume = this.pitch = 1.0F;
			this.atDay = this.atNight = true;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append(String.format("Biome %d [%s]:", this.biome.biomeID, this.biome.biomeName));
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
			if (this.sound != null) {
				builder.append("; sound:").append(this.sound);
				builder.append(" (volume:").append(this.volume);
				builder.append(", pitch:").append(this.pitch);
				builder.append(", day:").append(Boolean.toString(this.atDay));
				builder.append(", night:").append(Boolean.toString(this.atNight));
				builder.append(")");
			}
			return builder.toString();
		}
	}

	public static int getReloadCount() {
		return reloadCount;
	}
	
	public static void initialize() {

		reloadCount++;
		registry.clear();
		
		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		for (int i = 0; i < biomeArray.length; i++)
			if (biomeArray[i] != null) {
				registry.put(biomeArray[i].biomeID, new Entry(biomeArray[i]));
			}

		processConfig();

		for (final Entry entry : registry.valueCollection())
			ModLog.info(entry.toString());
	}

	public static boolean hasDust(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).hasDust;
	}

	public static boolean hasPrecipitation(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).hasPrecipitation;
	}

	public static boolean hasAurora(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).hasAurora;
	}

	public static boolean hasFog(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).hasFog;
	}

	public static boolean hasSound(final BiomeGenBase biome) {
		return !StringUtils.isEmpty(registry.get(biome.biomeID).sound);
	}

	public static Color getDustColor(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).dustColor;
	}

	public static Color getFogColor(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).fogColor;
	}

	public static float getFogDensity(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).fogDensity;
	}

	public static BiomeSound getSound(final BiomeGenBase biome) {
		final Entry entry = registry.get(biome.biomeID);
		if (StringUtils.isEmpty(entry.sound))
			return null;
		return new BiomeSound(entry.sound, entry.volume, entry.pitch, entry.atDay, entry.atNight);
	}

	private static void processConfig() {
		process(BiomeConfig.load(BetterRain.MOD_ID));

		final String[] configFiles = ModOptions.getBiomeConfigFiles();
		for (final String file : configFiles) {
			final File theFile = new File(BetterRain.dataDirectory(), file);
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
				final Matcher m = pattern.matcher(biomeEntry.biome.biomeName);
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
					if (entry.sound != null)
						biomeEntry.sound = entry.sound;
					if (entry.volume != null)
						biomeEntry.volume = entry.volume.floatValue();
					if (entry.pitch != null)
						biomeEntry.pitch = entry.pitch.floatValue();
					if (entry.atDay != null)
						biomeEntry.atDay = entry.atDay.booleanValue();
					if (entry.atNight != null)
						biomeEntry.atNight = entry.atNight.booleanValue();
				}
			}
		}
	}
}
