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

import java.util.ArrayList;
import java.util.List;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.mcmd.McMdRenderer;
import grondag.mcmd.McMdStyle;
import grondag.mcmd.node.Node;
import grondag.mcmd.renderer.mc.McMdContentRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MarkdownControl extends AbstractControl<MarkdownControl> {
	protected List<String> lines = null;
	final McMdContentRenderer renderer = McMdContentRenderer.builder().build();
	protected double textHeight = 0;

	protected double renderStart = 0;
	protected double buttonHeight = 0;
	protected double buttonOffset = 0;
	protected double maxRenderStart = 0;
	protected double maxButtonOffset = 0;

	protected Slider slider;
	Node markdown;
	final McMdRenderer mcmd;
	public MarkdownControl(ScreenRenderContext renderContext, Node document, TextRenderer baseFont) {
		super(renderContext);
		markdown = document;
		isDirty = true;
		mcmd = new McMdRenderer(new McMdStyle(), baseFont);
	}


	@Override
	protected void drawContent(int mouseX, int mouseY, float partialTicks) {
		final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		mcmd.drawMarkdown(Rotation3.identity().getMatrix(), immediate, lines, (float) left, (float) top, 0, (float) renderStart, (float) height, mouseY);
		immediate.draw();
		drawScrollIfNeeded();
	}

	protected void drawScrollIfNeeded() {
		if (buttonHeight != 0) {
			GuiUtil.drawRect(left + width - SCROLLBAR_WIDTH, top, right, bottom, 0xFF505050);

			GuiUtil.drawRect(left + width - SCROLLBAR_WIDTH + 1, top + 1 + buttonOffset, right - 1, top + buttonOffset + 1 + buttonHeight, 0xFF508080);
		}
	}

	protected void parse(McMdRenderer mcmd, int width) {
		isDirty = false;

		final String text = renderer.render(markdown);

		if (lines == null) {
			lines = new ArrayList<>();
		} else {
			lines.clear();
		}

		mcmd.wrapMarkdownToWidth(text, width, lines);
	}

	@Override
	protected void handleCoordinateUpdate() {
		parse(mcmd, (int) width - SCROLLBAR_WIDTH - CONTROL_INTERNAL_MARGIN);

		textHeight = mcmd.verticalHeight(lines);
		buttonHeight = textHeight > height ? (height - 2) * height / textHeight : 0;
		maxRenderStart = textHeight - height;
		maxButtonOffset = buttonHeight == 0 ? 0 : height - 2 - buttonHeight;
		buttonOffset = 0;
		renderStart = 0;
	}

	@Override
	public void drawToolTip(int mouseX, int mouseY, float partialTicks) {
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {

	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {

	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		if (buttonHeight != 0) {
			buttonOffset -= scrollDelta;
			clamp();
		}
	}

	protected void clamp() {
		if (maxButtonOffset != 0) {
			buttonOffset = MathHelper.clamp(buttonOffset, 0, maxButtonOffset);
			renderStart = maxRenderStart * buttonOffset / maxButtonOffset;
		}
	}
}
