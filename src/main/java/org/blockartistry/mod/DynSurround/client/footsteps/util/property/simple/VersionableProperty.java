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

package org.blockartistry.mod.DynSurround.client.footsteps.util.property.simple;

import java.util.Map;

import org.blockartistry.mod.DynSurround.client.footsteps.util.property.contract.IPropertyHolder;
import org.blockartistry.mod.DynSurround.client.footsteps.util.property.contract.IVersionable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VersionableProperty implements IPropertyHolder, IVersionable {
	private IPropertyHolder soft;
	private IPropertyHolder hard;

	public VersionableProperty() {
		this.soft = new PropertyCell();
		this.hard = new PropertyCell();
	}

	@Override
	public boolean commit() {
		if (this.soft.getAllProperties().size() == 0)
			return false;

		this.hard.getAllProperties().putAll(this.soft.getAllProperties());
		this.soft.getAllProperties().clear();

		return true;
	}

	@Override
	public void revert() {
		this.soft.getAllProperties().clear();
	}

	@Override
	public String getString(final String name) {
		try {
			return this.soft.getString(name);
		} catch (PropertyMissingException e) {
			return this.hard.getString(name);
		}
		/*
		 * catch (PropertyTypeException e) { return this.hard.getString(name); }
		 */
	}

	@Override
	public boolean getBoolean(final String name) {
		try {
			return this.soft.getBoolean(name);
		} catch (PropertyMissingException e) {
			return this.hard.getBoolean(name);
		} catch (PropertyTypeException e) {
			return this.hard.getBoolean(name);
		}
	}

	@Override
	public int getInteger(final String name) {
		try {
			return this.soft.getInteger(name);
		} catch (PropertyMissingException e) {
			return this.hard.getInteger(name);
		} catch (PropertyTypeException e) {
			return this.hard.getInteger(name);
		}
	}

	@Override
	public float getFloat(final String name) {
		try {
			return this.soft.getFloat(name);
		} catch (PropertyMissingException e) {
			return this.hard.getFloat(name);
		} catch (PropertyTypeException e) {
			return this.hard.getFloat(name);
		}
	}

	@Override
	public long getLong(final String name) {
		try {
			return this.soft.getLong(name);
		} catch (PropertyMissingException e) {
			return this.hard.getLong(name);
		} catch (PropertyTypeException e) {
			return this.hard.getLong(name);
		}
	}

	@Override
	public double getDouble(final String name) {
		try {
			return this.soft.getDouble(name);
		} catch (PropertyMissingException e) {
			return this.hard.getDouble(name);
		} catch (PropertyTypeException e) {
			return this.hard.getDouble(name);
		}
	}

	@Override
	public void setProperty(final String name, final Object o) {
		this.soft.setProperty(name, o);
	}

	@Override
	public Map<String, String> getAllProperties() {
		return this.hard.getAllProperties();
	}

}
