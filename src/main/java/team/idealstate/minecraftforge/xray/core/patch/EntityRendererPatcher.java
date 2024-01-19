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
 * <p>EntityRendererPatcher</p>
 *
 * <p>Created on 2024/1/15 17:06</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public class EntityRendererPatcher implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        byte[] ret = basicClass;
        if ("net.minecraft.client.renderer.EntityRenderer".equals(transformedName)) {
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
                // net.minecraft.client.renderer.EntityRenderer#updateLightmap(float)void
                if ("i".equals(name) && "(F)V".equals(desc)) {
                    return new UpdateLightmapMethodPatcher(api, methodVisitor, access, name, desc);
                }
                // net.minecraft.client.renderer.EntityRenderer#getNightVisionBrightness(net.minecraft.entity.player.EntityPlayer, float)float
                else if ("a".equals(name) && "(Lyz;F)F".equals(desc)) {
                    return new GetNightVisionBrightnessMethodPatcher(api, methodVisitor, access, name, desc);
                }
                // net.minecraft.client.renderer.EntityRenderer#updateFogColor(float)void
                else if ("j".equals(name) && "(F)V".equals(desc)) {
                    return new UpdateFogColorMethodPatcher(api, methodVisitor, access, name, desc);
                }
            }
            return methodVisitor;
        }
        
        private static class UpdateLightmapMethodPatcher extends AdviceAdapter {

            private UpdateLightmapMethodPatcher(int api, MethodVisitor mv, int access, String name, String desc) {
                super(api, mv, access, name, desc);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == Opcodes.INVOKEVIRTUAL && "bjk".equals(owner) && "a".equals(name) && "(Lrv;)Z".equals(desc) && !itf) {
                    invokeStatic(
                            Type.getType("XRayHookClient"),
                            Method.getMethod("boolean xrayNightVision_EntityRenderer_updateLightmap(boolean)", true)
                    );
                }
            }
        }

        private static class GetNightVisionBrightnessMethodPatcher extends AdviceAdapter {

            private int count = 0;

            private GetNightVisionBrightnessMethodPatcher(int api, MethodVisitor mv, int access, String name, String desc) {
                super(api, mv, access, name, desc);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                if (opcode == Opcodes.ALOAD && var == 1 && ++count == 1) {
                    invokeStatic(
                            Type.getType("XRayHookClient"),
                            Method.getMethod("boolean xrayNightVision_EntityRenderer_getNightVisionBrightness()", true)
                    );
                    Label originalLabel = newLabel();
                    ifZCmp(GeneratorAdapter.EQ, originalLabel);
                    visitInsn(Opcodes.FCONST_1);
                    returnValue();
                    mark(originalLabel);
                }
                super.visitVarInsn(opcode, var);
            }
        }

        private static class UpdateFogColorMethodPatcher extends AdviceAdapter {

            private int count = 0;

            private UpdateFogColorMethodPatcher(int api, MethodVisitor mv, int access, String name, String desc) {
                super(api, mv, access, name, desc);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (opcode == Opcodes.INVOKEVIRTUAL && "sv".equals(owner) && "a".equals(name) && "(Lrv;)Z".equals(desc) && !itf && ++count == 2) {
                    invokeStatic(
                            Type.getType("XRayHookClient"),
                            Method.getMethod("boolean xrayNightVision_EntityRenderer_updateFogColor(boolean)", true)
                    );
                }
            }
        }
    }
}
