package parser;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Expression expression = null;

        try {
            expression = Expression.getExpressionFrom(scanner.nextLine());
            System.out.println(expression);

            TruthTable truthTable = new TruthTable(expression, 'V', 'F');
            System.out.println(truthTable);
        }
        catch (IllegalArgumentException e) {
            for (int i = 0; i < Expression.errorIndex; i++) {
                System.err.print(" ");
            }
            System.err.println("^");

            System.err.println(e.getMessage());
        }
        scanner.close();
    }
}
