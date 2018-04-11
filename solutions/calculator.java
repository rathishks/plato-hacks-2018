import java.util.*;
import java.text.DecimalFormat;

/*
 * https://open.kattis.com/problems/calculator
 */

enum TokenType {
    NUMBER,
    OPERATOR,
    PAREN
}

class Token {
    public TokenType tokenType;
    public String value;

    public Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }
}

class Tokenizer {
    int i;
    char[] chars;
    Token previousToken;
    
    public Tokenizer(String input) {
        chars = sanitize(input).toCharArray();    
    }

    private String sanitize(String input) {
        String ret = input;

        ret = ret.replaceAll("\\s", "");
        ret = ret.replaceAll("--", "+");
        ret = ret.replaceAll("(\\d)(\\*|/)(\\d)", "($1$2$3)");
        
        return ret;
    }

    public Token nextToken() {
        Token ret = null;

        if (i < chars.length) {
            char first = chars[i];
            
            if (Character.isDigit(first) || (
                first == '-' && 
                previousToken != null && 
                previousToken.tokenType == TokenType.OPERATOR
            )) {
                i++;

                StringBuilder sb = new StringBuilder();

                while (i < chars.length && Character.isDigit(chars[i])) {
                    sb.append(chars[i++]);
                }

                ret = new Token(
                    TokenType.NUMBER, 
                    sb.insert(0, first).toString()
                );
            } else {
                i++;

                ret = new Token(
                    first == '(' || first == ')' ? 
                        TokenType.PAREN :
                        TokenType.OPERATOR, 
                    Character.toString(first)
                );
            }
        }

        previousToken = ret;

        return ret;
    }
}

class Evaluator {
    public double evaluate(String input) {
        return eval(new Tokenizer(input), 0D);
    }

    private double eval(Tokenizer tokenizer, double result) {
        while (true) {
            Token token = tokenizer.nextToken();

            if (token == null) {
                break;
            }

            switch (token.tokenType) {
                case NUMBER:
                    result = Double.parseDouble(token.value);
                case OPERATOR:
                    double left = result, right = 0D;

                    Token nextToken = tokenizer.nextToken();

                    if (nextToken.tokenType == TokenType.PAREN) {
                        right = eval(tokenizer, result);
                        tokenizer.nextToken();
                    } else {
                        right = Double.parseDouble(nextToken.value);
                    }

                    if (token.value == "+") {
                        result = left + right;
                    } else if (token.value == "-") {
                        result = left - right;
                    } else if (token.value == "*") {
                        result = left * right;
                    } else if (token.value == "/") {
                        result = left / right;
                    }
                case PAREN:
                    result = eval(tokenizer, result);
                    tokenizer.nextToken();
            }
        }

        return result;
    }
}

public class calculator {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        
        while (true) {
            String input = s.nextLine();

            if (input == null) {
                break;
            }

            double result = new Evaluator().evaluate(input);
            System.out.println(new DecimalFormat("#.00").format(result));
        }

        s.close();
    }
}