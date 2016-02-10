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
public class ConfigProperty implements IPropertyHolder, IVersionable {
	private VersionableProperty mixed;

	public ConfigProperty() {
		this.mixed = new VersionableProperty();

	}

	@Override
	public boolean commit() {
		return this.mixed.commit();

	}

	@Override
	public void revert() {
		this.mixed.revert();

	}

	@Override
	public String getString(String name) {
		return this.mixed.getString(name);
	}

	@Override
	public boolean getBoolean(String name) {
		return this.mixed.getBoolean(name);
	}

	@Override
	public int getInteger(String name) {
		return this.mixed.getInteger(name);
	}

	@Override
	public float getFloat(String name) {
		return this.mixed.getFloat(name);
	}

	@Override
	public long getLong(String name) {
		return this.mixed.getLong(name);
	}

	@Override
	public double getDouble(String name) {
		return this.mixed.getDouble(name);
	}

	@Override
	public void setProperty(String name, Object o) {
		this.mixed.setProperty(name, o);
	}

	@Override
	public Map<String, String> getAllProperties() {
		return this.mixed.getAllProperties();
	}

}
