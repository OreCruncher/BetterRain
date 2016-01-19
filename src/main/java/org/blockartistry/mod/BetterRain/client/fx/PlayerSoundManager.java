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

package org.blockartistry.mod.BetterRain.client.fx;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.BiomeRegistry;
import org.blockartistry.mod.BetterRain.data.BiomeRegistry.BiomeSound;
import org.blockartistry.mod.BetterRain.util.PlayerUtils;
import org.blockartistry.mod.BetterRain.util.WorldUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

public class PlayerSoundManager {

	private static final int INSIDE_Y_ADJUST = 3;
	private static final float VOLUME_INCREMENT = 0.02F;

	private static int reloadTracker = 0;

	private static class PlayerSound extends MovingSound {
		private boolean fadeAway;
		private final EntityPlayer player;
		private final BiomeSound sound;

		public PlayerSound(final EntityPlayer player, final BiomeSound sound) {
			super(new ResourceLocation(sound.sound));

			// Don't set volume to 0; MC will optimize out
			this.sound = sound;
			this.volume = 0.01F;
			this.field_147663_c = sound.pitch;
			this.player = player;
			this.repeat = true;
			this.fadeAway = false;

			// Repeat delay
			this.field_147665_h = 0;

			// Initial position
			this.xPosF = (float) (this.player.posX);
			this.yPosF = (float) (this.player.posY + 1);
			this.zPosF = (float) (this.player.posZ);
		}

		public void fadeAway() {
			this.fadeAway = true;
		}

		public boolean sameSound(final BiomeSound snd) {
			return this.sound.equals(snd);
		}

		@Override
		public void update() {
			if (this.fadeAway) {
				this.volume -= VOLUME_INCREMENT;
				if (this.volume < 0.0F) {
					this.volume = 0.0F;
				}
			} else if (this.volume < this.sound.volume) {
				this.volume += VOLUME_INCREMENT;
				if (this.volume > this.sound.volume)
					this.volume = this.sound.volume;
			}

			if (this.volume == 0.0F)
				this.donePlaying = true;
		}

		@Override
		public float getXPosF() {
			return (float) this.player.posX;
		}

		@Override
		public float getYPosF() {
			return (float) this.player.posY + 1;
		}

		@Override
		public float getZPosF() {
			return (float) this.player.posZ;
		}
	}

	private static boolean isInside(final EntityPlayer entity) {
		// If the player is underground
		if (PlayerUtils.isUnderGround(entity, INSIDE_Y_ADJUST))
			return true;

		final int range = 3;
		final int area = (range * 2 + 1) * (range * 2 + 1) / 2;
		int seeSky = 0;
		for (int x = -range; x <= range; x++)
			for (int z = -range; z <= range; z++) {
				final int y = entity.worldObj.getTopSolidOrLiquidBlock((int) (x + entity.posX),
						(int) (z + entity.posZ));
				if (y <= (entity.posY + 1))
					seeSky++;
			}
		return seeSky < area;
	}

	private static String getConditions(final World world) {
		final StringBuilder builder = new StringBuilder();
		if (WorldUtils.isDaytime(world))
			builder.append("day");
		else
			builder.append("night");
		if (world.getRainStrength(1.0F) > 0.0F)
			builder.append("raining");
		return builder.toString();
	}

	private static boolean didReloadOccur() {
		final int count = BiomeRegistry.getReloadCount();
		if (count != reloadTracker) {
			reloadTracker = count;
			return true;
		}
		return false;
	}

	// Current active background sound
	private static PlayerSound currentSound = null;

	public static void initialize() {
		if (ModOptions.getEnableBiomeSounds()) {
			final PlayerSoundManager manager = new PlayerSoundManager();
			MinecraftForge.EVENT_BUS.register(manager);
			FMLCommonHandler.instance().bus().register(manager);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clientTick(final TickEvent.ClientTickEvent event) {
		final Minecraft mc = Minecraft.getMinecraft();
		final World world = mc.theWorld;
		if (world == null || event.phase != Phase.START)
			return;

		// Dead player or they are covered with blocks
		if (mc.thePlayer.isDead || isInside(mc.thePlayer)) {
			if (currentSound != null) {
				currentSound.fadeAway();
				currentSound = null;
			}
			return;
		}

		final String conditions = getConditions(world);
		final BiomeGenBase playerBiome = PlayerUtils.getPlayerBiome(mc.thePlayer);
		final BiomeSound sound = BiomeRegistry.getSound(playerBiome, conditions);

		if (currentSound != null) {
			if (didReloadOccur() || sound == null || !currentSound.sameSound(sound)) {
				currentSound.fadeAway();
				currentSound = null;
			}
		}

		if (currentSound == null && sound != null) {
			currentSound = new PlayerSound(mc.thePlayer, sound);
			mc.getSoundHandler().playSound(currentSound);
		}
	}
}
