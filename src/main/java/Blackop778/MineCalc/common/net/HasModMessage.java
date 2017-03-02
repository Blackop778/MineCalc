package Blackop778.MineCalc.common.net;

import Blackop778.MineCalc.MineCalc;
import Blackop778.MineCalc.client.ClientProxy;
import Blackop778.MineCalc.server.HasMineCalcProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HasModMessage implements IMessage {

    public HasModMessage() {

    }

    public static class HasModMessageHandler implements IMessageHandler<HasModMessage, IMessage> {

	@Override
	public IMessage onMessage(HasModMessage message, MessageContext ctx) {
	    if (!ClientProxy.isClientSide()) {
		NetHandlerPlayServer server = ctx.getServerHandler();
		ctx.getServerHandler().playerEntity.getCapability(HasMineCalcProvider.HMC_CAP, null)
			.setHasMineCalc(true);
		MineCalc.LOGGER.info(server.playerEntity.getDisplayNameString() + " sent HasModMessage");
	    }
	    return null;
	}

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

}
