/*
 * This file is part of BetterRain, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.BetterRain.aurora;

import java.util.Random;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.AuroraData;
import org.blockartistry.mod.BetterRain.util.MathStuff;
import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public final class Aurora extends EntityEnvEffect {

	private static final boolean MULTIPLES = ModOptions.getAuroraAllowMultiples();

	private static final float ANGLE1 = MathStuff.PI_F / 16.0F;
	private static final float ANGLE2 = MathStuff.toRadians(90.0F / 7.0F);

	public static final float AURORA_SPEED = 0.75F;
	public static final float AURORA_AMPLITUDE = 18.0F;
	public static final float AURORA_WAVELENGTH = 8.0F;

	public static final Color[] COLOR1_SET = new Color[] { new Color(0x0, 0xff, 0x99), Color.BLUE, Color.TURQOISE,
			Color.YELLOW, Color.NAVY, Color.GREEN, Color.MAGENTA };
	public static final Color[] COLOR2_SET = new Color[] { new Color(0x33, 0xff, 0x00), Color.GREEN, Color.LGREEN,
			Color.RED, Color.INDIGO, Color.YELLOW, Color.GREEN };

	public static class Preset {
		public final int length;
		public final float nodeLength;
		public final float nodeWidth;
		public final int bandOffset;

		public Preset(final int length, final float nodeLength, final float nodeWidth, final int bandOffset) {
			this.length = length;
			this.nodeLength = nodeLength;
			this.nodeWidth = nodeWidth;
			this.bandOffset = bandOffset;
		}
	}

	public static final Preset[] PRESETS = new Preset[] {
			// Default
			new Preset(128, 30.0F, 30.0F, 90),
			new Preset(128, 30.0F, 30.0F, 45),

			new Preset(128, 15.0F, 15.0F, 90),
			new Preset(128, 15.0F, 15.0F, 45),

			new Preset(64, 30.0F, 30.0F, 90),
			new Preset(64, 30.0F, 30.0F, 45),

			new Preset(64, 15.0F, 15.0F, 90),
			new Preset(64, 15.0F, 15.0F, 45),

			// Original Original
			// new Preset(256, 7.0F, 30.0F, 60),
	};

	private long time;
	public long ticksExisted;
	public float posX;
	public float posZ;
	public float ticker = 0.0F;
	public int fadeTimer = 0;
	public boolean terminate = true;

	private int length;
	private float nodeLength;
	private float nodeWidth;
	private int bandOffset;

	private int colorSet = 0;

	public Aurora(final World world, final AuroraData data) {
		this(world, data.posX, data.posZ, data.time, data.colorSet, data.preset);
	}

	public Aurora(final World world, final float x, final float z, final long t, final int colorSet, final int preset) {
		super(world);
		this.time = t;
		this.colorSet = colorSet;
		this.posX = x;
		this.posZ = z;
		this.ticksExisted = 0L;

		setPreset(preset);

		final Node[] baseArray = populateNodeListFromCenterAlt();
		addNodeArray(baseArray);

		if (MULTIPLES) {
			addNodeArray(formBand(baseArray, this.bandOffset));
			addNodeArray(formBand(baseArray, -this.bandOffset));
		}
	}

	private void setPreset(final int preset) {
		final Preset p = PRESETS[MathHelper.clamp_int(preset, 0, PRESETS.length - 1)];
		this.length = p.length;
		this.nodeLength = p.nodeLength;
		this.nodeWidth = p.nodeWidth;
		this.bandOffset = p.bandOffset;
	}

	public Color getColor1() {
		return COLOR1_SET[colorSet];
	}

	public Color getColor2() {
		return COLOR2_SET[colorSet];
	}

	public void update() {
		if (this.fadeTimer < 1280) {
			if (this.fadeTimer % 10 == 0) {
				fade(this.terminate);
			}
			this.fadeTimer += 1;
		}

		if (this.ticker < 360.0F) {
			this.ticker += AURORA_SPEED;
		} else {
			this.ticker = 0.0F;
		}

		this.ticksExisted += 1L;
	}

	private Node[] formBand(final Node[] nodeList, final int offset) {
		final Node[] tet = new Node[nodeList.length];
		for (int i = 0; i < nodeList.length; i++) {
			final Node node = nodeList[i];
			final float rads = MathStuff.toRadians(90.0F + node.getAngle());
			final float posX = node.posX + MathStuff.cos(rads) * offset;
			final float posZ = node.posZ + MathStuff.sin(rads) * offset;
			tet[i] = new Node(posX, node.posY - 2.0F, posZ, node.getAngle());
		}

		return alterWidths(tet, 10.0F);
	}

	private void fade(final boolean boo) {
		final int adjustment = boo ? 1 : -1;
		for (final Node[] array : this.nodeList)
			for (int j = 0; j < array.length; j++)
				array[j].addA(adjustment);
	}

	private Node[] populateNodeListFromCenterAlt() {
		final Node[] nArray = new Node[this.length];
		final Random nodeRand = new XorShiftRandom(this.time);
		float angleTotal = 0.0F;
		for (int i = this.length / 8 / 2 - 1; i >= 0; i--) {
			float angle = (nodeRand.nextFloat() - 0.5F) * 8.0F;
			angleTotal += angle;
			if (MathStuff.abs(angleTotal) > 180.0F) {
				angle = -angle;
				angleTotal += angle;
			}

			for (int k = 7; k >= 0; k--) {
				if (i * 8 + k == this.length / 2 - 1) {
					nArray[i * 8 + k] = new Node(0.0F, 7.0F + nodeRand.nextFloat(), 0.0F, angle);

				} else {

					float y;
					if (i == 0)
						y = MathStuff.sin(ANGLE1 * k) * 7.0F + nodeRand.nextFloat() / 2.0F;
					else
						y = 10.0F + nodeRand.nextFloat() * 5.0F;

					final Node node = nArray[i * 8 + k + 1];
					final float subAngle = node.getAngle() + angle;
					final float subAngleRads = MathStuff.toRadians(subAngle);
					final float z = node.posZ - (MathStuff.sin(subAngleRads) * this.nodeLength);
					final float x = node.posX - (MathStuff.cos(subAngleRads) * this.nodeLength);

					nArray[i * 8 + k] = new Node(x, y, z, subAngle);
				}
			}
		}

		angleTotal = 0.0F;
		for (int j = this.length / 8 / 2; j < this.length / 8; j++) {
			float angle = (nodeRand.nextFloat() - 0.5F) * 8.0F;
			angleTotal += angle;
			if (MathStuff.abs(angleTotal) > 180.0F) {
				angle = -angle;
				angleTotal += angle;
			}
			for (int h = 0; h < 8; h++) {
				float y;
				if (j == this.length / 8 - 1)
					y = MathStuff.cos(ANGLE2 * h) * 7.0F + nodeRand.nextFloat() / 2.0F;
				else
					y = 10.0F + nodeRand.nextFloat() * 5.0F;

				final Node node = nArray[j * 8 + h - 1];
				final float subAngle = node.getAngle() + angle;
				final float subAngleRads = MathStuff.toRadians(subAngle);
				final float z = node.posZ + (MathStuff.sin(subAngleRads) * this.nodeLength);
				final float x = node.posX + (MathStuff.cos(subAngleRads) * this.nodeLength);

				nArray[j * 8 + h] = new Node(x, y, z, subAngle);
			}
		}

		return alterWidths(nArray, this.nodeWidth);
	}

	public Node[] translateNodeArray(final Node[] nodeList, final float partialTick) {
		for (int i = 0; i < nodeList.length; i++) {
			final Node node = nodeList[i];
			final float f = MathStuff
					.sin(MathStuff.toRadians(AURORA_WAVELENGTH * i + this.ticker + AURORA_SPEED * partialTick));
			node.setModZ(f * AURORA_AMPLITUDE);
			node.setModY(f * 3.0F);
		}

		return findAngles(nodeList);
	}

	private Node[] alterWidths(final Node[] nodeList, final float widthMax) {
		int count = 0;
		for (int i = 0; i < nodeList.length; i++) {
			float x = widthMax;
			if (i <= nodeList.length / 8) {
				x = MathStuff.sin((float) (MathStuff.PI_F / (nodeList.length / 4) * count)) * widthMax;
				count++;
			}
			if (i >= nodeList.length * 7 / 8) {
				x = MathStuff.sin((float) (MathStuff.PI_F / (nodeList.length / 4) * count)) * widthMax;
				count--;
			}
			nodeList[i].width = x;
		}
		return nodeList;
	}

	private Node[] findAngles(final Node[] nodeList) {
		for (int i = 0; i < nodeList.length; i++) {
			final Node node = nodeList[i];
			float angle = 0.0F;
			float x = 0.0F;
			float x2 = 0.0F;
			float z = 0.0F;
			float z2 = 0.0F;

			if (i == 0 || i == nodeList.length - 1) {
				x = node.posX;
				x2 = x;
				z = node.getModdedZ();
				z2 = z;
			} else {
				angle = MathStuff.atan2(node.getModdedZ() - nodeList[i + 1].getModdedZ(),
						node.posX - nodeList[i + 1].posX);

				final float deg90 = angle + MathStuff.PI_F / 2.0F;
				final float deg270 = deg90 + MathStuff.PI_F;

				x = node.posX + MathStuff.cos(deg90) * node.width;
				x2 = node.posX + MathStuff.cos(deg270) * node.width;
				z = node.getModdedZ() + MathStuff.sin(deg90) * node.width;
				z2 = node.getModdedZ() + MathStuff.sin(deg270) * node.width;
			}

			node.setAngle(angle);
			node.setTet(x, x2, z, z2);
		}

		return nodeList;
	}
}
