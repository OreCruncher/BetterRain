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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import org.blockartistry.mod.DynSurround.client.footsteps.util.property.contract.ConfigSource;
import org.blockartistry.mod.DynSurround.client.footsteps.util.property.contract.IPropertyHolder;
import org.blockartistry.mod.DynSurround.client.footsteps.util.property.contract.IVersionable;

import java.util.Properties;
import java.util.TreeSet;

public class ConfigProperty implements IPropertyHolder, IVersionable, ConfigSource {
	private VersionableProperty mixed;

	private String path;

	public ConfigProperty() {
		this.mixed = new VersionableProperty();

	}

	@Override
	public void setSource(final String path) {
		this.path = path;

	}

	@Override
	public boolean load() {
		File file = new File(this.path);

		if (file.exists()) {
			try {
				Reader reader = new FileReader(file);

				Properties props = new Properties();
				props.load(reader);

				for (Entry<Object, Object> entry : props.entrySet()) {
					this.mixed.setProperty(entry.getKey().toString(), entry.getValue().toString());

				}
				this.mixed.commit();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				this.mixed.revert();
				return false;

			} catch (IOException e) {
				e.printStackTrace();
				this.mixed.revert();
				return false;

			}
		} else
			return false;

		return true;

	}

	@Override
	public boolean save() {
		try {
			File userFile = new File(this.path);
			@SuppressWarnings("serial")
			Properties props = new Properties() {
				@Override
				public synchronized Enumeration<Object> keys() {
					return Collections.enumeration(new TreeSet<Object>(super.keySet()));
				}
			};
			for (Entry<String, String> property : this.mixed.getAllProperties().entrySet()) {
				props.setProperty(property.getKey(), property.getValue());
			}

			props.store(new FileWriter(userFile), "");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

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
