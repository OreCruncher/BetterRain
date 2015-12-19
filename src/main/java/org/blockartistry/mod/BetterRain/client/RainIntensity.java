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

package org.blockartistry.mod.BetterRain.client;

import org.blockartistry.mod.BetterRain.BetterRain;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.RainData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public enum RainIntensity {

	VANILLA, NONE(0.0F, "rain_calm", "snow_calm", "rain_calm"), CALM(0.1F, "rain_calm", "snow_calm",
			"rain_calm"), LIGHT(0.33F, "rain_light", "snow_light", "rain_calm"), NORMAL(0.66F, "rain_normal",
					"snow_normal", "rain_calm"), HEAVY(1.0F, "rain_heavy", "snow_heavy", "rain_calm");

	private static final float SOUND_LEVEL = ModOptions.getSoundLevel();

	private static float strength = 0.0F;
	private static RainIntensity intensity = VANILLA;

	private final float level;
	private final ResourceLocation rainTexture;
	private final ResourceLocation snowTexture;
	private final String rainSound;

	private RainIntensity() {
		this.level = -10.0F;
		this.rainTexture = EntityRenderer.locationRainPng;
		this.snowTexture = EntityRenderer.locationSnowPng;
		this.rainSound = String.format("%s:%s", BetterRain.MOD_ID, "rain_calm");
	}

	private RainIntensity(final float level, final String rainResource, final String snowResource, final String sound) {
		this.level = level;
		this.rainTexture = new ResourceLocation(BetterRain.MOD_ID,
				String.format("textures/environment/%s.png", rainResource));
		this.snowTexture = new ResourceLocation(BetterRain.MOD_ID,
				String.format("textures/environment/%s.png", snowResource));
		this.rainSound = String.format("%s:%s", BetterRain.MOD_ID, sound);
	}

	public static RainIntensity getIntensity() {
		return intensity;
	}

	public static float getStrength() {
		return strength;
	}

	public String getRainSound() {
		return this.rainSound;
	}

	public static float getCurrentRainVolume() {
		return (doVanillaRain() ? 0.66F : strength) * SOUND_LEVEL;
	}

	public static ResourceLocation getCurrentRainSound() {
		return new ResourceLocation(intensity.rainSound);
	}

	public static boolean doVanillaRain() {
		return intensity == VANILLA;
	}

	/**
	 * Sets the rain intensity based on the strength level provided. This is
	 * called by the packet handler when the server wants to set the intensity
	 * level on the client.
	 */
	public static void setIntensity(float level) {

		// If the level is Vanilla it means that
		// the rainfall in the dimension is to be
		// that of Vanilla.
		if (level == VANILLA.level) {
			intensity = VANILLA;
			strength = 0.0F;
			setTextures();
			return;
		}

		level = MathHelper.clamp_float(level, RainData.MIN_STRENGTH, RainData.MAX_STRENGTH);

		if (strength != level) {
			strength = level;
			if (strength <= NONE.level)
				intensity = NONE;
			else if (strength < CALM.level)
				intensity = CALM;
			else if (strength < LIGHT.level)
				intensity = LIGHT;
			else if (strength < NORMAL.level)
				intensity = NORMAL;
			else
				intensity = HEAVY;
		}
	}

	/**
	 * Set precipitation textures based on the current intensity. This is
	 * invoked before rendering takes place.
	 */
	public static void setTextures() {
		// AT transform removed final and made public.
		EntityRenderer.locationRainPng = intensity.rainTexture;
		EntityRenderer.locationSnowPng = intensity.snowTexture;
	}
}
