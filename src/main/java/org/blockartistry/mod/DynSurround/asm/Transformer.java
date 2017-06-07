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

import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Transformer implements IClassTransformer {

	private static final Logger logger = LogManager.getLogger("dsurround Transform");

	@Override
	public byte[] transform(final String name, final String transformedName, byte[] basicClass) {
		if ("net.minecraft.client.renderer.EntityRenderer".equals(name) || "blt".equals(name)) {
			logger.debug("Transforming EntityRenderer...");
			return transformEntityRenderer(basicClass);
		} else if ("net.minecraft.world.WorldServer".equals(name) || "mt".equals(name)) {
			logger.debug("Transforming WorldServer...");
			return transformWorldServer(basicClass);
		} else if ("net.minecraft.world.World".equals(name) || "ahb".equals(name)) {
			logger.debug("Transforming World...");
			return transformWorld(basicClass);
		} else if ("net.minecraft.client.audio.SoundHandler".equals(name) || "btp".equals(name)) {
			logger.debug("Transforming SoundHandler...");
			return transformSoundHandler(basicClass);
		} else if ("net.minecraft.client.audio.SoundManager".equals(name) || "btj".equals(name)) {
			logger.debug("Transforming SoundManager...");
			return transformSoundManager(basicClass);
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
				final InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new VarInsnNode(FLOAD, 1));
				final String sig = "(Lnet/minecraft/client/renderer/EntityRenderer;F)V";
				list.add(new MethodInsnNode(INVOKESTATIC, "org/blockartistry/mod/DynSurround/client/RenderWeather",
						targetName[0], sig, false));
				list.add(new InsnNode(RETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), list);
			} else if (m.name.equals(names[1])) {
				logger.debug("Hooking " + names[1]);
				final InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/client/renderer/EntityRenderer;)V";
				list.add(new MethodInsnNode(INVOKESTATIC, "org/blockartistry/mod/DynSurround/client/RenderWeather",
						targetName[1], sig, false));
				list.add(new InsnNode(RETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), list);
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
				final InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/world/WorldServer;)V";
				list.add(new MethodInsnNode(INVOKESTATIC, "org/blockartistry/mod/DynSurround/server/PlayerSleepHandler",
						targetName[0], sig, false));
				list.add(new InsnNode(RETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), list);
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
				final InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/world/World;)V";
				list.add(new MethodInsnNode(INVOKESTATIC, "org/blockartistry/mod/DynSurround/server/WorldHandler",
						targetName[0], sig, false));
				list.add(new InsnNode(RETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), list);
				break;
			}
		}

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private byte[] transformSoundHandler(final byte[] classBytes) {
		final String managerToReplace = "net/minecraft/client/audio/SoundManager";
		final String newManager = "org/blockartistry/mod/DynSurround/client/sound/SoundManagerReplacement";

		final ClassReader cr = new ClassReader(classBytes);
		final ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		for (final MethodNode m : cn.methods) {
			final ListIterator<AbstractInsnNode> itr = m.instructions.iterator();
			boolean foundNew = false;
			while (itr.hasNext()) {
				final AbstractInsnNode node = itr.next();
				if (node.getOpcode() == NEW) {
					final TypeInsnNode theNew = (TypeInsnNode) node;
					if (managerToReplace.equals(theNew.desc)) {
						m.instructions.set(node, new TypeInsnNode(NEW, newManager));
						foundNew = true;
					}
				} else if (node.getOpcode() == INVOKESPECIAL) {
					final MethodInsnNode theInvoke = (MethodInsnNode) node;
					if (managerToReplace.equals(theInvoke.owner)) {
						if (foundNew) {
							m.instructions.set(node, new MethodInsnNode(INVOKESPECIAL, newManager, theInvoke.name,
									theInvoke.desc, false));
							foundNew = false;
						}
					}
				}
			}
		}

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}

	private byte[] transformSoundManager(final byte[] classBytes) {
		final String urlNames[];

		if (TransformLoader.runtimeDeobEnabled) {
			urlNames = new String[] { "func_148612_a" };
		} else {
			urlNames = new String[] { "getURLForSoundResource" };
		}

		final String urlTargetName[] = new String[] { "getURLForSoundResource" };

		final ClassReader cr = new ClassReader(classBytes);
		final ClassNode cn = new ClassNode(ASM5);
		cr.accept(cn, 0);

		for (final MethodNode m : cn.methods) {
			if (m.name.equals(urlNames[0])) {
				logger.debug("Hooking " + m.name);
				InsnList list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/util/ResourceLocation;)Ljava/net/URL;";
				list.add(new MethodInsnNode(INVOKESTATIC,
						"org/blockartistry/mod/DynSurround/client/sound/cache/SoundCache", urlTargetName[0], sig,
						false));
				list.add(new InsnNode(ARETURN));
				m.instructions.insertBefore(m.instructions.getFirst(), list);

			}
		}

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
}
