package trisc.ondrej.oraculum;

import java.util.ArrayList;

class Expression {

    ArrayList<Expression> children;
    private String elementaryDefinition;
    private Parameter parameter;
    private Function definition;
    private Function value;

    private Expression(ArrayList<Expression> children, String elementaryDefinition, Parameter parameter) {

        this.children = children;
        this.elementaryDefinition = elementaryDefinition;
        this.parameter = parameter;
        this.definition = null;
        this.value = null;
    }
}
