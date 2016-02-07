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

package org.blockartistry.mod.DynSurround.client.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundManager {

	private static final Map<SoundEffect, Emitter> emitters = new HashMap<SoundEffect, Emitter>();

	public static void clearSounds() {
		for (final Emitter emit : emitters.values())
			emit.fade();
		emitters.clear();
	}

	public static void queueSounds(final List<SoundEffect> sounds) {
		// Need to remove sounds that are active but not
		// in the incoming list
		final List<SoundEffect> active = new ArrayList<SoundEffect>(emitters.keySet());
		for (final SoundEffect effect : active) {
			if (!sounds.remove(effect))
				emitters.remove(effect).fade();
		}

		// Add sounds from the incoming list that are not
		// active.
		for (final SoundEffect sound : sounds)
			emitters.put(sound, new Emitter(sound));
	}

	public static void update() {
		final Iterator<Entry<SoundEffect, Emitter>> itr = emitters.entrySet().iterator();
		while (itr.hasNext()) {
			final Entry<SoundEffect, Emitter> e = itr.next();
			e.getValue().update();
		}
	}

	public static List<SoundEffect> activeSounds() {
		return new ArrayList<SoundEffect>(emitters.keySet());
	}

}
