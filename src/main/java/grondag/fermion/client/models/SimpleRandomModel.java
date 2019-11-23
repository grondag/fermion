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

package grondag.fermion.client.models;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.HashCommon;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.SpriteIdentifier;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public class SimpleRandomModel extends SimpleModel {
	protected final Sprite[] sprites;
	protected final Renderer renderer = RendererAccess.INSTANCE.getRenderer();
	protected final RenderMaterial material = renderer.materialFinder().find();
	protected final int maxTextureIndex;

	public SimpleRandomModel(Function<SpriteIdentifier, Sprite> spriteMap, List<SpriteIdentifier> textures) {
		super(spriteMap.apply(textures.get(0)), ModelHelper.MODEL_TRANSFORM_BLOCK);
		final int textureCount = textures.size();
		maxTextureIndex = textureCount - 1;
		sprites = new Sprite[textureCount];
		for (int i = 0; i < textureCount; i++) {
			sprites[i] = spriteMap.apply(textures.get(i));
		}
	}

	@Override
	public final void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		final QuadEmitter qe = context.getEmitter();
		final long bits = HashCommon.mix(pos.asLong());
		emitQuads(qe, bits);
	}

	@Override
	protected Mesh createMesh() {
		final MeshBuilder mb = renderer.meshBuilder();
		emitQuads(mb.getEmitter(), 0);
		return mb.build();
	}

	protected final void emitQuads(QuadEmitter qe, long bits) {
		emitFace(qe, Direction.UP, (int) bits);

		bits >>= 8;
		emitFace(qe, Direction.DOWN, (int) bits);

		bits >>= 8;
		emitFace(qe, Direction.EAST, (int) bits);

		bits >>= 8;
		emitFace(qe, Direction.WEST, (int) bits);

		bits >>= 8;
		emitFace(qe, Direction.NORTH, (int) bits);

		bits >>= 8;
		emitFace(qe, Direction.SOUTH, (int) bits);
	}

	protected void emitFace(QuadEmitter qe, Direction face, int bits) {
		final int texture = Math.min(maxTextureIndex, bits & 3);
		final int rotation = (bits >> 2) & 3;

		qe.material(material)
		.square(face, 0, 0, 1, 1, 0)
		.spriteColor(0, -1, -1, -1, -1)
		.spriteBake(0, sprites[texture], MutableQuadView.BAKE_LOCK_UV + rotation);
		SimpleModels.contractUVs(0, sprites[texture], qe);
		qe.emit();
	}
}
