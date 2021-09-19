package grondag.fermion.gui.control;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import grondag.fermion.gui.ScreenRenderContext;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractParentControl <T extends AbstractParentControl<T>> extends AbstractControl<T> implements ContainerEventHandler {
	@Nullable
	private GuiEventListener focused;
	private boolean isDragging;
	protected ArrayList<AbstractControl<?>> children = new ArrayList<>();

	public AbstractParentControl(ScreenRenderContext renderContext) {
		super(renderContext);
	}

	@Override
	public final boolean isDragging() {
		return this.isDragging;
	}

	@Override
	public final void setDragging(boolean dragging) {
		this.isDragging = dragging;
	}

	@Override
	@Nullable
	public GuiEventListener getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(@Nullable GuiEventListener element) {
		this.focused = element;
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return children;
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		ContainerEventHandler.super.mouseClicked(mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		ContainerEventHandler.super.mouseDragged(mouseX, mouseY, clickedMouseButton, dx, dy);
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}
}
