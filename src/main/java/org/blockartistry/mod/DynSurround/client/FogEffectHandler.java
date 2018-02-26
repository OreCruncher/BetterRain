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

import javax.annotation.Nonnull;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.fog.BiomeFogColorCalculator;
import org.blockartistry.mod.DynSurround.client.fog.BiomeFogRangeCalculator;
import org.blockartistry.mod.DynSurround.client.fog.FogResult;
import org.blockartistry.mod.DynSurround.client.fog.HazeFogRangeCalculator;
import org.blockartistry.mod.DynSurround.client.fog.HolisticFogColorCalculator;
import org.blockartistry.mod.DynSurround.client.fog.HolisticFogRangeCalculator;
import org.blockartistry.mod.DynSurround.client.fog.MorningFogRangeCalculator;
import org.blockartistry.mod.DynSurround.client.fog.WeatherFogRangeCalculator;
import org.blockartistry.mod.DynSurround.event.DiagnosticEvent;
import org.blockartistry.mod.DynSurround.util.Color;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.world.WorldEvent;

@SideOnly(Side.CLIENT)
public class FogEffectHandler implements IClientEffectHandler {

	public FogEffectHandler() {
	}

	@Override
	public boolean hasEvents() {
		return true;
	}

	private boolean doFog() {
		return ModOptions.enableBiomeFog || ModOptions.allowDesertFog;
	}

	@Override
	public void process(World world, EntityPlayer player) {
		if (doFog()) {
			this.fogRange.tick();
			this.fogColor.tick();
		}
	}

	protected HolisticFogColorCalculator fogColor = new HolisticFogColorCalculator();
	protected HolisticFogRangeCalculator fogRange = new HolisticFogRangeCalculator();

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void fogColorEvent(final EntityViewRenderEvent.FogColors event) {
		if (doFog()) {
			final Material material = event.block.getMaterial();
			if (material != Material.lava && material != Material.water) {
				final Color color = this.fogColor.calculate(event);
				if (color != null) {
					event.red = color.red;
					event.green = color.green;
					event.blue = color.blue;
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void fogRenderEvent(final EntityViewRenderEvent.RenderFogEvent event) {
		if (doFog()) {
			final Material material = event.block.getMaterial();
			if (material != Material.lava && material != Material.water) {
				final FogResult result = this.fogRange.calculate(event);
				if (result != null) {
					GL11.glFogf(GL11.GL_FOG_START, result.getStart());
					GL11.glFogf(GL11.GL_FOG_END, result.getEnd());
				}
			}
		}
	}

	@SubscribeEvent
	public void diagnostics(final DiagnosticEvent.Gather event) {
		if (doFog()) {
			event.output.add("Fog Range: " + this.fogRange.toString());
			event.output.add("Fog Color: " + this.fogColor.toString());
		} else
			event.output.add("FOG: IGNORED");
	}

	@SubscribeEvent(receiveCanceled = false, priority = EventPriority.LOWEST)
	public void onWorldLoad(@Nonnull final WorldEvent.Load event) {
		// Only want client side world things
		if (!event.world.isRemote)
			return;

		setupTheme(event.world);
	}

	protected void setupTheme(@Nonnull final World world) {

		this.fogColor = new HolisticFogColorCalculator();
		this.fogRange = new HolisticFogRangeCalculator();

		if (ModOptions.enableBiomeFog) {
			this.fogColor.add(new BiomeFogColorCalculator());
			this.fogRange.add(new BiomeFogRangeCalculator());
		}

		if (ModOptions.enableElevationHaze)
			this.fogRange.add(new HazeFogRangeCalculator());

		if (ModOptions.enableMorningFog)
			this.fogRange.add(new MorningFogRangeCalculator());

		if (ModOptions.enableWeatherFog)
			this.fogRange.add(new WeatherFogRangeCalculator());
	}
}
