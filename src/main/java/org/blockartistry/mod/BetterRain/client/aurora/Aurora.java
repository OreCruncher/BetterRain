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

package org.blockartistry.mod.BetterRain.client.aurora;

import java.util.ArrayList;
import java.util.List;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.AuroraData;
import org.blockartistry.mod.BetterRain.data.AuroraPreset;
import org.blockartistry.mod.BetterRain.data.ColorPair;
import org.blockartistry.mod.BetterRain.util.Color;
import org.blockartistry.mod.BetterRain.util.MathStuff;
import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Aurora {

	private static final boolean MULTIPLES = ModOptions.getAuroraMultipleBands();

	private static final float ANGLE1 = MathStuff.PI_F / 16.0F;
	private static final float ANGLE2 = MathStuff.toRadians(90.0F / 7.0F);

	private static final int FADE_LIMIT = 1280;

	public static final float AURORA_SPEED = 0.75F;
	public static final float AURORA_AMPLITUDE = 18.0F;
	public static final float AURORA_WAVELENGTH = 8.0F;

	private long seed;
	public float posX;
	public float posZ;
	private float cycle = 0.0F;
	public int fadeTimer = 0;
	public boolean isAlive = true;
	protected List<Node[]> nodeList = new ArrayList<Node[]>();

	private int length;
	private float nodeLength;
	private float nodeWidth;
	private int bandOffset;

	// Base color of the aurora
	private final Color baseColor;
	// Fade color of the aurora
	private final Color fadeColor;
	// Alpha setting of the aurora for fade
	private int alpha = 1;

	public Aurora(final AuroraData data) {
		this(data.posX, data.posZ, data.seed, data.colorSet, data.preset);
	}

	public Aurora(final float x, final float z, final long seed, final int colorSet, final int preset) {
		this.seed = seed;
		this.posX = x;
		this.posZ = z;

		setPreset(preset);

		final ColorPair pair = ColorPair.get(colorSet);
		this.baseColor = pair.baseColor;
		this.fadeColor = pair.fadeColor;

		final Node[] baseArray = populateNodeListFromCenterAlt();
		this.nodeList.add(baseArray);

		if (MULTIPLES) {
			this.nodeList.add(bandFromTemplate(baseArray, this.bandOffset));
			this.nodeList.add(bandFromTemplate(baseArray, -this.bandOffset));
		}
		
		// Initialize at least once for a non-animated aurora
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

	public Color getBaseColor() {
		return this.baseColor;
	}

	public Color getFadeColor() {
		return this.fadeColor;
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

		if((this.cycle += AURORA_SPEED) >= 360.0F)
			this.cycle -= 360.0F;
	}

	private static Node[] bandFromTemplate(final Node[] template, final int offset) {
		final Node[] tet = new Node[template.length];
		for (int i = 0; i < template.length; i++) {
			final Node node = template[i];
			final float rads = MathStuff.toRadians(90.0F + node.angle);
			final float posX = node.posX + MathStuff.cos(rads) * offset;
			final float posZ = node.posZ + MathStuff.sin(rads) * offset;
			tet[i] = new Node(posX, node.posY - 2.0F, posZ, node.angle);
		}

		return alterWidths(tet, 10.0F);
	}

	private Node[] populateNodeListFromCenterAlt() {
		final Node[] nodeList = new Node[this.length];
		final XorShiftRandom nodeRand = new XorShiftRandom(this.seed);
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

	// Scales the widths for nodes at the head and tail of the
	// node list.
	private static Node[] alterWidths(final Node[] nodeList, final float widthMax) {
		final float factor = MathStuff.PI_F / (nodeList.length / 4);
		final int lowerBound = nodeList.length / 8 + 1;
		final int upperBound = nodeList.length * 7 / 8 - 1;

		int count = 0;
		for (int i = 0; i < nodeList.length; i++) {
			final float x;

			if (i < lowerBound) {
				x = MathStuff.sin(factor * count++) * widthMax;
			} else if (i > upperBound) {
				x = MathStuff.sin(factor * count--) * widthMax;
			} else {
				x = widthMax;
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
		final float c = this.cycle + AURORA_SPEED * partialTick;
		final Node[] nodeList = this.nodeList.get(0);
		for (int i = 0; i < nodeList.length; i++) {
			final Node node = nodeList[i];
			final float f = MathStuff.sin(MathStuff.toRadians(AURORA_WAVELENGTH * i + c));
			node.setModZ(f * AURORA_AMPLITUDE);
			node.setModY(f * 3.0F);
		}
		findAngles(nodeList);
		if(this.nodeList.size() > 1) {
			final Node[] second = this.nodeList.get(1);
			final Node[] third = this.nodeList.get(2);
			for(int i = 0; i < nodeList.length; i++) {
				final Node src = nodeList[i];
				final Node t1 = second[i];
				final Node t2 = third[i];
				
				t1.setModZ(src.getModZ());
				t2.setModZ(src.getModZ());
				t1.setModY(src.getModY());
				t2.setModY(src.getModY());
			}
			findAngles(second);
			findAngles(third);
		}
	}

	private static void findAngles(final Node[] nodeList) {
		nodeList[0].findAngles(null);
		nodeList[nodeList.length - 1].findAngles(null);
		for (int i = 1; i < nodeList.length - 1; i++)
			nodeList[i].findAngles(nodeList[i + 1]);
	}
}
