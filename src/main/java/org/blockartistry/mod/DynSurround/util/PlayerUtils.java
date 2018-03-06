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

package org.blockartistry.mod.DynSurround.util;

import java.util.regex.Pattern;

import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public final class PlayerUtils {

	private static final int INSIDE_Y_ADJUST = 3;

	private static final Pattern REGEX_DEEP_OCEAN = Pattern.compile("(?i).*deep.*ocean.*|.*abyss.*");
	private static final Pattern REGEX_OCEAN = Pattern.compile("(?i)(?!.*deep.*)(.*ocean.*|.*kelp.*|.*coral.*)");
	private static final Pattern REGEX_RIVER = Pattern.compile("(?i).*river.*");

	private PlayerUtils() {
	}

	public static BiomeGenBase getPlayerBiome(final EntityPlayer player, final boolean getTrue) {

		final int theX = MathHelper.floor_double(player.posX);
		final int theZ = MathHelper.floor_double(player.posZ);
		BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(theX, theZ);

		if (!getTrue) {
			if (player.isInsideOfMaterial(Material.water)) {
				if (REGEX_RIVER.matcher(biome.biomeName).matches())
					biome = BiomeRegistry.UNDERRIVER;
				else if (REGEX_OCEAN.matcher(biome.biomeName).matches())
					biome = BiomeRegistry.UNDEROCEAN;
				else if (REGEX_DEEP_OCEAN.matcher(biome.biomeName).matches())
					biome = BiomeRegistry.UNDERDEEPOCEAN;
				else
					biome = BiomeRegistry.UNDERWATER;
			} else {
				final DimensionRegistry info = DimensionRegistry.getData(player.getEntityWorld());
				final int theY = MathHelper.floor_double(player.posY);
				if ((theY + INSIDE_Y_ADJUST) < info.getSeaLevel())
					biome = BiomeRegistry.UNDERGROUND;
				else if (theY >= info.getSpaceHeight())
					biome = BiomeRegistry.OUTERSPACE;
				else if (theY >= info.getCloudHeight())
					biome = BiomeRegistry.CLOUDS;
			}
		}
		return biome;
	}

	private static int getPlayerDimension(final EntityPlayer player) {
		if (player == null || player.worldObj == null)
			return -256;
		return player.getEntityWorld().provider.dimensionId;
	}

	private static final int RANGE = 3;
	private static final int AREA = (RANGE * 2 + 1) * (RANGE * 2 + 1);

	private static float ceilingCoverageRatio(final EntityPlayer entity) {
		final World world = entity.getEntityWorld();
		final int targetY = MathHelper.floor_double(entity.posY);
		final int baseX = MathHelper.floor_double(entity.posX);
		final int baseZ = MathHelper.floor_double(entity.posZ);
		int seeSky = 0;
		for (int x = -RANGE; x <= RANGE; x++)
			for (int z = -RANGE; z <= RANGE; z++) {
				final int y = world.getTopSolidOrLiquidBlock(baseX + x, baseZ + z);
				if ((y - targetY) < 2)
					++seeSky;
			}
		return 1.0F - ((float) seeSky / AREA);
	}

	public static boolean isReallyInside(final EntityPlayer entity) {
		return ceilingCoverageRatio(entity) > 0.42F;
	}

	@SideOnly(Side.CLIENT)
	public static int getClientPlayerDimension() {
		return getPlayerDimension(FMLClientHandler.instance().getClient().thePlayer);
	}
}
