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

package org.blockartistry.mod.BetterRain.client;

import java.util.ArrayList;
import java.util.List;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.aurora.AuroraPreset;
import org.blockartistry.mod.BetterRain.aurora.Color;
import org.blockartistry.mod.BetterRain.aurora.ColorPair;
import org.blockartistry.mod.BetterRain.data.AuroraData;
import org.blockartistry.mod.BetterRain.util.MathStuff;
import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Aurora {

	private static final boolean MULTIPLES = ModOptions.getAuroraAllowMultiples();

	private static final float ANGLE1 = MathStuff.PI_F / 16.0F;
	private static final float ANGLE2 = MathStuff.toRadians(90.0F / 7.0F);

	private static final int FADE_LIMIT = 1280;

	public static final float AURORA_SPEED = 0.75F;
	public static final float AURORA_AMPLITUDE = 18.0F;
	public static final float AURORA_WAVELENGTH = 8.0F;

	private long time;
	public float posX;
	public float posZ;
	private float ticker = 0.0F;
	public int fadeTimer = 0;
	public boolean isAlive = true;
	protected List<Node[]> nodeList = new ArrayList<Node[]>();

	private int length;
	private float nodeLength;
	private float nodeWidth;
	private int bandOffset;

	// Base color of the aurora
	private final Color color1;
	// Fade color of the aurora
	private final Color color2;
	// Alpha setting of the aurora for fade
	private int alpha = 1;

	public Aurora(final AuroraData data) {
		this(data.posX, data.posZ, data.time, data.colorSet, data.preset);
	}

	public Aurora(final float x, final float z, final long t, final int colorSet, final int preset) {
		this.time = t;
		this.posX = x;
		this.posZ = z;

		setPreset(preset);

		final ColorPair pair = ColorPair.get(colorSet);
		this.color1 = pair.first;
		this.color2 = pair.second;

		final Node[] baseArray = populateNodeListFromCenterAlt();
		this.nodeList.add(baseArray);

		if (MULTIPLES) {
			this.nodeList.add(formBand(baseArray, this.bandOffset));
			this.nodeList.add(formBand(baseArray, -this.bandOffset));
		}
		
		translate(0);
	}

	public List<Node[]> getNodeList() {
		return this.nodeList;
	}

	private void setPreset(final int preset) {
		final AuroraPreset p = AuroraPreset.get(preset);
		this.length = p.length;
		this.nodeLength = p.nodeLength;
		this.nodeWidth = p.nodeWidth;
		this.bandOffset = p.bandOffset;
	}

	public Color getColor1() {
		return this.color1;
	}

	public Color getColor2() {
		return this.color2;
	}

	public int getAlpha() {
		return this.alpha;
	}

	public boolean isAlive() {
		return this.isAlive;
	}

	public void die() {
		if (this.isAlive) {
			this.isAlive = false;
			this.fadeTimer = 0;
		}
	}

	public void update() {
		if (this.fadeTimer < FADE_LIMIT) {
			if (this.fadeTimer % 10 == 0 && this.alpha > 0)
				this.alpha += this.isAlive ? 1 : -1;
			this.fadeTimer++;
		}

		if (this.ticker < 360.0F) {
			this.ticker += AURORA_SPEED;
		} else {
			this.ticker = 0.0F;
		}
	}

	private static Node[] formBand(final Node[] nodeList, final int offset) {
		final Node[] tet = new Node[nodeList.length];
		for (int i = 0; i < nodeList.length; i++) {
			final Node node = nodeList[i];
			final float rads = MathStuff.toRadians(90.0F + node.angle);
			final float posX = node.posX + MathStuff.cos(rads) * offset;
			final float posZ = node.posZ + MathStuff.sin(rads) * offset;
			tet[i] = new Node(posX, node.posY - 2.0F, posZ, node.angle);
		}

		return alterWidths(tet, 10.0F);
	}

	private Node[] populateNodeListFromCenterAlt() {
		final Node[] nodeList = new Node[this.length];
		final XorShiftRandom nodeRand = new XorShiftRandom(this.time);
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
					nodeList[i * 8 + k] = new Node(0.0F, 7.0F + nodeRand.nextFloat(), 0.0F, angle);

				} else {

					float y;
					if (i == 0)
						y = MathStuff.sin(ANGLE1 * k) * 7.0F + nodeRand.nextFloat() / 2.0F;
					else
						y = 10.0F + nodeRand.nextFloat() * 5.0F;

					final Node node = nodeList[i * 8 + k + 1];
					final float subAngle = node.angle + angle;
					final float subAngleRads = MathStuff.toRadians(subAngle);
					final float z = node.posZ - (MathStuff.sin(subAngleRads) * this.nodeLength);
					final float x = node.posX - (MathStuff.cos(subAngleRads) * this.nodeLength);

					nodeList[i * 8 + k] = new Node(x, y, z, subAngle);
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

				final Node node = nodeList[j * 8 + h - 1];
				final float subAngle = node.angle + angle;
				final float subAngleRads = MathStuff.toRadians(subAngle);
				final float z = node.posZ + (MathStuff.sin(subAngleRads) * this.nodeLength);
				final float x = node.posX + (MathStuff.cos(subAngleRads) * this.nodeLength);

				nodeList[j * 8 + h] = new Node(x, y, z, subAngle);
			}
		}

		return alterWidths(nodeList, this.nodeWidth);
	}

	private static Node[] alterWidths(final Node[] nodeList, final float widthMax) {
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
			nodeList[i].setWidth(x);
		}
		return nodeList;
	}

	/*
	 * Calculates the next "frame" of the aurora if it is being
	 * animated.
	 */
	public void translate(final float partialTick) {
		final float c = this.ticker + AURORA_SPEED * partialTick;
		for (final Node[] nodeList : this.nodeList) {
			for (int i = 0; i < nodeList.length; i++) {
				final float f = MathStuff.sin(MathStuff.toRadians(AURORA_WAVELENGTH * i + c));
				final Node node = nodeList[i];
				node.setModZ(f * AURORA_AMPLITUDE);
				node.setModY(f * 3.0F);
			}

			findAngles(nodeList);
		}
	}

	private static void findAngles(final Node[] nodeList) {
		for (int i = 0; i < nodeList.length; i++) {

			final Node node = nodeList[i];
			node.tetX = node.tetX2 = node.posX;
			node.tetZ = node.tetZ2 = node.getModdedZ();
			node.angle = 0.0F;

			if (i > 0 && i < nodeList.length - 1) {
				final Node nodePlus = nodeList[i + 1];
				node.angle = MathStuff.atan2(node.getModdedZ() - nodePlus.getModdedZ(), node.posX - nodePlus.posX);
				node.tetX += node.cosDeg90;
				node.tetX2 += node.cosDeg270;
				node.tetZ += node.sinDeg90;
				node.tetZ2 += node.sinDeg270;
			}
		}
	}
}
