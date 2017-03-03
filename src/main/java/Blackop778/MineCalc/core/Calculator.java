package Blackop778.MineCalc.core;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Blackop778.MineCalc.common.IMineCalcCompound;
import Blackop778.MineCalc.common.MineCalcCompoundProvider;
import Blackop778.MineCalc.core.CalcExceptions.AllStandinsUsedException;
import Blackop778.MineCalc.core.CalcExceptions.FancyRemainderException;
import Blackop778.MineCalc.core.CalcExceptions.InvalidNumberException;
import Blackop778.MineCalc.core.CalcExceptions.MultiplePointsException;
import Blackop778.MineCalc.core.CalcExceptions.OperatorException;
import Blackop778.MineCalc.core.CalcExceptions.ParenthesisException;
import Blackop778.MineCalc.core.CalcExceptions.PreviousOutputException;
import Blackop778.MineCalc.core.CalcExceptions.UsageException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;

public abstract class Calculator {
    public static final Character[] STANDINCHARS = { '$', '#', '@', '"', ';', ':', '?', '&', '[', '{', ']', '}', '|',
	    '!' };
    public static final Argument SORTING_HAT = new Argument(0, 0, "f");
    public static OperationHolder operations = new OperationHolder();
    public static Double consoleLastOutput = null;

    public static double evaluate(String math, boolean useOOPS, ICommandSender sender) throws CalcExceptions {
	math = math.replaceAll("\\s", "");
	ArgumentManager arguments = new ArgumentManager();
	Stack<Integer> parenthesisStartIndex = new Stack<Integer>();
	parenthesisStartIndex.add(0);
	int parenthesisLevel = 0;

	// Divide into arguments by parenthesis
	for (int i = 0; i <= math.length(); i++) {
	    if (i == math.length()) {
		arguments.add(new Argument(arguments.size(), parenthesisLevel,
			math.substring(parenthesisStartIndex.pop(), i)));
	    } else {
		Character currentChar = math.charAt(i);
		if (currentChar.equals('(')) {
		    parenthesisStartIndex.add(i);
		    parenthesisLevel++;
		} else if (currentChar.equals(')')) {
		    arguments.add(new Argument(arguments.size(), parenthesisLevel,
			    math.substring(parenthesisStartIndex.pop(), i + 1)));
		    parenthesisLevel--;
		    if (parenthesisLevel < 0)
			throw new ParenthesisException(false);
		}
	    }
	}
	if (parenthesisLevel > 0)
	    throw new ParenthesisException(true);

	arguments.sort(SORTING_HAT);

	// Solve everything
	while (true) {
	    IOperation op = null;
	    int index = 999;
	    String operator = "";
	    // Cycle through the OOPS levels
	    for (int i = 6; i > 0; i--) {
		IOperation[] level = operations.getLevel(i);
		// Cycle through the operations in the OOPS level
		for (int n = 0; n < level.length; n++) {
		    String[] current = level[n].getOperators();
		    // Cycle through the operation strings for the given
		    // operation
		    for (int x = 0; x < current.length; x++) {
			boolean run = true;
			int startIndex = 0;
			while (run) {
			    run = false;
			    int newIndex = arguments.get(0).contents.indexOf(current[x], startIndex);
			    updateOP: if (newIndex > -1 && newIndex < index) {
				if (current[x].equals("-")) {
				    String currentMath = arguments.get(0).contents;
				    int effectiveIndex = newIndex;
				    if (currentMath.startsWith("(") && currentMath.endsWith(")")) {
					currentMath = currentMath.substring(1, currentMath.length() - 1);
					effectiveIndex--;
				    }
				    if (isNumber(currentMath.charAt(effectiveIndex),
					    tryCharAt(currentMath, effectiveIndex - 1),
					    tryCharAt(currentMath, effectiveIndex - 2))) {
					run = true;
					startIndex = newIndex + 1;
					break updateOP;
				    }
				}
				index = newIndex;
				op = level[n];
				operator = current[x];
			    }
			}
		    }
		}

		if (op != null && (useOOPS || i == 0)) {
		    break;
		}
	    }
	    if (index == 999) {
		throw new OperatorException();
	    }

	    String contents = arguments.get(0).contents;
	    // Remove beginning and ending parenthesis first
	    if (contents.startsWith("(") && contents.endsWith(")")) {
		contents = contents.substring(1, contents.length() - 1);
		index--;
	    }
	    Character replacer = findUnusedStandin(contents);
	    if (operator.equals("-")) {
		String[] temp = takeMinuses(contents, index);
		contents = temp[0];
		replacer = temp[1].charAt(0);
	    }
	    String trimmedContents = trimToOperation(contents, operator, index, replacer);
	    String[] numbersS = trimmedContents.split(Pattern.quote(operator));
	    if (operator.equals("-")) {
		numbersS[0] = addMinus(numbersS[0], replacer);
		numbersS[1] = addMinus(numbersS[1], replacer);
		trimmedContents = addMinus(trimmedContents, replacer);
	    }
	    double[] numbers = { getDoubleValue(numbersS[0], sender), getDoubleValue(numbersS[1], sender) };
	    if (operator.equals("%") && arguments.size() == 1
		    && (arguments.get(0).contents.equals("(" + trimmedContents + ")")
			    || arguments.get(0).contents.equals(trimmedContents))) {
		throw new FancyRemainderException(numbers[0], numbers[1]);
	    }
	    double answer = op.evaluateFunction(numbers[0], numbers[1]);
	    if (arguments.get(0).contents.equals("(" + trimmedContents + ")")) {
		trimmedContents = arguments.get(0).contents;
	    }
	    if (arguments.updateMath(trimmedContents, String.valueOf(answer))) {
		break;
	    }
	}

	return Double.valueOf(arguments.get(0).contents);
    }

    public static double getDoubleValue(String number, ICommandSender sender)
	    throws PreviousOutputException, MultiplePointsException, InvalidNumberException {
	boolean negative = false;
	double toReturn;
	if (number.startsWith("-")) {
	    negative = true;
	    number = number.substring(1);
	}

	if (number.equalsIgnoreCase("pi")) {
	    toReturn = Math.PI;
	} else if (number.equalsIgnoreCase("e")) {
	    toReturn = Math.E;
	} else if (number.equalsIgnoreCase("l")) {
	    toReturn = getLastOutput(sender);
	} else {
	    try {
		toReturn = Double.valueOf(number);
	    } catch (NumberFormatException e) {
		if (e.getMessage().equals("multiple points")) {
		    throw new MultiplePointsException();
		} else
		    throw new InvalidNumberException();
	    }
	}

	return negative ? -toReturn : toReturn;
    }

    /**
     * @param sender
     *            Can be null
     * @return The last output the user received
     * @throws PreviousOutputException
     *             If there was no previous output
     */
    public static double getLastOutput(ICommandSender sender) throws PreviousOutputException {
	if (sender == null)
	    if (consoleLastOutput == null)
		throw new PreviousOutputException();
	    else
		return consoleLastOutput;
	else {
	    Entity e = sender.getCommandSenderEntity();
	    if (e == null)
		if (consoleLastOutput == null)
		    throw new PreviousOutputException();
		else
		    return consoleLastOutput;
	    IMineCalcCompound comp = e.getCapability(MineCalcCompoundProvider.MCC_CAP, null);
	    Double last = comp.getLastNumber();
	    if (last.isNaN())
		throw new PreviousOutputException();
	    else
		return last;
	}
    }

    public static void setLastOutput(ICommandSender sender, double newOutput) {
	if (sender == null)
	    consoleLastOutput = newOutput;
	else {
	    Entity e = sender.getCommandSenderEntity();
	    if (e == null)
		consoleLastOutput = newOutput;
	    else {
		IMineCalcCompound comp = e.getCapability(MineCalcCompoundProvider.MCC_CAP, null);
		comp.setLastNumber(newOutput);
	    }
	}
    }

    public static String trimToOperation(String math, String operationSymbol, int symbolStartIndex)
	    throws AllStandinsUsedException, UsageException {
	return trimToOperation(math, operationSymbol, symbolStartIndex, findUnusedStandin(math));
    }

    public static String trimToOperation(String math, String operationSymbol, int symbolStartIndex,
	    Character numberStandin) throws AllStandinsUsedException, UsageException {
	int index = 0;
	int lastIndex = 0;
	String[] maths = math.split(Pattern.quote(operationSymbol));

	try {
	    // Isolate the first number
	    String math1 = maths[0];
	    lastIndex = math1.length();
	    for (index = lastIndex - 1; index > -1; index--) {
		if (!isNumber(math1.charAt(index), tryCharAt(math1, index - 1), tryCharAt(math1, index - 2),
			numberStandin)) {
		    index++;
		    break;
		}

	    }
	    if (index == -1) {
		index++;
	    }
	    math1 = math1.substring(index, lastIndex);

	    // Isolate the second number
	    String math2 = maths[1];

	    lastIndex = 0;
	    for (index = lastIndex; index < math2.length(); index++) {
		if (!isNumber(math2.charAt(index), tryCharAt(math2, index - 1), tryCharAt(math2, index - 2),
			numberStandin)) {
		    break;
		}
	    }
	    if (index != math2.length() - 1) {
		math2 = math2.substring(lastIndex, index);
	    }
	    return math1 + operationSymbol + math2;
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new UsageException();
	}
    }

    /**
     * Gets the character at index in string, and returns null instead of
     * StringIndexOutOfBoundsException
     */
    public static Character tryCharAt(String string, int index) {
	try {
	    return string.charAt(index);
	} catch (StringIndexOutOfBoundsException e) {
	    return null;
	}
    }

    public static String concatNullableCharacters(Character first, Character... others) {
	StringBuilder toReturn = new StringBuilder();
	if (first != null)
	    toReturn.append(first);
	for (Character c : others) {
	    if (c != null)
		toReturn.append(c);
	}

	return toReturn.toString();
    }

    public static boolean isNumber(Character current, Character last, Character lastLast)
	    throws AllStandinsUsedException {
	return isNumber(current, last, lastLast, findUnusedStandin(concatNullableCharacters(current, last, lastLast)));
    }

    public static boolean isNumber(Character current, Character last, Character lastLast, Character numberStandIn) {
	if (current.toString().matches("\\d|\\.|[lpieLPIE]|" + Pattern.quote(numberStandIn.toString())))
	    return true;
	else if (current.equals('-') && !(new Character('/').equals(lastLast))) {
	    if (last == null)
		return true;
	    else if (!last.toString().matches("\\d|\\.|[lpieLPIE]|" + Pattern.quote(numberStandIn.toString())))
		return true;
	}

	return false;
    }

    public static Character findUnusedStandin(String text) throws AllStandinsUsedException {
	for (Character c : STANDINCHARS) {
	    if (!text.contains(new StringBuilder().append(c).toString()))
		return c;
	}

	StringBuilder chars = new StringBuilder();
	for (Character c : STANDINCHARS) {
	    chars.append("'").append(c).append("', ");
	}
	throw new AllStandinsUsedException(chars.toString().substring(0, chars.toString().length() - 2));
    }

    /**
     * 
     * @param toChange
     * @param unchangedIndex
     * @return the changed string followed by the char the extra minus signs
     *         were replaced by
     * @throws AllStandinsUsedException
     */
    public static String[] takeMinuses(String toChange, int unchangedIndex) throws AllStandinsUsedException {
	Character replacement = findUnusedStandin(toChange);

	toChange = toChange.replaceAll(Pattern.quote("-"), Matcher.quoteReplacement(replacement.toString()));
	toChange = toChange.substring(0, unchangedIndex) + "-"
		+ toChange.substring(unchangedIndex + 1, toChange.length());

	return new String[] { toChange, replacement.toString() };
    }

    public static String addMinus(String math, Character minusReplacer) {
	return math.replaceAll(Pattern.quote(minusReplacer.toString()), "-");
    }

    public static int countAppearances(String container, char toCount) {
	int count = 0;
	char[] cont = container.toCharArray();
	for (char c : cont) {
	    if (c == toCount)
		count++;
	}

	return count;
    }
}
