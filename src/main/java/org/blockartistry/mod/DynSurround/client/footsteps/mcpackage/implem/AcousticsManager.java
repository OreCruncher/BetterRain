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

package org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.implem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.implem.AcousticsLibrary;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.IOptions;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.IOptions.Option;
import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.ISoundPlayer;
import org.blockartistry.mod.DynSurround.client.footsteps.game.system.Association;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IDefaultStepPlayer;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IIsolator;
import org.blockartistry.mod.DynSurround.util.MyUtils;
import org.blockartistry.mod.DynSurround.util.random.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;

/**
 * A ILibrary that can also play sounds and default footsteps.
 *
 * @author Hurry
 */
@SideOnly(Side.CLIENT)
public class AcousticsManager extends AcousticsLibrary implements ISoundPlayer, IDefaultStepPlayer {

	private static final Random RANDOM = new XorShiftRandom();
	private static final boolean USING_LATENESS = true;
	private static final boolean USING_EARLYNESS = true;
	private static final float LATENESS_THRESHOLD_DIVIDER = 1.2f;
	private static final double EARLYNESS_THRESHOLD_POW = 0.75d;

	private final List<PendingSound> pending = new ArrayList<PendingSound>();
	private final IIsolator isolator;
	private long minimum;

	public AcousticsManager(final IIsolator isolator) {
		this.isolator = isolator;
	}

	@Override
	public void playStep(final EntityLivingBase entity, final Association assos) {

		try {
			final Block block = assos.getBlock();
			if (!block.getMaterial().isLiquid() && block.stepSound != null && block.stepSound.soundName != null) {
				Block.SoundType soundType = block.stepSound;

				if (EnvironState.getWorld().getBlock(assos.x, assos.y + 1, assos.z) == Blocks.snow_layer) {
					soundType = Blocks.snow_layer.stepSound;
				}

				entity.playSound(soundType.getStepResourcePath(), soundType.getVolume() * 0.15F, soundType.getPitch());
			}
		} catch (final Throwable t) {
			ModLog.error("Unable to play step", t);
		}
	}

	@Override
	public void playSound(final Object location, final String soundName, final float volume, final float pitch,
			final IOptions options) {
		if (!(location instanceof Entity))
			return;

		if (options != null) {
			if (options.hasOption(Option.DELAY_MIN) && options.hasOption(Option.DELAY_MAX)) {
				final long delay = randAB(RANDOM, (Long) options.getOption(Option.DELAY_MIN),
						(Long) options.getOption(Option.DELAY_MAX));

				if (delay < this.minimum) {
					this.minimum = delay;
				}

				this.pending.add(
						new PendingSound(location, soundName, volume, pitch, null, MyUtils.currentTimeMillis() + delay,
								options.hasOption(Option.SKIPPABLE) ? -1 : (Long) options.getOption(Option.DELAY_MAX)));
			} else {
				actuallyPlaySound((Entity) location, soundName, volume, pitch);
			}
		} else {
			actuallyPlaySound((Entity) location, soundName, volume, pitch);
		}
	}

	protected void actuallyPlaySound(final Entity location, final String soundName, final float volume,
			final float pitch) {
		if (ModLog.DEBUGGING)
			ModLog.debug("    Playing sound " + soundName + " ("
					+ String.format(Locale.ENGLISH, "v%.2f, p%.2f", volume, pitch) + ")");

		try {
			location.playSound(soundName, volume, pitch);
		} catch (final Throwable t) {
			ModLog.error("Unable to play sound", t);
		}
	}

	private long randAB(final Random rng, final long a, final long b) {
		return a >= b ? a : a + rng.nextInt((int) b + 1);
	}

	@Override
	public Random getRNG() {
		return RANDOM;
	}

	@Override
	public void think() {
		if (this.pending.isEmpty() || MyUtils.currentTimeMillis() < this.minimum)
			return;

		long newMinimum = Long.MAX_VALUE;
		final long time = MyUtils.currentTimeMillis();

		final Iterator<PendingSound> iter = this.pending.iterator();
		while (iter.hasNext()) {
			final PendingSound sound = iter.next();

			if (time >= sound.getTimeToPlay() || USING_EARLYNESS
					&& time >= sound.getTimeToPlay() - Math.pow(sound.getMaximumBase(), EARLYNESS_THRESHOLD_POW)) {
				if (ModLog.DEBUGGING && USING_EARLYNESS && time < sound.getTimeToPlay()) {
					ModLog.debug("    Playing early sound (early by " + (sound.getTimeToPlay() - time)
							+ "ms, tolerence is " + Math.pow(sound.getMaximumBase(), EARLYNESS_THRESHOLD_POW));
				}

				final long lateness = time - sound.getTimeToPlay();
				if (!USING_LATENESS || sound.getMaximumBase() < 0
						|| lateness <= sound.getMaximumBase() / LATENESS_THRESHOLD_DIVIDER) {
					sound.playSound(this);
				} else {
					if (ModLog.DEBUGGING)
						ModLog.debug("    Skipped late sound (late by " + lateness + "ms, tolerence is "
								+ sound.getMaximumBase() / LATENESS_THRESHOLD_DIVIDER + "ms)");
				}
				iter.remove();
			} else {
				newMinimum = sound.getTimeToPlay();
			}
		}

		this.minimum = newMinimum;
	}

	@Override
	protected ISoundPlayer mySoundPlayer() {
		return this.isolator.getSoundPlayer();
	}
}