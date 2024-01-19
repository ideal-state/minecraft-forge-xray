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

package team.idealstate.minecraftforge.xray.common.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

/**
 * <p>AbstractGuiScreen</p>
 *
 * <p>Created on 2024/1/18 11:59</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public abstract class AbstractGuiScreen extends GuiScreen {

    protected final int margin = 16;
    private final GuiScreen parent;
    private volatile String title;

    public AbstractGuiScreen(GuiScreen parent, String title) {
        this.mc = Minecraft.getMinecraft();
        this.parent = parent;
        this.title = title;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawTitle();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        GuiScreen currentParentGuiScreen = getParent();
        if (currentParentGuiScreen == null) {
            super.keyTyped(typedChar, keyCode);
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(currentParentGuiScreen);
        }
    }

    public void drawTitle() {
        String currentTitle = getTitle();
        if (currentTitle == null) {
            return;
        }
        drawCenteredString(fontRendererObj, I18n.format(currentTitle), width / 2, margin, 0xFFFFFF);
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

    public GuiScreen getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
