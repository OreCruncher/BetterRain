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

package org.blockartistry.mod.BetterRain;

import net.minecraftforge.common.config.Configuration;

public final class ModOptions {

	private ModOptions() {
	}

	protected static final String CATEGORY_LOGGING_CONTROL = "logging";
	protected static final String CONFIG_ENABLE_DEBUG_LOGGING = "Enable Debug Logging";
	protected static boolean enableDebugLogging = false;

	protected static final String CONFIG_ENABLE_ONLINE_VERSION_CHECK = "Enable Online Version Check";
	protected static boolean enableVersionChecking = true;

	public static void load(final Configuration config) {

		// CATEGORY: Logging
		String comment = "Enables/disables debug logging of the mod";
		enableDebugLogging = config.getBoolean(CONFIG_ENABLE_DEBUG_LOGGING, CATEGORY_LOGGING_CONTROL,
				enableDebugLogging, comment);

		comment = "Enables/disables online version checking";
		enableVersionChecking = config.getBoolean(CONFIG_ENABLE_ONLINE_VERSION_CHECK, CATEGORY_LOGGING_CONTROL,
				enableVersionChecking, comment);

	}

	public static boolean getEnableDebugLogging() {
		return enableDebugLogging;
	}

	public static boolean getOnlineVersionChecking() {
		return enableVersionChecking;
	}
}
