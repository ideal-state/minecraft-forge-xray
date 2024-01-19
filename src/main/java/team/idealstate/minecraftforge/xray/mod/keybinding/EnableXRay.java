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

package team.idealstate.minecraftforge.xray.mod.keybinding;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>EnableXRay</p>
 *
 * <p>Created on 2024/1/15 11:55</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public final class EnableXRay extends RunnableKeyBinding {

    private static volatile EnableXRay instance;

    public static EnableXRay getInstance() {
        if (instance == null) {
            synchronized (EnableXRay.class) {
                if (instance == null) {
                    instance = new EnableXRay();
                }
            }
        }
        return instance;
    }

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    private EnableXRay() {
        super("enable", Keyboard.KEY_X, "xray");
    }

    @Override
    protected void doRun() {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.renderGlobal.loadRenderers();
        EntityClientPlayerMP player = minecraft.thePlayer;
        String tip;
        if (enabled.get()) {
            enabled.set(false);
            tip = "tip.xray.disabled";
        } else {
            enabled.set(true);
            tip = "tip.xray.enabled";
        }
        player.addChatComponentMessage(new ChatComponentTranslation(tip));
    }

    public boolean isEnabled() {
        return enabled.get();
    }
}
