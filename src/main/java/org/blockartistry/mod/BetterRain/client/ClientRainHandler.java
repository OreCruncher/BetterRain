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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

@SideOnly(Side.CLIENT)
public class ClientRainHandler {

	public static float rainSoundCounter;
	public static float strength;

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
		if (event.entity instanceof EntityRainFX) {
			if (strength < 0.1F) {
				event.entity.setDead();
			} else if (strength < new Random().nextFloat()) {
				event.entity.setDead();
			}
		}
	}

	@SubscribeEvent
	public void tickEvent(final TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START)
			return;

		// For some reason this ticks when the client loads but
		// without a world loaded.
		final World world = Minecraft.getMinecraft().theWorld;
		if (world == null || world.provider.dimensionId != 0)
			return;

		final Minecraft mc = Minecraft.getMinecraft();
		final EntityPlayer player = mc.thePlayer;
		final boolean b = (mc.isSingleplayer()) && (mc.currentScreen != null) && (mc.currentScreen.doesGuiPauseGame())
				&& (!mc.getIntegratedServer().getPublic());

		if (b)
			return;

		final Random random = player.worldObj.rand;
		float f = world.getRainStrength(1.0F);

		if (!mc.gameSettings.fancyGraphics) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			final EntityLivingBase entitylivingbase = mc.renderViewEntity;
			final int playerX = MathHelper.floor_double(entitylivingbase.posX);
			final int playerY = MathHelper.floor_double(entitylivingbase.posY);
			final int playerZ = MathHelper.floor_double(entitylivingbase.posZ);
			byte b0 = 10;
			double xx = 0.0D;
			double yy = 0.0D;
			double zz = 0.0D;
			int l = 0;
			int particleCount = (int) (100.0F * f * f);
			if (mc.gameSettings.particleSetting == 1) {
				particleCount >>= 1;
			} else if (mc.gameSettings.particleSetting == 2) {
				particleCount = 0;
			}

			for (int i = 0; i < particleCount; i++) {
				final int locX = playerX + random.nextInt(b0) - random.nextInt(b0);
				final int locZ = playerZ + random.nextInt(b0) - random.nextInt(b0);
				final int locY = world.getPrecipitationHeight(locX, locZ);
				final Block block = world.getBlock(locX, locY - 1, locZ);
				final BiomeGenBase biomegenbase = world.getBiomeGenForCoords(locX, locZ);

				if ((locY <= playerY + b0) && (locY >= playerY - b0) && (biomegenbase.canSpawnLightningBolt())
						&& (biomegenbase.getFloatTemperature(locX, locY, locZ) >= 0.15F)) {
					final float f1 = random.nextFloat();
					final float f2 = random.nextFloat();

					if (block.getMaterial() == Material.lava) {
						mc.effectRenderer.addEffect(new EntitySmokeFX(world, locX + f1,
								locY + 0.1F - block.getBlockBoundsMinY(), locZ + f2, 0.0D, 0.0D, 0.0D));
					} else if (block.getMaterial() != Material.air) {
						l++;

						if (random.nextInt(l) == 0) {
							xx = locX + f1;
							yy = locY + 0.1F - block.getBlockBoundsMinY();
							zz = locZ + f2;
						}

						mc.effectRenderer.addEffect(new EntityRainFX(world, locX + f1,
								locY + 0.1F - block.getBlockBoundsMinY(), locZ + f2));
					}
				}
			}

			if ((l > 0) && (random.nextInt(3) < rainSoundCounter++)) {
				rainSoundCounter = 0.0F;

				float pitch = 1.0F;

				if ((yy > entitylivingbase.posY + 1.0D)
						&& (world.getPrecipitationHeight(MathHelper.floor_double(entitylivingbase.posX),
								MathHelper.floor_double(entitylivingbase.posZ)) > MathHelper
										.floor_double(entitylivingbase.posY))) {
					pitch = 0.5F;
				}
				world.playSound(xx, yy, zz, RainIntensity.getIntensity().getSound(), /* 0.2F */ strength, pitch, false);
			}
		}

		RainIntensity.setIntensity(strength);
	}
}
