package trisc.ondrej.oraculum;

import java.util.ArrayList;
import java.util.Arrays;

class Expression {

    ArrayList<Expression> children;
    private String elementaryDefinition;
    private Parameter parameter;
    private Function definition;
    private Function value;

    private Expression(ArrayList<Expression> children, String elementaryDefinition, Parameter parameter, Function definition) {

        this.children = children;
        this.elementaryDefinition = elementaryDefinition;
        this.parameter = parameter;
        this.definition = definition;
        this.value = null;
    }

    private static Expression readFromStringTree(StringTree tree) {

        ArrayList<Expression> children = new ArrayList<>();
        for (StringTree child : tree.children) {

            children.add(readFromStringTree(child));
        }

        Parameter parameter = null;

        Function definition = null;

        if (!tree.content.isEmpty()) {

            if (tree.content.charAt(0) == '{') {

                int dotIndex = tree.content.indexOf('.');

                String head = tree.content.substring(1, dotIndex);
                ArrayList<String> parameterNames = new ArrayList<>(Arrays.asList(head.split(",")));
                String body = tree.content.substring(dotIndex + 1, tree.content.length() - 1);
                definition = new Function(new ArrayList<>(), Expression.read(body));
                for (String name : parameterNames) {

                    definition.parameters.add(new Parameter(name, null));
                }
                definition.expression.referenceParameters(definition, 0);
            }
            else if (tree.content.charAt(0) == '[') {

                String s = tree.content.substring(1, tree.content.length() - 1);
                parameter = new Parameter("", read(s));
            }
        }

        return new Expression(children, tree.content, parameter, definition);
    }

    private void referenceParameters(Function context, int depth) {

        if (definition != null) {

            definition.expression.referenceParameters(context, depth + 1);
        }
        else if (!elementaryDefinition.isEmpty()){

            for (Parameter p : context.parameters) {

                if (elementaryDefinition.equals(p.name)) {

                    this.parameter = p;
                    break;
                }
            }
        }
        for (Expression child : children) {

            child.referenceParameters(context, depth);
        }
    }

    static Expression read(String s) {

        StringTree tree = StringTree.read(s);
        return readFromStringTree(tree);
    }
}
