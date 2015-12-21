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

package org.blockartistry.mod.BetterRain.asm;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Transformer implements IClassTransformer {

	private static final Logger logger = LogManager.getLogger("BetterRain Transform");

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		
		if("net.minecraft.client.renderer.EntityRenderer".equals(name) || "blt".equals(name)) {
			logger.debug("Transforming EntityRenderer...");
			return transformEntityRenderer(basicClass);
		}
		
		return basicClass;
	}

	private byte[] transformEntityRenderer(final byte[] classBytes) {
		String names[] = null;

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
				m.localVariables = null;
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				m.instructions.add(new VarInsnNode(FLOAD, 1));
				final String sig = "(Lnet/minecraft/client/renderer/EntityRenderer;F)V";
				m.instructions.add(new MethodInsnNode(INVOKESTATIC, "org/blockartistry/mod/BetterRain/client/RenderWeather",
						targetName[0], sig, false));
				m.instructions.add(new InsnNode(RETURN));
			} else if(m.name.equals(names[1])) {
				m.localVariables = null;
				m.instructions.clear();
				m.instructions.add(new VarInsnNode(ALOAD, 0));
				final String sig = "(Lnet/minecraft/client/renderer/EntityRenderer;)V";
				m.instructions.add(new MethodInsnNode(INVOKESTATIC, "org/blockartistry/mod/BetterRain/client/RenderWeather",
						targetName[1], sig, false));
				m.instructions.add(new InsnNode(RETURN));
			}
		}

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cn.accept(cw);
		return cw.toByteArray();
	}
}
