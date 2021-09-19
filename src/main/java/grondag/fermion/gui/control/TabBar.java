/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.fermion.gui.control;

import java.util.Collection;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.gui.ScreenTheme;
import grondag.fermion.varia.Useful;

@Environment(EnvType.CLIENT)
public abstract class TabBar<T> extends AbstractControl<TabBar<T>> {
	public static final int NO_SELECTION = -1;

	private int tabCount;
	private int itemsPerTab;
	private int columnsPerRow = 5;
	private int rowsPerTab;
	private int selectedItemIndex = NO_SELECTION;
	private int selectedTabIndex = 0;

	protected int itemSize = ScreenTheme.current().itemSize;
	protected int itemSpacing = ScreenTheme.current().itemSpacing;
	protected int itemSelectionMargin = ScreenTheme.current().itemSelectionMargin;

	protected int itemSlotSpacing = ScreenTheme.current().itemSlotSpacing;
	protected int itemRowHeightWithCaption = ScreenTheme.current().itemRowHeightWithCaption;


	private boolean allowSelection = true;

	protected float tabSize;
	protected float scrollHeight;
	protected float scrollBottom;

	private boolean focusOnSelection = false;

	protected List<T> items;
	/** used to detect changes to list */
	private int lastListSize = -1;

	protected enum MouseLocation {
		NONE, TOP_ARROW, BOTTOM_ARROW, TAB, ITEM
	}

	protected MouseLocation currentMouseLocation;
	protected int currentMouseIndex;

	public TabBar(ScreenRenderContext renderContext, List<T> items) {
		super(renderContext);
		this.items = items;
	}

	public List<T> getList() {
		return items;
	}

	public void setList(List<T> items) {
		this.items = items;
		isDirty = true;
	}

	/**
	 * Use if the list is externally modified.
	 */
	public void setDirty() {
		isDirty = true;
	}

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (items == null) {
			return;
		}

		this.handleListSizeUpdateIfNeeded();

		updateMouseLocation(mouseX, mouseY);

		int column = 0;

		final int itemHighlightIndex = this.currentMouseLocation == MouseLocation.ITEM ? this.currentMouseIndex : NO_SELECTION;

		// FIX: remove or repair
//		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();

		// really doesn't look good
		//GuiUtil.drawGradientRect(left, top, right - theme.tabWidth - theme.internalMargin, top + height, theme.itemSlotGradientTop, theme.itemSlotGradientBottom);

		this.drawHighlightIfNeeded(matrixStack, itemHighlightIndex, true);

		if (this.selectedItemIndex != itemHighlightIndex) {
			this.drawHighlightIfNeeded(matrixStack, this.selectedItemIndex, false);
		}

		final float halfTabWidth  = theme.tabWidth * 0.5f;

		final Matrix4f matrix = matrixStack.last().pose();

		// skip drawing tabs if there is only one
		if (this.tabCount > 1) {
			// if tabs are too small, just do a continuous bar
			float tabCenter = top + theme.tabWidth + itemSpacing;

			if (this.tabSize == 0.0) {

				GuiUtil.drawRect(matrix, right - theme.tabWidth, tabCenter, right, tabCenter + this.scrollHeight, theme.buttonColorInactive);

				// box pixelWidth is same as tab height, so need to have it be half that extra
				// to the right so that we keep our margins with the arrows
				final float selectionCenter = tabCenter + halfTabWidth
						+ (this.scrollHeight - theme.tabWidth) * this.selectedTabIndex / (this.tabCount - 1);

				GuiUtil.drawRect(matrix, right - theme.tabWidth, selectionCenter - halfTabWidth, right, selectionCenter + halfTabWidth,
						theme.buttonColorActive);

			} else {
				final int tabHighlightIndex = this.currentMouseLocation == MouseLocation.TAB ? this.currentMouseIndex : NO_SELECTION;

				for (int i = 0; i < this.tabCount; i++) {
					GuiUtil.drawRect(matrix, right - theme.tabWidth, tabCenter, right, tabCenter + this.tabSize,
							i == tabHighlightIndex ? theme.buttonColorFocus : i == this.selectedTabIndex ? theme.buttonColorActive : theme.buttonColorInactive);
					tabCenter += (this.tabSize + theme.tabMargin);
				}
			}

			final float arrowCenterX = right - halfTabWidth;

			GuiUtil.drawQuad(matrix, arrowCenterX, top, right - theme.tabWidth, top + theme.tabWidth, right, top + theme.tabWidth, arrowCenterX,
					top, this.currentMouseLocation == MouseLocation.TOP_ARROW ? theme.buttonColorFocus : theme.buttonColorInactive);

			GuiUtil.drawQuad(matrix, arrowCenterX, scrollBottom + theme.tabWidth, right, scrollBottom, right - theme.tabWidth, scrollBottom,
					arrowCenterX, scrollBottom + theme.tabWidth, this.currentMouseLocation == MouseLocation.BOTTOM_ARROW ? theme.buttonColorFocus : theme.buttonColorInactive);
		}

		final int start = this.getFirstDisplayedIndex();
		final int end = this.getLastDisplayedIndex();

		if(start == NO_SELECTION || end == NO_SELECTION) {
			return;
		}

		setupItemRendering();
		double itemX = left;
		double itemY = top;

		for (int i = start; i < end; i++) {
			this.drawItem(matrixStack, this.get(i), renderContext.minecraft(), renderContext.renderItem(), itemX, itemY, partialTicks, i == itemHighlightIndex);

			if (++column == this.columnsPerRow) {
				column = 0;
				itemY += itemRowHeightWithCaption;
				itemX = left;
			} else {
				itemX += itemSlotSpacing;
			}
		}

		tearDownItemRendering();
	}

	@Override
	public final void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.items == null) {
			return;
		}

		this.handleListSizeUpdateIfNeeded();

		this.updateMouseLocation(mouseX, mouseY);

		if (this.currentMouseLocation == MouseLocation.ITEM) {
			final T item = this.get(this.currentMouseIndex);

			if (item != null) {
				this.drawItemToolTip(matrixStack, item, renderContext, mouseX, mouseY, partialTicks);
			}
		}
	}

	protected abstract void drawItemToolTip(PoseStack matrixStack, T item, ScreenRenderContext renderContext, int mouseX, int mouseY, float partialTicks);

	/**
	 *
	 * @param index
	 */
	private void drawHighlightIfNeeded(PoseStack matrixStack, int index, boolean isHighlight) {
		if (index == NO_SELECTION) {
			return;
		}

		final int start = this.getFirstDisplayedIndex();
		final int end = this.getLastDisplayedIndex();

		if (index < start || index >= end) {
			return;
		}

		final int idx = index - start;
		final int x = (int) (left + (idx % this.columnsPerRow) * itemSlotSpacing);
		final int y = (int) (top + (idx / this.columnsPerRow) * itemRowHeightWithCaption);

		this.drawHighlight(matrixStack, index, x, y, isHighlight);
	}

	/**
	 * Coordinates given are top left of item area - does not account for margin
	 * offset. If isHighlight = true, mouse is over item. If false, item is
	 * selected.
	 */
	protected void drawHighlight(PoseStack matrixStack, int index, float x, float y, boolean isHighlight) {
		GuiUtil.drawBoxRightBottom(matrixStack.last().pose(), x - itemSelectionMargin, y - itemSelectionMargin, x + itemSize + itemSelectionMargin,
				y + itemSize + itemSelectionMargin, 1, isHighlight ? theme.buttonColorFocus : theme.buttonColorActive);
	}

	/** set (non-matrix) GL state needed for proper rending of this tab's items */
	protected abstract void setupItemRendering();

	protected abstract void tearDownItemRendering();

	protected abstract void drawItem(PoseStack matrixStack, T item, Minecraft mc, ItemRenderer itemRender, double left, double top, float partialTicks, boolean isHighlighted);

	private void updateMouseLocation(double mouseX, double mouseY) {
		if (items == null) {
			return;
		}

		if (mouseX < left || mouseX > right || mouseY < top || mouseY > bottom) {
			this.currentMouseLocation = MouseLocation.NONE;
		} else if (mouseX >= right - theme.tabWidth) {
			if (mouseY <= top + theme.tabWidth + itemSpacing / 2.0) {
				this.currentMouseLocation = MouseLocation.TOP_ARROW;
			} else if (mouseY < scrollBottom - itemSpacing / 2.0) {
				this.currentMouseLocation = MouseLocation.TAB;
				this.currentMouseIndex = Mth
						.clamp((int) ((mouseY - top - theme.tabWidth - itemSpacing / 2) / (this.scrollHeight) * this.tabCount), 0, this.tabCount - 1);
			} else if (mouseY > scrollBottom + theme.tabWidth + itemSpacing / 2.0) {
				this.currentMouseLocation = MouseLocation.NONE;
			} else {
				this.currentMouseLocation = MouseLocation.BOTTOM_ARROW;
			}
		} else {
			this.currentMouseLocation = MouseLocation.ITEM;

			final int newIndex = this.getFirstDisplayedIndex()
					+ (int) ((mouseY - top - itemSpacing / 2) / itemRowHeightWithCaption) * this.columnsPerRow
					+ Math.min((int) ((mouseX - left - itemSpacing / 2) / itemSlotSpacing), this.columnsPerRow - 1);

			this.currentMouseIndex = (newIndex >= 0 && newIndex < this.items.size()) ? newIndex : NO_SELECTION;
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		if (this.items != null) {
			rowsPerTab = (int) ((height + itemSpacing) / itemRowHeightWithCaption);
			scrollHeight = rowsPerTab * itemRowHeightWithCaption - itemSpacing - (theme.tabWidth + itemSpacing) * 2;
			scrollBottom = top + theme.tabWidth + itemSpacing * 2 + scrollHeight;
			itemsPerTab = columnsPerRow * rowsPerTab;

			handleListSizeUpdateIfNeeded();
		}

		if (focusOnSelection && selectedItemIndex != NO_SELECTION) {
			if (itemsPerTab > 0) {
				selectedTabIndex = selectedItemIndex / itemsPerTab;
			}
			focusOnSelection = false;
		}
	}

	/**
	 * Does NOT check for null list. Called expected to do so.
	 */
	protected void handleListSizeUpdateIfNeeded() {
		if (items.size() != lastListSize) {
			tabCount = itemsPerTab > 0 ? (items.size() + itemsPerTab - 1) / itemsPerTab : 0;
			tabSize = tabCount <= 0 ? 0 : (scrollHeight - (theme.tabMargin * (tabCount - 1))) / tabCount;

			if(selectedTabIndex >= tabCount) {
				selectedTabIndex = Math.max(0, tabCount - 1);
			}

			if (tabSize < theme.tabMargin * 2) {
				tabSize = 0;
			}

			lastListSize = items.size();
		}
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (items == null) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);

		switch (currentMouseLocation) {
		case ITEM:
			if (currentMouseIndex >= 0) {
				setSelectedIndex(currentMouseIndex);
			}
			break;

		case TOP_ARROW:
			if (selectedTabIndex > 0) {
				selectedTabIndex--;
			}
			GuiUtil.playPressedSound();
			break;

		case BOTTOM_ARROW:
			if (selectedTabIndex < tabCount - 1) {
				selectedTabIndex++;
			}
			GuiUtil.playPressedSound();
			break;

		case TAB:
			selectedTabIndex = currentMouseIndex;
			break;

		case NONE:
		default:
			break;

		}
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		if (items == null) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);
		switch (currentMouseLocation) {
		case ITEM:
			if (currentMouseIndex >= 0) {
				setSelectedIndex(currentMouseIndex);
			}
			break;

		case TOP_ARROW:
			break;

		case BOTTOM_ARROW:
			break;

		case TAB:
			selectedTabIndex = currentMouseIndex;
			break;

		case NONE:
		default:
			break;
		}
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		if (items == null) {
			return;
		}

		selectedTabIndex = Mth.clamp(selectedTabIndex + mouseIncrementDelta(), 0, tabCount - 1);
	}

	public void add(T item) {
		if (items == null) {
			return;
		}

		items.add(item);
		isDirty = true;
	}

	public void addAll(Collection<T> items) {
		if (items == null) {
			return;
		}

		items.addAll(items);
		isDirty = true;
	}

	public void addAll(T[] itemsIn) {
		if (items == null) {
			return;
		}

		for (final T item : itemsIn) {
			items.add(item);
		}
		isDirty = true;
	}

	public T get(int index) {
		if (items == null || index == NO_SELECTION) {
			return null;
		}

		return items.get(index);
	}

	public T getSelected() {
		if (items == null || selectedItemIndex == NO_SELECTION) {
			return null;
		}

		return get(getSelectedIndex());
	}

	public List<T> getDisplayed() {
		if (items == null) {
			return null;
		}

		return items.subList(getFirstDisplayedIndex(), getLastDisplayedIndex());
	}

	public void clear() {
		if (items == null) {
			return;
		}
		items.clear();
		isDirty = true;
	}

	public void setItemSize(int itemSize) {
		this.itemSize = itemSize;
		computeSpacing();
	}

	public void setItemSpacing(int itemSpacing) {
		this.itemSpacing = itemSpacing;
		computeSpacing();
	}

	protected void computeSpacing() {
		itemSlotSpacing = itemSize + itemSpacing;
		itemRowHeightWithCaption = itemSize + itemSpacing;
	}

	public void setItemsPerRow(int itemsPerRow) {
		columnsPerRow = Math.max(1, itemsPerRow);
		isDirty = true;
	}

	/**
	 * Automatic layout version
	 */
	public void setItemsPerRow() {
		setItemsPerRow((int) (width - theme.tabWidth) / itemSlotSpacing);
	}

	public int getItemsPerTab() {
		if (items == null) {
			return 0;
		}
		refreshContentCoordinatesIfNeeded();
		return itemsPerTab;
	}

	public int size() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	public void setSelectedIndex(int index) {
		if (items == null || !allowSelection) {
			return;
		}
		selectedItemIndex = Mth.clamp(index, NO_SELECTION, items.size() - 1);
		showSelected();
	}

	public void setSelected(T selectedItem) {
		if (items == null || selectedItem == null || !allowSelection) {
			setSelectedIndex(NO_SELECTION);
		} else {
			final int i = items.indexOf(selectedItem);
			if (i >= NO_SELECTION) {
				setSelectedIndex(i);
			}
		}
	}

	public int getSelectedIndex() {
		if (items == null) {
			return NO_SELECTION;
		}
		return selectedItemIndex;
	}

	/** index of start item on selected tab */
	public int getFirstDisplayedIndex() {
		if (items == null || items.isEmpty()) {
			return NO_SELECTION;
		}

		refreshContentCoordinatesIfNeeded();
		return selectedTabIndex * itemsPerTab;
	}

	/** index of start item on selected tab, EXCLUSIVE of the last item */
	public int getLastDisplayedIndex() {
		if (items == null || items.isEmpty()) {
			return NO_SELECTION;
		}

		refreshContentCoordinatesIfNeeded();
		return Useful.min((selectedTabIndex + 1) * itemsPerTab, items.size());
	}

	/**
	 * If the currently selected item is on the current tab, is the 0-based position
	 * within the tab. Returns NO_SELECTION if the currently selected item is not on
	 * the current tab or if no selection.
	 */
	public int getHighlightIndex() {
		if (items == null || selectedItemIndex == NO_SELECTION) {
			return NO_SELECTION;
		}
		refreshContentCoordinatesIfNeeded();
		final int result = selectedItemIndex - getFirstDisplayedIndex();
		return (result < 0 || result >= getItemsPerTab()) ? NO_SELECTION : result;
	}

	/** moves the tab selection to show the currently selected item */
	public void showSelected() {
		// can't implement here because layout may not be set when called - defer until
		// next refresh
		focusOnSelection = true;
		isDirty = true;
	}

	/**
	 * If false, user can click on items but not select them. Selection index is
	 * always {@link #NO_SELECTION} and no selection highlight is drawn.
	 */
	public boolean isSelectionEnabled() {
		return allowSelection;
	}

	/** see {@link #isSelectionEnabled()} */
	public TabBar<T> setSelectionEnabled(boolean allowSelection) {
		this.allowSelection = allowSelection;
		return this;
	}
}
