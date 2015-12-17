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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.RainData;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

@SideOnly(Side.CLIENT)
public class ClientRainHandler {

	private static final Random random = new Random();

	private static final int PARTICLE_SETTING_ALL = 0;
	private static final int PARTICLE_SETTING_DECREASED = 1;
	private static final int PARTICLE_SETTING_MINIMAL = 2;

	private static final float MIN_STRENGTH_FOR_PARTICLE_SPAWN = 0.1F;
	private static final int RANGE_FACTOR = 10;
	private static final float SOUND_LEVEL = ModOptions.getSoundLevel();

	public static int rainSoundCounter = 0;
	public static float strength = 0;

	public static void setRainStrength(final float str) {
		strength = MathHelper.clamp_float(str, RainData.MIN_STRENGTH, RainData.MAX_STRENGTH);
	}

	private ClientRainHandler() {
	}

	public static void initialize() {
		final ClientRainHandler handler = new ClientRainHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}

	/**
	 * Cull some of the rain entities that are spawned to give visual "density"
	 * of the rain.
	 */
	@SubscribeEvent
	public void entityEvent(final EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityRainFX
				&& (strength < MIN_STRENGTH_FOR_PARTICLE_SPAWN || strength < random.nextFloat()))
			event.entity.setDead();
	}

	@SubscribeEvent
	public void tickEvent(final TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START)
			return;

		// For some reason this ticks when the client loads but
		// without a world loaded. Also, if the loaded world
		// is not a surface world return.
		final World world = Minecraft.getMinecraft().theWorld;
		if (world == null || !world.provider.isSurfaceWorld())
			return;

		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.isSingleplayer() && mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame()
				&& !mc.getIntegratedServer().getPublic())
			return;

		// Set the intensity based on the strength we
		// have from the server.
		RainIntensity.setIntensity(strength);

		// Get the world rain strength. If it is 0 there is
		// nothing to do.
		float worldRainStrengthFactor = world.getRainStrength(1.0F);
		if (worldRainStrengthFactor == 0.0F)
			return;

		// Cut back on the strength factor if the client
		// is not running with fancy graphics.
		if (!mc.gameSettings.fancyGraphics)
			worldRainStrengthFactor /= 2.0F;

		// Determine the number of particle effects
		// that need to be spawned.
		worldRainStrengthFactor *= worldRainStrengthFactor;
		int particleCount = 0;
		switch (mc.gameSettings.particleSetting) {
		case PARTICLE_SETTING_ALL:
			particleCount = (int) (100.0F * worldRainStrengthFactor);
			break;
		case PARTICLE_SETTING_DECREASED:
			particleCount = (int) (50.0F * worldRainStrengthFactor);
			break;
		case PARTICLE_SETTING_MINIMAL:
			particleCount = 0;
			break;
		default:
			;
		}

		final EntityLivingBase entitylivingbase = mc.renderViewEntity;
		final int playerX = MathHelper.floor_double(entitylivingbase.posX);
		final int playerY = MathHelper.floor_double(entitylivingbase.posY);
		final int playerZ = MathHelper.floor_double(entitylivingbase.posZ);

		double xx = 0.0D;
		double yy = 0.0D;
		double zz = 0.0D;
		int rainParticlesSpawned = 0;

		for (int i = 0; i < particleCount; i++) {
			final int locX = playerX + random.nextInt(RANGE_FACTOR) - random.nextInt(RANGE_FACTOR);
			final int locZ = playerZ + random.nextInt(RANGE_FACTOR) - random.nextInt(RANGE_FACTOR);
			final BiomeGenBase biomegenbase = world.getBiomeGenForCoords(locX, locZ);

			// If a lightening bolt can't spawn at the location
			// it is not eligible for what we need.
			if (!biomegenbase.canSpawnLightningBolt())
				continue;

			// If it is freezing it is not what we need.
			final int locY = world.getPrecipitationHeight(locX, locZ);
			if (biomegenbase.getFloatTemperature(locX, locY, locZ) < 0.15F)
				continue;

			// If the Y location happens to be outside the range we
			// ignore.
			if ((locY > playerY + RANGE_FACTOR) || (locY < playerY - RANGE_FACTOR))
				continue;

			final Block block = world.getBlock(locX, locY - 1, locZ);

			// If landing block for the particle happens to be air
			// skip.
			if (block == Blocks.air)
				continue;

			final double spawnX = locX + random.nextFloat();
			final double spawnY = locY + 0.1F - block.getBlockBoundsMinY();
			final double spawnZ = locZ + random.nextFloat();

			if (block.getMaterial() == Material.lava) {
				mc.effectRenderer.addEffect(new EntitySmokeFX(world, spawnX, spawnY, spawnZ, 0.0D, 0.0D, 0.0D));
			} else {

				if (random.nextInt(++rainParticlesSpawned) == 0) {
					xx = spawnX;
					yy = spawnY;
					zz = spawnZ;
				}

				// Spawn extra splash particles if the minimum strength
				// rain is present. Reason is that we don't want to
				// spawn extra particles just to have them whacked in
				// the EntityConstructing event handler.
				if (strength >= MIN_STRENGTH_FOR_PARTICLE_SPAWN)
					mc.effectRenderer.addEffect(new EntityRainFX(world, spawnX, spawnY, spawnZ));
			}
		}

		if (rainParticlesSpawned > 0 && (random.nextInt(3) < rainSoundCounter++)) {
			rainSoundCounter = 0;

			float pitch = 1.0F;

			if ((yy > entitylivingbase.posY + 1.0D) && (world.getPrecipitationHeight(playerX, playerZ) > playerY)) {
				pitch = 0.5F;
			}
			world.playSound(xx, yy, zz, RainIntensity.getIntensity().getRainSound(), strength * SOUND_LEVEL, pitch, false);
		}
	}
}
