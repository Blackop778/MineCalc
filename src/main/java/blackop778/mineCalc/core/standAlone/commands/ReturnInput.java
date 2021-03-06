package blackop778.mineCalc.core.standAlone.commands;

import blackop778.mineCalc.core.standAlone.ICommandSA;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class ReturnInput implements ICommandSA {

    private static boolean returnInput = true;

    @Nonnull
    @Override
    public String getUsage() {
        return "returnInput <boolean>";
    }

    @Nonnull
    @Override
    public String getTrigger() {
        return "returnInput";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return new ArrayList<String>();
    }

    @Nonnull
    @Override
    public String execute(@Nonnull String[] arguments) {
        if (arguments.length == 0)
            return "Input will " + (returnInput ? "" : "not ") + "be returned";
        else if (arguments[0].equalsIgnoreCase("true") || arguments[0].equalsIgnoreCase("false")) {
            returnInput = Boolean.valueOf(arguments[0]);
            return "Input will " + (returnInput ? "" : "not ") + "be returned";
        } else
            return "Usage: " + getUsage();
    }

    @Nonnull
    @Override
    public String getEffect() {
        return "Determines whether or not 'calc' will prepend the input when returning answers";
    }

    public static boolean getReturnInput() {
        return returnInput;
    }
}
