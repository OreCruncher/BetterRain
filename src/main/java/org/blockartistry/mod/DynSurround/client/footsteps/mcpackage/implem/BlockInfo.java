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

import net.minecraft.block.Block;

public class BlockInfo {

	protected Block block;
	protected int meta;

	protected BlockInfo() {
		this.block = null;
		this.meta = -1;
	}

	public BlockInfo(final Block block, final int meta) {
		this.block = block;
		this.meta = meta;
	}

	public Block getBlock() {
		return this.block;
	}

	public int getMeta() {
		return this.meta;
	}

	@Override
	public int hashCode() {
		return this.block.hashCode() ^ this.meta * 31;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;

		final BlockInfo bi = (BlockInfo) o;
		return this.block == bi.block && this.meta == bi.meta;
	}

	public static class BlockInfoMutable extends BlockInfo {

		public BlockInfoMutable() {
			super();
		}

		public BlockInfoMutable(final BlockInfo bi) {
			super(bi.block, bi.meta);
		}

		public BlockInfoMutable setBlock(final Block block) {
			this.block = block;
			this.meta = -1;
			return this;
		}

		public BlockInfoMutable setMeta(final int meta) {
			this.meta = meta;
			return this;
		}

		public BlockInfoMutable asGeneric() {
			this.meta = -1;
			return this;
		}

		public BlockInfo asImmutable() {
			return new BlockInfo(this.block, this.meta);
		}
	}
}
