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
package org.blockartistry.mod.DynSurround.client.fog;

import javax.annotation.Nonnull;

import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.EntityViewRenderEvent;

/**
 * Calculates the fog ranges based on player elevation as compared to the
 * dimensions cloud height.
 */
@SideOnly(Side.CLIENT)
public class HazeFogRangeCalculator extends VanillaFogRangeCalculator {

	protected static final int BAND_OFFSETS = 15;
	protected static final int BAND_CORE_SIZE = 10;
	protected static final float IMPACT_FAR = 0.6F;
	protected static final float IMPACT_NEAR = 0.95F;

	protected final FogResult cached = new FogResult();

	public HazeFogRangeCalculator() {

	}

	@Override
	@Nonnull
	public FogResult calculate(@Nonnull final EntityViewRenderEvent.RenderFogEvent event) {
		final DimensionRegistry di = EnvironState.getDimensionInfo();
		if (di == null) {
			this.cached.set(event);
			return this.cached;
		}

		if (di.getHasHaze()) {
			final float lowY = di.getCloudHeight() - BAND_OFFSETS;
			final float highY = di.getCloudHeight() + BAND_OFFSETS + BAND_CORE_SIZE;

			// Calculate the players Y. If it's in the band range calculate the fog
			// parameters
			final double eyeHeight = EnvironState.getPlayer().posY + EnvironState.getPlayer().eyeHeight;
			if (eyeHeight >= lowY && eyeHeight <= highY) {
				final float coreLowY = lowY + BAND_OFFSETS;
				final float coreHighY = coreLowY + BAND_CORE_SIZE;

				float scaleFar = IMPACT_FAR;
				float scaleNear = IMPACT_NEAR;
				if (eyeHeight < coreLowY) {
					final float factor = (float) ((eyeHeight - lowY) / BAND_OFFSETS);
					scaleFar *= factor;
					scaleNear *= factor;
				} else if (eyeHeight > coreHighY) {
					final float factor = (float) ((highY - eyeHeight) / BAND_OFFSETS);
					scaleFar *= factor;
					scaleNear *= factor;
				}

				final float end = event.farPlaneDistance * (1F - scaleFar);
				final float start = event.farPlaneDistance * (1F - scaleNear);
				this.cached.set(start, end);
				return this.cached;
			}
		}

		this.cached.set(event);
		return this.cached;
	}

}
