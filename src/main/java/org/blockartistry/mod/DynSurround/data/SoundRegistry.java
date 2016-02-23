/* This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;

public final class SoundRegistry {

	private static final List<Pattern> cullSoundNamePatterns = new ArrayList<Pattern>();
	private static final List<Pattern> blockSoundNamePatterns = new ArrayList<Pattern>();

	static {
		for (final String sound : ModOptions.culledSounds) {
			try {
				cullSoundNamePatterns.add(Pattern.compile(sound));
			} catch (final Exception ex) {
				ModLog.info("Unable to compile pattern for cull sound '%s'", sound);
			}
		}
		
		for (final String sound : ModOptions.blockedSounds) {
			try {
				blockSoundNamePatterns.add(Pattern.compile(sound));
			} catch (final Exception ex) {
				ModLog.info("Unable to compile pattern for blocked sound '%s'", sound);
			}
		}
	}

	private SoundRegistry() {
		
	}

	public static boolean isSoundCulled(final String sound) {
		for (final Pattern pattern : cullSoundNamePatterns)
			if (pattern.matcher(sound).matches())
				return true;
		return false;
	}
	
	public static boolean isSoundBlocked(final String sound) {
		for (final Pattern pattern : blockSoundNamePatterns)
			if (pattern.matcher(sound).matches())
				return true;
		return false;
	}

}
