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

package org.blockartistry.mod.DynSurround.client.sound;

import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.DynSurround.data.config.SoundConfig;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public final class SoundEffect {

	private static final float[] pitchDelta = { -0.2F, 0.0F, 0.0F, 0.2F, 0.2F, 0.2F };

	public final String sound;
	public final String conditions;
	private final Pattern pattern;
	public float volume;
	public final float pitch;
	public final int weight;
	public final boolean isSpot;
	public final boolean variable;
	public final int repeatDelay;

	public SoundEffect(final String sound) {
		this(sound, 1.0F, 1.0F, 0, false);
	}

	public SoundEffect(final String sound, final float volume, final float pitch) {
		this(sound, volume, pitch, 0, false);
	}

	public SoundEffect(final String sound, final float volume, final float pitch, final boolean variable) {
		this(sound, volume, pitch, 0, variable);
	}

	public SoundEffect(final String sound, final float volume, final float pitch, final int repeatDelay,
			final boolean variable) {
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.conditions = ".*";
		this.pattern = null;
		this.weight = 1;
		this.isSpot = true;
		this.variable = variable;
		this.repeatDelay = repeatDelay;
	}
	
	public SoundEffect(final SoundEffect effect) {
		this.sound = effect.sound;
		this.volume = effect.volume;
		this.pitch = effect.pitch;
		this.conditions = effect.conditions;
		this.pattern = effect.pattern;
		this.weight = effect.weight;
		this.isSpot = effect.isSpot;
		this.variable = effect.variable;
		this.repeatDelay = effect.repeatDelay;
	}

	public SoundEffect(final SoundConfig record) {
		this.sound = record.sound;
		this.conditions = StringUtils.isEmpty(record.conditions) ? ".*" : record.conditions;
		this.volume = record.volume == null ? 1.0F : record.volume.floatValue();
		this.pitch = record.pitch == null ? 1.0F : record.pitch.floatValue();
		this.pattern = Pattern.compile(this.conditions);
		this.weight = record.weight == null ? 10 : record.weight.intValue();
		this.isSpot = record.spotSound != null && record.spotSound.booleanValue();
		this.variable = record.variable != null && record.variable.booleanValue();
		this.repeatDelay = record.repeatDelay == null ? 0 : record.repeatDelay.intValue();
	}

	public boolean matches(final String conditions) {
		return pattern.matcher(conditions).matches();
	}
	
	public float getVolume() {
		return this.volume;
	}

	public float getPitch(final Random rand) {
		if (rand != null && this.variable)
			return this.pitch + pitchDelta[rand.nextInt(pitchDelta.length)];
		return this.pitch;
	}

	public void doEffect(final Block block, final World world, final int x, final int y, final int z,
			final Random random) {
		SoundManager.playSoundAt(new BlockPos(x, y, z), this, 0);
	}

	@Override
	public boolean equals(final Object anObj) {
		if (this == anObj)
			return true;
		if (!(anObj instanceof SoundEffect))
			return false;
		final SoundEffect s = (SoundEffect) anObj;
		return this.sound.equals(s.sound);
	}
	
	@Override
	public int hashCode() {
		return this.sound.hashCode();
	}
	
	public static SoundEffect scaleVolume(final SoundEffect sound, final float scale) {
		final SoundEffect newEffect = new SoundEffect(sound);
		newEffect.volume *= scale;
		return newEffect;
	}

	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append('[').append(this.sound);
		if (!StringUtils.isEmpty(this.conditions))
			builder.append('(').append(this.conditions).append(')');
		builder.append(", v:").append(this.volume);
		builder.append(", p:").append(this.pitch);
		if (this.isSpot)
			builder.append(", w:").append(this.weight);
		builder.append(']');
		return builder.toString();
	}
}
