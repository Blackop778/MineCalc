package Blackop778.MineCalc.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import Blackop778.MineCalc.MineCalc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
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
public class Calculate extends CommandBase
{

	static HashMap<String, Double> lastMap = new HashMap<String, Double>();
	public static final Style redStyle = new Style().setColor(TextFormatting.RED);

	@Override
	public String getCommandName()
	{
		// What must be typed in following the / to trigger the command
		return "calc";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		// What is shown when "/help Calculate" is typed in
		return I18n.translateToLocal("minecalc.calc.help");
	}

	public ITextComponent calculate(ICommandSender icommandsender, String[] arguments)
	{
		// What happens when command is entered
		ITextComponent toReturn = null;
		boolean zeroPower = false;
		boolean zeroMult = false;
		if((arguments.length - 1) % 2 == 0 && arguments.length > 1)
		{
			try
			{
				// Get the first number to give us a starting place
				double number = getDouble(icommandsender, arguments, 0);
				// Process all the inputs, check for errors, and print back to
				// user
				for(int i = 1; i < arguments.length; i++)
				{

					if(arguments[i].equals("+"))
					{
						i++;
						number = number + getDouble(icommandsender, arguments, i);
					}
					else if(arguments[i].equals("-"))
					{
						i++;
						number = number - getDouble(icommandsender, arguments, i);
					}
					else if(arguments[i].equals("*") || arguments[i].equalsIgnoreCase("x"))
					{
						i++;
						number = number * getDouble(icommandsender, arguments, i);
						if(getDouble(icommandsender, arguments, i) == 0 && MCConfig.zeroMultWarns)
						{
							zeroMult = true;
						}
					}
					else if(arguments[i].equals("/"))
					{
						i++;
						if(getDouble(icommandsender, arguments, i) == 0)
							throw new DivisionException();
						else
						{
							number = number / getDouble(icommandsender, arguments, i);
						}
					}
					else if(arguments[i].equals("%"))
					{
						i++;
						if(i + 1 == arguments.length && MCConfig.fancyRemainders)
						{
							// Skip the math to be done later for fancy
							// remainder output
						}
						else
						{
							if(getDouble(icommandsender, arguments, i) == 0)
								throw new DivisionException();
							else
							{
								number = number % getDouble(icommandsender, arguments, i);
							}
						}
					}
					else if(arguments[i].equals("^"))
					{
						i++;
						double next = getDouble(icommandsender, arguments, i);
						if(number < 0)
						{
							double num = next / next / next;
							if(num % 2 == 0)
								throw new ImaginaryNumberException();
						}
						if(getDouble(icommandsender, arguments, i) == 0 && MCConfig.zeroMultWarns)
						{
							zeroPower = true;
						}
						number = Math.pow(number, next);
					}
					else if(arguments[i].equals("/-"))
					{
						i++;
						if(number < 0 && getDouble(icommandsender, arguments, i) % 2 == 0)
							throw new ImaginaryNumberException();
						else if(number == 0)
							throw new DivisionException();
						else
						{
							boolean neg = false;
							if(number < 0)
							{
								number = -number;
								neg = true;
							}
							number = Math.pow(number, 1.0 / getDouble(icommandsender, arguments, i));
							if(neg)
							{
								number = -number;
							}
						}
					}
					else
						throw new SymbolException();

					if(i + 1 == arguments.length)
					{
						if(arguments[i - 1].equals("%") && MCConfig.fancyRemainders)
						{ // Fancy remainder output
							if(getDouble(icommandsender, arguments, i) == 0)
								throw new DivisionException();
							else
							{
								lastMap.put(icommandsender.getName(), number % getDouble(icommandsender, arguments, i));
								toReturn = new TextComponentString(String
										.valueOf((int) (number / getDouble(icommandsender, arguments, i))) + "R"
										+ String.valueOf((int) (number % getDouble(icommandsender, arguments, i))));
							}
						}
						else if(number % 1 == 0)
						{ // Remove unnecessary doubles
							lastMap.put(icommandsender.getName(), number);
							int b = (int) (number);
							toReturn = new TextComponentString(String.valueOf(b));
						}
						else
						{
							lastMap.put(icommandsender.getName(), number);
							toReturn = new TextComponentString(String.valueOf(number));
						}

						// Append warnings if needed
						if(zeroPower)
						{
							toReturn.appendSibling(new TextComponentString(" ").appendSibling(
									new TextComponentTranslation("minecalc.calc.powerZeroWarning").setStyle(redStyle)));
						}
						else if(zeroMult)
						{
							toReturn.appendSibling(new TextComponentString(" ").appendSibling(
									new TextComponentTranslation("minecalc.calc.multZeroWarning").setStyle(redStyle)));
						}
					}
				}
			}
			catch(NumberFormatException e)
			{
				if(e.getMessage().equals("multiple points"))
					return new TextComponentTranslation("minecalc.calc.multiplePointsException").setStyle(redStyle);
				return new TextComponentTranslation("minecalc.calc.numberFormatException").setStyle(redStyle)
						.appendSibling(new TextComponentString(e.getMessage().substring(17, e.getMessage().length())));
			}
			catch(ImaginaryNumberException er)
			{
				return new TextComponentTranslation("minecalc.calc.imaginaryException").setStyle(redStyle);
			}
			catch(DivisionException err)
			{
				return new TextComponentTranslation("minecalc.calc.divZeroException").setStyle(redStyle);
			}
			catch(SymbolException erro)
			{
				return new TextComponentTranslation("minecalc.calc.symbolException").setStyle(redStyle)
						.appendSibling(new TextComponentString(" %"))
						.appendSibling(new TextComponentTranslation("minecalc.calc.symbolExceptionPartTwo"));
			}
			catch(PreviousOutputException error)
			{
				return new TextComponentTranslation("minecalc.calc.previousOutputException").setStyle(redStyle);
			}
		}
		else
		{ // If the number of arguments is wrong
			return new TextComponentTranslation("minecalc.calc.usage").setStyle(redStyle);
		}

		// Prepend the arguments to the output, if configured to
		if(MCConfig.returnInput)
		{
			String tempPrint;
			tempPrint = arguments[0];
			for(int i = 1; i < arguments.length; i++)
			{
				tempPrint = tempPrint + " " + arguments[i];
			}
			tempPrint = tempPrint + " = ";
			toReturn = new TextComponentString(tempPrint).appendSibling(toReturn);
		}

		return toReturn;
	}

	@Override
	public List<String> getCommandAliases()
	{
		// A list of alternate command names
		List<String> aliases = new ArrayList<String>(Arrays.asList("Calc", "calculate", "Calculate"));
		return aliases;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos pos)
	{
		if(args.length % 2 != 1)
		{
			ArrayList<String> options = new ArrayList<String>(Arrays.asList("+", "-", "*", "/", "%", "^", "/-"));
			return options;
		}
		else
			return null;
	}

	public double getDouble(ICommandSender sender, String[] args, int i)
			throws NumberFormatException, PreviousOutputException
	{
		if(args[i].equalsIgnoreCase("pi"))
			return Math.PI;
		else if(args[i].equalsIgnoreCase("l"))
		{
			if(lastMap.containsKey(sender.getName()))
				return lastMap.get(sender.getName());
			else
				throw new PreviousOutputException();
		}
		else
			return Double.valueOf(args[i]);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if(sender.getName().equals("@"))
		{
			MineCalc.Logger.warn("Command blocks cannot use /calc");
		}
		else
		{
			ArrayList<String> formattedArgs;
			formattedArgs = new ArrayList<String>();
			for(String arg : args)
			{
				int argStartIndex = 0;
				boolean lastIsNum = false;
				boolean thisIsNum = false;
				Character lastChar = 'z';
				for(int i = 0; i < arg.toCharArray().length + 1; i++)
				{
					if(i == arg.toCharArray().length)
					{
						formattedArgs.add(new String(arg.toCharArray(), argStartIndex, i - argStartIndex));
					}
					else
					{
						lastIsNum = thisIsNum;
						thisIsNum = isNumber(arg.toCharArray()[i], lastIsNum, lastChar);
						lastChar = arg.toCharArray()[i];
						if(thisIsNum != lastIsNum)
						{
							if(i != 0)
							{
								formattedArgs.add(new String(arg.toCharArray(), argStartIndex, i - argStartIndex));
								argStartIndex = i;
							}
						}
					}
				}
			}

			args = formattedArgs.toArray(new String[1]);

			ITextComponent output = calculate(sender, args);

			// Send the message back to the user
			if(sender.getName().equals("Server"))
			{
				MineCalc.Logger.info(output.getUnformattedComponentText());
			}
			else
			{
				EntityPlayer player = (EntityPlayer) sender;
				player.addChatMessage(output);
			}
		}
	}

	public static boolean isNumber(Character character, boolean lastIsNum, Character lastChar)
	{
		if(!character.equals('.'))
		{
			if(!character.toString().equalsIgnoreCase("l"))
			{
				if(!character.toString().equalsIgnoreCase("p"))
				{
					if(!character.toString().equalsIgnoreCase("i"))
					{
						if(!(character.equals('-') && (!lastIsNum && !lastChar.toString().equals("/"))))
						{
							try
							{
								Double.valueOf(String.valueOf(character));
							}
							catch(NumberFormatException e)
							{
								return false;
							}
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}
}

@SuppressWarnings("serial")
class ImaginaryNumberException extends Exception
{

}

@SuppressWarnings("serial")
class DivisionException extends Exception
{

}

@SuppressWarnings("serial")
class SymbolException extends Exception
{

}

@SuppressWarnings("serial")
class PreviousOutputException extends Exception
{

}
