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

package org.blockartistry.mod.DynSurround.data;

import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public final class BiomeSurvey {

	public int area;
	public final TObjectIntHashMap<BiomeGenBase> weights = new TObjectIntHashMap<BiomeGenBase>();

	/*
	 * Perform a biome survey around the player at the specified range.
	 */
	public static BiomeSurvey doSurvey(final EntityPlayer player, final int range) {
		final BiomeSurvey survey = new BiomeSurvey();
		if (EnvironState.isPlayerUnderground()) {
			survey.area = 1;
			survey.weights.put(BiomeRegistry.UNDERGROUND, 1);
		} else {
			final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			final int rangeSquared = range * range;
			final int x = MathHelper.floor_double(player.posX);
			final int z = MathHelper.floor_double(player.posZ);

			for (int dX = -range; dX <= range; dX++)
				for (int dZ = -range; dZ <= range; dZ++) {
					final int distSquared = dX * dX + dZ * dZ;
					if (distSquared <= rangeSquared) {
						survey.area++;
						pos.set(x + dX, 0, z + dZ);
						final BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(pos);
						survey.weights.put(biome, survey.weights.get(biome) + 1);
					}
				}
		}

		return survey;
	}

}
