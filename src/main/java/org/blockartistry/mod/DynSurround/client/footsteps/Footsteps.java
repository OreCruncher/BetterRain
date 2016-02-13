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

package org.blockartistry.mod.DynSurround.client.footsteps;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.client.IClientEffectHandler;
import org.blockartistry.mod.DynSurround.client.footsteps.game.system.PFIsolator;
import org.blockartistry.mod.DynSurround.client.footsteps.game.system.PFReaderH;
import org.blockartistry.mod.DynSurround.client.footsteps.game.system.PFResourcePackDealer;
import org.blockartistry.mod.DynSurround.client.footsteps.game.system.PFSolver;
import org.blockartistry.mod.DynSurround.client.footsteps.game.system.UserConfigSoundPlayerWrapper;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.implem.AcousticsManager;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.implem.BasicPrimitiveMap;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.implem.LegacyCapableBlockMap;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.implem.NormalVariator;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IBlockMap;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IPrimitiveMap;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IVariator;
import org.blockartistry.mod.DynSurround.client.footsteps.parsers.AcousticsJsonReader;
import org.blockartistry.mod.DynSurround.client.footsteps.parsers.Register;
import org.blockartistry.mod.DynSurround.client.footsteps.util.property.simple.ConfigProperty;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class Footsteps implements IResourceManagerReloadListener, IClientEffectHandler {

	// System
	private PFResourcePackDealer dealer = new PFResourcePackDealer();
	private PFIsolator isolator;

	public Footsteps() {

		reloadEverything();

		// Hooking
		final IResourceManager resMan = Minecraft.getMinecraft().getResourceManager();
		if (resMan instanceof IReloadableResourceManager) {
			((IReloadableResourceManager) resMan).registerReloadListener(this);
		}
	}

	public void reloadEverything() {
		this.isolator = new PFIsolator();

		final List<ResourcePackRepository.Entry> repo = ImmutableList.of();
		/*
		 * final List<ResourcePackRepository.Entry> repo =
		 * this.dealer.findResourcePacks(); if (repo.size() == 0) { ModLog.info(
		 * "Footsteps didn't find any compatible resource pack."); }
		 * 
		 * for (final ResourcePackRepository.Entry pack : repo) { ModLog.debug(
		 * "Will load: " + pack.getResourcePackName()); }
		 */

		reloadBlockMap(repo);
		reloadPrimitiveMap(repo);
		reloadAcoustics(repo);
		this.isolator.setSolver(new PFSolver(this.isolator));
		reloadVariator(repo);

		this.isolator.setGenerator(new PFReaderH(this.isolator));
		/*
		 * this.isolator.setGenerator(getConfig().getInteger("custom.stance") ==
		 * 0 ? new PFReaderH(this.isolator) : new PFReaderQP(this.isolator));
		 */
	}

	private void reloadVariator(final List<ResourcePackRepository.Entry> repo) {
		final IVariator var = new NormalVariator();

		try {
			var.loadConfig(ConfigProperty.fromStream(this.dealer.openVariator(null)));
		} catch (final Exception ex) {
			;
		}

		for (final ResourcePackRepository.Entry pack : repo) {
			try {
				var.loadConfig(ConfigProperty.fromStream(this.dealer.openVariator(pack.getResourcePack())));
			} catch (Exception e) {
				ModLog.debug("No variator found in " + pack.getResourcePackName() + ": " + e.getMessage());
			}
		}

		this.isolator.setVariator(var);
	}

	private void reloadBlockMap(final List<ResourcePackRepository.Entry> repo) {
		final IBlockMap blockMap = new LegacyCapableBlockMap();

		try {
			Register.setup(ConfigProperty.fromStream(this.dealer.openBlockMap(null)), blockMap);
		} catch (final Exception ex) {
			;
		}

		for (ResourcePackRepository.Entry pack : repo) {
			try {
				Register.setup(ConfigProperty.fromStream(this.dealer.openBlockMap(pack.getResourcePack())), blockMap);
			} catch (IOException e) {
				ModLog.debug("No blockmap found in " + pack.getResourcePackName() + ": " + e.getMessage());
			}
		}

		this.isolator.setBlockMap(blockMap);
	}

	private void reloadPrimitiveMap(final List<ResourcePackRepository.Entry> repo) {
		final IPrimitiveMap primitiveMap = new BasicPrimitiveMap();

		try {
			Register.setup(ConfigProperty.fromStream(this.dealer.openPrimitiveMap(null)), primitiveMap);
		} catch (final Exception ex) {
			;
		}

		for (final ResourcePackRepository.Entry pack : repo) {
			try {
				Register.setup(ConfigProperty.fromStream(this.dealer.openPrimitiveMap(pack.getResourcePack())),
						primitiveMap);
			} catch (IOException e) {
				ModLog.debug("No primitivemap found in " + pack.getResourcePackName() + ": " + e.getMessage());
			}
		}

		this.isolator.setPrimitiveMap(primitiveMap);
	}

	private void reloadAcoustics(final List<ResourcePackRepository.Entry> repo) {
		AcousticsManager acoustics = new AcousticsManager(this.isolator);
		Scanner scanner = null;

		try {
			scanner = new Scanner(this.dealer.openAcoustics(null));
			final String jasonString = scanner.useDelimiter("\\Z").next();

			new AcousticsJsonReader("").parseJSON(jasonString, acoustics);
		} catch (final Exception ex) {
			;
		} finally {
			if (scanner != null)
				scanner.close();
		}

		for (final ResourcePackRepository.Entry pack : repo) {

			try {
				scanner = new Scanner(this.dealer.openAcoustics(pack.getResourcePack()));
				final String jasonString = scanner.useDelimiter("\\Z").next();

				new AcousticsJsonReader("").parseJSON(jasonString, acoustics);
			} catch (IOException e) {
				ModLog.debug("No acoustics found in " + pack.getResourcePackName() + ": " + e.getMessage());
			} finally {
				if (scanner != null)
					scanner.close();
			}
		}

		this.isolator.setAcoustics(acoustics);
		this.isolator.setSoundPlayer(new UserConfigSoundPlayerWrapper(acoustics));
		this.isolator.setDefaultStepPlayer(acoustics);
	}

	@Override
	public void onResourceManagerReload(final IResourceManager var1) {
		ModLog.info("Resource Pack reload detected...");
		reloadEverything();
	}

	@Override
	public void process(World world, EntityPlayer player) {
		this.isolator.onFrame();
		player.nextStepDistance = Integer.MAX_VALUE;
	}

	@Override
	public boolean hasEvents() {
		return false;
	}
}
