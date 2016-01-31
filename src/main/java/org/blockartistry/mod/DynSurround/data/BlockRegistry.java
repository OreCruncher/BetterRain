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

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.Module;
import org.blockartistry.mod.DynSurround.client.fx.BlockEffect;
import org.blockartistry.mod.DynSurround.client.fx.JetEffect;
import org.blockartistry.mod.DynSurround.client.fx.SoundEffect;
import org.blockartistry.mod.DynSurround.data.config.BlockConfig;
import org.blockartistry.mod.DynSurround.data.config.SoundConfig;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

public final class BlockRegistry {

	private static final Map<Block, Entry> registry = new IdentityHashMap<Block, Entry>();

	private static void register(final Block block, final BlockEffect effect) {
		Entry entry = registry.get(block);
		if (entry == null) {
			entry = new Entry(block);
			registry.put(block, entry);
		}
		entry.effects.add(effect);
	}

	private static final class Entry {
		public final Block block;
		public int chance = 100;
		public final List<SoundEffect> sounds = new ArrayList<SoundEffect>();
		public final List<BlockEffect> effects = new ArrayList<BlockEffect>();

		public Entry(final Block block) {
			this.block = block;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append(String.format("Block [%s]:", this.block.getRegistryName()));

			if (!this.sounds.isEmpty()) {
				builder.append(" chance:").append(this.chance);
				builder.append("; sounds [");
				for (final SoundEffect sound : this.sounds)
					builder.append(sound.toString()).append(',');
				builder.append(']');
			}

			if (!this.effects.isEmpty())
				builder.append(" has effects");

			return builder.toString();
		}
	}

	public static void initialize() {

		// Particles
		if (ModOptions.getEnableFireJets())
			register(Blocks.lava, new JetEffect.Fire());
		if (ModOptions.getEnableBubbleJets())
			register(Blocks.water, new JetEffect.Bubble());
		if (ModOptions.getEnableSteamJets())
			register(Blocks.water, new JetEffect.Steam());

		processConfig();

		ModLog.info("*** BLOCK REGISTRY ***");
		for (final Entry entry : registry.values())
			ModLog.info(entry.toString());
	}

	public static List<BlockEffect> getEffects(final Block block) {
		final Entry entry = registry.get(block);
		return entry != null ? entry.effects : null;
	}

	public static SoundEffect getSound(final Block block, final Random random, final String conditions) {
		final Entry entry = registry.get(block);
		if (entry == null || entry.sounds.isEmpty() || random.nextInt(entry.chance) != 0)
			return null;

		int totalWeight = 0;
		final List<SoundEffect> candidates = new ArrayList<SoundEffect>();
		for (final SoundEffect s : entry.sounds)
			if (s.matches(conditions)) {
				candidates.add(s);
				totalWeight += s.weight;
			}
		if (totalWeight <= 0)
			return null;

		if (candidates.size() == 1)
			return candidates.get(0);

		int targetWeight = random.nextInt(totalWeight);
		int i = 0;
		for (i = candidates.size(); (targetWeight -= candidates.get(i - 1).weight) >= 0; i--)
			;

		return candidates.get(i - 1);
	}

	private static void processConfig() {
		try {
			process(BlockConfig.load("blocks"));
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final String[] configFiles = ModOptions.getBlockConfigFiles();
		for (final String file : configFiles) {
			final File theFile = new File(Module.dataDirectory(), file);
			if (theFile.exists()) {
				try {
					final BlockConfig config = BlockConfig.load(theFile);
					if (config != null)
						process(config);
					else
						ModLog.warn("Unable to process block config file " + file);
				} catch (final Exception ex) {
					ModLog.error("Unable to process block config file " + file, ex);
				}
			} else {
				ModLog.warn("Could not locate block config file [%s]", file);
			}
		}
	}

	private static void process(final BlockConfig config) {
		final BlockEffect effect = new JetEffect.Dust();
		for (final BlockConfig.Entry entry : config.entries) {
			if (entry.blocks.isEmpty())
				continue;

			for (final String blockName : entry.blocks) {
				final Block block = GameData.getBlockRegistry().getObject(new ResourceLocation(blockName));
				if (block == null || block == Blocks.air) {
					ModLog.warn("Unknown block [%s] in block config file", blockName);
					continue;
				}

				// Reset of a block clears all registry
				if (entry.reset != null && entry.reset.booleanValue())
					registry.remove(block);

				Entry blockData = registry.get(block);
				if (blockData == null) {
					blockData = new Entry(block);
					registry.put(block, blockData);
				}

				if (entry.dust != null && entry.dust.booleanValue())
					blockData.effects.add(effect);

				if (entry.chance != null)
					blockData.chance = entry.chance.intValue();

				for (final SoundConfig sr : entry.sounds) {
					if (sr.sound != null) {
						// Block sounds are always spot sounds
						sr.spotSound = true;
						blockData.sounds.add(new SoundEffect(sr));
					}
				}
			}
		}
	}

}
