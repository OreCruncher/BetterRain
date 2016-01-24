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

package org.blockartistry.mod.DynSurround.asm;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.ImmutableList;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Transformer implements IClassTransformer {

	private static final Logger logger = LogManager.getLogger("dsurround Transform");

	private static final List<String> soundTickClasses = ImmutableList.<String> builder()
			.add("net.minecraft.block.BlockIce", "alp").add("net.minecraft.block.BlockPackedIce", "amo")
			.add("net.minecraft.block.BlockLilyPad", "aoj").add("net.minecraft.block.BlockRedstoneOre", "amz")
			.add("net.minecraft.block.BlockSoulSand", "ano").build();

	private static final List<String> liquidTickClasses = ImmutableList.<String> builder()
			.add("net.minecraft.block.BlockLiquid", "alw").build();

	@Override
	public byte[] transform(final String name, final String transformedName, byte[] basicClass) {

		// Random display tick transforms
		if (soundTickClasses.contains(name)) {
			logger.debug("Transforming " + name);
			basicClass = transformRandomDisplayTick(basicClass,
					"org/blockartistry/mod/DynSurround/client/fx/BlockSoundHandler");
		}

		if (liquidTickClasses.contains(name)) {
			logger.debug("Transforming liquid " + name);
			basicClass = transformRandomDisplayTick(basicClass,
					"org/blockartistry/mod/DynSurround/client/fx/BlockLiquidHandler");
		}

		if ("net.minecraft.client.renderer.EntityRenderer".equals(name) || "blt".equals(name)) {
			logger.debug("Transforming EntityRenderer...");
			return transformEntityRenderer(basicClass);
		} else if ("net.minecraft.world.WorldServer".equals(name) || "mt".equals(name)) {
			logger.debug("Transforming WorldServer...");
			return transformWorldServer(basicClass);
		} else if ("net.minecraft.world.World".equals(name) || "ahb".equals(name)) {
			logger.debug("Transforming World...");
			return transformWorld(basicClass);
		}

		return basicClass;
	}

	private byte[] transformEntityRenderer(final byte[] classBytes) {
		final String names[];

		if (TransformLoader.runtimeDeobEnabled)
			names = new String[] { "func_78474_d", "func_78484_h" };
		else
			names = new String[] { "renderRainSnow", "addRainParticles" };

		final String targetName[] = new String[] { "renderRainSnow", "addRainParticles" };

		final ClassReader cr = new ClassReader(classBytes);
		final ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		for (final MethodNode m : cn.methods) {
			if (m.name.equals(names[0])) {
				logger.debug("Hooking " + names[0]);
				m.localVariables = null;
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new VarInsnNode(FLOAD, 1));
				final String sig = "(Lnet/minecraft/client/renderer/EntityRenderer;F)V";
				m.instructions.add(new MethodInsnNode(INVOKESTATIC,
						"org/blockartistry/mod/DynSurround/client/RenderWeather", targetName[0], sig, false));
				m.instructions.add(new InsnNode(RETURN));
			} else if (m.name.equals(names[1])) {
				logger.debug("Hooking " + names[1]);
				m.localVariables = null;
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/client/renderer/EntityRenderer;)V";
				m.instructions.add(new MethodInsnNode(INVOKESTATIC,
						"org/blockartistry/mod/DynSurround/client/RenderWeather", targetName[1], sig, false));
				m.instructions.add(new InsnNode(RETURN));
			}
		}

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private byte[] transformWorldServer(final byte[] classBytes) {
		final String names[];

		if (TransformLoader.runtimeDeobEnabled)
			names = new String[] { "func_73051_P" };
		else
			names = new String[] { "resetRainAndThunder" };

		final String targetName[] = new String[] { "resetRainAndThunder" };

		final ClassReader cr = new ClassReader(classBytes);
		final ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		for (final MethodNode m : cn.methods) {
			if (m.name.equals(names[0])) {
				logger.debug("Hooking " + names[0]);
				m.localVariables = null;
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/world/WorldServer;)V";
				m.instructions.add(new MethodInsnNode(INVOKESTATIC,
						"org/blockartistry/mod/DynSurround/server/PlayerSleepHandler", targetName[0], sig, false));
				m.instructions.add(new InsnNode(RETURN));
				break;
			}
		}

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private byte[] transformWorld(final byte[] classBytes) {
		final String names[];

		if (TransformLoader.runtimeDeobEnabled)
			names = new String[] { "updateWeatherBody" };
		else
			names = new String[] { "updateWeatherBody" };

		final String targetName[] = new String[] { "updateWeatherBody" };

		final ClassReader cr = new ClassReader(classBytes);
		final ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		for (final MethodNode m : cn.methods) {
			if (m.name.equals(names[0])) {
				logger.debug("Hooking " + names[0]);
				m.localVariables = null;
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/world/World;)V";
				m.instructions.add(new MethodInsnNode(INVOKESTATIC,
						"org/blockartistry/mod/DynSurround/server/WorldHandler", targetName[0], sig, false));
				m.instructions.add(new InsnNode(RETURN));
				break;
			}
		}

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private byte[] transformRandomDisplayTick(final byte[] classBytes, final String handlerClass) {

		final String names[];

		if (TransformLoader.runtimeDeobEnabled)
			names = new String[] { "func_149734_b" };
		else
			names = new String[] { "randomDisplayTick" };

		final String targetName[] = new String[] { "randomDisplayTick" };

		final ClassReader cr = new ClassReader(classBytes);
		final ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		InsnList list = new InsnList();
		list.add(new VarInsnNode(ALOAD, 0));
		list.add(new VarInsnNode(ALOAD, 1));
		list.add(new VarInsnNode(ILOAD, 2));
		list.add(new VarInsnNode(ILOAD, 3));
		list.add(new VarInsnNode(ILOAD, 4));
		list.add(new VarInsnNode(ALOAD, 5));
		final String sig = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;IIILjava/util/Random;)V";
		list.add(new MethodInsnNode(INVOKESTATIC, handlerClass, targetName[0], sig, false));

		MethodNode m = findMethod(cn.methods, names[0]);
		if (m == null) {
			list.add(new InsnNode(RETURN));
			final String desc = "(Lnet/minecraft/world/World;IIILjava/util/Random;)V";
			m = new MethodNode(Opcodes.ACC_PUBLIC, names[0], desc, null, null);
			m.localVariables.clear();
			m.instructions = list;
			cn.methods.add(m);
		} else {
			m.instructions.insertBefore(m.instructions.getFirst(), list);
		}

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

	// Helper methods
	private static MethodNode findMethod(final List<MethodNode> methods, final String name) {
		for (final MethodNode node : methods)
			if (node.name.equals(name))
				return node;
		return null;
	}
}
