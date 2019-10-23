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

import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

/** open and extensible implementation of vanilla signs */
public class OpenWallSignBlock extends AbstractOpenSignBlock {
	public static final DirectionProperty FACING;
	public static final Map<Direction, VoxelShape> FACING_TO_SHAPE;

	public OpenWallSignBlock(Block.Settings settings, Supplier<BlockEntity> beSupplier) {
		super(settings, beSupplier);
		setDefaultState(stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

	@Override
	public String getTranslationKey() {
		return asItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos pos, EntityContext context) {
		return FACING_TO_SHAPE.get(blockState.get(FACING));
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld world, BlockPos pos) {
		return world.getBlockState(pos.offset(blockState.get(FACING).getOpposite())).getMaterial().isSolid();
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = this.getDefaultState();
		final FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
		final ViewableWorld world = context.getWorld();
		final BlockPos pos = context.getBlockPos();
		final Direction[] faces = context.getPlacementDirections();
		final int limit = faces.length;

		for(int i = 0; i < limit; ++i) {
			final Direction face = faces[i];

			if (face.getAxis().isHorizontal()) {
				final Direction opposite = face.getOpposite();
				blockState = blockState.with(FACING, opposite);

				if (blockState.canPlaceAt(world, pos)) {
					return blockState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState blockState, Direction face, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos) {
		return face.getOpposite() == blockState.get(FACING) && !blockState.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(blockState, face, otherState, world, pos, otherPos);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	static {
		FACING = HorizontalFacingBlock.FACING;
		FACING_TO_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.createCuboidShape(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.EAST, Block.createCuboidShape(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), Direction.WEST, Block.createCuboidShape(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));
	}
}
