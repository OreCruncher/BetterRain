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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public enum RainIntensity {
	
	NONE(0.0F, "rain_calm", "rain_calm"),
	CALM(0.1F, "rain_calm", "rain_calm"),
	LIGHT(0.33F, "rain_light", "rain_calm"),
	NORMAL(0.66F, "rain_normal", "rain_calm"),
	HEAVY(1.0F, "rain_heavy", "rain_calm");

	private static RainIntensity current = NONE;
	
	private final float level;
	private final ResourceLocation texture;
	private final String sound;
	
	private RainIntensity(final float level, final String resource, final String sound) {
		this.level = level;
		this.texture = new ResourceLocation(BetterRain.MOD_ID, String.format("textures/environment/%s.png", resource));
		this.sound = String.format("%s:%s", BetterRain.MOD_ID, sound);
	}
	
	public static RainIntensity getIntensity() {
		return current;
	}
	
	public String getSound() {
		return this.sound;
	}
	
	public static void setIntensity(final float level) {
		
		RainIntensity intensity = null;
		if(level <= NONE.level)
			intensity = NONE;
		else if(level < CALM.level)
			intensity = CALM;
		else if(level < LIGHT.level)
			intensity = LIGHT;
		else if(level < NORMAL.level)
			intensity = NORMAL;
		else
			intensity = HEAVY;
		
		// If the intensity changed, change the PNG
		if(current != intensity) {
			current = intensity;
			// AT transform removed final and made public.
			EntityRenderer.locationRainPng = current.texture;
		}
	}
}
