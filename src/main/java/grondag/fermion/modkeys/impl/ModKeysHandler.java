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
package grondag.fermion.modkeys.impl;

import io.netty.buffer.Unpooled;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import com.mojang.blaze3d.platform.InputConstants;
import grondag.fermion.modkeys.ModKeysConfig;

public class ModKeysHandler {

	public static ResourceLocation PACKET_ID = new ResourceLocation("modkeys", "modifiers");

	@Environment(EnvType.CLIENT)
	private static byte lastFlags = 0;

	@Environment(EnvType.CLIENT)
	public static void update(Minecraft client) {
		final long handle = client.getWindow().getWindow();

		byte f = 0;

		if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
			f |= ModKeysAccess.SHIFT;
		}

		if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_CONTROL)) {
			f |= ModKeysAccess.CONTROL;
		}

		if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_ALT) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_ALT)) {
			f |= ModKeysAccess.ALT;
		}

		if (InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SUPER) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SUPER)) {
			f |= ModKeysAccess.SUPER;
		}

		if ((f & ModKeysConfig.primary().flag) != 0) {
			f |= ModKeysAccess.PRIMARY;
		}

		if ((f & ModKeysConfig.secondary().flag) != 0) {
			f |= ModKeysAccess.SECONDARY;
		}

		if ((f & ModKeysConfig.tertiary().flag) != 0) {
			f |= ModKeysAccess.TERTIARY;
		}

		if (f != lastFlags) {
			lastFlags = f;
			@SuppressWarnings("resource")
			final LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				((ModKeysAccess) player).mk_flags(f);
			}
			sendUpdatePacket(f);
		}
	}

	@Environment(EnvType.CLIENT)
	private static void sendUpdatePacket(byte flags) {
		if (Minecraft.getInstance().getConnection() != null) {
			final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeByte(flags);
			ClientPlayNetworking.send(PACKET_ID, buf);
		}
	}

	public static void accept(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
		if (player != null) {
			((ModKeysAccess) player).mk_flags(buf.readByte());
		}
	}
}
