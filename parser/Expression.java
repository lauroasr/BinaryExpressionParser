package parser;

import java.util.ArrayList;

public class Expression {
    public static final char PRIORITY_START = '(';
    public static final char PRIORITY_END = ')';
    public static ArrayList<String> log;
    public static int errorIndex;

    public Variable variableA, variableB;
    public Expression expressionA, expressionB;
    public char operator;

    public Expression() {

    }

    public Expression(Expression expressionA, Expression expressionB, char operator) {
        this.expressionA = expressionA;
        this.expressionB = expressionB;
        this.operator = operator;
    }

    public static boolean getValueBetween(Variable variable, Expression expression) {
        if (variable != null) {
            return variable.value;
        }
        if (expression != null) {
            return expression.getValue();
        }
        return false;
    }

    public static String toStringBetween(Variable variable, Expression expression) {
        if (variable != null) {
            return variable.toString();
        }
        if (expression != null) {
            if (Operator.isUnaryOperator(expression.operator)) {
                return expression.toString();
            }
            return "(" + expression.toString() + ")";
        }

        throw new IllegalArgumentException("Ambos os argumentos s�o nulos");
    }

    public static Expression getExpressionFrom(String s) {
        log = new ArrayList<>();

        // testa se a express�o � v�lida
        if (!isExpressionValid(s)) {
            throw new IllegalArgumentException(log.get(log.size() - 1));
        }

        Expression expression = new Expression();

        char operator = '\0';
        int operatorPosition = 0, operatorPrecedenceLevel = 0;
        boolean hasFoundOperator = false, hasFoundPriority = false;

        do {
            int numberOfPriorityStartsFound = 0, numberOfPriorityEndsFound = 0;
            int mainPriorityStartIndex = 0, mainPriorityEndIndex = 0;

            for (int i = 0, length = s.length(); i < length; i++) {
                if (s.charAt(i) == ' ') {
                    continue;
                }

                if (s.charAt(i) == PRIORITY_START) {
                    if (numberOfPriorityStartsFound == 0) {
                        mainPriorityStartIndex = i;
                        hasFoundPriority = true;
                    }
                    numberOfPriorityStartsFound++;

                    continue;
                }

                if (numberOfPriorityStartsFound > numberOfPriorityEndsFound) {
                    if (s.charAt(i) == PRIORITY_END) {
                        numberOfPriorityEndsFound++;

                        if (numberOfPriorityStartsFound == numberOfPriorityEndsFound) {
                            mainPriorityEndIndex = i;
                        }
                    } else {
                        continue;
                    }
                }

                // tenta encontrar um operador com menor n�vel de preced�ncia
                if (Operator.isOperator(s.charAt(i))) {
                    int pl = Operator.getPrecedenceLevelOf(s.charAt(i));
                    if (pl > operatorPrecedenceLevel) {
                         operatorPrecedenceLevel = pl;
                         operatorPosition = i;
                         operator = s.charAt(i);
                         hasFoundOperator = true;
                    }
                }
            }
            // se n�o encontrou nenhum operador
            if (!hasFoundOperator) {
                if (hasFoundPriority) {
                    // tenta novamente com uma nova String (sem par�nteses)
                    s = s.substring(mainPriorityStartIndex + 1, mainPriorityEndIndex);
                    hasFoundPriority = false;
                } else {
                    break;
                }
            }
        } while (!hasFoundOperator);

        if (!hasFoundOperator) {
            // se n�o encontrou nenhum operador, retorna uma express�o com s� uma vari�vel (express�o in�til)
            expression.variableA = Variable.getVariableFrom(s);
            return expression;
        }

        String operandA, operandB;
        expression.operator = operator;

        // se o operador for bin�rio
        if (!Operator.isUnaryOperator(expression.operator)) {
            operandA = s.substring(0, operatorPosition);
            // determina a vari�vel ou express�o A
            expression.variableA = Variable.getVariableFrom(operandA);
            if (expression.variableA == null ) {
                expression.expressionA = getExpressionFrom(operandA);
            }
        }

        operandB = s.substring(operatorPosition + 1);
        // determina a vari�vel ou express�o B
        expression.variableB = Variable.getVariableFrom(operandB);
        if (expression.variableB == null) {
            expression.expressionB = getExpressionFrom(operandB);
        }

        return expression;
    }

    public boolean getValue() {
        if (operator == '\0') {
            return getValueBetween(variableA, expressionA);
        }

        if (Operator.isUnaryOperator(operator)) {
            return Operator.solve(operator, getValueBetween(variableB, expressionB));
        }

        return Operator.solve(operator, getValueBetween(variableA, expressionA), getValueBetween(variableB, expressionB));
    }

    public int getOperatorIndexAtString() {
        if (operator == '\0' || Operator.isUnaryOperator(operator)) {
            return 0;
        }

        return toStringBetween(variableA, expressionA).length() + 1;
    }

    public ArrayList<Boolean> getValuesInOrder() {
        // retorna um array com os valores de todas as express�es
        // em ordem (do primeiro � esquerda at� o �ltimo da direita)

        ArrayList<Boolean> values = new ArrayList<>();

        if (expressionA != null) {
            values.addAll(expressionA.getValuesInOrder());
        }

        values.add(getValue());

        if (expressionB != null) {
            values.addAll(expressionB.getValuesInOrder());
        }

        return values;
    }

    public static boolean isExpressionValid(String s) {
        // esperava-se uma express�o ou vari�vel
        boolean expectingExpOrVar = true;
        // esperava-se um fim de express�o (fecha par�nteses)
        boolean expectingEndOfExp = false;
        // esperava-se um operador bin�rio
        boolean expectingBinaryOperator = false;

        int priorityExpressions = 0;
        int length = s.length();

        for (int i = 0; i < length; i++) {
            if (s.charAt(i) == ' ') {
                continue;
            }
            if (expectingExpOrVar) {
                if (s.charAt(i) == PRIORITY_START) {
                    priorityExpressions++;
                    continue;
                } else if (Variable.isVariable(s.charAt(i))) {
                    expectingExpOrVar = false;
                    expectingBinaryOperator = true;

                    if (priorityExpressions > 0) {
                        expectingEndOfExp = true;
                    }
                    continue;
                } else if (s.charAt(i) != Operator.NOT) {
                    log.add("Esperava-se uma express�o ou vari�vel na posi��o " + i);
                    errorIndex = i;
                    return false;
                }
            }

            if (expectingBinaryOperator) {
                if (s.charAt(i) == Operator.NOT || s.charAt(i) == PRIORITY_START || !Operator.isOperator(s.charAt(i))) {
                    if (!expectingEndOfExp) {
                        log.add("Esperava-se um operador bin�rio na posi��o " + i);
                        errorIndex = i;
                        return false;
                    }
                } else {
                    expectingBinaryOperator = false;
                    expectingExpOrVar = true;
                    expectingEndOfExp = false;
                    continue;
                }
            }

            if (expectingEndOfExp) {
                if (s.charAt(i) != PRIORITY_END) {
                    log.add("Esperava-se um fecha par�nteses na posi��o " + i);
                    errorIndex = i;
                    return false;
                } else {
                    priorityExpressions--;
                    if (priorityExpressions == 0) {
                        expectingEndOfExp = false;
                    }
                }
            }
        }

        if (priorityExpressions > 0) {
            log.add("Esperava-se um fecha par�nteses na posi��o " + length);
            errorIndex = length;
            return false;
        }
        return true;
    }

    public String toString() {
        try {
            if (operator == '\0') {
                return toStringBetween(variableB, expressionB);
            }

            if (Operator.isUnaryOperator(operator)) {
                return operator + toStringBetween(variableB, expressionB);
            }
            return toStringBetween(variableA, expressionA) +
                   " " + operator + " " +
                   toStringBetween(variableB, expressionB);
        } catch (IllegalArgumentException e) {
            throw new NullPointerException("A express�o n�o possui nenhum operando");
        }
    }
}
