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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

final class BiomeConfig {

	public static class SoundRecord {
		@SerializedName("sound")
		public String sound = null;
		@SerializedName("conditions")
		public String conditions = ".*";
		@SerializedName("volume")
		public Float volume = null;
		@SerializedName("pitch")
		public Float pitch = null;
	}
	
	public static class Entry {
		@SerializedName("biomeName")
		public String biomeName = null;
		@SerializedName("precipitation")
		public Boolean hasPrecipitation = null;
		@SerializedName("dust")
		public Boolean hasDust = null;
		@SerializedName("aurora")
		public Boolean hasAurora = null;
		@SerializedName("fog")
		public Boolean hasFog = null;
		@SerializedName("dustColor")
		public String dustColor = null;
		@SerializedName("fogColor")
		public String fogColor = null;
		@SerializedName("fogDensity")
		public Float fogDensity= null;
		@SerializedName("soundReset")
		public Boolean soundReset = null;
		@SerializedName("sounds")
		public List<SoundRecord> sounds = ImmutableList.of();
	}

	public List<Entry> entries = ImmutableList.of();

	@SuppressWarnings("unused")
	public static BiomeConfig load(final File file) throws Exception {
		InputStream stream = null;

		try {
			stream = new FileInputStream(file);
			if (stream != null)
				return load(stream);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (final Throwable t) {
				;
			}
		}
		return new BiomeConfig();
	}

	public static BiomeConfig load(final String modId) {
		final String fileName = modId.replaceAll("[^a-zA-Z0-9.-]", "_");
		InputStream stream = null;

		try {
			stream = BiomeConfig.class.getResourceAsStream("/assets/dsurround/data/" + fileName + ".json");
			if (stream != null)
				return load(stream);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (final Throwable t) {
				;
			}
		}
		return new BiomeConfig();
	}

	protected static BiomeConfig load(final InputStream stream) {
		InputStreamReader reader = null;
		JsonReader reader2 = null;

		try {
			reader = new InputStreamReader(stream);
			reader2 = new JsonReader(reader);
			return (BiomeConfig) new Gson().fromJson(reader, BiomeConfig.class);
		} finally {
			try {
				if (reader2 != null)
					reader2.close();
				if (reader != null)
					reader.close();
			} catch (final Exception ex) {
				;
			}
		}
	}

}
