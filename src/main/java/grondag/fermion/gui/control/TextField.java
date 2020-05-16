package grondag.fermion.gui.control;

import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.gui.ScreenTheme;

@Environment(EnvType.CLIENT)
public class TextField extends AbstractButtonWidget implements Drawable, Element {
	protected String text;
	protected int maxLength;
	protected int focusedTicks;
	protected boolean focused;
	protected boolean focusUnlocked;
	protected boolean editable;
	protected boolean selecting;
	protected int firstCharacterIndex;
	protected int selectionStart;
	protected int selectionEnd;
	protected String suggestion;
	protected Consumer<String> changedListener;
	protected Predicate<String> textPredicate;
	protected final ScreenTheme theme = ScreenTheme.current();

	protected final ScreenRenderContext renderContext;

	public TextField(ScreenRenderContext context, int left, int top, int width, int height, Text string) {
		this(context, left, top, width, height, null, string);
	}

	public TextField(ScreenRenderContext context, int left, int top, int width, int height, @Nullable TextField textField, Text string) {
		super(left, top, width, height, string);
		text = "";
		maxLength = 32;
		focused = true;
		focusUnlocked = true;
		editable = true;
		textPredicate = Predicates.alwaysTrue();
		renderContext = context;

		if (textField != null) {
			setText(textField.getText());
		}

	}

	public void setChangedListener(Consumer<String> consumer) {
		changedListener = consumer;
	}

	public void tick() {
		++focusedTicks;
	}

	@Override
	protected MutableText getNarrationMessage() {
		final Text text = getMessage();
		return new TranslatableText("gui.narrate.editBox", new Object[]{text, this.text});
	}

	public void setText(String string) {
		if (textPredicate.test(string)) {
			if (string.length() > maxLength) {
				text = string.substring(0, maxLength);
			} else {
				text = string;
			}

			setCursorToEnd();
			setSelectionEnd(selectionStart);
			onChanged(string);
		}
	}

	public String getText() {
		return text;
	}

	public String getSelectedText() {
		final int i = selectionStart < selectionEnd ? selectionStart : selectionEnd;
		final int j = selectionStart < selectionEnd ? selectionEnd : selectionStart;
		return text.substring(i, j);
	}

	public void setTextPredicate(Predicate<String> predicate) {
		textPredicate = predicate;
	}

	public void write(String string) {
		String string2 = "";
		final String string3 = SharedConstants.stripInvalidChars(string);
		final int i = selectionStart < selectionEnd ? selectionStart : selectionEnd;
		final int j = selectionStart < selectionEnd ? selectionEnd : selectionStart;
		final int k = maxLength - text.length() - (i - j);
		if (!text.isEmpty()) {
			string2 = string2 + text.substring(0, i);
		}

		int m;
		if (k < string3.length()) {
			string2 = string2 + string3.substring(0, k);
			m = k;
		} else {
			string2 = string2 + string3;
			m = string3.length();
		}

		if (!text.isEmpty() && j < text.length()) {
			string2 = string2 + text.substring(j);
		}

		if (textPredicate.test(string2)) {
			text = string2;
			setSelectionStart(i + m);
			setSelectionEnd(selectionStart);
			onChanged(text);
		}
	}

	private void onChanged(String string) {
		if (changedListener != null) {
			changedListener.accept(string);
		}

		nextNarration = Util.getMeasuringTimeMs() + 500L;
	}

	private void erase(int i) {
		if (Screen.hasControlDown()) {
			eraseWords(i);
		} else {
			eraseCharacters(i);
		}

	}

	public void eraseWords(int i) {
		if (!text.isEmpty()) {
			if (selectionEnd != selectionStart) {
				write("");
			} else {
				eraseCharacters(this.getWordSkipPosition(i) - selectionStart);
			}
		}
	}

	public void eraseCharacters(int i) {
		if (!text.isEmpty()) {
			if (selectionEnd != selectionStart) {
				write("");
			} else {
				final boolean bl = i < 0;
				final int j = bl ? selectionStart + i : selectionStart;
				final int k = bl ? selectionStart : selectionStart + i;
				String string = "";
				if (j >= 0) {
					string = text.substring(0, j);
				}

				if (k < text.length()) {
					string = string + text.substring(k);
				}

				if (textPredicate.test(string)) {
					text = string;
					if (bl) {
						moveCursor(i);
					}

					onChanged(text);
				}
			}
		}
	}

	public int getWordSkipPosition(int i) {
		return this.getWordSkipPosition(i, getCursor());
	}

	private int getWordSkipPosition(int i, int j) {
		return this.getWordSkipPosition(i, j, true);
	}

	private int getWordSkipPosition(int i, int j, boolean bl) {
		int k = j;
		final boolean bl2 = i < 0;
		final int l = Math.abs(i);

		for(int m = 0; m < l; ++m) {
			if (!bl2) {
				final int n = text.length();
				k = text.indexOf(32, k);
				if (k == -1) {
					k = n;
				} else {
					while(bl && k < n && text.charAt(k) == ' ') {
						++k;
					}
				}
			} else {
				while(bl && k > 0 && text.charAt(k - 1) == ' ') {
					--k;
				}

				while(k > 0 && text.charAt(k - 1) != ' ') {
					--k;
				}
			}
		}

		return k;
	}

	public void moveCursor(int i) {
		setCursor(selectionStart + i);
	}

	public void setCursor(int i) {
		setSelectionStart(i);
		if (!selecting) {
			setSelectionEnd(selectionStart);
		}

		onChanged(text);
	}

	public void setSelectionStart(int i) {
		selectionStart = MathHelper.clamp(i, 0, text.length());
	}

	public void setCursorToStart() {
		setCursor(0);
	}

	public void setCursorToEnd() {
		setCursor(text.length());
	}

	@SuppressWarnings("resource")
	@Override
	public boolean keyPressed(int c, int j, int k) {
		if (!isActive()) {
			return false;
		} else {
			selecting = Screen.hasShiftDown();
			if (Screen.isSelectAll(c)) {
				setCursorToEnd();
				setSelectionEnd(0);
				return true;
			} else if (Screen.isCopy(c)) {
				MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
				return true;
			} else if (Screen.isPaste(c)) {
				if (editable) {
					write(MinecraftClient.getInstance().keyboard.getClipboard());
				}

				return true;
			} else if (Screen.isCut(c)) {
				MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
				if (editable) {
					write("");
				}

				return true;
			} else {
				switch(c) {
				case 259:
					if (editable) {
						selecting = false;
						erase(-1);
						selecting = Screen.hasShiftDown();
					}

					return true;
				case 256: // esv
					return false;
				case 260:
				case 264:
				case 265:
				case 266:
				case 267:
				default:
					return Character.isLetterOrDigit(c) || Character.isWhitespace(c);
				case 261:
					if (editable) {
						selecting = false;
						erase(1);
						selecting = Screen.hasShiftDown();
					}

					return true;
				case 262:
					if (Screen.hasControlDown()) {
						setCursor(this.getWordSkipPosition(1));
					} else {
						moveCursor(1);
					}

					return true;
				case 263:
					if (Screen.hasControlDown()) {
						setCursor(this.getWordSkipPosition(-1));
					} else {
						moveCursor(-1);
					}

					return true;
				case 268:
					setCursorToStart();
					return true;
				case 269:
					setCursorToEnd();
					return true;
				}
			}
		}
	}

	public boolean isActive() {
		return isVisible() && isFocused() && isEditable();
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (!isActive()) {
			return false;
		} else if (SharedConstants.isValidChar(c)) {
			if (editable) {
				write(Character.toString(c));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (!isVisible()) {
			return false;
		} else {
			final boolean bl = d >= x && d < x + width && e >= y && e < y + height;
			if (focusUnlocked) {
				setSelected(bl);
			}

			if (isFocused() && bl && i == 0) {
				int j = MathHelper.floor(d) - x;
				if (focused) {
					j -= 4;
				}
				final TextRenderer textRenderer = renderContext.fontRenderer();
				final String string = textRenderer.trimToWidth(text.substring(firstCharacterIndex), getInnerWidth());
				setCursor(textRenderer.trimToWidth(string, j).length() + firstCharacterIndex);
				return true;
			} else {
				return false;
			}
		}
	}

	public void setSelected(boolean bl) {
		super.setFocused(bl);
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int i, int j, float f) {
		final TextRenderer textRenderer = renderContext.fontRenderer();

		if (isVisible()) {
			if (hasBorder()) {
				final int borderColor = isFocused() || hovered ? theme.buttonColorFocus : theme.textBorder;
				fill(matrixStack, x - 1, y - 1, x + width + 1, y + height + 1, borderColor);

			}

			fill(matrixStack, x, y, x + width, y + height, theme.textBackground);

			final int textColor = editable ? theme.textColorActive : theme.textColorInactive;
			final int startIndex = selectionStart - firstCharacterIndex;
			int selectionLength = selectionEnd - firstCharacterIndex;
			final String textToRender = textRenderer.trimToWidth(text.substring(firstCharacterIndex), getInnerWidth());
			final boolean caretFlag = startIndex >= 0 && startIndex <= textToRender.length();
			final boolean shouldCaretBlink = isFocused() && focusedTicks / 6 % 2 == 0 && caretFlag;
			final int n = focused ? x + 4 : x;
			final int o = focused ? y + (height - 8) / 2 : y;
			int p = n;

			if (selectionLength > textToRender.length()) {
				selectionLength = textToRender.length();
			}

			if (!textToRender.isEmpty()) {
				final String string2 = caretFlag ? textToRender.substring(0, startIndex) : textToRender;
				p = textRenderer.draw(matrixStack, string2, n, o, textColor);
			}

			final boolean bl3 = selectionStart < text.length() || text.length() >= getMaxLength();
			int q = p;
			if (!caretFlag) {
				q = startIndex > 0 ? n + width : n;
			} else if (bl3) {
				q = p - 1;
				--p;
			}

			if (!textToRender.isEmpty() && caretFlag && startIndex < textToRender.length()) {
				textRenderer.draw(matrixStack, textToRender.substring(startIndex), p, o, textColor);
			}

			if (!bl3 && suggestion != null) {
				textRenderer.draw(matrixStack, suggestion, q - 1, o, -8355712);
			}

			int var10002;
			int var10003;
			if (shouldCaretBlink) {
				if (bl3) {
					final int var10001 = o - 1;
					var10002 = q + 1;
					var10003 = o + 1;
					textRenderer.getClass();
					DrawableHelper.fill(matrixStack, q, var10001, var10002, var10003 + 9, -3092272);
				} else {
					textRenderer.draw(matrixStack, "_", q, o, textColor);
				}
			}

			if (selectionLength != startIndex) {
				final int r = n + textRenderer.getWidth(textToRender.substring(0, selectionLength));
				var10002 = o - 1;
				var10003 = r - 1;
				final int var10004 = o + 1;
				textRenderer.getClass();
				drawSelectionHighlight(q, var10002, var10003, var10004 + 9);
			}

		}
	}

	private void drawSelectionHighlight(int i, int j, int k, int l) {
		int n;
		if (i < k) {
			n = i;
			i = k;
			k = n;
		}

		if (j < l) {
			n = j;
			j = l;
			l = n;
		}

		if (k > x + width) {
			k = x + width;
		}

		if (i > x + width) {
			i = x + width;
		}

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex(i, l, 0.0D).next();
		bufferBuilder.vertex(k, l, 0.0D).next();
		bufferBuilder.vertex(k, j, 0.0D).next();
		bufferBuilder.vertex(i, j, 0.0D).next();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	public void setMaxLength(int i) {
		maxLength = i;
		if (text.length() > i) {
			text = text.substring(0, i);
			onChanged(text);
		}

	}

	private int getMaxLength() {
		return maxLength;
	}

	public int getCursor() {
		return selectionStart;
	}

	private boolean hasBorder() {
		return focused;
	}

	public void setHasBorder(boolean bl) {
		focused = bl;
	}

	@Override
	public boolean changeFocus(boolean bl) {
		return visible && editable ? super.changeFocus(bl) : false;
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return visible && d >= x && d < x + width && e >= y && e < y + height;
	}

	@Override
	protected void onFocusedChanged(boolean bl) {
		if (bl) {
			focusedTicks = 0;
		}
	}

	private boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean bl) {
		editable = bl;
	}

	public int getInnerWidth() {
		return hasBorder() ? width - 8 : width;
	}

	public void setSelectionEnd(int i) {
		final int j = text.length();
		selectionEnd = MathHelper.clamp(i, 0, j);
		final TextRenderer textRenderer = renderContext.fontRenderer();

		if (textRenderer != null) {
			if (firstCharacterIndex > j) {
				firstCharacterIndex = j;
			}

			final int k = getInnerWidth();
			final String string = textRenderer.trimToWidth(text.substring(firstCharacterIndex), k);
			final int l = string.length() + firstCharacterIndex;
			if (selectionEnd == firstCharacterIndex) {
				firstCharacterIndex -= textRenderer.trimToWidth(text, k, true).length();
			}

			if (selectionEnd > l) {
				firstCharacterIndex += selectionEnd - l;
			} else if (selectionEnd <= firstCharacterIndex) {
				firstCharacterIndex -= firstCharacterIndex - selectionEnd;
			}

			firstCharacterIndex = MathHelper.clamp(firstCharacterIndex, 0, j);
		}

	}

	public void setFocusUnlocked(boolean bl) {
		focusUnlocked = bl;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean bl) {
		visible = bl;
	}

	public void setSuggestion(@Nullable String string) {
		suggestion = string;
	}

	public int getCharacterX(int i) {
		return i > text.length() ? x : x + renderContext.fontRenderer().getWidth(text.substring(0, i));
	}

	public void setX(int i) {
		x = i;
	}
}
