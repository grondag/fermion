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
//import javax.annotation.Nullable;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.WallStandingBlockItem;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//public class OpenSignItem extends WallStandingBlockItem {
//	public OpenSignItem(Item.Settings settings, Block signBlock, Block wallBlock) {
//		super(signBlock, wallBlock, settings);
//	}
//
//	@Override
//	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState blockState) {
//		final boolean result = super.postPlacement(pos, world, player, stack, blockState);
//
//		if (!world.isClient && !result && player instanceof ServerPlayerEntity && world.getBlockEntity(pos) instanceof OpenSignBlockEntity) {
//			((OpenSignBlockEntity) world.getBlockEntity(pos)).edit((ServerPlayerEntity) player);
//		}
//
//		return result;
//	}
//}
