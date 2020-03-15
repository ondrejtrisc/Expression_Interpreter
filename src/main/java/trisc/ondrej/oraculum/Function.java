package trisc.ondrej.oraculum;

import java.util.ArrayList;

class Function {

    ArrayList<Parameter> parameters;
    Expression expression;

    Function(ArrayList<Parameter> parameters, Expression expression) {

        this.parameters = parameters;
        this.expression = expression;
    }

    Function copy() {

        ArrayList<Expression> originals = new ArrayList<>();
        ArrayList<Expression> copies = new ArrayList<>();
        ArrayList<Function> originalDefinitions = new ArrayList<>();
        ArrayList<Function> copyDefinitions = new ArrayList<>();

        Function ret = new Function(new ArrayList<>(), null);
        originalDefinitions.add(this);
        copyDefinitions.add(ret);

        ret.expression = this.expression.preCopy(originals, copies, originalDefinitions, copyDefinitions);

        for (Parameter parameter : this.parameters) {

            ret.parameters.add(new Parameter(parameter.name, null));
        }

        ArrayList<Expression> visited = new ArrayList<>();

        ret.expression.copyParameters(visited, originalDefinitions, copyDefinitions);

        return ret;
    }

    String write() {

        if (this.parameters.size() == 0) {

            if (this.expression.children.size() == 0) {

                return this.expression.write();
            }
            return "{" + this.expression.write() + "}";
        }

        StringBuilder head = new StringBuilder(this.parameters.get(0).name);
        for (int i = 1; i < this.parameters.size(); i++) {

            head.append(",").append(this.parameters.get(i).name);
        }
        return "{" + head + "." + this.expression.write() + "}";
    }

    Function evaluate(ArrayList<Expression> ins) {

        for (int i = 0; i < Math.min(this.parameters.size(), ins.size()); i++) {

            this.parameters.get(i).substituent = ins.get(i);
        }

        ArrayList<Integer> indicesOfSubstitution = new ArrayList<>();
        for (int i = 0; i < ins.size(); i++) {

            if (ins.get(i).nonEmpty()) {

                indicesOfSubstitution.add(i);
            }
        }

        if ((this.parameters.size() == ins.size()) && (this.parameters.size() == indicesOfSubstitution.size())) {

            return this.expression.evaluate();
        }

        Function ret = this.copy();

        for (int i = 0; i < Math.min(ret.parameters.size(), ins.size()); i++) {

            ret.parameters.get(i).substituent = ins.get(i);
        }
        ret.expression.substitute(ret, indicesOfSubstitution);
        for (Parameter parameter : ret.parameters) {

            parameter.substituent = null;
        }

        ArrayList<Parameter> newParameters = new ArrayList<>();
        for (int i = 0; i < ret.parameters.size(); i++) {

            if (!indicesOfSubstitution.contains(i)) {

                newParameters.add(ret.parameters.get(i));
            }
        }
        ret.parameters = newParameters;

        return ret;
    }
}
