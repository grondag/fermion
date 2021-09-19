package grondag.fermion.modkeys;

import com.mojang.blaze3d.vertex.PoseStack;
import grondag.fermion.modkeys.ModKeysConfig.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ModKeysConfigScreen extends Screen {
	protected final Screen parent;
	private Option primary = ModKeysConfig.primary();
	private Option secondary = ModKeysConfig.secondary();
	private Option tertiary = ModKeysConfig.tertiary();
	private final String keyPrefix = Minecraft.ON_OSX ? "config.modkeys.key.osx." : "config.modkeys.key.win.";

	private final Option[] options = Option.values();

	public ModKeysConfigScreen(Screen parent) {
		super(new TranslatableComponent("config.modkeys.title"));
		this.parent = parent;
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	@Override
	protected void init() {
		addRenderableWidget(new Button(width / 2 + 5, 100, 120, 20, new TranslatableComponent(keyPrefix + primary.key), (buttonWidget) -> {
			primary = primary.ordinal() == options.length - 1 ? options[0] : options[primary.ordinal() + 1];
		}) {
			@Override
			public MutableComponent getMessage() {
				return new TranslatableComponent(keyPrefix + primary.key);
			}

			@Override
			public void renderToolTip(PoseStack matrixStack, int i, int j) {
				ModKeysConfigScreen.this.renderTooltip(matrixStack, new TranslatableComponent("config.modkeys.help.primary"), i, j);
			}
		});

		addRenderableWidget(new Button(width / 2 + 5, 130, 120, 20, new TranslatableComponent(keyPrefix + secondary.key), (buttonWidget) -> {
			secondary = secondary.ordinal() == options.length - 1 ? options[0] : options[secondary.ordinal() + 1];
		}) {
			@Override
			public MutableComponent getMessage() {
				return new TranslatableComponent(keyPrefix + secondary.key);
			}

			@Override
			public void renderToolTip(PoseStack matrixStack, int i, int j) {
				ModKeysConfigScreen.this.renderTooltip(matrixStack, new TranslatableComponent("config.modkeys.help.secondary"), i, j);
			}
		});

		addRenderableWidget(new Button(width / 2 + 5, 160, 120, 20, new TranslatableComponent(keyPrefix + tertiary.key), (buttonWidget) -> {
			tertiary = tertiary.ordinal() == options.length - 1 ? options[0] : options[tertiary.ordinal() + 1];
		}) {
			@Override
			public MutableComponent getMessage() {
				return new TranslatableComponent(keyPrefix + tertiary.key);
			}

			@Override
			public void renderToolTip(PoseStack matrixStack, int i, int j) {
				ModKeysConfigScreen.this.renderTooltip(matrixStack, new TranslatableComponent("config.modkeys.help.tertiary"), i, j);
			}
		});

		addRenderableWidget(new Button(width / 2 - 155, 200, 150, 20, new TranslatableComponent("controls.reset"), (buttonWidget) -> {
			primary = Option.CONTROL;
			secondary = Option.ALT;
			tertiary = Option.SUPER;
		}));

		addRenderableWidget(new Button(width / 2 + 5, 200, 150, 20, new TranslatableComponent("gui.done"), (buttonWidget) -> {
			ModKeysConfig.saveOptions(primary, secondary, tertiary);
			minecraft.setScreen(parent);
		}));
	}

	@Override
	public void render(PoseStack matrixStack, int i, int j, float f) {
		renderBackground(matrixStack, 0);
		drawCenteredString(matrixStack, font, title, width / 2, 5, 16777215);
		drawRightAlignedText(matrixStack, new TranslatableComponent("config.modkeys.label.primary"), width / 2 - 5, 105, 16777215);
		drawRightAlignedText(matrixStack, new TranslatableComponent("config.modkeys.label.secondary"), width / 2 - 5, 135, 16777215);
		drawRightAlignedText(matrixStack, new TranslatableComponent("config.modkeys.label.tertiary"), width / 2 - 5, 165, 16777215);

		super.render(matrixStack, i, j, f);
	}

	private void drawRightAlignedText(PoseStack matrices, Component text, int x, int y, int color) {
		font.drawShadow(matrices, text, x - font.width(text), y, color);
	}
}
