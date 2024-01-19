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
 * <p>GuiScrollingListPatcher</p>
 *
 * <p>Created on 2024/1/15 17:06</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public class GuiScrollingListPatcher implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        byte[] ret = basicClass;
        if ("cpw.mods.fml.client.GuiScrollingList".equals(transformedName)) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            new ClassReader(basicClass).accept(new ClassPatcher(Opcodes.ASM5, writer), ClassReader.EXPAND_FRAMES);
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
                // cpw.mods.fml.client.GuiScrollingList#drawScreen(int, int, float)void
                if ("drawScreen".equals(name) && "(IIF)V".equals(desc)) {
                    return new DrawScreenMethodPatcher(api, methodVisitor, access, name, desc);
                }
            }
            return methodVisitor;
        }
        
        private static class DrawScreenMethodPatcher extends AdviceAdapter {

            private int step = 0;
            private Label originalIfLabel = null;
            private Label hookNewLabel = newLabel();
            private Label originalGotoLabel = null;

            /**
             * <code>
             *     if (this.initialMouseClickY >= 0.0F) {
             *         this.scrollDistance -= ((float)mouseY - this.initialMouseClickY) * this.scrollFactor;
             *         this.initialMouseClickY = (float)mouseY;
             *     }
             * <code/>
             */
            private int hookIsReady_IFLT_initialMouseClickY_0F = 0;

            private DrawScreenMethodPatcher(int api, MethodVisitor mv, int access, String name, String desc) {
                super(api, mv, access, name, desc);
            }

            private void xrayBlockListGui_GuiScrollingList_drawScreen(Label label) {
                loadThis();
                super.visitFieldInsn(Opcodes.GETFIELD, "cpw/mods/fml/client/GuiScrollingList",
                        "top", "I");
                loadThis();
                super.visitFieldInsn(Opcodes.GETFIELD, "cpw/mods/fml/client/GuiScrollingList",
                        "bottom", "I");
                loadThis();
                super.visitFieldInsn(Opcodes.GETFIELD, "cpw/mods/fml/client/GuiScrollingList",
                        "left", "I");
                loadThis();
                super.visitFieldInsn(Opcodes.GETFIELD, "cpw/mods/fml/client/GuiScrollingList",
                        "right", "I");
                loadArg(0);
                loadArg(1);
                loadArg(2);
                invokeStatic(
                        Type.getType("XRayHookClient"),
                        Method.getMethod("boolean xrayBlockListGui_GuiScrollingList_drawScreen(int, int, int, int, int, int, float)", true)
                );
                ifZCmp(GeneratorAdapter.EQ, label);
            }

            @Override
            public void visitInsn(int opcode) {
                if (step == 2 && opcode == Opcodes.ICONST_2) {
                    opcode = Opcodes.ICONST_1;
                } else if (opcode == Opcodes.FCONST_0) {
                    ++hookIsReady_IFLT_initialMouseClickY_0F;
                }
                super.visitInsn(opcode);
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                if (step == 2 && opcode == Opcodes.GOTO && originalIfLabel != null &&
                        hookNewLabel != null && originalIfLabel.equals(label)
                ) {
                    super.visitJumpInsn(opcode, hookNewLabel);
                    originalIfLabel = null;
                    hookNewLabel = null;
                    step = 3;
                    return;
                }
                super.visitJumpInsn(opcode, label);
                switch (opcode) {
                    case Opcodes.IFEQ:
                        if (step == 0 && originalIfLabel == null) {
                            originalIfLabel = label;
                        }
                        break;
                    case Opcodes.GOTO:
                        if (step <= 1 && originalIfLabel != null) {
                            originalGotoLabel = label;
                            step = 1;
                        }
                        break;
                    case Opcodes.IFLT:
                        if (hookIsReady_IFLT_initialMouseClickY_0F == 1) {
                            ++hookIsReady_IFLT_initialMouseClickY_0F;
                            xrayBlockListGui_GuiScrollingList_drawScreen(label);
                        }
                }
            }

            private int currentLine = -2;

            @Override
            public void visitLineNumber(int line, Label start) {
                if (step >= 2) {
                    currentLine = line + 1;
                } else {
                    currentLine = line;
                }
                super.visitLineNumber(currentLine, start);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (step == 1 && !originalGotoLabel.equals(hookNewLabel) && opcode == Opcodes.INVOKESTATIC &&
                        "org/lwjgl/input/Mouse".equals(owner) && "next".equals(name) && "()Z".equals(desc) && !itf
                ) {
                    xrayBlockListGui_GuiScrollingList_drawScreen(originalGotoLabel);
                    mark(hookNewLabel);
                    super.visitLineNumber(currentLine + 1, hookNewLabel);
                    originalGotoLabel = null;
                    step = 2;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }
}
