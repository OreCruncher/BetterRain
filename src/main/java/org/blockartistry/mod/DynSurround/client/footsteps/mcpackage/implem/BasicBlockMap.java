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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IBlockMap;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.strategy.IdentityHashingStrategy;
import net.minecraft.block.Block;

@SideOnly(Side.CLIENT)
public class BasicBlockMap implements IBlockMap {
	private static final Pattern pattern = Pattern.compile("([^:]+)>([^^+]+)\\^?(\\d+)?\\+?(\\w+)?");

	private final Map<Block, TIntObjectHashMap<String>> metaMap = new TCustomHashMap<Block, TIntObjectHashMap<String>>(
			IdentityHashingStrategy.INSTANCE);
	private final Map<Block, Map<String, String>> substrateMap = new TCustomHashMap<Block, Map<String, String>>(
			IdentityHashingStrategy.INSTANCE);

	public BasicBlockMap() {
	}

	@Override
	public String getBlockMap(final Block block, final int meta) {
		final TIntObjectHashMap<String> metas = this.metaMap.get(block);
		if (metas != null) {
			String result = metas.get(meta);
			if (result == null)
				result = metas.get(-1);
			return result;
		}
		return null;
	}

	@Override
	public String getBlockMapSubstrate(final Block block, final int meta, final String substrate) {
		final Map<String, String> sub = this.substrateMap.get(block);
		if (sub != null) {
			String result = sub.get(substrate + "." + meta);
			if (result == null)
				result = sub.get(substrate + ".-1");
			return result;
		}
		return null;
	}

	@Override
	public void register(final String key, final String value) {
		final Matcher matcher = pattern.matcher(key);
		if (matcher.matches()) {
			final Block block = GameData.getBlockRegistry().getRaw(matcher.group(1) + ":" + matcher.group(2));
			if (block != null) {
				final int meta = matcher.group(3) == null ? -1 : Integer.parseInt(matcher.group(3));
				final String substrate = matcher.group(4);
				if (StringUtils.isEmpty(substrate)) {
					TIntObjectHashMap<String> metas = this.metaMap.get(block);
					if (metas == null)
						this.metaMap.put(block, metas = new TIntObjectHashMap<String>());
					metas.put(meta, value);
				} else {
					Map<String, String> sub = this.substrateMap.get(block);
					if (sub == null)
						this.substrateMap.put(block, sub = new HashMap<String, String>());
					sub.put(substrate + "." + meta, value);
				}
			} else {
				ModLog.debug("Unable to locate block for blockmap '%s:%s'", matcher.group(1), matcher.group(2));
			}
		} else {
			ModLog.debug("Malformed key in blockmap '%s'", key);
		}
	}

	@Override
	public boolean hasEntryForBlock(final Block block) {
		return this.metaMap.containsKey(block) || this.substrateMap.containsKey(block);
	}
}
