package grondag.fermion.modkeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import grondag.fermion.modkeys.ModKeysConfig.Option;

public class ModKeysConfigScreen extends Screen {
	protected final Screen parent;
	private Option primary = ModKeysConfig.primary();
	private Option secondary = ModKeysConfig.secondary();
	private Option tertiary = ModKeysConfig.tertiary();
	private final String keyPrefix = MinecraftClient.IS_SYSTEM_MAC ? "config.modkeys.key.osx." : "config.modkeys.key.win.";

	private final Option[] options = Option.values();

	public ModKeysConfigScreen(Screen parent) {
		super(new TranslatableText("config.modkeys.title"));
		this.parent = parent;
	}

	@Override
	public void onClose() {
		client.openScreen(parent);
	}

	@Override
	protected void init() {
		addChild(new ButtonWidget(width / 2 + 5, 100, 120, 20, new TranslatableText(keyPrefix + primary.key), (buttonWidget) -> {
			primary = primary.ordinal() == options.length - 1 ? options[0] : options[primary.ordinal() + 1];
		}) {
			@Override
			public MutableText getMessage() {
				return new TranslatableText(keyPrefix + primary.key);
			}

			@Override
			public void renderToolTip(MatrixStack matrixStack, int i, int j) {
				renderTooltip(matrixStack, new TranslatableText("config.modkeys.help.primary"), i, j);
			}
		});

		method_37063(new ButtonWidget(width / 2 + 5, 130, 120, 20, new TranslatableText(keyPrefix + secondary.key), (buttonWidget) -> {
			secondary = secondary.ordinal() == options.length - 1 ? options[0] : options[secondary.ordinal() + 1];
		}) {
			@Override
			public MutableText getMessage() {
				return new TranslatableText(keyPrefix + secondary.key);
			}

			@Override
			public void renderToolTip(MatrixStack matrixStack, int i, int j) {
				renderTooltip(matrixStack, new TranslatableText("config.modkeys.help.secondary"), i, j);
			}
		});

		method_37063(new ButtonWidget(width / 2 + 5, 160, 120, 20, new TranslatableText(keyPrefix + tertiary.key), (buttonWidget) -> {
			tertiary = tertiary.ordinal() == options.length - 1 ? options[0] : options[tertiary.ordinal() + 1];
		}) {
			@Override
			public MutableText getMessage() {
				return new TranslatableText(keyPrefix + tertiary.key);
			}

			@Override
			public void renderToolTip(MatrixStack matrixStack, int i, int j) {
				renderTooltip(matrixStack, new TranslatableText("config.modkeys.help.tertiary"), i, j);
			}
		});

		method_37063(new ButtonWidget(width / 2 - 155, 200, 150, 20, new TranslatableText("controls.reset"), (buttonWidget) -> {
			primary = Option.CONTROL;
			secondary = Option.ALT;
			tertiary = Option.SUPER;
		}));

		method_37063(new ButtonWidget(width / 2 + 5, 200, 150, 20, new TranslatableText("gui.done"), (buttonWidget) -> {
			ModKeysConfig.saveOptions(primary, secondary, tertiary);
			client.openScreen(parent);
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int i, int j, float f) {
		renderBackground(matrixStack, 0);
		drawCenteredText(matrixStack, textRenderer, title, width / 2, 5, 16777215);
		drawRightAlignedText(matrixStack, new TranslatableText("config.modkeys.label.primary"), width / 2 - 5, 105, 16777215);
		drawRightAlignedText(matrixStack, new TranslatableText("config.modkeys.label.secondary"), width / 2 - 5, 135, 16777215);
		drawRightAlignedText(matrixStack, new TranslatableText("config.modkeys.label.tertiary"), width / 2 - 5, 165, 16777215);

		super.render(matrixStack, i, j, f);
	}

	private void drawRightAlignedText(MatrixStack matrices, Text text, int x, int y, int color) {
		textRenderer.drawWithShadow(matrices, text, x - textRenderer.getWidth(text), y, color);
	}
}
