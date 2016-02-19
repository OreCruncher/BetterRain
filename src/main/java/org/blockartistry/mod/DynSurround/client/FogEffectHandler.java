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
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.storm.StormProperties;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.BiomeSurvey;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.event.DiagnosticEvent;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.DiurnalUtils;
import org.blockartistry.mod.DynSurround.util.MathStuff;
import org.blockartistry.mod.DynSurround.util.PlayerUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;

@SideOnly(Side.CLIENT)
public class FogEffectHandler implements IClientEffectHandler {

	private static final boolean ENABLE_ELEVATION_HAZE = ModOptions.getEnableElevationHaze();
	private static final boolean ENABLE_DESERT_FOG = ModOptions.getAllowDesertFog();
	private static final boolean ENABLE_BIOME_FOG = ModOptions.getEnableBiomeFog();

	private static final int HAZE_THRESHOLD = 15;
	private static final float DESERT_DUST_FACTOR = ModOptions.getDesertFogFactor();
	private static final float ELEVATION_HAZE_FACTOR = ModOptions.getElevationHazeFactor();
	private static final float BIOME_FOG_FACTOR = ModOptions.getBiomeFogFactor();

	// The delta indicates how much per tick the density will shift
	// toward the target.
	private static float currentFogLevel = 0.0F;
	private static float insideFogOffset = 0.0F;
	private static Color currentFogColor = null;

	public static float currentFogLevel() {
		return currentFogLevel;
	}

	public FogEffectHandler() {
	}

	@Override
	public boolean hasEvents() {
		return true;
	}

	@Override
	public void process(final World world, final EntityPlayer player) {

		currentFogColor = new Color(world.getFogColor(1.0F));

		float biomeFog = 0.0F;
		float dustFog = 0.0F;
		float heightFog = 0.0F;

		if (ENABLE_BIOME_FOG || ENABLE_DESERT_FOG) {
			// Calculate the brightness factor to apply to the color. Need
			// to darken it a bit when it gets night.
			final float celestialAngle = DiurnalUtils.getCelestialAngle(world, 0.0F);
			final float baseScale = MathHelper
					.clamp_float(MathStuff.cos(celestialAngle * MathStuff.PI_F * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
			final float brightnessFactor = baseScale * 0.75F + 0.25F;

			final Color tint = new Color(0, 0, 0);
			final BiomeSurvey survey = EnvironState.getBiomeSurvey();
			int coverage = 0;
			for (final BiomeGenBase b : survey.weights.keySet()) {
				final int weight = survey.weights.get(b);
				final float scale = ((float) weight / (float) survey.area);
				if (ENABLE_BIOME_FOG && BiomeRegistry.hasFog(b)) {
					biomeFog += BiomeRegistry.getFogDensity(b) * scale;
					tint.blend(Color.scale(BiomeRegistry.getFogColor(b), brightnessFactor), scale);
					coverage += weight;
				} else if (ENABLE_DESERT_FOG && BiomeRegistry.hasDust(b)) {
					dustFog += StormProperties.getFogDensity() * scale;
					tint.blend(Color.scale(BiomeRegistry.getDustColor(b), brightnessFactor), scale);
					coverage += weight;
				}
			}

			currentFogColor.blend(tint, (float) coverage / (float) survey.area);
		}

		biomeFog *= BIOME_FOG_FACTOR;
		dustFog *= DESERT_DUST_FACTOR;

		if (ENABLE_ELEVATION_HAZE && DimensionRegistry.hasHaze(world)) {
			final float distance = MathHelper
					.abs(DimensionRegistry.getCloudHeight(world) - (float) (player.posY + player.getEyeHeight()));
			final float hazeBandRange = HAZE_THRESHOLD * (1.0F + world.getRainStrength(1.0F) * 2);
			if (distance < hazeBandRange) {
				heightFog = (hazeBandRange - distance) / 50.0F / hazeBandRange * ELEVATION_HAZE_FACTOR;
			}
		}

		// Get the max fog level between the three fog types
		currentFogLevel = Math.max(biomeFog, Math.max(dustFog, heightFog));
		insideFogOffset = PlayerUtils.ceilingCoverageRatio(player) * 15.0F;
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

		if (currentFogLevel == 0)
			return;

		final Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(event.entity.worldObj, event.entity,
				(float) event.renderPartialTicks);
		if (block.getMaterial() == Material.lava || block.getMaterial() == Material.water)
			return;

		event.red = currentFogColor.red;
		event.green = currentFogColor.green;
		event.blue = currentFogColor.blue;
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

		if (currentFogLevel == 0)
			return;

		float level = currentFogLevel;
		final float factor = 1.0F + level * 100.0F;
		final float near = (event.farPlaneDistance * 0.75F) / (factor * factor) + insideFogOffset;
		final float horizon = event.farPlaneDistance / (factor) + insideFogOffset;

		final float start = GL11.glGetFloat(GL11.GL_FOG_START);
		final float end = GL11.glGetFloat(GL11.GL_FOG_END);

		boolean didFog = false;
		if (near < start) {
			GL11.glFogf(GL11.GL_FOG_START, near);
			didFog = true;
		}
		if (horizon < end) {
			GL11.glFogf(GL11.GL_FOG_END, horizon);
			didFog = true;
		}

		if (didFog)
			event.setResult(Result.ALLOW);
	}

	@SubscribeEvent
	public void diagnostics(final DiagnosticEvent.Gather event) {
		final StringBuilder builder = new StringBuilder();
		builder.append("Fog:");
		builder.append(" c:").append(currentFogLevel);
		event.output.add(builder.toString());
	}

}
