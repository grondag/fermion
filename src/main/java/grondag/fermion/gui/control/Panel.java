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

import java.util.Arrays;

import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.Layout;
import grondag.fermion.gui.ScreenRenderContext;

@Environment(EnvType.CLIENT)
public class Panel extends AbstractParentControl<Panel> implements ParentElement {
	/** if false is horizontal */
	public final boolean isVertical;

	private int outerMarginWidth = 0;
	private int innerMarginWidth = 0;

	/**
	 * If true, don't adjustIfEnabled layout of any child controls. Useful for
	 * containers that have to conform to a specific pixel layout.
	 */
	private boolean isLayoutDisabled = false;

	public Panel(ScreenRenderContext renderContext, boolean isVertical) {
		super(renderContext);
		this.isVertical = isVertical;
	}

	public Panel addAll(AbstractControl<?>... controls) {
		children.addAll(Arrays.asList(controls));
		isDirty = true;
		return this;
	}

	public Panel add(AbstractControl<?> control) {
		children.add(control);
		isDirty = true;
		return this;
	}

	@Override
	protected void drawContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (getBackgroundColor() != 0) {
			GuiUtil.drawRect(matrixStack.peek().getModel(), left, top, right, bottom, getBackgroundColor());
		}

		for (final AbstractControl<?> control : children) {
			control.drawControl(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		if (isLayoutDisabled || children == null || children.isEmpty()) {
			return;
		}

		int totalWeight = 0;
		int totalFixed = 0;

		final float variableSpace = (isVertical ? height : width) - outerMarginWidth * 2;
		final float fixedSpace = (isVertical ? width : height) - outerMarginWidth * 2;

		// on start pass, gather the size/weights for the expanding dimension
		for (final AbstractControl<?> control : children) {
			if (isVertical) {
				switch (control.getVerticalLayout()) {
				case FIXED:
					totalFixed += control.getHeight();
					break;

				case PROPORTIONAL:
					totalFixed += fixedSpace * control.getAspectRatio();
					break;

				case WEIGHTED:
				default:
					totalWeight += control.getVerticalWeight();
					break;
				}
			} else {
				switch (control.getHorizontalLayout()) {
				case FIXED:
					totalFixed += control.getWidth();
					break;

				case PROPORTIONAL:
					totalFixed += fixedSpace / control.getAspectRatio();
					break;

				case WEIGHTED:
				default:
					totalWeight += control.getHorizontalWeight();
					break;
				}
			}
		}

		// now scale the weights to the amount of space available
		final float spaceFactor = totalWeight <= 0 ? 0 : (variableSpace - totalFixed - innerMarginWidth * (children.size() - 1)) / totalWeight;

		float contentLeft = left + outerMarginWidth;
		float contentTop = top + outerMarginWidth;
		final float fixedSize = (isVertical ? width : height) - outerMarginWidth * 2;

		// on second pass rescale
		for (final AbstractControl<?> control : children) {
			//            double variableSize;

			float controlHeight;
			float controlWidth;

			if (isVertical) {
				controlWidth = control.getHorizontalLayout() == Layout.FIXED ? control.getWidth() : fixedSize;

				switch (control.getVerticalLayout()) {
				case FIXED:
					controlHeight = control.getHeight();
					break;

				case PROPORTIONAL:
					controlHeight = controlWidth * control.getAspectRatio();
					break;

				case WEIGHTED:
				default:
					controlHeight = spaceFactor * control.getVerticalWeight();
					break;
				}

				if (control.getHorizontalLayout() == Layout.PROPORTIONAL) {
					controlWidth = controlHeight / control.getAspectRatio();
				}

				control.resize(contentLeft, contentTop, controlWidth, controlHeight);
				contentTop += controlHeight + innerMarginWidth;
			} else {
				controlHeight = control.getVerticalLayout() == Layout.FIXED ? control.getHeight() : fixedSize;

				switch (control.getHorizontalLayout()) {
				case FIXED:
					controlWidth = control.getWidth();
					break;

				case PROPORTIONAL:
					controlWidth = controlHeight / control.getAspectRatio();
					break;

				case WEIGHTED:
				default:
					controlWidth = spaceFactor * control.getHorizontalWeight();
					break;
				}

				if (control.getVerticalLayout() == Layout.PROPORTIONAL) {
					controlHeight = controlWidth * control.getAspectRatio();
				}

				control.resize(contentLeft, contentTop, controlWidth, controlHeight);
				contentLeft += controlWidth + innerMarginWidth;
			}
		}
	}

	//TODO: remove - should not longer be needed because events always go to hovered element

	//	@Override
	//	public boolean handleMouseClick(MinecraftClient mc, double mouseX, double mouseY, int clickedMouseButton) {
	//		for (final AbstractControl<?> child : children) {
	//			child.mouseClick(mc, mouseX, mouseY, clickedMouseButton);
	//		}
	//		return true;
	//	}
	//
	//	@Override
	//	public void handleMouseDrag(MinecraftClient mc, int mouseX, int mouseY, int clickedMouseButton) {
	//		for (final AbstractControl<?> child : children) {
	//			child.mouseDrag(mc, mouseX, mouseY, clickedMouseButton);
	//		}
	//	}
	//
	//	@Override
	//	protected void handleMouseScroll(int mouseX, int mouseY, int scrollDelta) {
	//		for (final AbstractControl<?> child : children) {
	//			child.mouseScroll(mouseX, mouseY, scrollDelta);
	//		}
	//	}

	/** the pixelWidth of the background from the edge of child controls */
	public int getOuterMarginWidth() {
		return outerMarginWidth;
	}

	/** sets the pixelWidth of the background from the edge of child controls */
	public Panel setOuterMarginWidth(int outerMarginWidth) {
		this.outerMarginWidth = outerMarginWidth;
		isDirty = true;
		return this;
	}

	/** the spacing between child controls */
	public int getInnerMarginWidth() {
		return innerMarginWidth;
	}

	/** sets the spacing between child controls */
	public Panel setInnerMarginWidth(int innerMarginWidth) {
		this.innerMarginWidth = innerMarginWidth;
		isDirty = true;
		return this;
	}

	/**
	 * Set true to disable automatic layout of child controls. Used for containers
	 * that require a fixed layout. Means you must write code to set position and
	 * size of all children.
	 */
	public boolean isLayoutDisabled() {
		return isLayoutDisabled;
	}

	public void setLayoutDisabled(boolean isLayoutDisabled) {
		this.isLayoutDisabled = isLayoutDisabled;
	}

	@Override
	public void drawToolTip(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}
}
