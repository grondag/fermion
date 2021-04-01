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
//import io.netty.buffer.Unpooled;
//
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.network.Packet;
//import net.minecraft.network.PacketByteBuf;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.text.LiteralText;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.BlockPos;
//
//import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
//import net.fabricmc.fabric.api.network.PacketContext;
//
//import grondag.fermion.Fermion;
//
//public enum OpenSignUpdateC2S {
//	;
//
//	public static void updateSignC2S(BlockPos pos, Text text1, Text text2, Text text3, Text text4) {
//		System.out.println("updateSignC2S");
//		final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
//		buf.writeBlockPos(pos);
//		buf.writeString(text1.getString());
//		buf.writeString(text2.getString());
//		buf.writeString(text3.getString());
//		buf.writeString(text4.getString());
//		final Packet<?> packet = ClientSidePacketRegistry.INSTANCE.toPacket(C2S_ID, buf);
//		ClientSidePacketRegistry.INSTANCE.sendToServer(packet);
//	}
//
//	public static void handleC2S(PacketContext context, PacketByteBuf buffer) {
//		System.out.println("handleC2S");
//		final ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
//		final BlockPos pos = buffer.readBlockPos();
//		final String tex0 = buffer.readString();
//		final String tex1 = buffer.readString();
//		final String tex2 = buffer.readString();
//		final String tex3 = buffer.readString();
//
//		context.getTaskQueue().execute(() -> {
//			final MinecraftServer server = player.server;
//			final ServerWorld world = server.getWorld(player.dimension);
//			player.updateLastActionTime();
//
//			if (world.isChunkLoaded(pos)) {
//				final BlockEntity be = world.getBlockEntity(pos);
//
//				if (!(be instanceof OpenSignBlockEntity)) {
//					return;
//				}
//
//				final OpenSignBlockEntity myBe = (OpenSignBlockEntity)be;
//
//				if (!myBe.isEditable() || myBe.getEditor() != player) {
//					server.sendSystemMessage(new LiteralText("Player " + player.getName().getString() + " just tried to change non-editable sign"));
//					return;
//				}
//
//				myBe.setText(tex0, tex1, tex2, tex3);
//				myBe.markDirty();
//				myBe.sync();
//			}
//		});
//
//	}
//
//	public static Identifier C2S_ID = new Identifier(Fermion.MOD_ID + ":sign_cs");
//}
