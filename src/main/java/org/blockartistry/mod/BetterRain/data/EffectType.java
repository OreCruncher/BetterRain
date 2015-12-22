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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;

import net.minecraft.world.biome.BiomeGenBase;

public enum EffectType {
	
	NONE,
	DUST,
	PRECIPITATION;

	private static final Map<BiomeGenBase, EffectType> registry = new IdentityHashMap<BiomeGenBase, EffectType>();
	private static final Map<String, BiomeGenBase> nameMap = new HashMap<String, BiomeGenBase>();
	
	public static BiomeGenBase findBiome(final String name) {
		return nameMap.get(name);
	}
	
	private static boolean looksLikeDust(final BiomeGenBase biome) {
		final String name = biome.biomeName.toLowerCase();
		return name.contains("desert") || name.contains("sand") || name.contains("mesa") || name.contains("wasteland");
	}
	
	private static void processBiomeList(final String biomeNames, final EffectType type) {
		final String[] names = StringUtils.split(biomeNames, ',');
		if(names == null || names.length == 0)
			return;
		for(final String name: names)
			registerBiome(name, type);
	}
	
	public static void initialize() {
		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		for(int i = 0; i < biomeArray.length; i++) {
			if(biomeArray[i] != null) {
				final BiomeGenBase biome = biomeArray[i];
				EffectType type = NONE;
				if(biome.canSpawnLightningBolt() || biome.getEnableSnow())
					type = PRECIPITATION;
				else if(looksLikeDust(biome))
					type = DUST;
				registerBiome(biome, type);
				nameMap.put(biome.biomeName, biome);
			}
		}
		
		processBiomeList(ModOptions.getPrecipitationBiomes(), PRECIPITATION);
		processBiomeList(ModOptions.getDustBiomes(), DUST);
		processBiomeList(ModOptions.getNoneBiomes(), NONE);
		
		for(final Entry<BiomeGenBase, EffectType> entry : registry.entrySet()) {
			ModLog.info("Biome [%s]: %s", entry.getKey().biomeName, entry.getValue().toString());
		}
	}
	
	public static void registerBiome(final BiomeGenBase biome, final EffectType type) {
		registry.put(biome, type);
	}
	
	public static void registerBiome(final int biomeId, final EffectType type) {
		final BiomeGenBase[] biomeArray = BiomeGenBase.getBiomeGenArray();
		if(biomeArray[biomeId] == null) {
			ModLog.warn("Unknown biome ID registering biome " + biomeId);
		} else {
			registerBiome(biomeArray[biomeId], type);
		}
	}
	
	public static void registerBiome(final String name, final EffectType type) {
		final BiomeGenBase biome = nameMap.get(name);
		if(biome != null)
			registerBiome(biome, type);
	}
	
	public static EffectType get(final BiomeGenBase biome) {
		final EffectType type = registry.get(biome);
		return type == null ? NONE : type;
	}

	public static boolean hasDust(final BiomeGenBase biome) {
		return get(biome) == DUST;
	}
	
	public static boolean hasPrecipitation(final BiomeGenBase biome) {
		return get(biome) == PRECIPITATION;
	}
}
