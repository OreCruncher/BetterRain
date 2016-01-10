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

import java.util.HashSet;
import java.util.Set;

import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.client.aurora.Aurora;
import org.blockartistry.mod.BetterRain.client.rain.RainProperties;
import org.blockartistry.mod.BetterRain.data.AuroraData;
import org.blockartistry.mod.BetterRain.data.EffectType;
import org.blockartistry.mod.BetterRain.util.PlayerUtils;
import org.blockartistry.mod.BetterRain.util.WorldUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public final class ClientEffectHandler {

	private static final boolean ALWAYS_OVERRIDE_SOUND = ModOptions.getAlwaysOverrideSound();
	private static final boolean ENABLE_ELEVATION_HAZE = ModOptions.getEnableElevationHaze();

	// Desert dust color for fog blending
	private static final float DESERT_RED = 204.0F / 255.0F;
	private static final float DESERT_GREEN = 185.0F / 255.0F;
	private static final float DESERT_BLUE = 102.0F / 255.0F;

	private static final int DESERT_FOG_Y_CUTOFF = 3;
	private static float dustFade = 0.0F;
	private static final float DUST_FADE_SPEED = 1.0F;
	private static final boolean ALLOW_DESERT_FOG = ModOptions.getAllowDesertFog();
	private static final float DESERT_DUST_FACTOR = ModOptions.getDesertFogFactor();

	private static float currentDustFog = 0.0F;
	private static float currentHeightFog = 0.0F;
	private static float effectiveFog = 0.0F;

	// Aurora information
	private static final boolean AURORA_ENABLE = ModOptions.getAuroraEnable();
	private static int auroraDimension = 0;
	private static final Set<AuroraData> auroras = new HashSet<AuroraData>();
	public static Aurora currentAurora;

	// Elevation information
	private static final float ELEVATION_HAZE_FACTOR = ModOptions.getElevationHazeFactor();

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
	public void soundEvent(final PlaySoundEvent event) {
		if ((ALWAYS_OVERRIDE_SOUND || !RainProperties.doVanillaRain()) && replaceRainSound(event.name)) {
			final ISound sound = event.sound;
			event.result = new PositionedSoundRecord(RainProperties.getCurrentRainSound(),
					RainProperties.getCurrentRainVolume(), sound.getPitch(), sound.getXPosF(), sound.getYPosF(),
					sound.getZPosF());
		}
	}

	private Aurora getClosestAurora(final TickEvent.ClientTickEvent event) {
		if (auroraDimension != PlayerUtils.getClientPlayerDimension()) {
			auroras.clear();
		}

		if (auroras.size() == 0) {
			currentAurora = null;
			return null;
		}

		final EntityPlayerSP player = FMLClientHandler.instance().getClient().thePlayer;
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

	/*
	 * Need to get called every tick to process the dust fade timer as well as
	 * aurora processing.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clientTick(final TickEvent.ClientTickEvent event) {
		final World world = FMLClientHandler.instance().getClient().theWorld;
		if (world == null)
			return;

		if (event.phase == Phase.START) {
			currentDustFog = 0.0F;
			currentHeightFog = 0.0F;
			effectiveFog = 0.0F;
			if (!WorldUtils.hasSky(world)) {
				dustFade = 0;
				return;
			}

			final Minecraft mc = Minecraft.getMinecraft();
			if (isDustFogApplicable(mc.thePlayer)) {
				switch (RainProperties.getRainPhase()) {
				case STARTING:
				case RAINING:
					dustFade += DUST_FADE_SPEED;
					break;
				case STOPPING:
				case NOT_RAINING:
					dustFade -= DUST_FADE_SPEED;
					break;
				default:
					;
				}
			} else {
				dustFade -= DUST_FADE_SPEED;
			}

			dustFade = MathHelper.clamp_float(dustFade, 0.0F, 100.0F);

			if (dustFade > 0) {
				currentDustFog = RainProperties.getFogDensity() * dustFade / 100.0F * DESERT_DUST_FACTOR;
			}

			if (ENABLE_ELEVATION_HAZE) {
				final float factor = 1.0F + world.getRainStrength(1.0F) * RainProperties.getIntensityLevel();
				final float skyHeight = WorldUtils.getSkyHeight(world) / factor;
				final float groundLevel = WorldUtils.getSeaLevel(world);
				currentHeightFog = (float) Math
						.abs(Math.pow(((FMLClientHandler.instance().getClient().thePlayer.posY - groundLevel)
								/ (skyHeight - groundLevel)), 4))
						* ELEVATION_HAZE_FACTOR;
			}
			effectiveFog = Math.max(currentDustFog, currentHeightFog);
			return;
		} else if (!AURORA_ENABLE) {
			return;
		}

		if (auroras.size() > 0) {
			final long time = WorldUtils.getWorldTime(world);
			if (WorldUtils.isDaytime(time)) {
				auroras.clear();
				currentAurora = null;
			} else {
				final Aurora aurora = getClosestAurora(event);
				if(aurora != null) {
					aurora.update();
					if (aurora.isAlive() && auroraTimeToDie(time)) {
						ModLog.info("Aurora fade...");
						aurora.die();
					}
				}
			}
		}
	}

	/*
	 * Determines if dust fog is applicable for where the entity is standing.
	 */
	public static boolean isDustFogApplicable(final EntityLivingBase entity) {
		if (!ALLOW_DESERT_FOG || !WorldUtils.hasSky(entity.worldObj))
			return false;

		final RainProperties intensity = RainProperties.getIntensity();
		if (intensity == RainProperties.VANILLA)
			return false;

		final int cutOff = WorldUtils.getSeaLevel(entity.worldObj) - DESERT_FOG_Y_CUTOFF;
		final int posY = MathHelper.floor_double(entity.posY + entity.getEyeHeight());
		if (posY < cutOff)
			return false;

		final int posX = MathHelper.floor_double(entity.posX);
		final int posZ = MathHelper.floor_double(entity.posZ);

		if (Minecraft.getMinecraft().theWorld.provider.doesXZShowFog(posX, posZ))
			return false;

		final BiomeGenBase biome = entity.worldObj.getBiomeGenForCoords(new BlockPos(posX, 0, posZ));
		return EffectType.hasDust(biome);
	}

	/*
	 * Hook the fog color event so that the fog can be tinted a sand color if
	 * needed.
	 */
	@SubscribeEvent
	public void fogColorEvent(final EntityViewRenderEvent.FogColors event) {
		if (currentDustFog > 0.0F) {
			// Blend in the dust color - like mixing paint
			event.red = (event.red + DESERT_RED) / 2.0F;
			event.green = (event.green + DESERT_GREEN) / 2.0F;
			event.blue = (event.blue + DESERT_BLUE) / 2.0F;
		}
	}

	/*
	 * Hook the fog density event so that the fog settings can be reset based on
	 * rain intensity. This routine will overwrite what the vanilla code has
	 * done in terms of fog.
	 */
	@SubscribeEvent
	public void fogRenderEvent(final EntityViewRenderEvent.RenderFogEvent event) {
		final float factor = 1.0F + effectiveFog * 100.0F;
		final float near = (event.farPlaneDistance * 0.75F) / (factor * factor);
		final float horizon = event.farPlaneDistance / (factor);
		GL11.glFogf(GL11.GL_FOG_START, near);
		GL11.glFogf(GL11.GL_FOG_END, horizon);
	}
}
