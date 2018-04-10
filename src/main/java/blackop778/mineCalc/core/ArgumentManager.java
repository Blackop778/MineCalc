package blackop778.mineCalc.core;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class ArgumentManager {
    private ArrayList<Argument> arguments;

    public ArgumentManager() {
        arguments = new ArrayList<Argument>();
    }

    public void add(Argument arg) {
        arguments.add(arg);
    }

    public void sort(Comparator<? super Argument> c) {
        Collections.sort(arguments, c);
    }

    @Nonnull
    public Argument get(int index) {
        return new Argument(arguments.get(index));
    }

    public int size() {
        return arguments.size();
    }

    /**
     * Returns whether or not the arguments are fully computed
     */
    public boolean updateMath(@Nonnull String oldMath, @Nonnull String answer) {
        ListIterator<Argument> it = arguments.listIterator();

        while (it.hasNext()) {
            Argument arg = it.next();
            if (!arg.contents.equals(oldMath)) {
                arg.contents = arg.contents.replaceAll(Pattern.quote(oldMath), answer);
            } else if (arguments.size() == 1) {
                arg.contents = arg.contents.replaceAll(Pattern.quote(oldMath), answer);
                return true;
            } else
                it.remove();
        }

        return false;
    }

    private void verifyPostUpdateIntegrity(@Nonnull String answer)
    {
        ListIterator<Argument> it = arguments.listIterator();

        while (it.hasNext()) {
            Argument arg = it.next();
            String contents = arg.contents;

            int index = contents.indexOf(answer);
            if(index > -1 && !contents.equals(answer))
            {
                if(index > 0)
                {
                    if(contents.charAt(index - 1) != '(' && )
                }
            }
        }
    }
}
