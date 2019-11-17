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
import java.util.Arrays;

import grondag.fermion.gui.ScreenRenderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VisibilityPanel extends Panel {

	private final ArrayList<ArrayList<AbstractControl<?>>> groups = new ArrayList<ArrayList<AbstractControl<?>>>();

	private final ArrayList<String> labels = new ArrayList<String>();

	private int visiblityIndex = VisiblitySelector.NO_SELECTION;

	public VisibilityPanel(ScreenRenderContext renderContext, boolean isVertical) {
		super(renderContext, isVertical);
	}

	public int getVisiblityIndex() {
		return visiblityIndex;
	}

	public void setVisiblityIndex(int visiblityIndex) {
		this.visiblityIndex = visiblityIndex;
		children = groups.get(visiblityIndex);
		isDirty = true;
		refreshContentCoordinatesIfNeeded();
	}

	/**
	 * Creates a new visibility group with the given caption and returns its index.
	 * Must call this before adding controls using the index.
	 */
	public int createVisiblityGroup(String label) {
		labels.add(label);
		groups.add(new ArrayList<AbstractControl<?>>());
		return labels.size() - 1;
	}

	public VisibilityPanel addAll(int visibilityIndex, AbstractControl<?>... controls) {
		groups.get(visibilityIndex).addAll(Arrays.asList(controls));
		isDirty = true;
		return this;
	}

	public VisibilityPanel add(int visibilityIndex, AbstractControl<?> control) {
		groups.get(visibilityIndex).add(control);
		isDirty = true;
		return this;
	}

	public VisibilityPanel remove(int visibilityIndex, int controlindex) {
		groups.get(visibilityIndex).remove(controlindex);
		isDirty = true;
		return this;
	}

	public String getLabel(int visiblityIndex) {
		return labels.get(visiblityIndex);
	}

	public int size() {
		return labels.size();
	}
}
