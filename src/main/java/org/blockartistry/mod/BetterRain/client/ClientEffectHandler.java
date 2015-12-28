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

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashSet;
import java.util.Set;

import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.client.aurora.Aurora;
import org.blockartistry.mod.BetterRain.client.rain.RainIntensity;
import org.blockartistry.mod.BetterRain.data.AuroraData;
import org.blockartistry.mod.BetterRain.data.EffectType;
import org.blockartistry.mod.BetterRain.util.Color;
import org.blockartistry.mod.BetterRain.util.PlayerUtils;
import org.blockartistry.mod.BetterRain.util.WorldUtils;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public final class ClientEffectHandler {

	private static final Color DESERT_FOG_COLOR = new Color(204, 185, 102);
	private static final boolean ALWAYS_OVERRIDE_SOUND = ModOptions.getAlwaysOverrideSound();
	private static final boolean ALLOW_DESERT_FOG = ModOptions.getAllowDesertFog();

	// Aurora information
	private static final boolean AURORA_ENABLE = ModOptions.getAuroraEnable();
	private static int auroraDimension = 0;
	private static final Set<AuroraData> auroras = new HashSet<AuroraData>();
	public static Aurora currentAurora;

	public static void addAurora(final AuroraData data) {
		if (!AURORA_ENABLE)
			return;

		if (auroraDimension != data.dimensionId || PlayerUtils.getClientPlayerDimension() != data.dimensionId) {
			auroras.clear();
			currentAurora = null;
			auroraDimension = data.dimensionId;
		}
		auroras.add(data);
	}

	private ClientEffectHandler() {
	}

	public static void initialize() {
		final ClientEffectHandler handler = new ClientEffectHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}

	/*
	 * Determines if the sound needs to be replaced by the event handler.
	 */
	private static boolean replaceRainSound(final String name) {
		return "ambient.weather.rain".equals(name);
	}

	/*
	 * Intercept the sound events and patch up the rain sound. If the rain
	 * experience is to be Vanilla let it just roll on through.
	 */
	@SubscribeEvent
	public void soundEvent(final PlaySoundEvent17 event) {
		if ((ALWAYS_OVERRIDE_SOUND || !RainIntensity.doVanillaRain()) && replaceRainSound(event.name)) {
			final ISound sound = event.sound;
			event.result = new PositionedSoundRecord(RainIntensity.getCurrentRainSound(),
					RainIntensity.getCurrentRainVolume(), sound.getPitch(), sound.getXPosF(), sound.getYPosF(),
					sound.getZPosF());
		}
	}

	private Aurora getClosestAurora(final TickEvent.ClientTickEvent event) {
		if (auroraDimension != PlayerUtils.getClientPlayerDimension()) {
			auroras.clear();
			currentAurora = null;
		}

		if (auroras.size() == 0) {
			currentAurora = null;
			return null;
		}

		final EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		final int playerX = (int) player.posX;
		final int playerZ = (int) player.posZ;
		boolean started = false;
		int distanceSq = 0;
		AuroraData ad = null;
		for (final AuroraData data : auroras) {
			final int deltaX = data.posX - playerX;
			final int deltaZ = data.posZ - playerZ;
			final int d = deltaX * deltaX + deltaZ * deltaZ;
			if (!started || distanceSq > d) {
				started = true;
				distanceSq = d;
				ad = data;
			}
		}

		if (ad == null) {
			currentAurora = null;
		} else if (currentAurora == null || (currentAurora.posX != ad.posX && currentAurora.posZ != ad.posZ)) {
			ModLog.info("New aurora: " + ad.toString());
			currentAurora = new Aurora(ad);
		}

		return currentAurora;
	}

	private static boolean auroraTimeToDie(final long time) {
		return time >= 22220L && time < 23500L;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void processAurora(final TickEvent.ClientTickEvent event) {
		if(!AURORA_ENABLE || event.phase != Phase.END)
			return;
		
		final World world = FMLClientHandler.instance().getClient().theWorld;
		if (world != null && auroras.size() > 0) {
			final long time = WorldUtils.getWorldTime(world);
			if (WorldUtils.isDaytime(time)) {
				auroras.clear();
				currentAurora = null;
			} else {
				final Aurora aurora = getClosestAurora(event);
				aurora.update();
				if (aurora.isAlive() && auroraTimeToDie(time)) {
					ModLog.info("Aurora fade...");
					aurora.die();
				}
			}
		}
	}

	public static boolean isFogApplicable(final EntityLivingBase entity) {
		if (!ALLOW_DESERT_FOG || RainIntensity.getIntensity() == RainIntensity.VANILLA)
			return false;

		final int posX = MathHelper.floor_double(entity.posX);
		final int posZ = MathHelper.floor_double(entity.posZ);

		if (Minecraft.getMinecraft().theWorld.provider.doesXZShowFog(posX, posZ))
			return false;

		final BiomeGenBase biome = entity.worldObj.getBiomeGenForCoords(posX, posZ);
		return EffectType.hasDust(biome) && RainIntensity.getIntensity() != RainIntensity.NONE;
	}

	public void fogColorEvent(final EntityViewRenderEvent.FogColors event) {
		if (isFogApplicable(event.entity)) {
			event.red = DESERT_FOG_COLOR.red;
			event.green = DESERT_FOG_COLOR.green;
			event.blue = DESERT_FOG_COLOR.blue;
		}
	}

	@SubscribeEvent
	public void fogDensityEvent(final EntityViewRenderEvent.RenderFogEvent event) {
		if (isFogApplicable(event.entity)) {
			final float distanceFactor = 0.5F - 0.45F * RainIntensity.getIntensityLevel();
			final float distance = event.farPlaneDistance * distanceFactor;
			GL11.glFogf(GL11.GL_FOG_START, event.fogMode < 0 ? 0.0F : distance * 0.75F);
			GL11.glFogf(GL11.GL_FOG_END, distance);
		}
	}
}
