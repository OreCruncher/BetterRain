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

package org.blockartistry.mod.DynSurround.client.footsteps.game.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IBlockMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class ForgeDictionary {

	private static final String[] oreBlocks = { "oreIron", "oreGold", "oreCopper", "oreTin", "oreSilver", "oreLead",
			"oreNickle", "orePlatinum", "oreManaInfused", "oreElectrum", "oreInvar", "oreBronze", "oreSignalum",
			"oreEnderium", "oreLapis", "oreDiamond", "oreEmerald", "oreRedstone", "oreQuartz", "oreCoal" };

	private static final String[] metalBlocks = { "blockIron", "blockGold", "blockCopper", "blockTin", "blockSilver",
			"blockLead", "blockNickle", "blockPlatinum", "blockMithril", "blockElectrum", "blockInvar", "blockBronze",
			"blockSignalum", "blockLumium", "blockEnderium", "blockSteel" };

	private static final String[] woodBlocks = { "logWood", "planksWood", "slabWood", "stairWood" };

	private static final String[] saplings = { "treeSaplings" };

	private static final String[] glassBlocks = { "blockGlass" };

	private static final String[] leafBlocks = { "treeLeaves" };

	private static final String[] stoneBlocks = { "stone", "cobblestone" };

	private static final String[] sandstoneBlocks = { "sandstone" };

	private static final String[] sandBlocks = { "sand" };

	private static final String[] woodChests = { "chestWood", "chestTrapped" };

	private static final String[] rugBlocks = { "wool", "blockClothRock" };

	private static final Map<String, String[]> dictionaryMaps = new HashMap<String, String[]>();

	static {
		dictionaryMaps.put("ore", oreBlocks);
		dictionaryMaps.put("hardmetal", metalBlocks);
		dictionaryMaps.put("wood", woodBlocks);
		dictionaryMaps.put("glass", glassBlocks);
		dictionaryMaps.put("#sapling", saplings);
		dictionaryMaps.put("leaves", leafBlocks);
		dictionaryMaps.put("stone", stoneBlocks);
		dictionaryMaps.put("sandstone", sandstoneBlocks);
		dictionaryMaps.put("sand", sandBlocks);
		dictionaryMaps.put("squeakywood", woodChests);
		dictionaryMaps.put("rug", rugBlocks);
	}

	private ForgeDictionary() {

	}

	public static void dumpOreNames() {
		ModLog.debug("**** FORGE ORE DICTIONARY NAMES ****");
		for (final String oreName : OreDictionary.getOreNames())
			ModLog.debug(oreName);
		ModLog.debug("************************************");
	}

	public static void initialize(final IBlockMap blockMap) {
		for (final Entry<String, String[]> entry : dictionaryMaps.entrySet()) {
			final String value = entry.getKey();
			for (final String oreName : entry.getValue()) {
				final List<ItemStack> stacks = OreDictionary.getOres(oreName, false);
				for (final ItemStack stack : stacks) {
					final Block block = Block.getBlockFromItem(stack.getItem());
					if (block != null) {
						String blockName = PFHelper.nameOf(block);
						if (stack.getHasSubtypes() && stack.getItemDamage() != OreDictionary.WILDCARD_VALUE)
							blockName += "^" + stack.getItemDamage();
						blockMap.register(blockName, value);
					}
				}
			}
		}
	}

}
