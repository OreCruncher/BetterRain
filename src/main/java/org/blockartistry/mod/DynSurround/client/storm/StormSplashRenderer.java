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

package org.blockartistry.mod.DynSurround.client.storm;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.client.WeatherUtils;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.fx.IParticleFactory;
import org.blockartistry.mod.DynSurround.client.fx.particle.ParticleFactory;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.util.DiurnalUtils;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

@SideOnly(Side.CLIENT)
public class StormSplashRenderer {

	private static final TIntObjectHashMap<StormSplashRenderer> splashRenderers = new TIntObjectHashMap<StormSplashRenderer>();
	private static final StormSplashRenderer DEFAULT = new StormSplashRenderer();

	static {
		splashRenderers.put(0, DEFAULT);
		splashRenderers.put(-1, new NetherSplashRenderer());
		splashRenderers.put(1, new NullSplashRenderer());
	}

	public static void renderStormSplashes(final int dimensionId, final EntityRenderer renderer) {
		StormSplashRenderer splash = splashRenderers.get(dimensionId);
		if (splash == null)
			splash = DEFAULT;
		splash.addRainParticles(renderer);
	}

	protected StormSplashRenderer() {

	}

	protected static final int RANGE_FACTOR = 10;
	protected static final XorShiftRandom random = new XorShiftRandom();
	protected static final NoiseGeneratorSimplex gen = new NoiseGeneratorSimplex(random);

	protected static float calculateRainSoundVolume(final World world) {
		return MathHelper.clamp_float((float) (StormProperties.getCurrentVolume()
				+ gen.func_151605_a(DiurnalUtils.getClockTime(world) / 100, 1) / 5.0F), 0.0F, 1.0F);
	}

	protected EntityFX getBlockParticleFX(final Block block, final boolean dust, final World world, final double x,
			final double y, final double z) {
		IParticleFactory factory = null;

		if (dust) {
			factory = null;
		} else if (block == Blocks.soul_sand) {
			factory = null;
		} else if (block == Blocks.netherrack && random.nextInt(20) == 0) {
			factory = ParticleFactory.lavaSpark;
		} else if (block.getMaterial() == Material.lava) {
			factory = ParticleFactory.smoke;
		} else if (block.getMaterial() != Material.air) {
			factory = ParticleFactory.rain;
		}

		return factory != null ? factory.getEntityFX(0, world, x, y, z, 0, 0, 0) : null;
	}

	protected String getBlockSoundFX(final Block block, final boolean hasDust, final World world) {
		if (hasDust)
			return StormProperties.getIntensity().getDustSound();
		if (block == Blocks.netherrack)
			return "minecraft:liquid.lavapop";
		return StormProperties.getIntensity().getStormSound();
	}

	protected int getPrecipitationHeight(final World world, final int range, final int x, final int z) {
		return world.getPrecipitationHeight(x, z);
	}

	protected void playSplashSound(final EntityRenderer renderer, final WorldClient world,
			final EntityLivingBase player, double x, double y, double z) {
		final int theX = MathHelper.floor_double(x);
		final int theY = MathHelper.floor_double(y);
		final int theZ = MathHelper.floor_double(z);

		final boolean hasDust = WeatherUtils.biomeHasDust(world.getBiomeGenForCoords(theX, theZ));
		final Block block = world.getBlock(theX, theY - 1, theZ);
		final String sound = getBlockSoundFX(block, hasDust, world);
		if (!StringUtils.isEmpty(sound)) {
			final float volume = calculateRainSoundVolume(world);
			float pitch = 1.0F;
			final int playerX = MathHelper.floor_double(player.posX);
			final int playerY = MathHelper.floor_double(player.posY);
			final int playerZ = MathHelper.floor_double(player.posZ);
			if (y > player.posY + 1.0D && world.getPrecipitationHeight(playerX, playerZ) > playerY)
				pitch = 0.5F;
			renderer.mc.theWorld.playSound(x, y, z, sound, volume, pitch, false);
		}
	}

	public void addRainParticles(final EntityRenderer theThis) {
		if (theThis.mc.gameSettings.particleSetting == 2)
			return;

		if (!DimensionRegistry.hasWeather(EnvironState.getWorld()))
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
			final int locY = getPrecipitationHeight(worldclient, RANGE_FACTOR / 2, locX, locZ);
			final BiomeGenBase biome = worldclient.getBiomeGenForCoords(locX, locZ);
			final boolean hasDust = WeatherUtils.biomeHasDust(biome);

			if (locY <= playerY + RANGE_FACTOR && locY >= playerY - RANGE_FACTOR
					&& (hasDust || (BiomeRegistry.hasPrecipitation(biome)
							&& biome.getFloatTemperature(locX, locY, locZ) >= 0.15F))) {

				final Block block = worldclient.getBlock(locX, locY - 1, locZ);
				final double posX = locX + random.nextFloat();
				final double posY = locY + 0.1F - block.getBlockBoundsMinY();
				final double posZ = locZ + random.nextFloat();

				final EntityFX particle = getBlockParticleFX(block, hasDust, worldclient, posX, posY, posZ);
				if (particle != null)
					theThis.mc.effectRenderer.addEffect(particle);

				if (random.nextInt(++particlesSpawned) == 0) {
					spawnX = posX;
					spawnY = posY;
					spawnZ = posZ;
				}
			}
		}

		if (particlesSpawned > 0 && random.nextInt(50) < theThis.rainSoundCounter++) {
			theThis.rainSoundCounter = 0;
			playSplashSound(theThis, worldclient, entity, spawnX, spawnY, spawnZ);
		}
	}
}
