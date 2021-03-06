package blackop778.mineCalc.client;

import blackop778.mineCalc.MineCalc;
import blackop778.mineCalc.common.CommonEventHandlers;
import blackop778.mineCalc.common.MCConfig;
import blackop778.mineCalc.common.net.HasModMessage;
import blackop778.mineCalc.common.net.NetHub;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import javax.annotation.Nonnull;

public class ClientEventHandlers extends CommonEventHandlers {

    public static class SubEvent {
        @SubscribeEvent
        public void onPlayerJoinedServer(@Nonnull EntityJoinWorldEvent event) {
            if (event.getEntity() instanceof EntityPlayer) {
                if (event.getEntity().equals(Minecraft.getMinecraft().player)) {
                    NetHub.NETWORKWRAPPER.sendToServer(new HasModMessage());
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        }
    }

    @SubscribeEvent
    // Occurs on server thread
    public void onPlayerLeftServer(ClientDisconnectionFromServerEvent event) {
        MinecraftForge.EVENT_BUS.register(new SubEvent());
    }

    @SubscribeEvent
    public void onConfigChanged(@Nonnull OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(MineCalc.MODID)) {
            MCConfig.syncConfig();
        }
    }
}
