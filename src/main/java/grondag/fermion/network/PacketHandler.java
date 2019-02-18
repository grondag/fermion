package grondag.fermion.network;

import grondag.exotic_matter.ExoticMatter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(ExoticMatter.MODID);
        
    private static int packetID = 0;

    
    /**
     * Slightly more streamlined version of other routines already available.
     * Uses integer vs. double precision floating point arithmetic and consumes player list directly.
     * Also assumes range is pre-squared, probably a constant value.
     * Expected to be heavily used, thus the attempt to be the efficient.
     */
    public static void sendToPlayersNearPos(IMessage message, int dimension, BlockPos pos, int distanceSquared)
    {
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            if (player.dimension == dimension)
            {
                int dx = pos.getX() - (int)player.posX;
                int dy = pos.getY() - (int)player.posY;
                int dz = pos.getZ() - (int)player.posZ;

                if (dx * dx + dy * dy + dz * dz < distanceSquared)
                {
                    CHANNEL.sendTo(message, player);
                }
            }
        }
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
    {
        CHANNEL.registerMessage(messageHandler, requestMessageType, packetID++, side);
    }
}
