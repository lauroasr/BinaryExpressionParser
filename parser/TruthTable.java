package parser;

import java.util.ArrayList;

public class TruthTable {
    public Expression expression;  
    public ArrayList<Variable> variables;
    public ArrayList<ArrayList<Integer>> variablesIndexes;    
    
    public TruthTable(Expression expression) {
        this.expression = expression;
    }
    
    public void findVariablesAndIndexes() {
        variables = new ArrayList<>();                
        findVariablesAt(expression);
        
        variablesIndexes = new ArrayList<>();       
        String s = expression.toString();
        
        for (int i = 0; i < variablesIndexes.size(); i++) {
            variablesIndexes.add(new ArrayList<Integer>());
            for (int j = 0; j < s.length(); i++) {
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
    
    public String getLine() {
        String line = new String();
        
        
        
        
        
        
        return null;
    }


    
    public String toString() {

        
        
        
        
        
        
        return null;
    }

}