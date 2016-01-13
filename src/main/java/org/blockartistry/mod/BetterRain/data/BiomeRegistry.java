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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.util.Color;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.biome.BiomeGenBase;

public final class BiomeRegistry {

	private static final Color DEFAULT_DUST_COLOR = new Color(204, 185, 102);
	private static final Color DEFAULT_FOG_COLOR = new Color(127, 127, 127);
	private static final float DEFAULT_FOG_DENSITY = 0.1F;

	private static final String[] DESERT_TOKENS = new String[] { "desert", "sand", "mesa", "wasteland", "sahara" };
	private static final String[] POLAR_TOKENS = new String[] { "taiga", "frozen", "ice", "tundra", "polar", "snow",
			"glacier", "arctic" };
	private static final String[] GROUND_FOG_TOKENS = new String[] { "fen", "bog", "swamp", "marsh", "moor", "bayou" };

	private static boolean contains(String name, final String[] list) {
		name = name.toLowerCase();
		for (int i = 0; i < list.length; i++)
			if (name.contains(list[i]))
				return true;
		return false;
	}

	private static boolean looksLikeDust(final BiomeGenBase biome) {
		return contains(biome.biomeName, DESERT_TOKENS);
	}

	private static boolean looksLikeAurora(final BiomeGenBase biome) {
		return contains(biome.biomeName, POLAR_TOKENS);
	}

	private static boolean looksLikeFog(final BiomeGenBase biome) {
		return contains(biome.biomeName, GROUND_FOG_TOKENS);
	}

	private static final TIntObjectHashMap<Entry> registry = new TIntObjectHashMap<Entry>();
	private static final Map<String, BiomeGenBase> nameMap = new HashMap<String, BiomeGenBase>();

	private static class Entry {

		public final BiomeGenBase biome;
		public boolean hasPrecipitation;
		public boolean hasDust;
		public boolean hasAurora;
		public boolean hasFog;

		public Color dustColor;
		public Color fogColor;
		public float fogDensity;

		public Entry(final BiomeGenBase biome) {
			this.biome = biome;
			this.hasPrecipitation = biome.canSpawnLightningBolt() || biome.getEnableSnow();
			this.hasDust = looksLikeDust(biome);
			this.hasAurora = looksLikeAurora(biome);
			this.hasFog = looksLikeFog(biome);

			if (this.hasDust)
				this.dustColor = DEFAULT_DUST_COLOR;
			if (this.hasFog) {
				this.fogColor = DEFAULT_FOG_COLOR;
				this.fogDensity = DEFAULT_FOG_DENSITY;
			}
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
			return builder.toString();
		}
	}

	private static final int NONE = 0;
	private static final int DUST = 1;
	private static final int PRECIPITATION = 2;
	private static final int FOG = 3;
	private static final int AURORA = 4;

	private static void processBiomeList(final String biomeNames, final int type) {
		final String[] names = StringUtils.split(biomeNames, ',');
		if (names == null || names.length == 0)
			return;
		for (final String name : names) {
			final BiomeGenBase biome = nameMap.get(name);
			if (biome != null) {
				final Entry entry = registry.get(biome.biomeID);
				if (entry != null) {
					switch (type) {
					case NONE:
						entry.hasAurora = false;
						entry.hasDust = false;
						entry.hasFog = false;
						entry.hasPrecipitation = false;
						entry.dustColor = null;
						entry.fogColor = null;
						entry.fogDensity = 0.0F;
						break;
					case PRECIPITATION:
						entry.hasPrecipitation = true;
						break;
					case DUST:
						entry.hasDust = true;
						entry.dustColor = DEFAULT_DUST_COLOR;
						break;
					case AURORA:
						entry.hasAurora = true;
						break;
					case FOG:
						entry.hasFog = true;
						entry.fogColor = DEFAULT_FOG_COLOR;
						entry.fogDensity = DEFAULT_FOG_DENSITY;
						break;
					}
				}
			}
		}
	}

	public static void initialize() {

		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		for (int i = 0; i < biomeArray.length; i++)
			if (biomeArray[i] != null) {
				registry.put(biomeArray[i].biomeID, new Entry(biomeArray[i]));
				nameMap.put(biomeArray[i].biomeName, biomeArray[i]);
			}

		processBiomeList(ModOptions.getPrecipitationBiomes(), PRECIPITATION);
		processBiomeList(ModOptions.getDustBiomes(), DUST);
		processBiomeList(ModOptions.getNoneBiomes(), NONE);
		processBiomeList(ModOptions.getAuroraTriggerBiomes(), AURORA);
		// processBiomeList(null, GROUND_FOG);

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

	public static Color getDustColor(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).dustColor;
	}

	public static Color getFogColor(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).fogColor;
	}

	public static float getFogDensity(final BiomeGenBase biome) {
		return registry.get(biome.biomeID).fogDensity;
	}
}
