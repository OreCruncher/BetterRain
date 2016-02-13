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
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

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

		final List<IResourcePack> repo = this.dealer.findResourcePacks();

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

	private void reloadVariator(final List<IResourcePack> repo) {
		final IVariator var = new NormalVariator();

		for (final IResourcePack pack : repo) {
			try {
				var.loadConfig(ConfigProperty.fromStream(this.dealer.openVariator(pack)));
			} catch (final Exception e) {
				ModLog.debug("Unable to load variator data from pack %s", pack.getPackName());;
			}
		}

		this.isolator.setVariator(var);
	}

	private void reloadBlockMap(final List<IResourcePack> repo) {
		final IBlockMap blockMap = new LegacyCapableBlockMap();

		for (final IResourcePack pack : repo) {
			try {
				Register.setup(ConfigProperty.fromStream(this.dealer.openBlockMap(pack)), blockMap);
			} catch (final IOException e) {
				ModLog.debug("Unable to load block map data from pack %s", pack.getPackName());;
			}
		}

		this.isolator.setBlockMap(blockMap);
	}

	private void reloadPrimitiveMap(final List<IResourcePack> repo) {
		final IPrimitiveMap primitiveMap = new BasicPrimitiveMap();

		for (final IResourcePack pack : repo) {
			try {
				Register.setup(ConfigProperty.fromStream(this.dealer.openPrimitiveMap(pack)),
						primitiveMap);
			} catch (final IOException e) {
				ModLog.debug("Unable to load primitive map data from pack %s", pack.getPackName());;
			}
		}

		this.isolator.setPrimitiveMap(primitiveMap);
	}

	private void reloadAcoustics(final List<IResourcePack> repo) {
		AcousticsManager acoustics = new AcousticsManager(this.isolator);
		Scanner scanner = null;

		for (final IResourcePack pack : repo) {

			try {
				scanner = new Scanner(this.dealer.openAcoustics(pack));
				final String jasonString = scanner.useDelimiter("\\Z").next();

				new AcousticsJsonReader("").parseJSON(jasonString, acoustics);
			} catch (final IOException e) {
				ModLog.debug("Unable to load acoustic data from pack %s", pack.getPackName());;
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
