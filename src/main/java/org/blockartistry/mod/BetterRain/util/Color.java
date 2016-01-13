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

/*
 * The MIT License (MIT)
 * Copyright (c) 2013 Ben Holland
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

// Portions from: https://github.com/benjholla/ColorMixer
package org.blockartistry.mod.BetterRain.util;

import net.minecraft.util.Vec3;

/**
 * Holds an RGB triple. See: http://www.rapidtables.com/web/color/RGB_Color.htm
 */
public final class Color {

	public static final Color RED = new Color(255, 0, 0);
	public static final Color ORANGE = new Color(255, 127, 0);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color LGREEN = new Color(127, 255, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color TURQOISE = new Color(0, 255, 127);
	public static final Color CYAN = new Color(0, 255, 255);
	public static final Color AUQUAMARINE = new Color(0, 127, 255);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color VIOLET = new Color(127, 0, 255);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color RASPBERRY = new Color(255, 0, 127);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color PURPLE = new Color(80, 0, 80);
	public static final Color INDIGO = new Color(75, 0, 130);
	public static final Color NAVY = new Color(0, 0, 128);
	public static final Color TAN = new Color(210, 180, 140);

	public final int red;
	public final int green;
	public final int blue;

	public Color(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public Color(final Vec3 vec) {
		this((int) (vec.xCoord * 255.0D), (int) (vec.yCoord * 255.0D), (int) (vec.zCoord * 255.0D));
	}

	public Color(final float red, final float green, final float blue) {
		this((int) (red * 255.0F), (int) (green * 255.0F), (int) (blue * 255.0F));
	}

	public Vec3 toVec3() {
		return new Vec3(this.red / 255.0D, this.green / 255.0D, this.blue / 255.0D);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[r:").append(this.red);
		builder.append(",g:").append(this.green);
		builder.append(",b:").append(this.blue);
		builder.append(']');
		return builder.toString();
	}

	/**
	 * @author Ben Holland
	 * 
	 *         A helper class for mixing RGB Colors. Uses a simplified
	 *         Kubelka-Munk model. Assumes all colors are opaque and that all
	 *         colors have equal weight when blending.
	 * 
	 *         Example Usage: KMColor color = new KMColor(java.awt.Color.RED);
	 *         color.mix(java.awt.Color.ORANGE); java.awt.Color result =
	 *         color.getRGBColor();
	 */
	private static class KMColor {

		// Kubelka-Munk absorption coefficient to scattering coefficient ratios
		// for each channel (also known as the Absorbance)
		private double A_r; // RED channel absorbance
		private double A_g; // GREEN channel absorbance
		private double A_b; // BLUE channel absorbance

		/**
		 * Returns a Reflectance measure. Assumes the color is opaque.
		 * 
		 * @param absortionRatio
		 *            Kubelka-Munk absorption coefficient to scattering
		 *            coefficient ratio
		 * @return
		 */
		private double calculateReflectance(double absorbtionRatio) {
			return 1.0 + absorbtionRatio - Math.sqrt(Math.pow(absorbtionRatio, 2.0) + (2.0 * absorbtionRatio));
		}

		/**
		 * Returns an absorbance (K/S) measure for a given RGB channel.
		 * 
		 * @param RGBChannelValue
		 *            (integer value between 0 and 255).
		 * @return
		 */
		private double calculateAbsorbance(double RGBChannelValue) {
			return Math.pow((1.0 - RGBChannelValue), 2.0) / (2.0 * RGBChannelValue);
		}

		/**
		 * Creates a new Color
		 * 
		 * @param color
		 *            The color to create
		 */
		public KMColor(final Color color) {
			// normalize the RGB color values
			final double red = color.red == 0 ? 0.00001 : (double) color.red / 255.0;
			final double green = color.green == 0 ? 0.00001 : (double) color.green / 255.0;
			final double blue = color.blue == 0 ? 0.00001 : (double) color.blue / 255.0;

			// calculate an Absorbance measure for each channel of the color
			this.A_r = calculateAbsorbance(red);
			this.A_g = calculateAbsorbance(green);
			this.A_b = calculateAbsorbance(blue);
		}

		/**
		 * Mixes a collection of colors into this color Calculates a new K and S
		 * coefficient using a weighted average of all K and S coefficients In
		 * this implementation we assume each color has an equal concentration
		 * so each concentration weight will be equal to 1/(1 + colors.size)
		 * 
		 * @param colors
		 *            The Colors to mix into this Color
		 */
		/*
		 * public void mix(final Color... colors){ // just a little error
		 * checking if(colors == null || colors.length == 0){ return; }
		 * 
		 * // calculate a concentration weight for a equal concentration of all
		 * colors in mix final double concentration = 1.0 / (1.0 +
		 * (double)colors.length);
		 * 
		 * // calculate first iteration double A_r = this.A_r * concentration;
		 * double A_g = this.A_g * concentration; double A_b = this.A_b *
		 * concentration;
		 * 
		 * // sum the weighted average for(int i=0; i<colors.length; i++){
		 * KMColor color = new KMColor(colors[i]); A_r += color.A_r *
		 * concentration; A_g += color.A_g * concentration; A_b += color.A_b *
		 * concentration; }
		 * 
		 * // update with results this.A_r = A_r; this.A_g = A_g; this.A_b =
		 * A_b; }
		 */

		/**
		 * Mixes another color into this color Calculates a new K and S
		 * coefficient using by averaging the K and S values In this
		 * implementation we assume each color has an equal concentration
		 * 
		 * @param color
		 *            The Color to mix into this Color
		 */
		public void mix(final Color color) {
			// calculate new KS (Absorbance) for mix with one color of equal
			// concentration
			final KMColor kmColor = new KMColor(color);
			this.A_r = (this.A_r + kmColor.A_r) / 2.0;
			this.A_g = (this.A_g + kmColor.A_g) / 2.0;
			this.A_b = (this.A_b + kmColor.A_b) / 2.0;
		}

		/**
		 * Returns a standard RGB color as a java.awt.Color object
		 * 
		 * @return
		 */
		public Color getColor() {
			final int red = (int) (calculateReflectance(this.A_r) * 255.0);
			final int green = (int) (calculateReflectance(this.A_g) * 255.0);
			final int blue = (int) (calculateReflectance(this.A_b) * 255.0);
			return new Color(red, green, blue);
		}
	}

	/**
	 * Simple wrapper method for mixing two colors
	 * 
	 * @param colorA
	 * @param colorB
	 * @return
	 */
	public static Color mix(final Color colorA, final Color colorB) {
		final KMColor color = new KMColor(colorA);
		color.mix(colorB);
		return color.getColor();
	}

	/**
	 * Simple wrapper method for mixing a collection of colors
	 * 
	 * @param colors
	 * @return
	 */
	public static Color mix(final Color... colors) {
		if (colors.length >= 1) {
			final KMColor color = new KMColor(colors[0]);
			for (int i = 1; i < colors.length; i++) {
				color.mix(colors[i]);
			}
			return color.getColor();
		}
		return null;
	}

}
