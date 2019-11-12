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

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

/** open and extensible implementation of vanilla signs */
public class OpenSignBlock extends AbstractOpenSignBlock {
	public static final IntProperty ROTATION;

	public OpenSignBlock(Block.Settings settings, Supplier<BlockEntity> beSupplier) {
		super(settings, beSupplier);
		setDefaultState(stateFactory.getDefaultState().with(ROTATION, 0).with(WATERLOGGED, false));
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		final FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		return getDefaultState().with(ROTATION, MathHelper.floor(((180.0F + context.getPlayerYaw()) * 16.0F / 360.0F) + 0.5D) & 15).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState blockState, Direction face, BlockState otherState, IWorld iWorld, BlockPos pos, BlockPos otherPos) {
		return face == Direction.DOWN && !canPlaceAt(blockState, iWorld, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(blockState, face, otherState, iWorld, pos, otherPos);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation rotation) {
		return blockState.with(ROTATION, rotation.rotate(blockState.get(ROTATION), 16));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror mirror) {
		return blockState.with(ROTATION, mirror.mirror(blockState.get(ROTATION), 16));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(ROTATION, WATERLOGGED);
	}

	static {
		ROTATION = Properties.ROTATION;
	}
}
