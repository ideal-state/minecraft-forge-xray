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

import net.minecraft.client.settings.KeyBinding;
import team.idealstate.minecraftforge.xray.common.Runnable;

/**
 * <p>RunnableKeyBinding</p>
 *
 * <p>Created on 2024/1/15 11:56</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public abstract class RunnableKeyBinding extends KeyBinding implements Runnable {

    public RunnableKeyBinding(String description, int keyCode, String category) {
        super("keybinding." + category + "." + description, keyCode, "category." + category);
    }

    @Override
    public void run() {
        if (isPressed()) {
            doRun();
        }
    }

    protected abstract void doRun();
}
