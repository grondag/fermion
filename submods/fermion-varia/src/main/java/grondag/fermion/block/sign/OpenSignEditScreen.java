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
package grondag.fermion.block.sign;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class OpenSignEditScreen extends Screen {
	public final OpenSignBlockEntity sign;
	protected int ticksSinceOpened;
	protected int currentRow;
	protected SelectionManager selectionManager;

	public OpenSignEditScreen(OpenSignBlockEntity blockEntity) {
		super(new TranslatableText("sign.edit", new Object[0]));
		sign = blockEntity;
	}

	@Override
	protected void init() {
		minecraft.keyboard.enableRepeatEvents(true);

		addButton(new ButtonWidget(width / 2 - 100, height / 4 + 120, 200, 20, I18n.translate("gui.done"), (widget) -> {
			finishEditing();
		}));

		sign.setEditable(false);
		selectionManager = new SelectionManager(minecraft, () -> {
			return sign.getTextOnRow(currentRow).getString();
		}, (s) -> {
			sign.setTextOnRow(currentRow, new LiteralText(s));
		}, 90);
	}

	@Override
	public void removed() {
		minecraft.keyboard.enableRepeatEvents(false);
		OpenSignUpdateC2S.updateSignC2S(sign.getPos(), sign.getTextOnRow(0), sign.getTextOnRow(1), sign.getTextOnRow(2), sign.getTextOnRow(3));
		sign.setEditable(true);
	}

	@Override
	public void tick() {
		++ticksSinceOpened;

		if (!sign.getType().supports(sign.getCachedState().getBlock())) {
			finishEditing();
		}

	}

	protected void finishEditing() {
		sign.markDirty();
		minecraft.openScreen(null);
	}

	@Override
	public boolean charTyped(char typed, int unused) {
		selectionManager.insert(typed);
		return true;
	}

	@Override
	public void onClose() {
		finishEditing();
	}

	@Override
	public boolean keyPressed(int key, int x, int y) {
		if (key == 265) {
			currentRow = currentRow - 1 & 3;
			selectionManager.moveCaretToEnd();
			return true;
		} else if (key != 264 && key != 257 && key != 335)
			return selectionManager.handleSpecialKey(key) ? true : super.keyPressed(key, x, y);
		else {
			currentRow = currentRow + 1 & 3;
			selectionManager.moveCaretToEnd();
			return true;
		}
	}

	@Override
	public void render(int x, int y, float tickDelta) {
		renderBackground();
		super.render(x, y, tickDelta);

		drawCenteredString(font, title.asFormattedString(), width / 2, 40, 16777215);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((width / 2), 0.0F, 50.0F);
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);

		final BlockState blockState = sign.getCachedState();
		float rot;

		if (blockState.getBlock() instanceof OpenSignBlock) {
			rot = blockState.get(OpenSignBlock.ROTATION) * 360 / 16.0F;
		} else {
			rot = blockState.get(OpenWallSignBlock.FACING).asRotation();
		}

		GlStateManager.rotatef(rot, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
		sign.setSelectionState(currentRow, selectionManager.getSelectionStart(), selectionManager.getSelectionEnd(), ticksSinceOpened / 6 % 2 == 0);
		OpenSignRenderer.isScreen = true;
		BlockEntityRenderDispatcher.INSTANCE.renderEntity(sign, -0.5D, -0.75D, -0.5D, 0.0F);
		OpenSignRenderer.isScreen = false;
		sign.resetSelectionState();
		GlStateManager.popMatrix();
	}
}
