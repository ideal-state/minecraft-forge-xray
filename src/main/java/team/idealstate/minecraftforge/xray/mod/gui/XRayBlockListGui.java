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

package team.idealstate.minecraftforge.xray.mod.gui;

import cpw.mods.fml.client.GuiScrollingList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutablePair;
import team.idealstate.minecraftforge.xray.common.gui.AbstractGuiScreen;

import java.lang.reflect.Field;
import java.util.*;

/**
 * <p>XRayBlockListGui</p>
 *
 * <p>Created on 2024/1/18 16:46</p>
 *
 * @author ketikai
 * @since 1.0.0
 */
public class XRayBlockListGui extends GuiScrollingList {

    private final AbstractGuiScreen parent;
    private final String title;
    private final String subTitle;
    private final String unknown = I18n.format("unknown");
    private final List<String> blockIdentifiers;
    private final Set<String> blockIdentifiersSet;
    private final List<Map.Entry<String, String>> blocksView;
    private final Field scrollDistanceField;

    private int selectedIndex = -1;
    private final Deque<ElementListener> elementListeners = new ArrayDeque<>();
    private final Deque<ElementFilter> includeElementFilters = new ArrayDeque<>();
    private final Deque<ElementFilter> excludeElementFilters = new ArrayDeque<>();

    private final int width;
    private final int height;
    private final int top;
    private final int bottom;
    private final int left;
    private final int right;

    public XRayBlockListGui(AbstractGuiScreen parent, String title, String subTitle, Collection<String> blockIdentifiers, int width, int left) {
        super(parent.mc, width, parent.height - 32 - 32 - 16, 32, parent.height - 32 - 16, left, 20);
        this.parent = parent;
        this.title = title;
        this.subTitle = subTitle;

        this.width = width;
        this.height = parent.height - 32 - 32 - 16;
        this.top = 32;
        this.bottom = this.top + this.height;
        this.left = left;
        this.right = this.left + this.width;

        try {
            this.scrollDistanceField = GuiScrollingList.class.getDeclaredField("scrollDistance");
            scrollDistanceField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        this.blockIdentifiers = new ArrayList<>(blockIdentifiers.size() + 1);
        this.blockIdentifiers.add(title);
        this.blockIdentifiers.addAll(getElements(blockIdentifiers));
        this.blockIdentifiersSet = new HashSet<>(this.blockIdentifiers);
        this.blocksView = new LinkedList<>();
    }

    public boolean containsElement(String blockIdentifier) {
        if (title.equals(blockIdentifier) || subTitle.equals(blockIdentifier)) {
            return true;
        }
        return blockIdentifiersSet.contains(blockIdentifier);
    }

    public boolean addElement(String blockIdentifier) {
        if (title.equals(blockIdentifier) || subTitle.equals(blockIdentifier)) {
            return false;
        }
        if (blockIdentifiersSet.add(blockIdentifier)) {
            blockIdentifiers.add(blockIdentifier);
            return true;
        }
        return false;
    }

    public boolean removeElement(int index) {
        if (isOperable(index)) {
            String removed = blockIdentifiers.remove(index);
            if (removed != null) {
                blockIdentifiersSet.remove(removed);
            }
            return true;
        }
        return false;
    }

    public List<String> getElements() {
        return getElements(blockIdentifiers);
    }

    private List<String> getElements(Collection<String> elements) {
        List<String> ret = new ArrayList<>(elements.size());
        for (String element : elements) {
            if (title.equals(element) || subTitle.equals(element)) {
                continue;
            }
            ret.add(element);
        }
        return ret;
    }

    public boolean isOperable(int index) {
        return index > 0 && index < blockIdentifiers.size();
    }

    public String getElement(int index) {
        if (isOperable(index)) {
            return blockIdentifiers.get(index);
        }
        return null;
    }

    public boolean isOperableView(int index) {
        return index > 0 && index < blocksView.size();
    }

    public String getElementView(int index) {
        if (isOperableView(index)) {
            return blocksView.get(index).getKey();
        }
        return null;
    }

    public void addElementListener(ElementListener elementListener) {
        this.elementListeners.add(elementListener);
    }

    public void addIncludeElementFilters(ElementFilter elementFilter) {
        this.includeElementFilters.add(elementFilter);
    }

    public void addExcludeElementFilters(ElementFilter elementFilter) {
        this.excludeElementFilters.add(elementFilter);
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        this.selectedIndex = index;
        if (isSelected(selectedIndex)) {
            elementListeners.forEach(elementListener -> elementListener.onClick(this, index, doubleClick));
        }
    }

    @Override
    protected boolean isSelected(int index) {
        return isOperable(index) && index == selectedIndex;
    }

    @Override
    protected void drawBackground() {

    }

    public void slideToBottom() {
        try {
            scrollDistanceField.set(this, this.getContentHeight() - (this.bottom - this.top - 4));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerScrollButtons(List p_22240_1_, int p_22240_2_, int p_22240_3_) {

    }

    private static Map<String, String> nameCaches;

    private void updateBlocksView() {
        blocksView.clear();
        boolean first = true;
        for (String blockIdentifier : blockIdentifiers) {
            if (first) {
                blocksView.add(ImmutablePair.of(I18n.format(getSubTitle()), I18n.format(getTitle())));
                first = false;
                continue;
            }
            Block block = (Block) Block.blockRegistry.getObject(blockIdentifier);
            String blockName = null;
            if (block == null) {
                blockName = unknown;
            } else {
                if (block.getMaterial() == Material.air) {
                    continue;
                }
                if (nameCaches == null) {
                    nameCaches = new HashMap<>(blockIdentifiers.size());
                } else {
                    blockName = nameCaches.get(blockIdentifier);
                }
                if (blockName == null) {
                    Item item = Item.getItemFromBlock(block);
                    if (item == null) {
                        blockName = unknown;
                    } else {
                        blockName = new ItemStack(item).getDisplayName();
                    }
                    nameCaches.put(blockIdentifier, blockName);
                }
            }
            boolean visiable = includeElementFilters.isEmpty();
            for (ElementFilter elementFilter : includeElementFilters) {
                if (elementFilter.filter(blockIdentifier, blockName)) {
                    visiable = true;
                    break;
                }
            }
            if (visiable) {
                for (ElementFilter elementFilter : excludeElementFilters) {
                    if (elementFilter.filter(blockIdentifier, blockName)) {
                        visiable = false;
                        break;
                    }
                }
                if (visiable) {
                    blocksView.add(ImmutablePair.of(blockIdentifier, blockName));
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updateBlocksView();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected int getSize() {
        return blocksView.size();
    }

    @Override
    protected void drawSlot(int index, int var2, int var3, int var4, Tessellator var5) {
        if (index < 0 || index >= blocksView.size()) {
            return;
        }
        AbstractGuiScreen currentParent = getParent();
        if (currentParent == null) {
            return;
        }
        FontRenderer fontRenderer = currentParent.getFontRenderer();
        if (fontRenderer == null) {
            return;
        }

        Map.Entry<String, String> entry = blocksView.get(index);
        fontRenderer.drawString(fontRenderer.trimStringToWidth(entry.getValue(), listWidth - 10), this.left + 3 , var3, 0xFFFFFF);
        fontRenderer.drawString(fontRenderer.trimStringToWidth(entry.getKey(), listWidth - 10), this.left + 3 , var3 + 8, 0xCCCCCC);
    }

    public AbstractGuiScreen getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    @FunctionalInterface
    public interface ElementListener {

        void onClick(XRayBlockListGui self, int index, boolean doubleClick);
    }

    @FunctionalInterface
    public interface ElementFilter {

        boolean filter(String blockIdentifier, String blockName);
    }
}
