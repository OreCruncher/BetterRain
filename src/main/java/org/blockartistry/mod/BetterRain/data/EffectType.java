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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;

import net.minecraft.world.biome.BiomeGenBase;

public final class EffectType {

	private static final int AURORA = -1;

	private static final int NONE = 0;
	private static final int DUST = 1;
	private static final int PRECIPITATION = 2;
	private static final String[] names = new String[] { "NONE", "DUST", "PRECIPITATION" };

	private static final Map<BiomeGenBase, Integer> registry = new IdentityHashMap<BiomeGenBase, Integer>();
	private static final Map<String, BiomeGenBase> nameMap = new HashMap<String, BiomeGenBase>();
	private static final Set<BiomeGenBase> auroraBiomes = new HashSet<BiomeGenBase>();

	public static BiomeGenBase findBiome(final String name) {
		return nameMap.get(name);
	}

	private static final String[] DESERT_TOKENS = new String[] { "desert", "sand", "mesa", "wasteland", "sahara" };

	private static final String[] POLAR_TOKENS = new String[] { "taiga", "frozen", "ice", "tundra", "polar", "snow",
			"glacier" };

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

	private static void processBiomeList(final String biomeNames, final int type) {
		final String[] names = StringUtils.split(biomeNames, ',');
		if (names == null || names.length == 0)
			return;
		for (final String name : names) {
			if (type == AURORA)
				registerAuroraBiome(name);
			else
				registerBiome(name, type);
		}
	}

	public static void initialize() {
		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		for (int i = 0; i < biomeArray.length; i++) {
			if (biomeArray[i] != null) {
				final BiomeGenBase biome = biomeArray[i];
				int type = NONE;
				if (biome.canSpawnLightningBolt() || biome.getEnableSnow())
					type = PRECIPITATION;
				else if (looksLikeDust(biome))
					type = DUST;
				registerBiome(biome, type);
				nameMap.put(biome.biomeName, biome);

				if (looksLikeAurora(biome))
					auroraBiomes.add(biome);
			}
		}

		processBiomeList(ModOptions.getPrecipitationBiomes(), PRECIPITATION);
		processBiomeList(ModOptions.getDustBiomes(), DUST);
		processBiomeList(ModOptions.getNoneBiomes(), NONE);
		processBiomeList(ModOptions.getAuroraAffectedBiomes(), AURORA);

		for (final Entry<BiomeGenBase, Integer> entry : registry.entrySet()) {
			final BiomeGenBase biome = entry.getKey();
			ModLog.info("Biome %d [%s]: %s %s", biome.biomeID, biome.biomeName, names[entry.getValue()],
					(hasAuroraEffect(biome) ? "AURORA" : ""));
		}
	}

	public static void registerBiome(final BiomeGenBase biome, final int type) {
		registry.put(biome, Integer.valueOf(type));
	}

	public static void registerBiome(final int biomeId, final int type) {
		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		if (biomeArray[biomeId] == null) {
			ModLog.warn("Unknown biome ID registering biome " + biomeId);
		} else {
			registerBiome(biomeArray[biomeId], type);
		}
	}

	public static void registerBiome(final String name, final int type) {
		final BiomeGenBase biome = nameMap.get(name);
		if (biome != null)
			registerBiome(biome, type);
	}

	public static int get(final BiomeGenBase biome) {
		final Integer type = registry.get(biome);
		return type == null ? NONE : type.intValue();
	}

	public static boolean hasDust(final BiomeGenBase biome) {
		return get(biome) == DUST;
	}

	public static boolean hasPrecipitation(final BiomeGenBase biome) {
		return get(biome) == PRECIPITATION;
	}

	public static void registerAuroraBiome(final String name) {
		final BiomeGenBase biome = nameMap.get(name);
		if (biome != null)
			registerAuroraBiome(biome);
	}

	public static void registerAuroraBiome(final BiomeGenBase biome) {
		auroraBiomes.add(biome);
	}

	public static boolean hasAuroraEffect(final BiomeGenBase biome) {
		return auroraBiomes.contains(biome);
	}
}
