package grondag.fermion.modkeys;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
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
	public void removed() {
		client.options.write();
	}

	@Override
	public void onClose() {
		client.openScreen(parent);
	}

	@Override
	protected void init() {
		addButton(new ButtonWidget(width / 2 + 5, 100, 120, 20, I18n.translate(keyPrefix + primary.key), (buttonWidget) -> {
			primary = primary.ordinal() == options.length - 1 ? options[0] : options[primary.ordinal() + 1];
			buttonWidget.queueNarration(250);
		}) {
			@Override
			public String getMessage() {
				return I18n.translate(keyPrefix + primary.key);
			}

			@Override
			public void renderToolTip(int i, int j) {
				renderTooltip(I18n.translate("config.modkeys.help.primary"), i, j);
			}
		});

		addButton(new ButtonWidget(width / 2 + 5, 130, 120, 20, I18n.translate(keyPrefix + secondary.key), (buttonWidget) -> {
			secondary = secondary.ordinal() == options.length - 1 ? options[0] : options[secondary.ordinal() + 1];
			buttonWidget.queueNarration(250);
		}) {
			@Override
			public String getMessage() {
				return I18n.translate(keyPrefix + secondary.key);
			}

			@Override
			public void renderToolTip(int i, int j) {
				renderTooltip(I18n.translate("config.modkeys.help.secondary"), i, j);
			}
		});

		addButton(new ButtonWidget(width / 2 + 5, 160, 120, 20, I18n.translate(keyPrefix + tertiary.key), (buttonWidget) -> {
			tertiary = tertiary.ordinal() == options.length - 1 ? options[0] : options[tertiary.ordinal() + 1];
			buttonWidget.queueNarration(250);
		}) {
			@Override
			public String getMessage() {
				return I18n.translate(keyPrefix + tertiary.key);
			}

			@Override
			public void renderToolTip(int i, int j) {
				renderTooltip(I18n.translate("config.modkeys.help.tertiary"), i, j);
			}
		});

		addButton(new ButtonWidget(width / 2 - 155, 200, 150, 20, I18n.translate("controls.reset"), (buttonWidget) -> {
			primary = Option.CONTROL;
			secondary = Option.ALT;
			tertiary = Option.SUPER;
		}));

		addButton(new ButtonWidget(width / 2 + 5, 200, 150, 20, I18n.translate("gui.done"), (buttonWidget) -> {
			ModKeysConfig.saveOptions(primary, secondary, tertiary);
			client.openScreen(parent);
		}));
	}

	@Override
	public void render(int i, int j, float f) {
		renderDirtBackground(0);
		drawCenteredString(textRenderer, title.asFormattedString(), width / 2, 5, 16777215);
		drawString(textRenderer, I18n.translate("config.modkeys.label.primary"), width / 2 - 5, 105, 16777215);
		drawString(textRenderer, I18n.translate("config.modkeys.label.secondary"), width / 2 - 5, 135, 16777215);
		drawString(textRenderer, I18n.translate("config.modkeys.label.tertiary"), width / 2 - 5, 165, 16777215);

		super.render(i, j, f);
	}
}
