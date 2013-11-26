package parser;

import java.util.ArrayList;

public class TruthTable {    
    public Expression expression;        
    public ArrayList<Variable> variables;
    public ArrayList<ArrayList<Integer>> variablesIndexes;
    
    public char trueRepresentation;
    public char falseRepresentation;
    
    public TruthTable(Expression expression) {
        this.expression = expression;
    }
    
    public TruthTable(Expression expression, char trueRepresentation, char falseRepresentation) {
        this.expression = expression;
        this.trueRepresentation = trueRepresentation;
        this.falseRepresentation = falseRepresentation;
    }
    
    public void updateVariablesAndIndexes() {
        variables = new ArrayList<>();                
        findVariablesAt(expression);
        
        variablesIndexes = new ArrayList<>();       
        String s = expression.toString();
        
        for (int i = 0; i < variables.size(); i++) {
            variablesIndexes.add(new ArrayList<Integer>());
            for (int j = 0; j < s.length(); j++) {
                if (variables.get(i).letter == s.charAt(j)) {
                    variablesIndexes.get(i).add(j);
                }
            }
        }                              
    }
    
    public void findVariablesAt(Expression expression) {
        if (expression.variableA != null) {
            expression.variableA = getVariableReference(expression.variableA.letter);
        } else if (expression.expressionA != null) {
            findVariablesAt(expression.expressionA);
        }
        if (expression.variableB != null) {
            expression.variableB = getVariableReference(expression.variableB.letter);
        } else if (expression.expressionB != null) {
            findVariablesAt(expression.expressionB);
        }
    }
    
    public Variable getVariableReference(char letter) {
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).letter == letter) {
                return variables.get(i);
            }
        }
        variables.add(new Variable(letter));
        return variables.get(variables.size() - 1);
    }
    
    public String getLine(int lineNumber) {                              
        String binaryValues = Integer.toBinaryString(lineNumber);
               
        int bVIndex = binaryValues.length() - 1;
        // define os valores das variáveis
        for (int i = variables.size() - 1; i >= 0; i--) {
            if (bVIndex >= 0) {
                if (binaryValues.charAt(bVIndex) == '1') {
                    variables.get(i).value = false;
                } else {
                    variables.get(i).value = true;
                }
                bVIndex--;
            } else {
                variables.get(i).value = true;                
            }                       
        }
        
        StringBuilder line = new StringBuilder();
        // coloca os valores das variáveis em line
        for (int i = 0; i < variablesIndexes.size(); i++) {
            ArrayList<Integer> indexes = variablesIndexes.get(i);
            
            for (int j = 0; j < indexes.size(); j++) {                
                while (line.length() <= indexes.get(j)) {
                    line.append(' ');
                }
                line.setCharAt(indexes.get(j), variables.get(i).value ? trueRepresentation : falseRepresentation);               
            }
        }
        
        line.setCharAt(expression.getOperatorIndexAtString(), expression.getValue() ? trueRepresentation : falseRepresentation);
               
                
        return line.toString();
    }
    
    public String toString() {
        updateVariablesAndIndexes();               
        int numberOfLines = (int) Math.pow(2, variables.size());
        
        String s = new String();
        for (int i = 0; i < numberOfLines; i++) {
            s += getLine(i) + '\n';
        }
        return s;
    }
    
    

}