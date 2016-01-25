/* This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.DynSurround.data.world;

import net.minecraft.world.World;

public class CelestialProvider  implements IDiurnalProvider {

	@Override
	public boolean isDaytime(final World world) {
		final float celestialAngle = WorldData.getCelestialAngle(world, 0.0F);
		// 0.785 0.260
		return celestialAngle >= 0.785F || celestialAngle < 0.285F;
	}

	@Override
	public boolean isNighttime(final World world) {
		final float celestialAngle = WorldData.getCelestialAngle(world, 0.0F);
		// 0.260 0.705
		return celestialAngle >= 0.285F && celestialAngle < 0.701F;
	}

	@Override
	public boolean isSunrise(final World world) {
		final float celestialAngle = WorldData.getCelestialAngle(world, 0.0F);
		// 0.705
		return celestialAngle >= 0.701F && celestialAngle < 0.785F;
	}

	@Override
	public boolean isSunset(final World world) {
		final float celestialAngle = WorldData.getCelestialAngle(world, 0.0F);
		return celestialAngle > 0.215 && celestialAngle <= 0.306F;
	}
}