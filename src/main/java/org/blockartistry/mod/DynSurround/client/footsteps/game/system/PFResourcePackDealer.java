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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.blockartistry.mod.DynSurround.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

public class PFResourcePackDealer {
	
	// Used for existing packs that have been configured for Presence Footsteps
	private final ResourceLocation pf_pack = new ResourceLocation("presencefootsteps", "pf_pack.json");
	private final ResourceLocation acoustics = new ResourceLocation("presencefootsteps", "acoustics.json");
	private final ResourceLocation blockmap = new ResourceLocation("presencefootsteps", "blockmap.cfg");
	private final ResourceLocation primitivemap = new ResourceLocation("presencefootsteps", "primitivemap.cfg");
	private final ResourceLocation variator = new ResourceLocation("presencefootsteps", "variator.cfg");

	public List<ResourcePackRepository.Entry> findResourcePacks() {
		final List<ResourcePackRepository.Entry> repo = Minecraft.getMinecraft().getResourcePackRepository()
				.getRepositoryEntries();

		final List<ResourcePackRepository.Entry> foundEntries = new ArrayList<ResourcePackRepository.Entry>();

		for (final ResourcePackRepository.Entry pack : repo) {
			if (checkCompatible(pack)) {
				foundEntries.add(pack);
			}
		}
		return foundEntries;
	}

	public List<ResourcePackRepository.Entry> findDisabledResourcePacks() {
		final ResourcePackRepository rrr = Minecraft.getMinecraft().getResourcePackRepository();

		final List<ResourcePackRepository.Entry> repo = new ArrayList<ResourcePackRepository.Entry>(
				rrr.getRepositoryEntriesAll());
		repo.removeAll(rrr.getRepositoryEntries());

		final List<ResourcePackRepository.Entry> foundEntries = new ArrayList<ResourcePackRepository.Entry>();
		for (final ResourcePackRepository.Entry pack : repo) {
			if (checkCompatible(pack)) {
				foundEntries.add(pack);
			}
		}
		return foundEntries;
	}

	private boolean checkCompatible(final ResourcePackRepository.Entry pack) {
		return pack.getResourcePack().resourceExists(this.pf_pack);
	}

	public InputStream openPackDescriptor(final IResourcePack pack) throws IOException {
		if (pack == null)
			return Module.class.getResourceAsStream("/assets/dsurround/data/footsteps/pf_pack.json");
		return pack.getInputStream(this.pf_pack);
	}

	public InputStream openAcoustics(final IResourcePack pack) throws IOException {
		if (pack == null)
			return Module.class.getResourceAsStream("/assets/dsurround/data/footsteps/acoustics.json");
		return pack.getInputStream(this.acoustics);
	}

	public InputStream openBlockMap(final IResourcePack pack) throws IOException {
		if (pack == null)
			return Module.class.getResourceAsStream("/assets/dsurround/data/footsteps/blockmap.cfg");
		return pack.getInputStream(this.blockmap);
	}

	public InputStream openPrimitiveMap(final IResourcePack pack) throws IOException {
		if (pack == null)
			return Module.class.getResourceAsStream("/assets/dsurround/data/footsteps/primitivemap.cfg");
		return pack.getInputStream(this.primitivemap);
	}

	public InputStream openVariator(final IResourcePack pack) throws IOException {
		if (pack == null)
			return Module.class.getResourceAsStream("/assets/dsurround/data/footsteps/variator.cfg");
		return pack.getInputStream(this.variator);
	}
}
