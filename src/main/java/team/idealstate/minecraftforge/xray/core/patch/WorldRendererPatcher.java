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
import org.objectweb.asm.commons.Method;

/**
 * <p>WorldRendererPatcher</p>
 *
 * <p>Created on 2024/1/15 17:06</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public class WorldRendererPatcher implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        byte[] ret = basicClass;
        if ("net.minecraft.client.renderer.WorldRenderer".equals(transformedName)) {
            LocalSearcher localSearcher = new LocalSearcher(Opcodes.ASM5);
            new ClassReader(basicClass).accept(localSearcher, ClassReader.EXPAND_FRAMES);
            if (localSearcher.renderBlocksLocal >= 0 && localSearcher.blockLocal >= 0) {
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                new ClassReader(basicClass).accept(new ClassPatcher(Opcodes.ASM5, writer, localSearcher.renderBlocksLocal, localSearcher.blockLocal), ClassReader.EXPAND_FRAMES);
                ret = writer.toByteArray();
            }
//            team.idealstate.minecraftforge.xray.core.Test.dumpClass(name, ret);
        }
        return ret;
    }

    private static class LocalSearcher extends ClassVisitor {

        private int blockLocal = -1;
        private int renderBlocksLocal = -1;

        public LocalSearcher(int api) {
            super(api);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            // net.minecraft.client.renderer.WorldRenderer#updateRenderer(net.minecraft.entity.EntityLivingBase)void
            if ("a".equals(name) && "(Lsv;)V".equals(desc)) {
                return new LocalSearch(api);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        private class LocalSearch extends MethodVisitor {

            private LocalSearch(int api) {
                super(api);
            }

            @Override
            public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                super.visitLocalVariable(name, desc, signature, start, end, index);
                if ("Laji;".equals(desc)) {
                    if (LocalSearcher.this.blockLocal == -1) {
                        LocalSearcher.this.blockLocal = index;
                    } else {
                        LocalSearcher.this.blockLocal = -2;
                    }
                } else if ("Lblm;".equals(desc)) {
                    if (LocalSearcher.this.renderBlocksLocal == -1) {
                        LocalSearcher.this.renderBlocksLocal = index;
                    } else {
                        LocalSearcher.this.renderBlocksLocal = -2;
                    }
                }
            }
        }
    }

    private static class ClassPatcher extends ClassVisitor {

        private final int renderBlocksLocal;
        private final int blockLocal;

        public ClassPatcher(int api, ClassVisitor cv, int renderBlocksLocal, int blockLocal) {
            super(api, cv);
            this.renderBlocksLocal = renderBlocksLocal;
            this.blockLocal = blockLocal;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            // net.minecraft.client.renderer.WorldRenderer#updateRenderer(net.minecraft.entity.EntityLivingBase)void
            if (methodVisitor != null && "a".equals(name) && "(Lsv;)V".equals(desc)) {
                return new MethodPatcher(api, methodVisitor, access, name, desc);
            }
            return methodVisitor;
        }
        
        private class MethodPatcher extends AdviceAdapter {

            private MethodPatcher(int api, MethodVisitor mv, int access, String name, String desc) {
                super(api, mv, access, name, desc);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                super.visitVarInsn(opcode, var);
                if (opcode == ASTORE && var == ClassPatcher.this.blockLocal) {
                    loadLocal(var - 1);
                    loadLocal(var - 3);
                    loadLocal(var - 2);
                    loadLocal(ClassPatcher.this.renderBlocksLocal);
                    loadLocal(var);
                    invokeStatic(
                            Type.getType("XRayHookClient"),
                            Method.getMethod("aji xrayBlockRender_WorldRender_updateRenderer(int, int, int, blm, aji)", true)
                    );
                    storeLocal(var);
                }
            }
        }
    }
}
