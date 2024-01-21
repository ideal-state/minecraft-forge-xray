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

package team.idealstate.minecraftforge.xray.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;
import team.idealstate.minecraftforge.xray.core.patch.GuiScrollingListPatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * <p>XRayFMLPlugin</p>
 *
 * <p>Created on 2024/1/16 12:56</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
@SortingIndex(-18)
@Name("XRayFMLPlugin")
@TransformerExclusions({
        "team.idealstate.minecraftforge.xray.core"
})
@MCVersion("1.7.10")
public class XRayFMLPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                "team.idealstate.minecraftforge.xray.core.patch.GuiScrollingListPatcher",
                "team.idealstate.minecraftforge.xray.core.patch.BlockPatcher",
                "team.idealstate.minecraftforge.xray.core.patch.EntityRendererPatcher",
                "team.idealstate.minecraftforge.xray.core.patch.WorldRendererPatcher"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
