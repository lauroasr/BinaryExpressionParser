package parser;

public final class Operator {	
    public static final char NOT = '!';
    public static final char AND = '*';
    public static final char XOR = 'x';
    public static final char OR = '+';           
    public static final char IMP = '-';
    public static final char XNOR = '=';

    public static boolean isOperator(char symbol) {
        return symbol == NOT ||
               symbol == AND ||
               symbol == XOR ||
               symbol == OR ||
               symbol == IMP ||
               symbol == XNOR;               
    }

    public static boolean isUnaryOperator(char symbol) {
        return symbol == NOT;
    }

    public static boolean solve(char operator, boolean valueA, boolean valueB) {
        if (operator == AND) {
            return valueA && valueB;
        } else if (operator == XOR) {
            return valueA != valueB;
        } else if (operator == OR) {
            return valueA || valueB;
        } else if (operator == IMP) {
            return !valueA || valueB;
        } else if (operator == XNOR) {
            return valueA == valueB;
        }

        throw new IllegalArgumentException("Operador binário inválido");
    }
    
    public static boolean solve(char operator, boolean value) {     
        if (operator == NOT) {
            return !value;
        }
        
        throw new IllegalArgumentException("Operador unário inválido");
    }
    
    public static int getPrecedenceLevelOf(char operator) {
        if (operator == NOT) {
            return 1;
        } else if (operator == AND) {
            return 2;
        } else if (operator == XOR) {
            return 3;
        } else if (operator == OR) {
            return 4;
        } else if (operator == IMP) {
            return 5;
        } else if (operator == XNOR) {
            return 6;
        }
        
        throw new IllegalArgumentException("Operador inválido");
    }    
}
