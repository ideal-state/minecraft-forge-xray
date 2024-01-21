/*
 *     <one line to give the program's name and a brief idea of what it does.>
 *     Copyright (C) 2024/01/19 ideal-state
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package team.idealstate.minecraftforge.xray.core.patch;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * <p>BlockPatcher</p>
 *
 * <p>Created on 2024/1/15 17:06</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public class BlockPatcher implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        byte[] ret = basicClass;
        if ("net.minecraft.block.Block".equals(transformedName)) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            new ClassReader(basicClass).accept(new ClassPatcher(Opcodes.ASM5, writer), ClassReader.EXPAND_FRAMES);
//            new ClassReader(basicClass).accept(writer, ClassReader.EXPAND_FRAMES);
            ret = writer.toByteArray();
//            team.idealstate.minecraftforge.xray.core.Test.dumpClass(name, ret);
        }
        return ret;
    }

    private static class ClassPatcher extends ClassVisitor {

        public ClassPatcher(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if (methodVisitor != null) {
                // net/minecraft/block/Block/shouldSideBeRendered (Lnet/minecraft/world/IBlockAccess;IIII)Z
                if ("a".equals(name) && "(Lahl;IIII)Z".equals(desc)) {
                    return new ShouldSideBeRenderedMethodPatcher(api, methodVisitor, access, name, desc);
                }
            }
            return methodVisitor;
        }

        private static class ShouldSideBeRenderedMethodPatcher extends AdviceAdapter {

            private ShouldSideBeRenderedMethodPatcher(int api, MethodVisitor mv, int access, String name, String desc) {
                super(api, mv, access, name, desc);
            }

            @Override
            public void visitInsn(int opcode) {
                if (opcode == Opcodes.IRETURN) {
                    loadArg(0);
                    loadArg(1);
                    loadArg(2);
                    loadArg(3);
                    loadArg(4);
                    invokeStatic(
                            Type.getType("XRayHookClient"),
                            Method.getMethod("boolean xrayBlockRender_Block_shouldSideBeRendered(boolean, ahl, int, int, int, int)", true)
                    );
                }
                super.visitInsn(opcode);
            }
        }
    }
}
