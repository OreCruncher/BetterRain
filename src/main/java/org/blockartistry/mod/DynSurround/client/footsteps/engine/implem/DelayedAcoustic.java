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

package org.blockartistry.mod.DynSurround.client.footsteps.engine.implem;

import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.IOptions;

public class DelayedAcoustic extends BasicAcoustic implements IOptions {
	protected long delayMin = 0;
	protected long delayMax = 0;

	public DelayedAcoustic() {
		super();

		this.outputOptions = this;
	}

	@Override
	public boolean hasOption(final String option) {
		return option.equals("delay_min") || option.equals("delay_max");
	}

	@Override
	public Object getOption(final String option) {
		return option.equals("delay_min") ? this.delayMin : option.equals("delay_max") ? this.delayMax : null;
	}

	public void setDelayMin(final long delay) {
		this.delayMin = delay;
	}

	public void setDelayMax(final long delay) {
		this.delayMax = delay;
	}
}
