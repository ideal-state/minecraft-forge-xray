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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import team.idealstate.minecraftforge.xray.mod.config.XRayOptions;
import team.idealstate.minecraftforge.xray.mod.keybinding.EnableXRay;

/**
 * <p>XRayHookClient</p>
 *
 * <p>Created on 2024/1/16 18:09</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@SuppressWarnings({"all"})
public abstract class XRayHookClient {

    public static Block xrayBlockRender_WorldRender_updateRenderer(int x, int y, int z, RenderBlocks renderBlocks, Block block) {
        if (EnableXRay.getInstance().isEnabled()){
            if (XRayOptions.getInstance().isExcludeBlock(block.delegate.name())) {
                EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
                int distance = XRayOptions.getInstance().getDistance();
                if (Math.abs(player.posX - x) <= distance &&
                        Math.abs(player.posY - y) <= distance &&
                        Math.abs(player.posZ - z) <= distance) {
                    renderBlocks.setRenderAllFaces(true);
                    return block;
                }
            }
            return Block.getBlockById(0);
        }
        return block;
    }

    public static boolean xrayBlockRender_Block_shouldSideBeRendered(boolean original, IBlockAccess worldIn, int x, int y, int z, int side) {
        if (EnableXRay.getInstance().isEnabled()) {
            return true;
        }
        return original;
    }

    public static boolean xrayNightVision_EntityRenderer_updateLightmap(boolean original) {
        if (EnableXRay.getInstance().isEnabled()) {
            return true;
        }
        return original;
    }

    public static boolean xrayNightVision_EntityRenderer_getNightVisionBrightness() {
        return EnableXRay.getInstance().isEnabled();
    }

    public static boolean xrayNightVision_EntityRenderer_updateFogColor(boolean original) {
        if (EnableXRay.getInstance().isEnabled()) {
            return true;
        }
        return original;
    }

    public static boolean xrayBlockListGui_GuiScrollingList_drawScreen(
            int top, int bottom,
            int left, int right,
            int mouseX, int mouseY,
            float partialTicks
    ) {
        return left < mouseX && mouseX < right && top < mouseY && mouseY < bottom;
    }
}
