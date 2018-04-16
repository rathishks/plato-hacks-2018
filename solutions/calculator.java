import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/*
 * https://open.kattis.com/problems/calculator
 */

public class calculator {
    private static Map<Character, Integer> operatorPriority;
    private static List<String> parsed = new ArrayList<String>();

    static {
        operatorPriority = new HashMap<Character, Integer>();

        operatorPriority.put('-', 0);
        operatorPriority.put('+', 0);
        operatorPriority.put('*', 1);
        operatorPriority.put('/', 1);
        operatorPriority.put('#', 2);
    }

    private static boolean equalOperators(Character op1, Character op2) {
        if (!operatorPriority.containsKey(op1) || !operatorPriority.containsKey(op2)) {
            return false;
        }

        return operatorPriority.get(op1) <= operatorPriority.get(op2);
    }

    private static void parse(String input) {
        Stack<Character> stack = new Stack<Character>();

        char p = '\0';
        int skip = -1;

        char[] chars = input.replaceAll("\\s", "").trim().toCharArray();

        parsed.clear();

        for (int i = 0; i < chars.length; i++) {
            if (i >= skip) {
                char ch = chars[i];

                if (ch == '-' && (operatorPriority.containsKey(p) || p == '(' || p == '\0')) {
                    stack.push('#');
                } else if (operatorPriority.containsKey(ch)) {
                    while (!stack.isEmpty() && equalOperators(ch, stack.peek())) {
                        parsed.add(Character.toString(stack.pop()));
                    }

                    stack.push(ch);
                } else if (ch == '(') {
                    stack.push(ch);
                } else if (ch == ')') {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        parsed.add(Character.toString(stack.pop()));
                    }

                    stack.pop();
                } else {
                    StringBuilder sb = new StringBuilder();

                    sb.append(ch);

                    for (int j = i + 1; j < chars.length; j++) {
                        if (Character.isDigit(chars[j])) {
                            sb.append(chars[j]);
                            skip = j + 1;
                        } else {
                            break;
                        }
                    }

                    parsed.add(sb.toString());
                }

                p = ch;
            }
        }

        while (!stack.isEmpty()) {
            parsed.add(Character.toString(stack.pop()));
        }
    }

    private static double evaluate() {
        Stack<Double> stack = new Stack<Double>();

        for (String seq : parsed) {
            if (Character.isDigit(seq.charAt(0))) {
                stack.push(Double.parseDouble(seq));
            } else if (seq.charAt(0) == '#') {
                stack.push(-1 * stack.pop());
            } else {
                Double one = stack.pop(), two = stack.pop();

                if (seq.charAt(0) == '-') {
                    stack.push(two - one);
                } else if (seq.charAt(0) == '+') {
                    stack.push(one + two);
                } else if (seq.charAt(0) == '*') {
                    stack.push(two * one);
                } else if (seq.charAt(0) == '/') {
                    stack.push(two / one);
                }
            }
        }

        return stack.pop();
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        DecimalFormat formatter = new DecimalFormat("0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();

        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        while (s.hasNext()) {
            String input = s.nextLine();

            parse(input);
            double result = evaluate();

            System.out.println(formatter.format(result));
        }

        s.close();
    }
}
