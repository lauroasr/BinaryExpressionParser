package parser;

public class Expression {
    public static final char PRIORITY_START = '(';
    public static final char PRIORITY_END = ')';
    public static String log; 

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
            return "(" + expression.toString() + ")";
        }

        throw new IllegalArgumentException("Ambos os argumentos são nulos");
    }

    public static Expression getExpressionFrom(String s) {
        if (!isExpressionValid(s)) {
            throw new IllegalArgumentException(log);
        }
        
        Expression expression = new Expression();
              
        
        char operator = 0;
        int operatorPosition = 0, operatorPrecedenceLevel = 0;       
        boolean hasFoundOperator = false, hasFoundPriority = false;
        
        do { 
            int numberOfPriorityStartsFound = 0, numberOfPriorityEndsFound = 0;
            int mainPriorityStartPosition = 0, mainPriorityEndPosition = 0;
            
            for (int i = 0, length = s.length(); i < length; i++) {
                if (s.charAt(i) == ' ') {
                    continue;
                }
                
                if (s.charAt(i) == PRIORITY_START) {
                    if (numberOfPriorityStartsFound == 0) {
                        mainPriorityStartPosition = i;
                        hasFoundPriority = true;
                    }                    
                    numberOfPriorityStartsFound++;
                    
                    continue;
                }
                
                if (numberOfPriorityStartsFound > numberOfPriorityEndsFound) {
                    if (s.charAt(i) == PRIORITY_END) {
                        numberOfPriorityEndsFound++;
                        
                        if (numberOfPriorityStartsFound == numberOfPriorityEndsFound) {
                            mainPriorityEndPosition = i;
                        }
                    } else {
                        continue;
                    }              
                }
                
                if (Operator.isOperator(s.charAt(i))) {
                    int pl = Operator.getPrecedenceLevelOf(s.charAt(i));
                    if (pl > operatorPrecedenceLevel) {
                         operatorPrecedenceLevel = pl;
                         operatorPosition = i;
                         operator = s.charAt(i);
                         hasFoundOperator = true;
                         System.out.println("* Operator found at " + i + ": " + s.charAt(i) + ", precedence level: " + pl);
                         System.out.println("* Current string is " + s);
                    }
                }                             
            }
            // se não encontrou nenhum operador
            if (!hasFoundOperator) {
                if (hasFoundPriority) {
                    // tenta novamente com uma nova String (sem parênteses)                    
                    s = s.substring(mainPriorityStartPosition + 1, mainPriorityEndPosition);
                    System.out.println("hasn't found any operator, but found priority. trying with " + s);
                    hasFoundPriority = false;
                } else {
                    break;
                }
            }                                   
        } while (!hasFoundOperator);
        
        if (!hasFoundOperator) {
            System.out.println("hasn't found any operator. " + s);
            expression.variableB = Variable.getVariableFrom(s);
            return expression;
        }
        
        String operandA, operandB;
        expression.operator = operator;
        
        // se o operador for binário
        if (!Operator.isUnaryOperator(expression.operator)) {
            operandA = s.substring(0, operatorPosition);
            // determina a variável ou expressão A
            expression.variableA = Variable.getVariableFrom(operandA);            
            if (expression.variableA == null ) {
                expression.expressionA = getExpressionFrom(operandA);
            }
        }
        
        // determina a variável ou expressão B
        operandB = s.substring(operatorPosition + 1);
        
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

    public static boolean isExpressionValid(String s) {		
        boolean expectingExpOrVar = true;
        boolean expectingEndOfExp = false;
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
                } else if (Character.isLetter(s.charAt(i))) {
                    expectingExpOrVar = false;
                    expectingBinaryOperator = true;

                    if (priorityExpressions > 0) {
                        expectingEndOfExp = true;
                    }
                    continue;
                } else if (s.charAt(i) != Operator.NOT) {
                    log = "Esperava-se uma expressão ou variável na posição " + i;
                    return false;
                }
            }

            if (expectingBinaryOperator) {
                if (s.charAt(i) == Operator.NOT || s.charAt(i) == PRIORITY_START || !Operator.isOperator(s.charAt(i))) {
                    if (!expectingEndOfExp) {
                        log = "Esperava-se um operador binário na posição " + i;
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
                    log = "Esperava-se um fecha parênteses na posição " + i;
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
            log = "Esperava-se um fecha parênteses na posição " + length;
            return false;
        }
        return true;
    }

    public String toString() {					
        if (Operator.isUnaryOperator(operator)) {
            return operator + toStringBetween(variableB, expressionB);
        }
        return toStringBetween(variableA, expressionA) +
               " " + operator + " " + 
               toStringBetween(variableB, expressionB);
    }   
}
