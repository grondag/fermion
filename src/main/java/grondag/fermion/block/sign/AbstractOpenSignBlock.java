// TODO: restore or remove
///*******************************************************************************
// * Copyright 2019 grondag
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License.  You may obtain a copy
// * of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
// * License for the specific language governing permissions and limitations under
// * the License.
// ******************************************************************************/
//package grondag.fermion.block.sign;
//
//import java.util.function.Supplier;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.BlockWithEntity;
//import net.minecraft.block.ShapeContext;
//import net.minecraft.block.Waterloggable;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.fluid.FluidState;
//import net.minecraft.fluid.Fluids;
//import net.minecraft.item.DyeItem;
//import net.minecraft.item.ItemStack;
//import net.minecraft.state.property.BooleanProperty;
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.ActionResult;
//import net.minecraft.util.Hand;
//import net.minecraft.util.hit.BlockHitResult;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.shape.VoxelShape;
//import net.minecraft.world.BlockView;
//import net.minecraft.world.IWorld;
//import net.minecraft.world.World;
//
///** open and extensible implementation of vanilla signs */
//public abstract class AbstractOpenSignBlock extends BlockWithEntity implements Waterloggable {
//	public static final BooleanProperty WATERLOGGED;
//	public static final VoxelShape SHAPE;
//
//	protected Supplier<BlockEntity> beSupplier;
//
//	protected AbstractOpenSignBlock(Block.Settings settings, Supplier<BlockEntity> beSupplier) {
//		super(settings);
//		this.beSupplier = beSupplier;
//	}
//
//	@Override
//	public BlockState getStateForNeighborUpdate(BlockState blockState, Direction face, BlockState otherState, IWorld iWorld, BlockPos pos, BlockPos otherPos) {
//		if (blockState.get(WATERLOGGED)) {
//			iWorld.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
//		}
//
//		return super.getStateForNeighborUpdate(blockState, face, otherState, iWorld, pos, otherPos);
//	}
//
//	@Override
//	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos pos, ShapeContext context) {
//		return SHAPE;
//	}
//
//	@Override
//	public boolean canMobSpawnInside() {
//		return true;
//	}
//
//	@Override
//	public BlockEntity createBlockEntity(BlockView blockView) {
//		return beSupplier.get();
//	}
//
//	@Override
//	public ActionResult onUse(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//		final ItemStack stack = player.getStackInHand(hand);
//		final boolean isRecolor = stack.getItem() instanceof DyeItem && player.abilities.allowModifyWorld;
//
//		if (world.isClient) {
//			return isRecolor ? ActionResult.SUCCESS : ActionResult.CONSUME;
//		} else {
//			final BlockEntity be = world.getBlockEntity(pos);
//
//			if (be instanceof OpenSignBlockEntity) {
//				final OpenSignBlockEntity myBe = (OpenSignBlockEntity)be;
//
//				if (stack.getItem() instanceof DyeItem && player.abilities.allowModifyWorld) {
//					final boolean canDye = myBe.setTextColor(((DyeItem)stack.getItem()).getColor());
//
//					if (canDye && !player.isCreative()) {
//						stack.decrement(1);
//					}
//				}
//
//				return myBe.onActivate(player) ? ActionResult.SUCCESS : ActionResult.PASS;
//			} else {
//				return ActionResult.PASS;
//			}
//		}
//	}
//
//	@Override
//	public FluidState getFluidState(BlockState blockState) {
//		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
//	}
//
//	static {
//		WATERLOGGED = Properties.WATERLOGGED;
//		SHAPE = Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
//	}
//}
