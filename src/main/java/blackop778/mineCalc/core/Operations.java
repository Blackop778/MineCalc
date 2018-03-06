package blackop778.mineCalc.core;

import blackop778.mineCalc.core.CalcExceptions.BitwiseDecimalException;
import blackop778.mineCalc.core.CalcExceptions.DivisionException;
import blackop778.mineCalc.core.CalcExceptions.ImaginaryNumberException;

import java.util.List;

public abstract class Operations {

    /**
     * In most cases is automatically called by an OperationHolder, only if it
     * was passed 'false' in the constructor does this need to be called
     * manually
     *
     * @param toAddTo
     * @return
     */
    public static List<IOperation> addOperations(List<IOperation> toAddTo) {
        toAddTo.add(new Addition());
        toAddTo.add(new Subtraction());
        toAddTo.add(new Multiplication());
        toAddTo.add(new Division());
        toAddTo.add(new Modulus());
        toAddTo.add(new Exponent());
        toAddTo.add(new Root());
        toAddTo.add(new BitAnd());
        toAddTo.add(new BitOr());
        toAddTo.add(new BitXOr());
        toAddTo.add(new BitNot());
        toAddTo.add(new BitShiftRight());
        toAddTo.add(new BitShiftLeft());
        toAddTo.add(new BitShiftRightFillZeros());
        return toAddTo;
    }

    public static class Addition implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"+"};
        }

        @Override
        public double evaluateFunction(double num1, double num2) {
            return num1 + num2;
        }

        @Override
        public int getImportance() {
            return 10;
        }
    }

    public static class Subtraction implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"-"};
        }

        @Override
        public double evaluateFunction(double num1, double num2) {
            return num1 - num2;
        }

        @Override
        public int getImportance() {
            return 10;
        }
    }

    public static class Multiplication implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"*", "x", "X"};
        }

        @Override
        public double evaluateFunction(double num1, double num2) {
            return num1 * num2;
        }

        @Override
        public int getImportance() {
            return 12;
        }
    }

    public static class Division implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"/"};
        }

        @Override
        public double evaluateFunction(double num1, double num2) throws CalcExceptions {
            if (num2 == 0)
                throw new DivisionException();
            return num1 / num2;
        }

        @Override
        public int getImportance() {
            return 12;
        }
    }

    public static class Modulus implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"%"};
        }

        @Override
        public double evaluateFunction(double num1, double num2) throws CalcExceptions {
            if (num2 == 0)
                throw new DivisionException();
            return num1 % num2;
        }

        @Override
        public int getImportance() {
            return 12;
        }
    }

    public static class Exponent implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"^"};
        }

        @Override
        public double evaluateFunction(double base, double num2) throws CalcExceptions {
            boolean negative = false;
            if (base < 0) {
                double num3 = 1 / num2;
                if (num3 % 2 == 0)
                    throw new ImaginaryNumberException();
                else {
                    base = -base;
                    negative = true;
                }
            }
            double answer = Math.pow(base, num2);
            return negative ? -answer : answer;
        }

        @Override
        public int getImportance() {
            return 14;
        }
    }

    public static class Root extends Exponent {
        @Override
        public String[] getOperators() {
            return new String[]{"/--"};
        }

        @Override
        public double evaluateFunction(double base, double num2) throws CalcExceptions {
            return super.evaluateFunction(base, 1 / num2);
        }
    }

    public static class BitAnd implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"&"};
        }

        @Override
        public int getImportance() {
            return 6;
        }

        @Override
        public double evaluateFunction(double numbers, double numbers2) throws CalcExceptions {
            if ((int) numbers != numbers)
                throw new BitwiseDecimalException(numbers);
            if ((int) numbers2 != numbers2)
                throw new BitwiseDecimalException(numbers2);
            int int1 = (int) numbers;
            int int2 = (int) numbers2;
            return int1 & int2;
        }
    }

    public static class BitOr implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"|"};
        }

        @Override
        public int getImportance() {
            return 2;
        }

        @Override
        public double evaluateFunction(double numbers, double numbers2) throws CalcExceptions {
            if ((int) numbers != numbers)
                throw new BitwiseDecimalException(numbers);
            if ((int) numbers2 != numbers2)
                throw new BitwiseDecimalException(numbers2);
            int int1 = (int) numbers;
            int int2 = (int) numbers2;
            return int1 | int2;
        }
    }

    public static class BitXOr implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"^^"};
        }

        @Override
        public int getImportance() {
            return 4;
        }

        @Override
        public double evaluateFunction(double numbers, double numbers2) throws CalcExceptions {
            if ((int) numbers != numbers)
                throw new BitwiseDecimalException(numbers);
            if ((int) numbers2 != numbers2)
                throw new BitwiseDecimalException(numbers2);
            int int1 = (int) numbers;
            int int2 = (int) numbers2;
            return int1 ^ int2;
        }
    }

    public static class BitNot implements IUnaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"~"};
        }

        @Override
        public int getImportance() {
            return 16;
        }

        @Override
        public double evaluateFunction(double input) throws CalcExceptions {
            if ((int) input != input)
                throw new BitwiseDecimalException(input);
            int int1 = (int) input;
            return ~int1;
        }
    }

    public static class BitShiftRight implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{">>"};
        }

        @Override
        public int getImportance() {
            return 8;
        }

        @Override
        public double evaluateFunction(double numbers, double numbers2) throws CalcExceptions {
            if ((int) numbers != numbers)
                throw new BitwiseDecimalException(numbers);
            if ((int) numbers2 != numbers2)
                throw new BitwiseDecimalException(numbers2);
            int int1 = (int) numbers;
            int int2 = (int) numbers2;
            return int1 >> int2;
        }
    }

    public static class BitShiftLeft implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{"<<"};
        }

        @Override
        public int getImportance() {
            return 8;
        }

        @Override
        public double evaluateFunction(double numbers, double numbers2) throws CalcExceptions {
            if ((int) numbers != numbers)
                throw new BitwiseDecimalException(numbers);
            if ((int) numbers2 != numbers2)
                throw new BitwiseDecimalException(numbers2);
            int int1 = (int) numbers;
            int int2 = (int) numbers2;
            return int1 << int2;
        }
    }

    public static class BitShiftRightFillZeros implements IBinaryOperation {
        @Override
        public String[] getOperators() {
            return new String[]{">>>"};
        }

        @Override
        public int getImportance() {
            return 8;
        }

        @Override
        public double evaluateFunction(double numbers, double numbers2) throws CalcExceptions {
            if ((int) numbers != numbers)
                throw new BitwiseDecimalException(numbers);
            if ((int) numbers2 != numbers2)
                throw new BitwiseDecimalException(numbers2);
            int int1 = (int) numbers;
            int int2 = (int) numbers2;
            return int1 >>> int2;
        }
    }
}
