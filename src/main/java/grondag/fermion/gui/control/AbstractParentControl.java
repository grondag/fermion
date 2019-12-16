package grondag.fermion.gui.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;

import grondag.fermion.gui.ScreenRenderContext;

public abstract class AbstractParentControl <T extends AbstractParentControl<T>> extends AbstractControl<T> implements ParentElement {
	@Nullable
	private Element focused;
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
	public Element getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(@Nullable Element element) {
		this.focused = element;
	}

	@Override
	public List<? extends Element> children() {
		return children;
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		ParentElement.super.mouseClicked(mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		ParentElement.super.mouseDragged(mouseX, mouseY, clickedMouseButton, dx, dy);
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		ParentElement.super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}
}
