package blackop778.mineCalc.core;

public interface IBinaryOperation extends IOperation {
    enum Associativity { Left, Right };

    /**
     * @return The result of performing the function on the math
     * @throws CalcExceptions Problems we may encounter. Should be a subclass.
     */
    double evaluateFunction(double numbers, double numbers2) throws CalcExceptions;

    Associativity getAssociativity();
}
