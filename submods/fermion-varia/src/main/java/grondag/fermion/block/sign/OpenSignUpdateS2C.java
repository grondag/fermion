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

import grondag.fermion.Fermion;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public enum OpenSignUpdateS2C {
	;

	public static void updateSignS2C(PlayerEntity player, OpenSignBlockEntity be) {
		System.out.println("updateSignS2C");
		final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(BlockEntityType.getId(be.getType()));
		buf.writeBlockPos(be.getPos());
		buf.writeString(be.getTextOnRow(0).getString());
		buf.writeString(be.getTextOnRow(1).getString());
		buf.writeString(be.getTextOnRow(2).getString());
		buf.writeString(be.getTextOnRow(3).getString());

		System.out.println("updateSignS2C getText0: " + be.getTextOnRow(0).getString());

		final Packet<?> packet = ServerSidePacketRegistry.INSTANCE.toPacket(S2C_ID, buf);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet);
	}

	@SuppressWarnings("unchecked")
	public static void handleS2C(PacketContext context, PacketByteBuf buffer) {
		System.out.println("handleS2C");
		final Identifier id = buffer.readIdentifier();
		final BlockPos pos = buffer.readBlockPos();
		final String tex0 = buffer.readString();
		final String tex1 = buffer.readString();
		final String tex2 = buffer.readString();
		final String tex3 = buffer.readString();
		
		context.getTaskQueue().execute(() -> {
			BlockEntity be = context.getPlayer().world.getBlockEntity(pos);
			if (!(be instanceof OpenSignBlockEntity)) {
				be = new OpenSignBlockEntity((BlockEntityType<OpenSignBlockEntity>) Registry.BLOCK_ENTITY.get(id));
				be.setWorld(context.getPlayer().world);
				be.setPos(pos);
			}
			
			((OpenSignBlockEntity) be).setText(tex0, tex1, tex2, tex3);
			
			System.out.println("handleS2C getText0: " + ((OpenSignBlockEntity) be).getTextOnRow(0).getString());
			
			MinecraftClient.getInstance().openScreen(new OpenSignEditScreen((OpenSignBlockEntity) be));
		});
	}

	public static Identifier S2C_ID = new Identifier(Fermion.MOD_ID + ":sign_sc");
}
