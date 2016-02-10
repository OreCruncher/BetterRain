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

package org.blockartistry.mod.DynSurround.client.footsteps.engine.implem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.IAcoustic;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.EventType;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.ILibrary;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.INamedAcoustic;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.IOptions;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.ISoundPlayer;
import org.blockartistry.mod.DynSurround.client.footsteps.game.system.Association;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AcousticsLibrary implements ILibrary {
	private Map<String, IAcoustic> acoustics = new LinkedHashMap<String, IAcoustic>();

	public AcousticsLibrary() {
	}

	@Override
	public void addAcoustic(final INamedAcoustic acoustic) {
		this.acoustics.put(acoustic.getName(), acoustic);
	}

	@Override
	public Set<String> getAcousticsKeySet() {
		return this.acoustics.keySet();
	}

	@Override
	public IAcoustic getAcoustic(final String acoustic) {
		if (this.acoustics.containsKey(acoustic)) {
			return this.acoustics.get(acoustic);
		}
		return null;
	}

	@Override
	public void playAcoustic(final Object location, final Association acousticName, final EventType event) {
		playAcoustic(location, acousticName, event, null);
	}

	@Override
	public void playAcoustic(final Object location, final Association acousticName, final EventType event,
			final IOptions inputOptions) {
		if (acousticName.getData().contains(",")) {
			final String fragments[] = acousticName.getData().split(",");
			for (final String fragment : fragments) {
				playAcoustic(location, fragment, event, inputOptions);
			}
		} else if (!this.acoustics.containsKey(acousticName.getData())) {
			onAcousticNotFound(location, acousticName.getData(), event, inputOptions);
		} else {
			if (ModLog.DEBUGGING)
				ModLog.debug("  Playing acoustic " + acousticName.getData() + " for event "
						+ event.toString().toUpperCase());
			this.acoustics.get(acousticName.getData()).playSound(mySoundPlayer(), location, event, inputOptions);
		}
	}

	public void playAcoustic(final Object location, final String acousticName, final EventType event,
			final IOptions inputOptions) {
		if (acousticName.contains(",")) {
			final String fragments[] = acousticName.split(",");
			for (final String fragment : fragments) {
				playAcoustic(location, fragment, event, inputOptions);
			}
		} else if (!this.acoustics.containsKey(acousticName)) {
			onAcousticNotFound(location, acousticName, event, inputOptions);
		} else {
			if (ModLog.DEBUGGING)
				ModLog.debug("  Playing acoustic " + acousticName + " for event " + event.toString().toUpperCase());
			this.acoustics.get(acousticName).playSound(mySoundPlayer(), location, event, inputOptions);
		}
	}

	@Override
	public boolean hasAcoustic(final String acousticName) {
		return this.acoustics.containsKey(acousticName);
	}

	protected abstract void onAcousticNotFound(final Object location, final String acousticName, final EventType event,
			final IOptions inputOptions);

	protected abstract ISoundPlayer mySoundPlayer();
}