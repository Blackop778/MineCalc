package Blackop778.MineCalc;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Blackop778.MineCalc.common.CommonProxy;
import Blackop778.MineCalc.common.IFunction;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = MineCalc.MODID, name = MineCalc.MODNAME, version = MineCalc.MODVER, acceptableRemoteVersions = "*", guiFactory = "Blackop778.MineCalc.client.config.GuiFactoryMineCalc", updateJSON = MineCalc.UPDATEJSONURL)
public class MineCalc {
    public static final String MODID = "minecraftcalculator778";
    public static final String MODNAME = "MineCalc";
    public static final String MODVER = "3.1.4";
    public static final Logger Logger = LogManager.getLogger(MODID);
    public static final String UPDATEJSONURL = "https://raw.githubusercontent.com/Blackop778/ModUpdateJsons/master/MineCalc.json";
    public static ArrayList<IFunction> functions = new ArrayList<IFunction>();

    @SidedProxy(clientSide = "Blackop778.MineCalc.client.ClientProxy", serverSide = "Blackop778.MineCalc.common.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
	proxy.preInit(event);
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
	proxy.load(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
	proxy.postInit(event);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
	proxy.serverStart(event);
    }
}
