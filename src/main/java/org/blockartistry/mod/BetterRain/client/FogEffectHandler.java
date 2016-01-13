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

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.client.rain.RainProperties;
import org.blockartistry.mod.BetterRain.data.BiomeRegistry;
import org.blockartistry.mod.BetterRain.util.Color;
import org.blockartistry.mod.BetterRain.util.PlayerUtils;
import org.blockartistry.mod.BetterRain.util.WorldUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class FogEffectHandler {

	private static final boolean ENABLE_ELEVATION_HAZE = ModOptions.getEnableElevationHaze();
	private static final boolean ENABLE_DESERT_FOG = ModOptions.getAllowDesertFog();
	private static final boolean ENABLE_BIOME_FOG = ModOptions.getEnableBiomeFog();

	private static final int FOG_Y_CUTOFF = 3;
	private static final float DESERT_DUST_FACTOR = ModOptions.getDesertFogFactor();
	private static final float ELEVATION_HAZE_FACTOR = ModOptions.getElevationHazeFactor();

	// The delta indicates how much per tick the density will shift
	// toward the target.
	private static final float FOG_DELTA = 0.003F;
	private static float currentFogLevel = 0.0F;
	private static float targetFogLevel = 0.0F;

	// Time period, in ticks, to transition fog colors
	private static final int COLOR_TRANSIITON_PERIOD = 40;
	private static Vec3 currentFogColor = null;
	private static Vec3 targetFogColor = null;
	private static Vec3 fogColorTransitionAdjustments = null;

	private static boolean areEqual(final Vec3 color1, final Vec3 color2) {
		return color1.xCoord == color2.xCoord && color1.yCoord == color2.yCoord && color1.zCoord == color2.zCoord;
	}

	private FogEffectHandler() {
	}

	public static void initialize() {
		final FogEffectHandler handler = new FogEffectHandler();
		MinecraftForge.EVENT_BUS.register(handler);
	}

	/*
	 * Need to get called every tick to calculate the effect of fog.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clientTick(final TickEvent.ClientTickEvent event) {
		final World world = FMLClientHandler.instance().getClient().theWorld;
		if (world == null || !WorldUtils.hasSky(world) || event.phase != Phase.START)
			return;

		final Minecraft mc = Minecraft.getMinecraft();
		final BiomeGenBase biome = PlayerUtils.getPlayerBiome(mc.thePlayer);

		if (currentFogColor == null)
			currentFogColor = world.getFogColor(0.0F);

		if (targetFogColor == null)
			targetFogColor = world.getFogColor(0.0F);

		float biomeFog = 0.0F;
		float dustFog = 0.0F;
		float heightFog = 0.0F;

		final int cutOff = WorldUtils.getSeaLevel(world) - FOG_Y_CUTOFF;
		final int posY = MathHelper.floor_double(mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

		// If the player Y is higher than the cutoff Y then assess desert
		// and elevation haze. Don't want to do needless calculations if they
		// are under ground.
		if (posY >= cutOff) {
			if (ENABLE_BIOME_FOG)
				biomeFog = BiomeRegistry.getFogDensity(biome);

			if (ENABLE_DESERT_FOG && BiomeRegistry.hasDust(biome)) {
				final int posX = MathHelper.floor_double(mc.thePlayer.posX);
				final int posZ = MathHelper.floor_double(mc.thePlayer.posZ);

				if (!Minecraft.getMinecraft().theWorld.provider.doesXZShowFog(posX, posZ))
					dustFog = RainProperties.getFogDensity() * DESERT_DUST_FACTOR;
			}

			if (ENABLE_ELEVATION_HAZE) {
				final float factor = 1.0F + world.getRainStrength(1.0F) * RainProperties.getIntensityLevel();
				final float skyHeight = WorldUtils.getSkyHeight(world) / factor;
				final float groundLevel = WorldUtils.getSeaLevel(world);
				final float ratio = (posY - groundLevel) / (skyHeight - groundLevel);
				heightFog = ratio * ratio * ratio * ratio * ELEVATION_HAZE_FACTOR;
			}
		}

		// Get the max fog level between the three fog types
		targetFogLevel = Math.max(biomeFog, Math.max(dustFog, heightFog));

		// Get the appropriate fog color based on the predominant
		// fog effect.
		Vec3 newTargetColor = null;
		if (targetFogLevel == dustFog) {
			final Color color = BiomeRegistry.getDustColor(biome);
			if (color != null)
				newTargetColor = color.toVec3();
		} else if (targetFogLevel == biomeFog) {
			final Color color = BiomeRegistry.getFogColor(biome);
			if (color != null)
				newTargetColor = color.toVec3();
		}

		// Height fog/world default
		if (newTargetColor == null)
			newTargetColor = world.getFogColor(0.0F);

		// Calculate the rate of color change from the current fog
		// color to the new target color. Each of the RGB components
		// is scaled so that the color transition is smooth.
		if (fogColorTransitionAdjustments == null || !areEqual(newTargetColor, targetFogColor)) {
			targetFogColor = newTargetColor;
			final double deltaRed = (targetFogColor.xCoord - currentFogColor.xCoord) / COLOR_TRANSIITON_PERIOD;
			final double deltaGreen = (targetFogColor.yCoord - currentFogColor.yCoord) / COLOR_TRANSIITON_PERIOD;
			final double deltaBlue = (targetFogColor.zCoord - currentFogColor.zCoord) / COLOR_TRANSIITON_PERIOD;
			fogColorTransitionAdjustments = new Vec3(deltaRed, deltaGreen, deltaBlue);
		}

		// Move the current fog density to the desired target
		// density.
		if (currentFogLevel > targetFogLevel) {
			currentFogLevel -= FOG_DELTA;
			if (currentFogLevel < targetFogLevel)
				currentFogLevel = targetFogLevel;
		} else if (currentFogLevel < targetFogLevel) {
			currentFogLevel += FOG_DELTA;
			if (currentFogLevel > targetFogLevel)
				currentFogLevel = targetFogLevel;
		}

		// Adjust the fog color toward the target color based
		// on the scaled adjustments per tick.
		double currentRed = currentFogColor.xCoord + fogColorTransitionAdjustments.xCoord;
		if ((fogColorTransitionAdjustments.xCoord < 0.0F && currentRed < targetFogColor.xCoord)
				|| (fogColorTransitionAdjustments.xCoord > 0.0F && currentRed > targetFogColor.xCoord)) {
			currentRed = targetFogColor.xCoord;
		}

		double currentGreen = currentFogColor.yCoord + fogColorTransitionAdjustments.yCoord;
		if ((fogColorTransitionAdjustments.yCoord < 0.0F && currentGreen < targetFogColor.yCoord)
				|| (fogColorTransitionAdjustments.yCoord > 0.0F && currentGreen > targetFogColor.yCoord)) {
			currentGreen = targetFogColor.yCoord;
		}

		double currentBlue = currentFogColor.zCoord + fogColorTransitionAdjustments.zCoord;
		if ((fogColorTransitionAdjustments.zCoord < 0.0F && currentBlue < targetFogColor.zCoord)
				|| (fogColorTransitionAdjustments.zCoord > 0.0F && currentBlue > targetFogColor.zCoord)) {
			currentBlue = targetFogColor.zCoord;
		}

		currentFogColor = new Vec3(currentRed, currentGreen, currentBlue);
	}

	/*
	 * Hook the fog color event so we can tell the renderer what color the fog
	 * should be.
	 */
	@SubscribeEvent
	public void fogColorEvent(final EntityViewRenderEvent.FogColors event) {
		// Timing is everything...
		if (currentFogColor == null)
			return;

		final Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(event.entity.worldObj, event.entity,
				(float) event.renderPartialTicks);
		if (block.getMaterial() == Material.lava || block.getMaterial() == Material.water)
			return;

		event.red = (event.red + (float) currentFogColor.xCoord) / 2.0F;
		event.green = (event.green + (float) currentFogColor.yCoord) / 2.0F;
		event.blue = (event.blue + (float) currentFogColor.zCoord) / 2.0F;
	}

	/*
	 * Hook the fog density event so that the fog settings can be reset based on
	 * rain intensity. This routine will overwrite what the vanilla code has
	 * done in terms of fog.
	 */
	@SubscribeEvent
	public void fogRenderEvent(final EntityViewRenderEvent.RenderFogEvent event) {
		final float factor = 1.0F + currentFogLevel * 100.0F;
		final float near = (event.farPlaneDistance * 0.75F) / (factor * factor);
		final float horizon = event.farPlaneDistance / (factor);
		GL11.glFogf(GL11.GL_FOG_START, near);
		GL11.glFogf(GL11.GL_FOG_END, horizon);
	}

}
