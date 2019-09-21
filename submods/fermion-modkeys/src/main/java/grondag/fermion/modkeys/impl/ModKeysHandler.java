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

import org.lwjgl.glfw.GLFW;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class ModKeysHandler {

    public static Identifier PACKET_ID = new Identifier("modkeys", "modifiers");

    @Environment(EnvType.CLIENT)
    private static byte lastFlags = 0;

    @Environment(EnvType.CLIENT)
    public static void update(MinecraftClient client) {
        final long handle = client.method_22683().getHandle();
        
        byte f = 0;
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            f |= ModKeysAccess.SHIFT;
        }
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            f |= ModKeysAccess.CONTROL;
        }
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_ALT)) {
            f |= ModKeysAccess.ALT;
        }
        if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT_SUPER) || InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT_SUPER)) {
            f |= ModKeysAccess.SUPER;
        }

        if (f != lastFlags) {
            lastFlags = f;
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                ((ModKeysAccess) player).mk_flags(f);
            }
            sendUpdatePacket(f);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void sendUpdatePacket(byte flags) {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte(flags);
        ClientSidePacketRegistry.INSTANCE.sendToServer(PACKET_ID, buf);
    }

    public static void accept(PacketContext context, PacketByteBuf buf) {
        final PlayerEntity player = context.getPlayer();
        if (player != null) {
            ((ModKeysAccess) player).mk_flags(buf.readByte());
        }
    }
}
