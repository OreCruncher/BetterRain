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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;

@SideOnly(Side.CLIENT)
public class Association {
	
	private Block block;
	private int meta;
	
	public int x;
	public int y;
	public int z;
	
	private String data = null;
	
	private boolean noAssociation = false;
	private boolean isPrimative = false;
	
	public Association() {
	}
	
	public Association(final String raw) {
		setAssociation(raw);
	}
	
	public Association(final Block block, final int meta, final int xx, final int yy, final int zz) {
		init(block, meta, xx, yy, zz);
	}
	
	public Association init(final Block block, final int meta, final int xx, final int yy, final int zz) {
		this.block = block;
		this.meta = meta;
		x = xx;
		y = yy;
		z = zz;
		return this;
	}
	
	public String getData() {
		return data;
	}
	
	public Association setAssociation(final String association) {
		data = association;
		noAssociation = false;
		return this;
	}
	
	public Association setNoAssociation() {
		noAssociation = true;
		return this;
	}
	
	public boolean getNoAssociation() {
		return noAssociation;
	}
	
	public Association setPrimitive(final String primative) {
		data = primative;
		isPrimative = true;
		return this;
	}
	
	public boolean isPrimative() {
		return isPrimative;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public int getMeta() {
		return meta;
	}
	
	public boolean isNotEmitter() {
		return data != null && data.contentEquals("NOT_EMITTER");
	}
}