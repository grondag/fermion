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

package grondag.fermion.registrar;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import com.google.common.collect.ImmutableList;

public abstract class AbstractRegistrar {
	public final String modId;

	protected AbstractRegistrar(String modId) {
		this.modId = modId;
	}

	public ResourceLocation id(String name) {
		return new ResourceLocation(modId, name);
	}

	public List<ResourceLocation> idList(String... ids) {
		final ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
		for (final String id : ids) {
			builder.add(id(id));
		}
		return builder.build();
	}
}
