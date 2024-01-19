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

package team.idealstate.minecraftforge.xray.mod.handler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import team.idealstate.minecraftforge.xray.common.Runnable;
import team.idealstate.minecraftforge.xray.mod.keybinding.ConfigureXRay;
import team.idealstate.minecraftforge.xray.mod.keybinding.EnableXRay;
import team.idealstate.minecraftforge.xray.mod.keybinding.RunnableKeyBinding;

import java.util.Arrays;
import java.util.List;

/**
 * <p>KeyBindingsHandler</p>
 *
 * <p>Created on 2024/1/15 11:21</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public class KeyBindingsHandler {

    private static final KeyBindingsHandler INSTANCE = new KeyBindingsHandler();
    
    private static final List<RunnableKeyBinding> KEY_BINDINGS = Arrays.asList(
            ConfigureXRay.getInstance(),
            EnableXRay.getInstance()
    );
    
    public static void init() {
        KEY_BINDINGS.forEach(ClientRegistry::registerKeyBinding);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    private KeyBindingsHandler() {}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if ((!FMLClientHandler.instance().isGUIOpen(GuiChat.class)) && (minecraft.currentScreen == null) && (minecraft.theWorld != null)) {
            KEY_BINDINGS.forEach(Runnable::run);
        }
    }
}
