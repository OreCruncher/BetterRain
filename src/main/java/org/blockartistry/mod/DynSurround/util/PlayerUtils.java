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

import codechicken.lib.math.MathHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.biome.BiomeGenBase;

public final class PlayerUtils {

	private PlayerUtils() {
	}

	public static BiomeGenBase getPlayerBiome(final EntityPlayer player) {
		final int theX = MathHelper.floor_double(player.posX);
		final int theZ = MathHelper.floor_double(player.posZ);
		return player.worldObj.getBiomeGenForCoords(theX, theZ);
	}

	public static int getPlayerDimension(final EntityPlayer player) {
		if (player == null || player.worldObj == null)
			return -256;
		return player.worldObj.provider.dimensionId;
	}

	public static boolean isUnderGround(final EntityPlayer player, final int offset) {
		return MathHelper.floor_double(player.posY + offset) < WorldUtils.getSeaLevel(player.worldObj);
	}

	private static final int RANGE = 3;
	private static final int AREA = (RANGE * 2 + 1) * (RANGE * 2 + 1) / 2;

	public static boolean isInside(final EntityPlayer entity, final int yOffset) {
		// If the player is underground
		if (PlayerUtils.isUnderGround(entity, yOffset))
			return true;

		final int targetY = (int) entity.posY;
		int seeSky = 0;
		for (int x = -RANGE; x <= RANGE; x++)
			for (int z = -RANGE; z <= RANGE; z++) {
				final int theX = MathHelper.floor_double(x + entity.posX);
				final int theZ = MathHelper.floor_double(z + entity.posZ);
				final int y = entity.worldObj.getTopSolidOrLiquidBlock(theX, theZ);
				if ((y - targetY) < 3) {
					if (++seeSky >= AREA)
						return false;
				}
			}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static int getClientPlayerDimension() {
		return getPlayerDimension(FMLClientHandler.instance().getClient().thePlayer);
	}
}
