package parser;

import java.util.ArrayList;

public class TruthTable {
    public Expression expression;
    public ArrayList<Variable> variables;
    public ArrayList<ArrayList<Integer>> variablesIndexes;
    public ArrayList<Boolean> values;
    public ArrayList<Integer> valuesIndex;

    public char trueRepresentation;
    public char falseRepresentation;

    public TruthTable(Expression expression) {
        this.expression = expression;
    }

    public TruthTable(Expression expression, char trueRepresentation,
                      char falseRepresentation) {
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
            // armazena a posição das variáveis na string de expression
            for (int j = 0; j < s.length(); j++) {
                if (variables.get(i).letter == s.charAt(j)) {
                    variablesIndexes.get(i).add(j);
                }
            }
        }

        valuesIndex = new ArrayList<>();
        // armazena a posição dos operadores na string de expression
        for (int i = 0; i < s.length(); i++) {
            if (Operator.isOperator(s.charAt(i))) {
                valuesIndex.add(i);
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
        // busca se já existe uma variável com letter
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).letter == letter) {
                // se achar, retorna sua referência
                return variables.get(i);
            }
        }
        // se não, adiciona uma nova variável e a retorna
        variables.add(new Variable(letter));
        return variables.get(variables.size() - 1);
    }

    public StringBuilder getLine(int lineNumber) {
        // converte lineNumbers para binário
        String binaryValues = Integer.toBinaryString(lineNumber);

        int bVIndex = binaryValues.length() - 1;
        // define os valores das variáveis
        for (int i = variables.size() - 1; i >= 0; i--) {
            // seta os valores das variáveis (o oposto de binaryValues)
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
                // preenche com espaços se a string for menor
                while (line.length() <= indexes.get(j)) {
                    line.append(' ');
                }
                // coloca o valor da varíavel em sua posição
                line.setCharAt(indexes.get(j),
                        toCharRepresentation(variables.get(i).value));
            }
        }

        values = expression.getValuesInOrder();
        // coloca os valores das expressões em line
        for (int i = 0; i < valuesIndex.size(); i++) {
            line.setCharAt(valuesIndex.get(i),
                    toCharRepresentation(values.get(i)));
        }

        return line;
    }

    public char toCharRepresentation(boolean value) {
        // retorna a representação do boolean em caractere
        return value ? trueRepresentation : falseRepresentation;
    }

    @Override
    public String toString() {
        updateVariablesAndIndexes();

        // o número de linhas é 2 elevado à quantidade de variáveis
        int numberOfLines = (int) Math.pow(2, variables.size());

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < numberOfLines; i++) {
            // adiciona uma linha
            s.append(getLine(i));
            // quebra a linha para a próxima
            s.append('\n');
        }

        return s.toString();
    }
}