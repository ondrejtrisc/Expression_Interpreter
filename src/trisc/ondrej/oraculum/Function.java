package trisc.ondrej.oraculum;

import java.util.ArrayList;

class Function {

    ArrayList<Parameter> parameters;
    Expression expression;

    Function(ArrayList<Parameter> parameters, Expression expression) {

        this.parameters = parameters;
        this.expression = expression;
    }
}
