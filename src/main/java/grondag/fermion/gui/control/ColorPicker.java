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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.color.Chroma;
import grondag.fermion.color.Color;
import grondag.fermion.color.ColorAtlas;
import grondag.fermion.color.ColorSet;
import grondag.fermion.color.ColorSet.Tone;
import grondag.fermion.color.Hue;
import grondag.fermion.color.Luminance;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;

@Environment(EnvType.CLIENT)
public class ColorPicker extends AbstractControl<ColorPicker> {
	private Hue selectedHue = Hue.AZURE;
	private Chroma selectedChroma = null;

	private int colorMapID = 0;

	private double centerX;
	private double centerY;
	private double radiusInner;
	private double radiusOuter;
	private final double arc = 360.0 / Hue.COUNT;

	private float gridLeft;
	private float gridTop;
	private float gridIncrementX;
	private float gridIncrementY;

	public Hue getHue() {
		return selectedHue;
	}

	public void setHue(Hue h) {
		selectedHue = h;
	}

	public int getColorMapID() {
		return colorMapID;
	}

	public boolean showLampColors = false;

	public void setColorMapID(int colorMapID) {
		this.colorMapID = colorMapID;
		selectedHue = ColorAtlas.INSTANCE.getColorMap(colorMapID).hue;
		selectedChroma = ColorAtlas.INSTANCE.getColorMap(colorMapID).chroma;
	}

	public ColorPicker(ScreenRenderContext renderContext) {
		super(renderContext);
		setAspectRatio((float) height(1.0f));
	}

	@Override
	protected void drawContent(int mouseX, int mouseY, float partialTicks) {
		for (int h = 0; h < Hue.COUNT; h++) {
			final double radius = (h == selectedHue.ordinal()) ? radiusOuter : radiusInner;
			final double arcStart = Math.toRadians(arc * h);
			final double arcEnd = Math.toRadians(arc * (h + 1));

			final double x0 = centerX;// + Math.sin(arcStart) * radiusInner;
			final double y0 = centerY;// + Math.cos(arcStart) * radiusInner;

			final double x1 = centerX + Math.sin(arcStart) * radius;
			final double y1 = centerY + Math.cos(arcStart) * radius;

			final double x2 = centerX + Math.sin(arcEnd) * radius;
			final double y2 = centerY + Math.cos(arcEnd) * radius;

			final double x3 = centerX;// + Math.sin(arcEnd) * radiusInner;
			final double y3 = centerY;// + Math.cos(arcEnd) * radiusInner;

			GuiUtil.drawQuad((float) x0, (float) y0, (float) x1, (float) y1, (float) x2, (float) y2, (float) x3, (float) y3, Hue.VALUES[h].hueSample());
		}

		float left;
		float top = gridTop;
		float right;
		float bottom;

		final Tone map = showLampColors ? Tone.LAMP : Tone.BASE;

		for (int l = 0; l < Luminance.COUNT; l++) {
			bottom = top + gridIncrementY;
			left = gridLeft;
			for (int c = 0; c < Chroma.COUNT; c++) {
				right = left + gridIncrementX;
				final ColorSet colormap = ColorAtlas.INSTANCE.getColorMap(selectedHue, Chroma.VALUES[c], Luminance.VALUES[l]);
				if (colormap != null) {
					GuiUtil.drawRect(left, top, right, bottom, colormap.getColor(map));
				}
				left = right;
			}
			top = bottom;
		}

		final ColorSet selectedColormap = ColorAtlas.INSTANCE.getColorMap(colorMapID);

		final float sLeft = gridLeft + selectedColormap.chroma.ordinal() * gridIncrementX;
		final float sTop = gridTop + selectedColormap.luminance.ordinal() * gridIncrementY;

		GuiUtil.drawRect(sLeft - 1, sTop - 1, sLeft + gridIncrementX + 1, sTop + gridIncrementY + 1, showLampColors ? Color.BLACK : Color.WHITE);
		GuiUtil.drawRect(sLeft - 0.5f, sTop - 0.5f, sLeft + gridIncrementX + 0.5f, sTop + gridIncrementY + 0.5f, selectedColormap.getColor(map));
	}

	private void changeHueIfDifferent(Hue newHue) {
		if (newHue != selectedHue) {
			selectedHue = newHue;

			final ColorSet currentMap = ColorAtlas.INSTANCE.getColorMap(colorMapID);
			Chroma currentChroma = selectedChroma;
			final Luminance currentLuminance = currentMap.luminance;

			ColorSet newMap = ColorAtlas.INSTANCE.getColorMap(newHue, currentChroma, currentLuminance);

			while (newMap == null) {
				currentChroma = Chroma.VALUES[currentChroma.ordinal() - 1];
				newMap = ColorAtlas.INSTANCE.getColorMap(newHue, currentChroma, currentLuminance);
			}

			colorMapID = newMap.ordinal;
		}
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		final double distance = Math.sqrt((Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2)));

		if (distance < radiusOuter + 2) {
			double angle = Math.toDegrees(Math.atan2(mouseX - centerX, mouseY - centerY));
			if (angle < 1) {
				angle += 360;
			}
			int index = (int) Math.floor(angle / arc);

			if (index >= Hue.COUNT) {
				index = 0;
			}

			changeHueIfDifferent(Hue.VALUES[index]);

		} else if (mouseX >= gridLeft) {
			final int l = (int) Math.floor((mouseY - gridTop) / gridIncrementY);
			final int c = (int) Math.floor((mouseX - gridLeft) / gridIncrementX);

			if (l >= 0 && l < Luminance.COUNT && c >= 0 && c < Chroma.COUNT) {
				final ColorSet testMap = ColorAtlas.INSTANCE.getColorMap(selectedHue, Chroma.VALUES[c], Luminance.VALUES[l]);

				if (testMap != null) {
					colorMapID = testMap.ordinal;
					selectedChroma = testMap.chroma;
				}
			}
		}
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		//handleMouseClick(mc, mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		final int inc = mouseIncrementDelta();
		if (inc != 0) {
			int ord = selectedHue.ordinal() + inc;
			if (ord < 0) {
				ord = Hue.COUNT - 1;
			} else if (ord >= Hue.COUNT) {
				ord = 0;
			}
			changeHueIfDifferent(Hue.VALUES[ord]);
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		radiusOuter = outerRadius(height);
		centerX = left + radiusOuter;
		centerY = top + radiusOuter;
		radiusInner = innerRadius(height);

		gridIncrementX = (width - height) / (Chroma.COUNT + 1);
		gridIncrementY = (float) (radiusInner * 2 / Luminance.COUNT);
		gridLeft = left + height + gridIncrementX;
		gridTop = (float) (centerY - radiusInner);
	}

	private static double outerRadius(double height) {
		return height / 2.0;
	}

	private static double innerRadius(double height) {
		return outerRadius(height) * 0.85;
	}
	//    private static double gridIncrement(double height) { return innerRadius(height) * 2 / Luminance.values().length; }
	//    private static double pixelWidth(double height) { return height + gridIncrement(height) * (Chroma.values().length + 1); }

	private static double height(double width) {
		/**
		 * w = h + gi(h) * (cvl + 1) h + gi(h) * (cvl + 1) = w h + innerRadius(h) * 2 /
		 * lvl * (cvl + 1) = w h + outerRadius(h) * 0.85 * 2 / lvl * (cvl + 1) = w h + h
		 * / 2 * 0.85 * 2 / lvl * (cvl + 1) = w h + h (0.85 / lvl * (cvl + 1)) = w h *
		 * (1 + 0.85 / lvl * (cvl + 1)) = w h = w / (1 + 0.85 / lvl * (cvl + 1))
		 */
		return width / (1.0 + 0.85 / Luminance.COUNT * (Chroma.COUNT + 1));
	}

	@Override
	public ColorPicker setWidth(float width) {
		// pixelWidth is always derived from height, so have to work backwards to
		// correct height value
		return setHeight((float) height(width));
	}

	@Override
	public void drawToolTip(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}
}
