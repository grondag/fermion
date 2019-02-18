package grondag.fermion.network;

import javax.annotation.Nullable;

import grondag.exotic_matter.serialization.IMessagePlus;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractServerToPlayerPacket<T extends IMessagePlus> implements IMessageHandler<T, IMessage>, IMessagePlus
{
    @Override
    public IMessage onMessage(final @Nullable T message, @Nullable MessageContext context) 
    {
        FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> handle(message, context));
        return null;
    }

    protected abstract void handle(T message, MessageContext context);
}
