package blackop778.mineCalc.core.standAlone.commands;

import blackop778.mineCalc.core.standAlone.ICommandSA;

import java.util.ArrayList;
import java.util.List;

public class Version implements ICommandSA {

    public static final String version = "4.0.0";

    @Override
    public String getUsage() {
        return "version";
    }

    @Override
    public String getTrigger() {
        return "version";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<String>();
    }

    @Override
    public String execute(String[] arguments) {
        return version;
    }

    @Override
    public String getEffect() {
        return "Returns the version of MineCalc running";
    }

}
