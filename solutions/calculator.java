import java.util.*;
import java.text.DecimalFormat;

/*
 * https://open.kattis.com/problems/calculator
 * 
 * This is a bit over-engineered for the task.
 * But hey, we have put a "Visitor" pattern to a great use here!
 */
 
enum NodeType {
    CONSTANT,
    BINARY,
    GROUP
}

enum BinaryOperator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE
}

abstract class Node {
    public abstract NodeType getNodeType();
}

class ConstantNode extends Node {
    private final double value;

    public NodeType getNodeType() {
        return NodeType.CONSTANT;
    }

    public double getValue() {
        return value;
    }

    public ConstantNode(double value) {
        this.value = value;
    }
}

class BinaryNode extends Node {
    private final Node left;
    private final Node right;
    private final BinaryOperator operator;

    public NodeType getNodeType() {
        return NodeType.BINARY;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public BinaryOperator getOperator() {
        return operator;
    }
    
    public BinaryNode(Node left, Node right, BinaryOperator op) {
        this.left = left;
        this.right = right;
        this.operator = op;
    }
}

class NodeGroup extends Node {
    private final Node node;

    public NodeType getNodeType() {
        return NodeType.GROUP;
    }
    
    public Node getNode() {
        return node;
    }

    public NodeGroup(Node node) {
        this.node = node;
    }
}

class Tuple<X, Y> { 
    public final X x; 
    public final Y y; 

    public Tuple(X x, Y y) { 
        this.x = x; 
        this.y = y; 
    } 
} 

public class calculator {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        StringBuilder output = new StringBuilder();

        while (true) {
            String input = s.next();

            if (input == null || input.trim().length() == 0) {
                break;
            }

            double constant = 0D;
            boolean isParsed = false;

            try {
                constant = Double.parseDouble(input);
                isParsed = true;
            } catch (NumberFormatException ex) { }

            if (isParsed) {
                output.append(Double.toString(constant) + "\n");
            } else {
                Node expr = parse(input);

                if (expr != null) {
                    double evaluated = eval(expr);
                    output.append(new DecimalFormat("#.00").format(evaluated).replaceAll(",", "") + "\n");
                }
            }
        }

        System.out.println(output.toString());

        s.close();
    }

    private static Node parse(String input) {
        return parseInternal(input, 0).x;
    }

    private static double eval(Node node) {
        switch (node.getNodeType()) {
            case CONSTANT:
                return ((ConstantNode)node).getValue();
            case BINARY:
                BinaryNode n = (BinaryNode)node;
                
                double left = eval(n.getLeft());
                double right = eval(n.getRight());

                switch (n.getOperator()) {
                    case DIVIDE:
                        return left / right;
                    case MULTIPLY:
                        return left * right;
                    case PLUS:
                        return left + right;
                    case MINUS:
                        return left - right;
                }

                return 0D;
            case GROUP:
                return eval(((NodeGroup)node).getNode());
            default: return 0D;
        }
    }

    private static Tuple<Node, Integer> parseInternal(String input, int start) {
        int i = start;
        Node n = null;

        while (i < input.length()) {
            i = advance(input, i, false);

            if (i < input.length()) {
                char ch = input.charAt(i);

                if (ch == '(') {
                    Tuple<Node, Integer> parsedInner = parseInternal(input, i + 1);

                    i = parsedInner.y;
                    i = advance(input, i, false);

                    if (i < input.length() && input.charAt(i) == ')') {
                        n = new NodeGroup(parsedInner.x);
                    }

                    i++;
                } else if (ch == ')') {
                    i--;
                    break;
                } else {
                    Tuple<BinaryOperator, Boolean> binaryOp = parseBinaryOperator(ch);

                    if (binaryOp.y) {
                        if (n == null) {
                            Tuple<ConstantNode, Integer> parsedConstant = parseConstant(input, i);

                            n = parsedConstant.x;
                            i = parsedConstant.y;
                            i = advance(input, i, false);
                        } else {
                            Tuple<Node, Integer> parsedInner = parseInternal(input, i + 1);
                            
                            if (parsedInner.x instanceof BinaryNode) {
                                BinaryNode b = (BinaryNode)parsedInner.x;

                                if (isLowPriorityOperator(b.getOperator()) || !isLowPriorityOperator(binaryOp.x)) {
                                    Node origNode = n;

                                    n = new BinaryNode(
                                        new BinaryNode(n, b.getLeft(), binaryOp.x),
                                        b.getRight(), b.getOperator()
                                    );

                                    BinaryNode r = b;
                                    Node leafLeft = b;
                                    Stack<Tuple<BinaryOperator, Node>> rearrange = new Stack<>();

                                    while (r != null && (isLowPriorityOperator(r.getOperator()) || !isLowPriorityOperator(binaryOp.x))) {
                                        rearrange.push(new Tuple<BinaryOperator, Node>(r.getOperator(), r.getRight()));
                                        leafLeft = r.getLeft();
                                        r = (BinaryNode)r.getLeft();
                                    }

                                    n = new BinaryNode(origNode, leafLeft, binaryOp.x);

                                    while (rearrange.size() > 0) {
                                        Tuple<BinaryOperator, Node> nextRearrange = rearrange.pop();
                                        n = new BinaryNode(n, nextRearrange.y, nextRearrange.x);
                                    }
                                } else {
                                    n = new BinaryNode(n, b, binaryOp.x);
                                }
                            } else {
                                n = new BinaryNode(n, parsedInner.x, binaryOp.x);
                            }

                            i = parsedInner.y;
                            i = advance(input, i, false);
                        }
                    } else {
                        Tuple<ConstantNode, Integer> parsedConstant = parseConstant(input, i);

                        n = parsedConstant.x;
                        i = parsedConstant.y;
                        i = advance(input, i, false);
                    }
                }
            }
        }

        return new Tuple<Node, Integer>(n, i + 1);
    }

    private static Tuple<ConstantNode, Integer> parseConstant(String input, int start) {
        int i = start;
        boolean seenNumber = false;
        boolean isNegativeSign = false;
        StringBuilder raw = new StringBuilder();

        while (i < input.length()) {
            i = advance(input, i, false);

            if (i < input.length()) {
                char ch = input.charAt(i);

                if (Character.isDigit(ch) || ch == '.' || ch == ',') {
                    raw.append(ch);
                    seenNumber = true;
                    i = advance(input, i, true);
                } else {
                    Tuple<BinaryOperator, Boolean> binaryOp = parseBinaryOperator(ch);

                    if (binaryOp.y && !seenNumber) {
                        if (binaryOp.x == BinaryOperator.MINUS) {
                            isNegativeSign = !isNegativeSign;
                        }

                        i = advance(input, i, true);
                    } else {
                        i--;
                        break;
                    }
                }
            }
        }

        return new Tuple<ConstantNode, Integer>(raw.length() > 0 ?
            new ConstantNode(Double.parseDouble(raw.toString()) * (isNegativeSign ? -1 : 1)) : null, i + 1);
    }

    private static int advance(String input, int i, boolean stepOverCurrent) {
        if (stepOverCurrent) {
            i++;
        }

        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }

        return i;
    }

    private static Tuple<BinaryOperator, Boolean> parseBinaryOperator(char ch) {
        if (ch == '+') {
            return new Tuple<>(BinaryOperator.PLUS, true);
        } else if (ch == '-') {
            return new Tuple<>(BinaryOperator.MINUS, true);
        } else if (ch == '/') {
            return new Tuple<>(BinaryOperator.DIVIDE, true);
        } else if (ch == '*') {
            return new Tuple<>(BinaryOperator.MULTIPLY, true);
        } else {
            return new Tuple<>(BinaryOperator.PLUS, false);
        }
    }

    private static boolean isLowPriorityOperator(BinaryOperator op) {
        return op == BinaryOperator.PLUS || op == BinaryOperator.MINUS;
    }
}