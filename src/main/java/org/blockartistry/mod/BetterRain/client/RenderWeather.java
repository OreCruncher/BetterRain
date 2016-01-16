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

import java.util.ArrayList;
import java.util.List;

import org.blockartistry.mod.BetterRain.client.aurora.AuroraRenderer;
import org.blockartistry.mod.BetterRain.client.rain.RainProperties;
import org.blockartistry.mod.BetterRain.client.rain.RainSnowRenderer;
import org.blockartistry.mod.BetterRain.data.BiomeRegistry;
import org.blockartistry.mod.BetterRain.util.XorShiftRandom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

@SideOnly(Side.CLIENT)
public final class RenderWeather {
	
	private static final List<IAtmosRenderer> renderList = new ArrayList<IAtmosRenderer>();
	public static void register(final IAtmosRenderer renderer) {
		renderList.add(renderer);
	}
	
	static {
		register(new RainSnowRenderer());
		register(new AuroraRenderer());
	}

	private static final XorShiftRandom random = new XorShiftRandom();

	private static final int RANGE_FACTOR = 10;

	/*
	 * Render rain particles.
	 * 
	 * Redirect from EntityRenderer.
	 */
	public static void addRainParticles(final EntityRenderer theThis) {
		if (theThis.mc.gameSettings.particleSetting == 2)
			return;

		float rainStrengthFactor = theThis.mc.theWorld.getRainStrength(1.0F);
		if (!theThis.mc.gameSettings.fancyGraphics)
			rainStrengthFactor /= 2.0F;

		if (rainStrengthFactor == 0.0F)
			return;

		random.setSeed((long) theThis.rendererUpdateCount * 312987231L);
		final EntityLivingBase entity = theThis.mc.renderViewEntity;
		final WorldClient worldclient = theThis.mc.theWorld;
		final int playerX = MathHelper.floor_double(entity.posX);
		final int playerY = MathHelper.floor_double(entity.posY);
		final int playerZ = MathHelper.floor_double(entity.posZ);
		double spawnX = 0.0D;
		double spawnY = 0.0D;
		double spawnZ = 0.0D;
		int particlesSpawned = 0;

		int particleCount = (int) (250.0F * rainStrengthFactor * rainStrengthFactor); 

		if (theThis.mc.gameSettings.particleSetting == 1)
			particleCount >>= 1;

		for (int j1 = 0; j1 < particleCount; ++j1) {
			final int locX = playerX + random.nextInt(RANGE_FACTOR) - random.nextInt(RANGE_FACTOR);
			final int locZ = playerZ + random.nextInt(RANGE_FACTOR) - random.nextInt(RANGE_FACTOR);
			final int locY = worldclient.getPrecipitationHeight(locX, locZ);
			final BiomeGenBase biome = worldclient.getBiomeGenForCoords(locX, locZ);
			final boolean hasDust = WeatherUtils.biomeHasDust(biome);

			if (locY <= playerY + RANGE_FACTOR && locY >= playerY - RANGE_FACTOR && (hasDust
					|| (BiomeRegistry.hasPrecipitation(biome) && biome.getFloatTemperature(locX, locY, locZ) >= 0.15F))) {

				final Block block = worldclient.getBlock(locX, locY - 1, locZ);
				final double posX = locX + random.nextFloat();
				final double posY = locY + 0.1F - block.getBlockBoundsMinY();
				final double posZ = locZ + random.nextFloat();

				EntityFX particle = null;
				if (block.getMaterial() == Material.lava) {
					if (!hasDust)
						particle = new EntitySmokeFX(worldclient, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
				} else if (block.getMaterial() != Material.air) {

					if (random.nextInt(++particlesSpawned) == 0) {
						spawnX = posX;
						spawnY = posY;
						spawnZ = posZ;
					}

					if (!hasDust)
						particle = new EntityRainFX(worldclient, posX, posY, posZ);
				}

				if (particle != null)
					theThis.mc.effectRenderer.addEffect(particle);
			}
		}

		// Handle precipitation sounds
		if (particlesSpawned > 0 && random.nextInt(3) < theThis.rainSoundCounter++) {
			theThis.rainSoundCounter = 0;

			final boolean hasDust = WeatherUtils.biomeHasDust(worldclient.getBiomeGenForCoords((int) spawnX, (int) spawnZ));
			final String sound = hasDust ? RainProperties.getIntensity().getDustSound()
					: RainProperties.getIntensity().getRainSound();
			final float volume = RainProperties.getCurrentRainVolume();
			float pitch = 1.0F;
			if (spawnY > entity.posY + 1.0D && worldclient.getPrecipitationHeight(playerX, playerZ) > playerY)
				pitch = 0.5F;
			theThis.mc.theWorld.playSound(spawnX, spawnY, spawnZ, sound, volume, pitch, false);
		}
	}

	/*
	 * Render atmospheric effects.
	 * 
	 * Redirect from EntityRenderer.
	 */
	public static void renderRainSnow(final EntityRenderer theThis, final float partialTicks) {
		for(final IAtmosRenderer renderer: renderList)
			renderer.render(theThis, partialTicks);
	}
}
