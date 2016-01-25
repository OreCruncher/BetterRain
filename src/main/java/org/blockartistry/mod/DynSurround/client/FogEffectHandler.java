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

package org.blockartistry.mod.DynSurround.client;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.rain.RainProperties;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.world.WorldData;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.MathStuff;
import org.blockartistry.mod.DynSurround.util.PlayerUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class FogEffectHandler implements IClientEffectHandler {

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
	private static Color currentFogColor = null;
	private static Color targetFogColor = null;
	private static Vec3 fogColorTransitionAdjustments = null;
	private static float brightnessFactor = 1.0F;

	public FogEffectHandler() {
	}

	@Override
	public boolean hasEvents() {
		return true;
	}

	@Override
	public void process(final World world, final EntityPlayer player) {
		final BiomeGenBase biome = PlayerUtils.getPlayerBiome(player);

		if (currentFogColor == null)
			currentFogColor = new Color(world.getFogColor(1.0F));

		if (targetFogColor == null)
			targetFogColor = new Color(world.getFogColor(1.0F));

		float biomeFog = 0.0F;
		float dustFog = 0.0F;
		float heightFog = 0.0F;

		final int cutOff = WorldData.getSeaLevel(world) - FOG_Y_CUTOFF;
		final int posY = MathHelper.floor_double(player.posY + player.getEyeHeight());

		// If the player Y is higher than the cutoff Y then assess desert
		// and elevation haze. Don't want to do needless calculations if they
		// are under ground.
		if (posY >= cutOff) {
			if (ENABLE_BIOME_FOG && BiomeRegistry.hasFog(biome))
				biomeFog = BiomeRegistry.getFogDensity(biome);

			if (ENABLE_DESERT_FOG && BiomeRegistry.hasDust(biome)) {
				dustFog = RainProperties.getFogDensity() * DESERT_DUST_FACTOR;
			}

			if (ENABLE_ELEVATION_HAZE && WorldData.hasHaze(world)) {
				final float factor = 1.0F + world.getRainStrength(1.0F);
				final float skyHeight = WorldData.getSkyHeight(world) / factor;
				final float groundLevel = WorldData.getSeaLevel(world);
				final float ratio = (posY - groundLevel) / (skyHeight - groundLevel);
				heightFog = ratio * ratio * ratio * ratio * ELEVATION_HAZE_FACTOR;
			}
		}

		// Get the max fog level between the three fog types
		targetFogLevel = Math.max(biomeFog, Math.max(dustFog, heightFog));

		// Get the appropriate fog color based on the predominant
		// fog effect.
		Color newTargetColor = null;
		if (targetFogLevel == dustFog) {
			newTargetColor = BiomeRegistry.getDustColor(biome);
		} else if (targetFogLevel == biomeFog) {
			newTargetColor = BiomeRegistry.getFogColor(biome);
		}

		// Height fog/world default
		if (newTargetColor == null)
			newTargetColor = new Color(world.getFogColor(1.0F));

		// Calculate the rate of color change from the current fog
		// color to the new target color. Each of the RGB components
		// is scaled so that the color transition is smooth.
		if (fogColorTransitionAdjustments == null || !targetFogColor.equals(newTargetColor)) {
			targetFogColor = newTargetColor;
			fogColorTransitionAdjustments = currentFogColor.transitionTo(targetFogColor, COLOR_TRANSIITON_PERIOD);
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
		currentFogColor.adjust(fogColorTransitionAdjustments, targetFogColor);

		// Calculate the brightness factor to apply to the color. Need
		// to darken it a bit when it gets night.
		final float celestialAngle = WorldData.getCelestialAngle(world, 0.0F);
		final float baseScale = MathHelper
				.clamp_float(MathStuff.cos(celestialAngle * MathStuff.PI_F * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);

		brightnessFactor = baseScale * 0.75F + 0.25F;
	}

	/*
	 * Hook the fog color event so we can tell the renderer what color the fog
	 * should be.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void fogColorEvent(final EntityViewRenderEvent.FogColors event) {
		// Timing is everything...
		if (currentFogColor == null || event.getResult() != Result.DEFAULT)
			return;

		final Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(event.entity.worldObj, event.entity,
				(float) event.renderPartialTicks);
		if (block.getMaterial() == Material.lava || block.getMaterial() == Material.water)
			return;

		final Color color = Color.scale(currentFogColor, brightnessFactor).mix(event.red, event.green, event.blue);
		event.red = color.red;
		event.green = color.green;
		event.blue = color.blue;
		event.setResult(Result.ALLOW);
	}

	/*
	 * Hook the fog density event so that the fog settings can be reset based on
	 * rain intensity. This routine will overwrite what the vanilla code has
	 * done in terms of fog.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void fogRenderEvent(final EntityViewRenderEvent.RenderFogEvent event) {
		if (event.getResult() != Result.DEFAULT)
			return;

		float level = currentFogLevel;
		if (level > targetFogLevel)
			level -= event.renderPartialTicks * FOG_DELTA;
		else if (level < targetFogLevel)
			level += event.renderPartialTicks * FOG_DELTA;

		final float factor = 1.0F + level * 100.0F;
		final float near = (event.farPlaneDistance * 0.75F) / (factor * factor);
		final float horizon = event.farPlaneDistance / (factor);
		GL11.glFogf(GL11.GL_FOG_START, near);
		GL11.glFogf(GL11.GL_FOG_END, horizon);
		event.setResult(Result.ALLOW);
	}

}
