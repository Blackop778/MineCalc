package Blackop778.MineCalc.client.updateChecker;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class NetworkEventHandler
{
	@SubscribeEvent
	public void clientConnectedToServer(ClientConnectedToServerEvent event)
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		});
	}
}
