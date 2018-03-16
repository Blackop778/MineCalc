package blackop778.mineCalc.common.net;

import blackop778.mineCalc.client.ClientProxy;
import blackop778.mineCalc.common.MineCalcCompoundProvider;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HasModMessage implements IMessage {

    public HasModMessage() {

    }

    public static class HasModMessageHandler implements IMessageHandler<HasModMessage, IMessage> {

        @Nullable
        @Override
        public IMessage onMessage(HasModMessage message, @Nonnull MessageContext ctx) {
            if (!ClientProxy.isClientSide()) {
                ctx.getServerHandler().playerEntity.getCapability(MineCalcCompoundProvider.MCC_CAP, null)
                        .setHasMineCalc(true);
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
