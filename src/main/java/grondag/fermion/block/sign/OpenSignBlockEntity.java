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

import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import grondag.fermion.client.RenderRefreshProxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.SpriteIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

/** open and extensible implementation of vanilla signs */
public class OpenSignBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
	public final Text[] text = new Text[]{new LiteralText(""), new LiteralText(""), new LiteralText(""), new LiteralText("")};
	@Environment(EnvType.CLIENT)
	protected boolean caretVisible;
	protected int currentRow = -1;
	protected int selectionStart = -1;
	protected int selectionEnd = -1;
	protected boolean editable = true;
	protected PlayerEntity editor;
	protected final String[] textBeingEdited = new String[4];
	protected DyeColor textColor;
	protected boolean lit = false;

	public <T extends OpenSignBlockEntity> OpenSignBlockEntity(BlockEntityType<T> beTyoe) {
		super(beTyoe);
		textColor = DyeColor.BLACK;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);

		for(int i = 0; i < 4; ++i) {
			final String t = Text.Serializer.toJson(text[i]);
			tag.putString("Text" + (i + 1), t);
		}

		tag.putString("Color", textColor.getName());
		return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		editable = false;
		super.fromTag(tag);
		textColor = DyeColor.byName(tag.getString("Color"), DyeColor.BLACK);

		for(int i = 0; i < 4; ++i) {
			final String s = tag.getString("Text" + (i + 1));
			final Text t = Text.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);

			if (world instanceof ServerWorld) {
				try {
					text[i] = Texts.parse(getCommandSource((ServerPlayerEntity)null), t, (Entity)null, 0);
				} catch (final CommandSyntaxException e) {
					text[i] = t;
				}
			} else {
				text[i] = t;
			}

			textBeingEdited[i] = null;
		}
	}

	@Environment(EnvType.CLIENT)
	public Text getTextOnRow(int row) {
		return text[row];
	}

	public void setTextOnRow(int row, Text text) {
		this.text[row] = text;
		textBeingEdited[row] = null;
	}

	public void setText(String text0, String text1, String text2, String text3) {
		System.out.println("setText: " + text0);
		setTextOnRow(0, new LiteralText(text0));
		setTextOnRow(1, new LiteralText(text1));
		setTextOnRow(2, new LiteralText(text2));
		setTextOnRow(3, new LiteralText(text3));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String getTextBeingEditedOnRow(int row, Function<Text, String> textFunc) {
		if (textBeingEdited[row] == null && text[row] != null) {
			textBeingEdited[row] = textFunc.apply(text[row]);
		}

		return textBeingEdited[row];
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toTag(new CompoundTag());
	}

	@Override
	public boolean shouldNotCopyTagFromItem() {
		return true;
	}

	public boolean isLit() {
		return lit;
	}

	public void setLit(boolean isLit) {
		lit = isLit;
	}

	public boolean isEditable() {
		return editable;
	}

	@Environment(EnvType.CLIENT)
	public void setEditable(boolean canEdit) {
		editable = canEdit;

		if (!canEdit) {
			editor = null;
		}
	}

	public void setEditor(PlayerEntity player) {
		editor = player;
	}

	public PlayerEntity getEditor() {
		return editor;
	}

	public boolean onActivate(PlayerEntity player) {
		if (player.isSneaking()) {
			edit((ServerPlayerEntity) player);
			return true;
		}

		final Text[] text = this.text;
		final int limit = text.length;

		for(int i = 0; i < limit; ++i) {
			final Text t = text[i];
			final Style style = t == null ? null : t.getStyle();

			if (style != null && style.getClickEvent() != null) {
				final ClickEvent click = style.getClickEvent();

				if (click.getAction() == ClickEvent.Action.RUN_COMMAND) {
					player.getServer().getCommandManager().execute(getCommandSource((ServerPlayerEntity) player), click.getValue());
				}
			}
		}

		return true;
	}

	public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity player) {
		final String src = player == null ? "Sign" : player.getName().getString();
		final Text txt = player == null ? new LiteralText("Sign") : player.getDisplayName();
		return new ServerCommandSource(CommandOutput.DUMMY, new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), Vec2f.ZERO, (ServerWorld) world, 2, src, txt, world.getServer(), player);
	}

	public DyeColor getTextColor() {
		return textColor;
	}

	public boolean setTextColor(DyeColor dyeColor) {
		if (dyeColor != textColor) {
			textColor = dyeColor;
			markDirty();

			if (world != null) {
				world.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
			}

			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public void setSelectionState(int row, int start, int end, boolean showCaret) {
		currentRow = row;
		selectionStart = start;
		selectionEnd = end;
		caretVisible = showCaret;
	}

	@Environment(EnvType.CLIENT)
	public void resetSelectionState() {
		currentRow = -1;
		selectionStart = -1;
		selectionEnd = -1;
		caretVisible = false;
	}

	@Environment(EnvType.CLIENT)
	public boolean isCaretVisible() {
		return caretVisible;
	}

	@Environment(EnvType.CLIENT)
	public int getCurrentRow() {
		return currentRow;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionStart() {
		return selectionStart;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionEnd() {
		return selectionEnd;
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
		RenderRefreshProxy.refresh(pos);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}

	public void edit(ServerPlayerEntity player) {
		setEditor(player);
		OpenSignUpdateS2C.updateSignS2C(player, this);
	}

	public SpriteIdentifier getModelTexture() {
		return null;
	}
}
