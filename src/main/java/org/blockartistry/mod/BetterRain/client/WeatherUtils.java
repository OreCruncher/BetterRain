package org.blockartistry.mod.BetterRain.client;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.client.rain.RainProperties;
import org.blockartistry.mod.BetterRain.data.EffectType;

import net.minecraft.world.biome.BiomeGenBase;

public class WeatherUtils {

	private static final boolean BLOW_DUST = ModOptions.getAllowDesertDust();

	public static boolean biomeHasDust(final BiomeGenBase biome) {
		return BLOW_DUST && EffectType.hasDust(biome) && !RainProperties.doVanillaRain();
	}
}
