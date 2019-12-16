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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.varia.Useful;

@Environment(EnvType.CLIENT)
public abstract class TabBar<T> extends AbstractControl<TabBar<T>> {
	public static final int NO_SELECTION = -1;

	private int tabCount;
	private int itemsPerTab;
	private int columnsPerRow = 5;
	private int rowsPerTab;
	private int selectedItemIndex = NO_SELECTION;
	private int selectedTabIndex;

	private boolean allowSelection = true;

	public static int DEFAULT_TAB_MARGIN = 2;
	public static int DEFAULT_TAB_WIDTH = 8;
	public static int DEFAULT_ITEM_SPACING = 4;
	public static int DEFAULT_CAPTION_HEIGHT = 0;
	private int tabMargin = DEFAULT_TAB_MARGIN;
	private int tabWidth = DEFAULT_TAB_WIDTH;
	private int itemSpacing = DEFAULT_ITEM_SPACING;
	private int itemSelectionMargin = 2;
	private int captionHeight = DEFAULT_CAPTION_HEIGHT;

	private float actualItemSize;
	/** {@link #actualItemSize()} rounded down */
	private int actualItemPixels;
	private float tabSize;
	private float scrollHeight;

	private boolean focusOnSelection = false;

	private List<T> items;
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
	protected void drawContent(int mouseX, int mouseY, float partialTicks) {
		if (items == null) {
			return;
		}

		this.handleListSizeUpdateIfNeeded();

		updateMouseLocation(mouseX, mouseY);

		int column = 0;

		final int itemHighlightIndex = this.currentMouseLocation == MouseLocation.ITEM ? this.currentMouseIndex : NO_SELECTION;

		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();

		this.drawHighlightIfNeeded(itemHighlightIndex, true);

		if (this.selectedItemIndex != itemHighlightIndex) {
			this.drawHighlightIfNeeded(this.selectedItemIndex, false);
		}

		final float halfTabWidth  = tabWidth * 0.5f;

		// skip drawing tabs if there is only one
		if (this.tabCount > 1) {
			// if tabs are too small, just do a continuous bar
			float tabCenter = top + this.tabWidth + this.itemSpacing;
			if (this.tabSize == 0.0) {

				GuiUtil.drawRect(right - this.tabWidth, tabCenter, right, tabCenter + this.scrollHeight, BUTTON_COLOR_INACTIVE);

				// box pixelWidth is same as tab height, so need to have it be half that extra
				// to the right so that we keep our margins with the arrows
				final float selectionCenter = tabCenter + halfTabWidth
						+ (this.scrollHeight - this.tabWidth) * this.selectedTabIndex / (this.tabCount - 1);

				GuiUtil.drawRect(right - this.tabWidth, selectionCenter - halfTabWidth, right, selectionCenter + halfTabWidth,
						BUTTON_COLOR_ACTIVE);

			} else {
				final int tabHighlightIndex = this.currentMouseLocation == MouseLocation.TAB ? this.currentMouseIndex : NO_SELECTION;

				for (int i = 0; i < this.tabCount; i++) {
					GuiUtil.drawRect(right - this.tabWidth, tabCenter, right, tabCenter + this.tabSize,
							i == tabHighlightIndex ? BUTTON_COLOR_FOCUS : i == this.selectedTabIndex ? BUTTON_COLOR_ACTIVE : BUTTON_COLOR_INACTIVE);
					tabCenter += (this.tabSize + this.tabMargin);
				}
			}

			final float arrowCenterX = right - halfTabWidth;

			GuiUtil.drawQuad(arrowCenterX, top, right - this.tabWidth, top + this.tabWidth, right, top + this.tabWidth, arrowCenterX,
					top, this.currentMouseLocation == MouseLocation.TOP_ARROW ? BUTTON_COLOR_FOCUS : BUTTON_COLOR_INACTIVE);

			GuiUtil.drawQuad(arrowCenterX, bottom, right, bottom - this.tabWidth, right - this.tabWidth, bottom - this.tabWidth,
					arrowCenterX, bottom, this.currentMouseLocation == MouseLocation.BOTTOM_ARROW ? BUTTON_COLOR_FOCUS : BUTTON_COLOR_INACTIVE);
		}

		setupItemRendering();
		final int start = this.getFirstDisplayedIndex();
		final int end = this.getLastDisplayedIndex();
		double itemX = left;
		double itemY = top;

		for (int i = start; i < end; i++) {
			this.drawItem(this.get(i), renderContext.minecraft(), renderContext.renderItem(), itemX, itemY, partialTicks, i == itemHighlightIndex);

			if (++column == this.columnsPerRow) {
				column = 0;
				itemY += (this.actualItemSize + this.itemSpacing + this.captionHeight);
				itemX = left;
			} else {
				itemX += (this.actualItemSize + this.itemSpacing);
			}
		}

		tearDownItemRendering();

	}

	@Override
	public final void drawToolTip(int mouseX, int mouseY, float partialTicks) {
		if (this.items == null) {
			return;
		}

		this.handleListSizeUpdateIfNeeded();

		this.updateMouseLocation(mouseX, mouseY);

		if (this.currentMouseLocation == MouseLocation.ITEM) {
			final T item = this.get(this.currentMouseIndex);
			if (item != null) {
				this.drawItemToolTip(item, renderContext, mouseX, mouseY, partialTicks);
			}
		}
	}

	protected abstract void drawItemToolTip(T item, ScreenRenderContext renderContext, int mouseX, int mouseY, float partialTicks);

	/**
	 *
	 * @param index
	 */
	private void drawHighlightIfNeeded(int index, boolean isHighlight) {
		if (index == NO_SELECTION) {
			return;
		}

		final int start = this.getFirstDisplayedIndex();
		final int end = this.getLastDisplayedIndex();

		if (index < start || index >= end) {
			return;
		}

		final int idx = index - start;
		final int x = (int) (left + (idx % this.columnsPerRow) * (this.actualItemSize + this.itemSpacing));
		final int y = (int) (top + (idx / this.columnsPerRow) * (this.actualItemSize + this.itemSpacing + this.captionHeight));

		this.drawHighlight(index, x, y, isHighlight);
	}

	/**
	 * Coordinates given are top left of item area - does not account for margin
	 * offset. If isHighlight = true, mouse is over item. If false, item is
	 * selected.
	 */
	protected void drawHighlight(int index, float x, float y, boolean isHighlight) {
		GuiUtil.drawBoxRightBottom(x - itemSelectionMargin, y - itemSelectionMargin, x + this.actualItemSize + itemSelectionMargin,
				y + this.actualItemSize + itemSelectionMargin, 1, isHighlight ? BUTTON_COLOR_FOCUS : BUTTON_COLOR_ACTIVE);
	}

	/** set (non-matrix) GL state needed for proper rending of this tab's items */
	protected abstract void setupItemRendering();

	protected abstract void tearDownItemRendering();

	protected abstract void drawItem(T item, MinecraftClient mc, ItemRenderer itemRender, double left, double top, float partialTicks, boolean isHighlighted);

	private void updateMouseLocation(double mouseX, double mouseY) {
		if (items == null) {
			return;
		}

		if (mouseX < left || mouseX > right || mouseY < top || mouseY > bottom) {
			this.currentMouseLocation = MouseLocation.NONE;
		} else if (mouseX >= right - this.tabWidth) {
			if (mouseY <= top + this.tabWidth + this.itemSpacing / 2.0) {
				this.currentMouseLocation = MouseLocation.TOP_ARROW;
			} else if (mouseY >= bottom - this.tabWidth - this.itemSpacing / 2.0) {
				this.currentMouseLocation = MouseLocation.BOTTOM_ARROW;
			} else {
				this.currentMouseLocation = MouseLocation.TAB;
				this.currentMouseIndex = MathHelper
						.clamp((int) ((mouseY - top - this.tabWidth - this.itemSpacing / 2) / (this.scrollHeight) * this.tabCount), 0, this.tabCount - 1);
				//                this.currentMouseIndex = (int) ((mouseX - this.left - this.tabWidth - this.actualItemMargin / 2) / (this.tabWidth + this.tabMargin));
			}
		} else {
			this.currentMouseLocation = MouseLocation.ITEM;

			final int newIndex = this.getFirstDisplayedIndex()
					+ (int) ((mouseY - top - this.itemSpacing / 2) / (this.actualItemSize + this.itemSpacing + this.captionHeight)) * this.columnsPerRow
					+ Math.min((int) ((mouseX - left - this.itemSpacing / 2) / (this.actualItemSize + this.itemSpacing)), this.columnsPerRow - 1);

			this.currentMouseIndex = (newIndex < this.items.size()) ? newIndex : NO_SELECTION;
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		if (this.items != null) {
			final float horizontalSpaceRemaining = width - this.tabWidth;
			this.actualItemSize = horizontalSpaceRemaining / this.columnsPerRow - this.itemSpacing;
			this.actualItemPixels = (int) actualItemSize;
			this.rowsPerTab = (int) ((height + this.itemSpacing) / (actualItemSize + this.itemSpacing + this.captionHeight));
			this.scrollHeight = height - (this.tabWidth + this.itemSpacing) * 2;
			this.itemsPerTab = columnsPerRow * rowsPerTab;
			this.handleListSizeUpdateIfNeeded();
		}

		if (this.focusOnSelection && this.selectedItemIndex != NO_SELECTION) {
			if (this.itemsPerTab > 0) {
				this.selectedTabIndex = this.selectedItemIndex / this.itemsPerTab;
			}
			this.focusOnSelection = false;
		}
	}

	/**
	 * Does NOT check for null list. Called expected to do so.
	 */
	protected void handleListSizeUpdateIfNeeded() {
		if (items.size() != this.lastListSize) {
			this.tabCount = this.itemsPerTab > 0 ? (this.items.size() + this.itemsPerTab - 1) / this.itemsPerTab : 0;
			this.tabSize = tabCount <= 0 ? 0 : (this.scrollHeight - (this.tabMargin * (this.tabCount - 1))) / tabCount;
			if (tabSize < this.tabMargin * 2) {
				tabSize = 0;
			}
			this.lastListSize = items.size();
		}
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (items == null) {
			return;
		}

		this.updateMouseLocation(mouseX, mouseY);
		switch (this.currentMouseLocation) {
		case ITEM:
			if (this.currentMouseIndex >= 0) {
				this.setSelectedIndex(this.currentMouseIndex);
			}
			break;

		case TOP_ARROW:
			if (this.selectedTabIndex > 0) {
				this.selectedTabIndex--;
			}
			GuiUtil.playPressedSound();
			break;

		case BOTTOM_ARROW:
			if (this.selectedTabIndex < this.tabCount - 1) {
				this.selectedTabIndex++;
			}
			GuiUtil.playPressedSound();
			break;

		case TAB:
			this.selectedTabIndex = this.currentMouseIndex;
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

		this.updateMouseLocation(mouseX, mouseY);
		switch (this.currentMouseLocation) {
		case ITEM:
			if (this.currentMouseIndex >= 0) {
				this.setSelectedIndex(this.currentMouseIndex);
			}
			break;

		case TOP_ARROW:
			break;

		case BOTTOM_ARROW:
			break;

		case TAB:
			this.selectedTabIndex = this.currentMouseIndex;
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

		this.selectedTabIndex = MathHelper.clamp(this.selectedTabIndex + mouseIncrementDelta(), 0, this.tabCount - 1);
	}

	public void add(T item) {
		if (items == null) {
			return;
		}

		this.items.add(item);
		isDirty = true;
	}

	public void addAll(Collection<T> items) {
		if (items == null) {
			return;
		}

		this.items.addAll(items);
		isDirty = true;
	}

	public void addAll(T[] itemsIn) {
		if (items == null) {
			return;
		}

		for (final T item : itemsIn) {
			this.items.add(item);
		}
		isDirty = true;
	}

	public T get(int index) {
		if (items == null || index == NO_SELECTION) {
			return null;
		}

		return this.items.get(index);
	}

	public T getSelected() {
		if (items == null || this.selectedItemIndex == NO_SELECTION) {
			return null;
		}

		return this.get(this.getSelectedIndex());
	}

	public List<T> getDisplayed() {
		if (items == null) {
			return null;
		}

		return this.items.subList(this.getFirstDisplayedIndex(), this.getLastDisplayedIndex());
	}

	public void clear() {
		if (items == null) {
			return;
		}
		this.items.clear();
		isDirty = true;
	}

	public void setItemsPerRow(int itemsPerRow) {
		this.columnsPerRow = Math.max(1, itemsPerRow);
		isDirty = true;
	}

	public int getItemsPerTab() {
		if (items == null) {
			return 0;
		}
		refreshContentCoordinatesIfNeeded();
		return this.itemsPerTab;
	}

	public int size() {
		if (items == null) {
			return 0;
		}
		return this.items.size();
	}

	public void setSelectedIndex(int index) {
		if (items == null || !this.allowSelection) {
			return;
		}
		this.selectedItemIndex = MathHelper.clamp(index, NO_SELECTION, this.items.size() - 1);
		this.showSelected();
	}

	public void setSelected(T selectedItem) {
		if (items == null || selectedItem == null || !this.allowSelection) {
			this.setSelectedIndex(NO_SELECTION);
		} else {
			final int i = this.items.indexOf(selectedItem);
			if (i >= NO_SELECTION) {
				this.setSelectedIndex(i);
			}
		}
	}

	public int getSelectedIndex() {
		if (items == null) {
			return NO_SELECTION;
		}
		return this.selectedItemIndex;
	}

	/** index of start item on selected tab */
	public int getFirstDisplayedIndex() {
		if (items == null) {
			return NO_SELECTION;
		}
		refreshContentCoordinatesIfNeeded();
		return this.selectedTabIndex * this.itemsPerTab;
	}

	/** index of start item on selected tab, EXCLUSIVE of the last item */
	public int getLastDisplayedIndex() {
		if (items == null) {
			return NO_SELECTION;
		}
		refreshContentCoordinatesIfNeeded();
		return Useful.min((this.selectedTabIndex + 1) * this.itemsPerTab, this.items.size());
	}

	/**
	 * If the currently selected item is on the current tab, is the 0-based position
	 * within the tab. Returns NO_SELECTION if the currently selected item is not on
	 * the current tab or if no selection.
	 */
	public int getHighlightIndex() {
		if (items == null || this.selectedItemIndex == NO_SELECTION) {
			return NO_SELECTION;
		}
		refreshContentCoordinatesIfNeeded();
		final int result = this.selectedItemIndex - this.getFirstDisplayedIndex();
		return (result < 0 || result >= this.getItemsPerTab()) ? NO_SELECTION : result;
	}

	/** moves the tab selection to show the currently selected item */
	public void showSelected() {
		// can't implement here because layout may not be set when called - defer until
		// next refresh
		this.focusOnSelection = true;
		isDirty = true;
	}

	protected double actualItemSize() {
		refreshContentCoordinatesIfNeeded();
		return this.actualItemSize;
	}

	/** {@link #actualItemSize()} rounded down to nearest integer */
	protected int actualItemPixels() {
		refreshContentCoordinatesIfNeeded();
		return this.actualItemPixels;
	}

	public int getTabMargin() {
		return tabMargin;
	}

	public TabBar<T> setTabMargin(int tabMargin) {
		this.tabMargin = tabMargin;
		this.setDirty();
		return this;
	}

	public int getTabWidth() {
		return tabWidth;
	}

	public TabBar<T> setTabWidth(int tabWidth) {
		this.tabWidth = tabWidth;
		this.setDirty();
		return this;
	}

	public int getItemSpacing() {
		return itemSpacing;
	}

	public TabBar<T> setItemSpacing(int itemSpacing) {
		this.itemSpacing = itemSpacing;
		this.setDirty();
		return this;
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

	/**
	 * Pixels distance from item used to draw item borders for hovered/selected
	 * items. Default is 2.
	 */
	public int getItemSelectionMargin() {
		return itemSelectionMargin;
	}

	/**
	 * See {@link #getItemSelectionMargin()}
	 */
	public TabBar<T> setItemSelectionMargin(int itemSelectionMargin) {
		this.itemSelectionMargin = Math.max(0, itemSelectionMargin);
		return this;
	}

	/**
	 * Pixels reserved under each item for labeling. Subclass should draw the label.
	 * Default is 0.
	 */
	public int getCaptionHeight() {
		return captionHeight;
	}

	/**
	 * See {@link #getCaptionHeight()}
	 */
	public TabBar<T> setCaptionHeight(int captionHeight) {
		this.captionHeight = captionHeight;
		return this;
	}
}
