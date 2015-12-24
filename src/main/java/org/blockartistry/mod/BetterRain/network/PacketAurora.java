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

package org.blockartistry.mod.BetterRain.network;

import org.blockartistry.mod.BetterRain.client.ClientEffectHandler;
import org.blockartistry.mod.BetterRain.data.AuroraData;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public final class PacketAurora implements IMessage, IMessageHandler<PacketAurora, IMessage> {

	private int dimension;
	private long time;
	private int posX;
	private int posZ;
	private int colorSet;

	public PacketAurora() {
	}

	public PacketAurora(final AuroraData data) {
		this(data.dimensionId, data.time, data.posX, data.posZ, data.colorSet);
	}

	public PacketAurora(final int dimensionId, final long time, final int posX, final int posZ, final int colorSet) {
		this.dimension = dimensionId;
		this.time = time;
		this.posX = posX;
		this.posZ = posZ;
		this.colorSet = colorSet;
	}

	public void fromBytes(final ByteBuf buf) {
		this.dimension = buf.readInt();
		this.time = buf.readLong();
		this.posX = buf.readInt();
		this.posZ = buf.readInt();
		this.colorSet = buf.readByte();
	}

	public void toBytes(final ByteBuf buf) {
		buf.writeInt(this.dimension);
		buf.writeLong(this.time);
		buf.writeInt(this.posX);
		buf.writeInt(this.posZ);
		buf.writeByte(this.colorSet);
	}

	@Override
	public IMessage onMessage(final PacketAurora message, final MessageContext ctx) {
		ClientEffectHandler.addAurora(new AuroraData(message.dimension, message.posX, message.posZ, message.time, message.colorSet));
		return null;
	}
}
