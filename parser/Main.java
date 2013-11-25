package parser;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Expression expression;
        try {
            expression = Expression.getExpressionFrom(scanner.nextLine());
            System.out.println(expression);			
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }


        scanner.close();
        
        
        
    }

}