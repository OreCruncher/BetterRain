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

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class SpotSound extends PositionedSound {

	private static final int SPOT_SOUND_RANGE = 6;

	private final SoundEffect sound;
	private final int timeMark;

	public SpotSound(final BlockPos pos, final SoundEffect sound, final int delay) {
		super(new ResourceLocation(sound.sound));

		this.sound = sound;
		this.volume = sound.volume;
		this.pitch = sound.getPitch(EnvironState.RANDOM);
		this.repeat = false;
		this.repeatDelay = 0;

		this.xPosF = (float) pos.getX() + 0.5F;
		this.yPosF = (float) pos.getY() + 0.5F;
		this.zPosF = (float) pos.getZ() + 0.5F;

		this.timeMark = EnvironState.getTickCounter() + delay;
	}

	public SpotSound(final EntityPlayer player, final SoundEffect sound) {
		super(new ResourceLocation(sound.sound));

		this.sound = sound;
		this.volume = sound.volume;
		this.pitch = sound.getPitch(EnvironState.RANDOM);
		this.repeat = false;
		this.repeatDelay = 0;

		this.xPosF = MathHelper.floor_double(player.posX + EnvironState.RANDOM.nextInt(SPOT_SOUND_RANGE)
				- EnvironState.RANDOM.nextInt(SPOT_SOUND_RANGE));
		this.yPosF = MathHelper.floor_double(player.posY + 1 + EnvironState.RANDOM.nextInt(SPOT_SOUND_RANGE)
				- EnvironState.RANDOM.nextInt(SPOT_SOUND_RANGE));
		this.zPosF = MathHelper.floor_double(player.posZ + EnvironState.RANDOM.nextInt(SPOT_SOUND_RANGE)
				- EnvironState.RANDOM.nextInt(SPOT_SOUND_RANGE));

		this.timeMark = EnvironState.getTickCounter();
	}

	@Override
	public float getVolume() {
		return super.getVolume() * ModOptions.masterSoundScaleFactor;
	}

	public int getTickAge() {
		return EnvironState.getTickCounter() - this.timeMark;
	}

	public SoundEffect getSoundEffect() {
		return this.sound;
	}

	@Override
	public String toString() {
		return this.sound.toString();
	}

}