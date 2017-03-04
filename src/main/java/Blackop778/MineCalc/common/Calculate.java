package Blackop778.MineCalc.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import Blackop778.MineCalc.MineCalc;
import Blackop778.MineCalc.client.ClientProxy;
import Blackop778.MineCalc.core.CalcExceptions;
import Blackop778.MineCalc.core.CalcExceptions.AllStandinsUsedException;
import Blackop778.MineCalc.core.CalcExceptions.CustomFunctionException;
import Blackop778.MineCalc.core.CalcExceptions.DivisionException;
import Blackop778.MineCalc.core.CalcExceptions.FancyRemainderException;
import Blackop778.MineCalc.core.CalcExceptions.ImaginaryNumberException;
import Blackop778.MineCalc.core.CalcExceptions.InvalidNumberException;
import Blackop778.MineCalc.core.CalcExceptions.MultiplePointsException;
import Blackop778.MineCalc.core.CalcExceptions.OperatorException;
import Blackop778.MineCalc.core.CalcExceptions.ParenthesisException;
import Blackop778.MineCalc.core.CalcExceptions.PreviousOutputException;
import Blackop778.MineCalc.core.CalcExceptions.UsageException;
import Blackop778.MineCalc.core.Calculator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class Calculate extends CommandBase {

    public static final Style redStyle = new Style().setColor(TextFormatting.RED);

    @Override
    public String getCommandName() {
	// What must be typed in following the / to trigger the command
	return "calc";
    }

    /**
     * What is shown when "/help Calculate" is typed in
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
	if (playerHasMod(sender.getCommandSenderEntity()))
	    return "minecalc.calc.help";
	else
	    return I18n.translateToLocal("minecalc.calc.help");
    }

    public ITextComponent calculate(MinecraftServer server, ICommandSender sender, String[] args) {
	ITextComponent print = null;
	boolean useOOPS;
	double answer;

	if (args.length == 0)
	    return new TextComponentTranslation("minecalc.calc.usage").setStyle(redStyle);
	useOOPS = Boolean.valueOf(args[0]);
	if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false")) {
	    args[0] = "";
	}
	String condensedMath = "";
	for (String s : args) {
	    condensedMath += s;
	}
	try {
	    answer = Calculator.evaluate(condensedMath, useOOPS, getLastOutput(sender));
	    setLastOutput(sender, answer);
	    if (answer % 1 == 0) {
		int i = (int) answer;
		print = new TextComponentString(String.valueOf(i));
	    } else {
		print = new TextComponentString(String.valueOf(answer));
	    }
	} catch (ImaginaryNumberException e) {
	    return new TextComponentTranslation("minecalc.calc.imaginaryException").setStyle(redStyle);
	} catch (DivisionException er) {
	    return new TextComponentTranslation("minecalc.calc.divZeroException").setStyle(redStyle);
	} catch (OperatorException err) {
	    return new TextComponentTranslation("minecalc.calc.symbolException").setStyle(redStyle)
		    .appendSibling(new TextComponentString(" %"))
		    .appendSibling(new TextComponentTranslation("minecalc.calc.symbolExceptionPartTwo"));
	} catch (PreviousOutputException erro) {
	    return new TextComponentTranslation("minecalc.calc.previousOutputException").setStyle(redStyle);
	} catch (CustomFunctionException error) {
	    return new TextComponentString(error.getMessage()).setStyle(redStyle);
	} catch (FancyRemainderException errors) {
	    int num1 = (int) (errors.numerator / errors.denominator);
	    double num2 = errors.numerator % errors.denominator;
	    print = new TextComponentString(num1 + "R");
	    if (num2 % 1 == 0) {
		int i = (int) num2;
		print.appendSibling(new TextComponentString(String.valueOf(i)));
	    } else {
		print.appendSibling(new TextComponentString(String.valueOf(num2)));
	    }
	    setLastOutput(sender, num2);
	} catch (AllStandinsUsedException errorsA) {
	    return new TextComponentTranslation("minecalc.calc.standInsException").setStyle(redStyle)
		    .appendSibling(new TextComponentString(errorsA.getLocalizedMessage()));
	} catch (MultiplePointsException errorsAr) {
	    return new TextComponentTranslation("minecalc.calc.multiplePointsException").setStyle(redStyle);
	} catch (UsageException errorsAre) {
	    return new TextComponentTranslation("minecalc.calc.usage").setStyle(redStyle);
	} catch (InvalidNumberException errorsAreF) {
	    return new TextComponentTranslation("minecalc.calc.numberFormatException").setStyle(redStyle);
	} catch (ParenthesisException errorsAreFu) {
	    if (errorsAreFu.tooMany)
		return new TextComponentTranslation("minecalc.calc.manyParenthesisException").setStyle(redStyle);
	    else
		return new TextComponentTranslation("minecalc.calc.fewParenthesisException").setStyle(redStyle);
	} catch (CalcExceptions errorsAreFun) {
	    errorsAreFun.printStackTrace();
	    return new TextComponentString(
		    "Error: An unknown error occured" + (errorsAreFun.getLocalizedMessage() == null ? ""
			    : ". Message: " + errorsAreFun.getLocalizedMessage()));
	}

	// Prepend the arguments to the output, if configured to
	if (MCConfig.returnInput) {
	    print = new TextComponentString(condensedMath + "=").appendSibling(print);
	}

	return print;
    }

    /**
     * @param sender
     *            Can be null
     * @return The last output the user received
     * @throws PreviousOutputException
     *             If there was no previous output
     */
    private double getLastOutput(ICommandSender sender) throws PreviousOutputException {
	if (sender == null)
	    if (Calculator.consoleLastOutput == null)
		throw new PreviousOutputException();
	    else
		return Calculator.consoleLastOutput;
	else {
	    Entity e = sender.getCommandSenderEntity();
	    if (e == null)
		if (Calculator.consoleLastOutput == null)
		    throw new PreviousOutputException();
		else
		    return Calculator.consoleLastOutput;
	    IMineCalcCompound comp = e.getCapability(MineCalcCompoundProvider.MCC_CAP, null);
	    Double last = comp.getLastNumber();
	    if (last.isNaN())
		throw new PreviousOutputException();
	    else
		return last;
	}
    }

    private void setLastOutput(ICommandSender sender, double newOutput) {
	if (sender == null)
	    Calculator.consoleLastOutput = newOutput;
	else {
	    Entity e = sender.getCommandSenderEntity();
	    if (e == null)
		Calculator.consoleLastOutput = newOutput;
	    else {
		IMineCalcCompound comp = e.getCapability(MineCalcCompoundProvider.MCC_CAP, null);
		comp.setLastNumber(newOutput);
	    }
	}
    }

    @Override
    public List<String> getCommandAliases() {
	// A list of alternate command names
	List<String> aliases = new ArrayList<String>(Arrays.asList("Calc", "calculate", "Calculate"));
	return aliases;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
	    BlockPos pos) {
	if (args.length % 2 != 1) {
	    ArrayList<String> options = new ArrayList<String>(Arrays.asList("+", "-", "*", "/", "%", "^", "/--"));
	    return options;
	} else
	    return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	if (sender.getName().equals("@")) {
	    MineCalc.LOGGER.warn("Command blocks cannot use /calc");
	} else {
	    ITextComponent output = calculate(server, sender, args);
	    output = translateCheck(sender, output);

	    // Send the message back to the user
	    if (sender.getName().equals("Server")) {
		MineCalc.LOGGER.info(output.getUnformattedText());
	    } else {
		EntityPlayer player = (EntityPlayer) sender;
		player.addChatMessage(output);
	    }
	}
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
	return true;
    }

    private ITextComponent translateCheck(ICommandSender sender, ITextComponent toOutput) {
	if (playerHasMod(sender.getCommandSenderEntity()))
	    return toOutput;
	else {
	    Iterator<ITextComponent> it = toOutput.iterator();
	    ITextComponent toReturn = null;
	    while (it.hasNext()) {
		ITextComponent current = it.next();
		if (current instanceof TextComponentString) {
		    if (toReturn == null) {
			toReturn = new TextComponentString(current.getUnformattedText()).setStyle(current.getStyle());
		    } else {
			toReturn.appendSibling(new TextComponentString(current.getUnformattedComponentText())
				.setStyle(current.getStyle()));
		    }
		} else {
		    TextComponentTranslation tran = (TextComponentTranslation) current;
		    String text = I18n.translateToLocal(tran.getKey());
		    if (toReturn == null) {
			toReturn = new TextComponentString(text).setStyle(tran.getStyle());
		    } else {
			toReturn.appendSibling(new TextComponentString(text).setStyle(tran.getStyle()));
		    }
		}
	    }

	    return toReturn;
	}
    }

    private boolean playerHasMod(Entity ent) {
	if (ClientProxy.isClientSide())
	    return true;
	if (ent == null)
	    return true;
	return ent.getCapability(MineCalcCompoundProvider.MCC_CAP, null).getHasMineCalc();
    }
}
