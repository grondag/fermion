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

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.Layout;
import grondag.fermion.gui.ScreenRenderContext;

@Environment(EnvType.CLIENT)
public abstract class AbstractControl<T extends AbstractControl<T>> extends DrawableHelper implements Element {
	public static final int BUTTON_COLOR_ACTIVE = 0xFFFFFFFF;
	public static final int BUTTON_COLOR_INACTIVE = 0xFFA0A0A0;
	public static final int BUTTON_COLOR_FOCUS = 0xFFBAF6FF;
	public static final int TEXT_COLOR_ACTIVE = 0xFF000000;
	public static final int TEXT_COLOR_INACTIVE = 0xFFEEEEEE;
	public static final int TEXT_COLOR_FOCUS = 0xFF000000;
	public static final int TEXT_COLOR_LABEL = 0xFFFFFFFF;

	public static final int CONTROL_INTERNAL_MARGIN = 5;
	public static final int CONTROL_EXTERNAL_MARGIN = 5;
	public static final int CONTROL_BACKGROUND = 0x4AFFFFFF;

	public static final int SCROLLBAR_WIDTH = 10;

	public static final int NO_SELECTION = -1;

	protected float top;
	protected float left;
	protected float height;
	protected float width;
	protected float bottom;
	protected float right;

	protected int horizontalWeight = 1;
	protected int verticalWeight = 1;

	protected Layout horizontalLayout = Layout.WEIGHTED;
	protected Layout verticalLayout = Layout.WEIGHTED;

	protected int backgroundColor = 0;

	protected boolean isDirty = false;

	protected boolean isVisible = true;

	/** cumulative scroll distance from all events */
	protected float scrollDistance;
	/** cumulative distance before scroll is recognized */
	protected float scrollIncrementDistance = 1;
	/** last scroll increment - used to compute a delta */
	protected int lastScrollIncrement = 0;

	/**
	 * If a control has consistent shape, is height / pixelWidth. Multiply
	 * pixelWidth by this number to get height. Divide height by this number to get
	 * pixelWidth.
	 */
	protected float aspectRatio = 1.0f;

	protected final ScreenRenderContext renderContext;

	public AbstractControl(ScreenRenderContext renderContext) {
		this.renderContext = renderContext;
	}

	public AbstractControl<T> resize(float left, float top, float width, float height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.isDirty = true;
		return this;
	}

	public final void drawControl(int mouseX, int mouseY, float partialTicks) {
		this.refreshContentCoordinatesIfNeeded();

		if (this.isVisible) {
			// set hover start, so that controls further down the stack can overwrite
			if (this.isMouseOver(mouseX, mouseY)) {
				renderContext.setHoverControl(this);
			}

			this.drawContent(mouseX, mouseY, partialTicks);
		}
	}

	public abstract void drawToolTip(int mouseX, int mouseY, float partialTicks);

	protected abstract void drawContent(int mouseX, int mouseY, float partialTicks);

	/** called after any coordinate-related input changes */
	protected void handleCoordinateUpdate() {

	}

	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {

	}

	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {

	}

	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {

	}

	@Override
	public final boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (this.isVisible) {
			if (mouseX < this.left || mouseX > this.right || mouseY < this.top || mouseY > this.bottom) {
				return false;
			}

			this.scrollDistance += scrollDelta;
			this.handleMouseScroll(mouseX, mouseY, scrollDelta);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public final boolean mouseClicked(double mouseX, double mouseY, int clickedMouseButton) {
		if (this.isVisible) {
			if (mouseX < this.left || mouseX > this.right || mouseY < this.top || mouseY > this.bottom) {
				return false;
			}

			this.handleMouseClick(mouseX, mouseY, clickedMouseButton);
			return true;
		}  else {
			return false;
		}
	}

	@Override
	public final boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		if (this.isVisible) {
			if (mouseX < this.left || mouseX > this.right || mouseY < this.top || mouseY > this.bottom) {
				return false;
			}

			this.handleMouseDrag(mouseX, mouseY, clickedMouseButton, dx, dy);
			return true;
		}  else {
			return false;
		}
	}

	protected int mouseIncrementDelta() {
		final int newIncrement = (int) (this.scrollDistance / this.scrollIncrementDistance);
		final int result = newIncrement - this.lastScrollIncrement;

		if (result != 0) {
			this.lastScrollIncrement = newIncrement;
		}

		return result;
	}

	protected void refreshContentCoordinatesIfNeeded() {
		if (this.isDirty) {
			this.bottom = this.top + this.height;
			this.right = this.left + this.width;

			this.handleCoordinateUpdate();
			this.isDirty = false;
		}
	}

	public float getTop() {
		return top;
	}

	public float getBottom() {
		this.refreshContentCoordinatesIfNeeded();
		return this.bottom;
	}

	public float getLeft() {
		return left;
	}

	public float getRight() {
		this.refreshContentCoordinatesIfNeeded();
		return this.right;
	}

	public float getHeight() {
		return height;
	}

	@SuppressWarnings("unchecked")
	public T setTop(float top) {
		this.top = top;
		this.isDirty = true;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLeft(float left) {
		this.left = left;
		this.isDirty = true;
		return (T) this;
	}

	/**
	 * Use when control needs to be a square size. Controls that require this
	 * generally don't enforce it. Sometimes life isn't fair.
	 */
	@SuppressWarnings("unchecked")
	public T setSquareSize(float size) {
		this.height = size;
		this.width = size;
		this.isDirty = true;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setHeight(float height) {
		this.height = height;
		this.isDirty = true;
		return (T) this;
	}

	public float getWidth() {
		return width;
	}

	@SuppressWarnings("unchecked")
	public T setWidth(float width) {
		this.width = width;
		this.isDirty = true;
		return (T) this;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	@SuppressWarnings("unchecked")
	public T setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		return (T) this;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	@SuppressWarnings("unchecked")
	public T setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
		return (T) this;
	}

	public int getHorizontalWeight() {
		return horizontalWeight;
	}

	@SuppressWarnings("unchecked")
	public T setHorizontalWeight(int horizontalWeight) {
		this.horizontalWeight = horizontalWeight;
		return (T) this;
	}

	public int getVerticalWeight() {
		return verticalWeight;
	}

	@SuppressWarnings("unchecked")
	public T setVerticalWeight(int verticalWeight) {
		this.verticalWeight = verticalWeight;
		return (T) this;
	}

	public Layout getHorizontalLayout() {
		return horizontalLayout;
	}

	@SuppressWarnings("unchecked")
	public T setHorizontalLayout(Layout horizontalLayout) {
		this.horizontalLayout = horizontalLayout;
		return (T) this;
	}

	public Layout getVerticalLayout() {
		return verticalLayout;
	}

	@SuppressWarnings("unchecked")
	public T setVerticalLayout(Layout verticalLayout) {
		this.verticalLayout = verticalLayout;
		return (T) this;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return !(mouseX < this.left || mouseX > this.right || mouseY < this.top || mouseY > this.bottom);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
