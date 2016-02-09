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

package org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.implem;

import java.util.LinkedHashMap;
import java.util.Map;

import org.blockartistry.mod.DynSurround.client.footsteps.game.system.PFHelper;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IBlockMap;

import net.minecraft.block.Block;

public class BasicBlockMap implements IBlockMap {
	private Map<String, String> blockMap;

	public BasicBlockMap() {
		this.blockMap = new LinkedHashMap<String, String>();
	}

	@Override
	public String getBlockMap(final Block block, final int meta) {
		final String blockName = PFHelper.nameOf(block);
		final String result = this.blockMap.get(blockName + "^" + meta);
		return result != null ? result : this.blockMap.get(blockName);
	}

	@Override
	public String getBlockMapSubstrate(final Block block, final int meta, String substrate) {
		final String blockName = PFHelper.nameOf(block);
		final String result = this.blockMap.get(blockName + "^" + meta + "." + substrate);
		return result != null ? result : this.blockMap.get(blockName + "." + substrate);
	}

	@Override
	public void register(final String key, final String value) {
		this.blockMap.put(key.replace('>', ':'), value);
	}

	@Override
	public boolean hasEntryForBlock(final Block block) {
		final String blockName = PFHelper.nameOf(block);
		for (final Map.Entry<String, String> i : blockMap.entrySet()) {
			if (i.getKey().startsWith(blockName)) {
				return true;
			}
		}
		return false;
	}
}
