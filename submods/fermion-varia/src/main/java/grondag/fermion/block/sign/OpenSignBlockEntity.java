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
		this.textColor = DyeColor.BLACK;
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
					text[i] = Texts.parse(this.getCommandSource((ServerPlayerEntity)null), t, (Entity)null, 0);
				} catch (CommandSyntaxException e) {
					text[i] = t;
				}
			} else {
				this.text[i] = t;
			}

			this.textBeingEdited[i] = null;
		}
	}

	@Environment(EnvType.CLIENT)
	public Text getTextOnRow(int row) {
		return this.text[row];
	}

	public void setTextOnRow(int row, Text text) {
		this.text[row] = text;
		this.textBeingEdited[row] = null;
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
		if (this.textBeingEdited[row] == null && this.text[row] != null) {
			this.textBeingEdited[row] = textFunc.apply(this.text[row]);
		}

		return this.textBeingEdited[row];
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
			Text t = text[i];
			Style style = t == null ? null : t.getStyle();

			if (style != null && style.getClickEvent() != null) {
				ClickEvent click = style.getClickEvent();

				if (click.getAction() == ClickEvent.Action.RUN_COMMAND) {
					player.getServer().getCommandManager().execute(getCommandSource((ServerPlayerEntity) player), click.getValue());
				}
			}
		}

		return true;
	}

	public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity player) {
		String src = player == null ? "Sign" : player.getName().getString();
		Text txt = player == null ? new LiteralText("Sign") : player.getDisplayName();
		return new ServerCommandSource(CommandOutput.DUMMY, new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), Vec2f.ZERO, (ServerWorld) world, 2, src, txt, world.getServer(), player);
	}

	public DyeColor getTextColor() {
		return this.textColor;
	}

	public boolean setTextColor(DyeColor dyeColor) {
		if (dyeColor != this.textColor) {
			this.textColor = dyeColor;
			this.markDirty();
			
			if (this.world != null) {
				this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
			}
			
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public void setSelectionState(int row, int start, int end, boolean showCaret) {
		this.currentRow = row;
		this.selectionStart = start;
		this.selectionEnd = end;
		this.caretVisible = showCaret;
	}

	@Environment(EnvType.CLIENT)
	public void resetSelectionState() {
		this.currentRow = -1;
		this.selectionStart = -1;
		this.selectionEnd = -1;
		this.caretVisible = false;
	}

	@Environment(EnvType.CLIENT)
	public boolean isCaretVisible() {
		return this.caretVisible;
	}

	@Environment(EnvType.CLIENT)
	public int getCurrentRow() {
		return this.currentRow;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionStart() {
		return this.selectionStart;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionEnd() {
		return this.selectionEnd;
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
}
