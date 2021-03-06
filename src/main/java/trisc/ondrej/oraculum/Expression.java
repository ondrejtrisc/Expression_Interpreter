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
                parameter = new Parameter("", Expression.read(s));
            }
        }

        return new Expression(children, tree.content, parameter, definition);
    }

    private void referenceParameters(Function context, int depth) {

        if (this.definition != null) {

            this.definition.expression.referenceParameters(context, depth + 1);
        }
        else if (!this.elementaryDefinition.isEmpty()){

            for (Parameter p : context.parameters) {

                if (this.elementaryDefinition.equals(p.name)) {

                    this.parameter = p;
                    break;
                }
            }
        }
        for (Expression child : this.children) {

            child.referenceParameters(context, depth);
        }
    }

    static Expression read(String s) {

        StringTree tree = StringTree.read(s);
        return Expression.readFromStringTree(tree);
    }

    boolean nonEmpty() {

        return !(this.elementaryDefinition.isEmpty() && this.definition == null && this.value == null);
    }

    Expression preCopy(ArrayList<Expression> originals, ArrayList<Expression> copies, ArrayList<Function> originalDefinitions, ArrayList<Function> copyDefinitions) {

        if (originals.indexOf(this) == -1) {

            Expression ret = new Expression(new ArrayList<>(), this.elementaryDefinition, this.parameter, null);

            originals.add(this);
            copies.add(ret);

            if (this.definition != null) {

                if (originalDefinitions.indexOf(this.definition) == -1) {

                    ret.definition = new Function(new ArrayList<>(), null);
                    originalDefinitions.add(this.definition);
                    copyDefinitions.add(ret.definition);

                    ret.definition.expression = this.definition.expression.preCopy(originals, copies, originalDefinitions, copyDefinitions);

                    for (Parameter parameter : this.definition.parameters) {

                        ret.definition.parameters.add(new Parameter(parameter.name, null));
                    }
                }
                else {

                    ret.definition = copyDefinitions.get(originalDefinitions.indexOf(this.definition));
                }
            }
            for (Expression child : this.children) {

                ret.children.add(child.preCopy(originals, copies, originalDefinitions, copyDefinitions));
            }
            return ret;
        }
        else {

            return copies.get(originals.indexOf(this));
        }
    }

    void copyParameters(ArrayList<Expression> visited, ArrayList<Function> originalDefinitions, ArrayList<Function> copyDefinitions) {

        if (visited.indexOf(this) == -1) {

            visited.add(this);

            if (this.parameter != null) {

                for (Function function : originalDefinitions) {

                    for (int i = 0; i < function.parameters.size(); i++) {

                        if (this.parameter == function.parameters.get(i)) {

                            this.parameter = copyDefinitions.get(originalDefinitions.indexOf(function)).parameters.get(i);
                        }
                    }
                }
            }
            if (this.definition != null) {

                this.definition.expression.copyParameters(visited, originalDefinitions, copyDefinitions);
            }
            for (Expression child : children) {

                child.copyParameters(visited, originalDefinitions, copyDefinitions);
            }
        }
    }

    void substitute(Function context, ArrayList<Integer> indicesOfSubstitution) {

        if (this.parameter != null && context.parameters.contains(this.parameter)) {

            int in = context.parameters.indexOf(this.parameter);

            if (indicesOfSubstitution.contains(in)) {

                this.parameter = new Parameter("", this.parameter.substituent);
                this.elementaryDefinition = "";
            }
        }
        if (this.definition != null) {

            this.definition.expression.substitute(context, indicesOfSubstitution);
        }
        for (Expression child : this.children) {

            child.substitute(context, indicesOfSubstitution);
        }
    }

    String write() {

        String rootString;
        if (this.parameter != null) {

            Expression referent = this.parameter.substituent;
            if (referent != null) {

                if (referent.children.size() == 0 || this.children.size() == 0) {

                    rootString = referent.write();
                }
                else {

                    rootString = "[" + referent.write() + "]";
                }
            }
            else {

                rootString = this.elementaryDefinition;
            }
        }
        else if (this.definition != null) {

            ArrayList<Expression> ins = new ArrayList<>();
            for (Parameter parameter : this.definition.parameters) {

                ins.add(parameter.substituent);
            }
            for (Parameter parameter : this.definition.parameters) {

                parameter.substituent = null;
            }
            rootString = this.definition.write();
            for (Parameter parameter : this.definition.parameters) {

                parameter.substituent = ins.get(this.definition.parameters.indexOf(parameter));
            }
        }
        else {

            rootString = this.elementaryDefinition;
        }

        if (this.children.size() == 0) {

            return rootString;
        }

        StringBuilder childrenString = new StringBuilder(this.children.get(0).write());
        for (int i = 1; i < this.children.size(); i++) {

            childrenString.append(", ").append(this.children.get(i).write());
        }
        return rootString + "(" + childrenString + ")";
    }

    Function evaluate() {

        if (this.value != null) {

            return this.value;
        }

        Function ret = null;

        if (this.children.size() == 0) {

            if (this.definition != null) {

                ret = this.definition;
            }
            else if (this.parameter != null) {

                this.definition = this.parameter.substituent.evaluate();
                ret = this.definition;
            }
            else {

                ret = new Function(new ArrayList<>(), this);
            }
        }
        else if (this.definition != null) {

            ret = this.definition.evaluate(this.children);
        }
        else if (this.parameter != null) {

            this.definition = this.parameter.substituent.evaluate().copy();
            ret = this.definition.evaluate(this.children);
        }
        else {

            String retString = null;

            switch(this.elementaryDefinition) {

                case "eval":

                    ArrayList<Expression> ins;
                    if (this.children.size() > 1) {

                        ins = new ArrayList<>(this.children.subList(1, this.children.size()));
                    }
                    else {

                        ins = new ArrayList<>();
                    }
                    ret = this.children.get(0).evaluate().copy().evaluate(ins);
                    break;

                case "id":

                    ret = this.children.get(0).evaluate();
                    break;

                case "!":

                    if (this.children.get(0).evaluate().write().equals("true")) {

                        retString = "false";
                    }
                    else if (this.children.get(0).evaluate().write().equals("false")) {

                        retString = "true";
                    }
                    else {

                        retString = "error";
                    }
                    break;

                case "&":

                    retString = "true";
                    for (Expression child : this.children) {

                        if (child.evaluate().write().equals("false")) {

                            retString = "false";
                            break;
                        }
                        else if (child.evaluate().write().equals("true")) {

                            continue;
                        }
                        retString = "error";
                        break;
                    }
                    break;

                case "||":

                    retString = "false";
                    for (Expression child : this.children) {

                        if (child.evaluate().write().equals("true")) {

                            retString = "true";
                            break;
                        }
                        else if (child.evaluate().write().equals("false")) {

                            continue;
                        }
                        retString = "error";
                        break;
                    }
                    break;

                case "=":

                    try {

                        String s = String.valueOf(Double.parseDouble(this.children.get(0).evaluate().write()));
                        retString = "true";
                        for (int i = 1; i < this.children.size(); i++) {

                            if (!s.equals(String.valueOf(Double.parseDouble(this.children.get(i).evaluate().write())))) {

                                retString = "false";
                                break;
                            }
                        }
                    }
                    catch (NumberFormatException e) {

                        String s = this.children.get(0).evaluate().write();
                        retString = "true";
                        for (int i = 1; i < this.children.size(); i++) {

                            if (!s.equals(this.children.get(i).evaluate().write())) {

                                retString = "false";
                                break;
                            }
                        }
                    }
                    break;

                case "<=":

                    try {

                        double d1 = Double.parseDouble(this.children.get(0).evaluate().write());
                        double d2 = Double.parseDouble(this.children.get(1).evaluate().write());
                        if (d1 <= d2) {

                            retString = "true";
                        }
                        else {

                            retString = "false";
                        }
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                case ">=":

                    try {

                        double d1 = Double.parseDouble(this.children.get(0).evaluate().write());
                        double d2 = Double.parseDouble(this.children.get(1).evaluate().write());
                        if (d1 >= d2) {

                            retString = "true";
                        }
                        else {

                            retString = "false";
                        }
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                case "<":

                    try {

                        double d1 = Double.parseDouble(this.children.get(0).evaluate().write());
                        double d2 = Double.parseDouble(this.children.get(1).evaluate().write());
                        if (d1 < d2) {

                            retString = "true";
                        }
                        else {

                            retString = "false";
                        }
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                case ">":

                    try {

                        double d1 = Double.parseDouble(this.children.get(0).evaluate().write());
                        double d2 = Double.parseDouble(this.children.get(1).evaluate().write());
                        if (d1 > d2) {

                            retString = "true";
                        }
                        else {

                            retString = "false";
                        }
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                case "if":

                    if (this.children.get(0).evaluate().write().equals("true")) {

                        ret = this.children.get(1).evaluate();
                    }
                    else if (this.children.get(0).evaluate().write().equals("false")) {

                        ret = this.children.get(2).evaluate();
                    }
                    else {

                        retString = "error";
                    }
                    break;

                case "+":

                    try {

                        double retNum = 0;
                        for (Expression child : this.children) {

                            retNum += Double.parseDouble(child.evaluate().write());
                        }
                        retString = String.valueOf(retNum);
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                case "-":

                    try {

                        retString = String.valueOf(Double.parseDouble(this.children.get(0).evaluate().write()) - Double.parseDouble(this.children.get(1).evaluate().write()));
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                case "*":

                    try {

                        double retNum = 1;
                        for (Expression child : this.children) {

                            retNum *= Double.parseDouble(child.evaluate().write());
                        }
                        retString = String.valueOf(retNum);
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                case "/":

                    try {

                        double denominator = Double.parseDouble(this.children.get(1).evaluate().write());
                        if (denominator == 0) {

                            retString = "error";
                        }
                        else {

                            retString = String.valueOf(Double.parseDouble(this.children.get(0).evaluate().write()) / denominator);
                        }
                    }
                    catch (NumberFormatException e) {

                        retString = "error";
                    }
                    break;

                default:

                    StringBuilder s = new StringBuilder();
                    for (int i = 0; i < this.children.size(); i++) {

                        if (i == this.children.size() - 1) {

                            s.append(this.children.get(i).evaluate().write());
                        }
                        else {

                            s.append(this.children.get(i).evaluate().write()).append(", ");
                        }
                    }
                    retString = this.elementaryDefinition + "(" + s + ")";
            }

            if (retString != null) {

                ret = new Function(new ArrayList<>(), Expression.read(retString));
            }
        }

        this.value = ret;
        return this.value;
    }

    private ArrayList<String> writeSubEvaluation() {

        Expression referent = this.parameter.substituent;
        ArrayList<String> referentEvaluation = referent.writeEvaluation();

        if (this.children.size() == 0) {

            return referentEvaluation;
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.children.size() - 1; i++) {

            s.append(this.children.get(i).write()).append(", ");
        }
        s.append(this.children.get(this.children.size() - 1).write());

        ArrayList<String> ret = new ArrayList<>();
        String rootString;
        for (String line : referentEvaluation) {

            if (line.charAt(line.length() - 1) == ')') {

                rootString = "[" + line + "]";
            }
            else {

                rootString = line;
            }
            ret.add(rootString + "(" + s + ")");
        }

        return ret;
    }

    private boolean nontrivialEvaluation() {

        if (this.children.size() > 0) {

            return true;
        }
        if (this.parameter == null) {

            return false;
        }
        return this.parameter.substituent.nontrivialEvaluation();
    }

    private ArrayList<String> writeEvaluation(ArrayList<String> previousPart) {

        if (this.definition != null) {

            ArrayList<Integer> indicesOfSubstitution = new ArrayList<>();
            for (int i = 0; i < this.children.size(); i++) {

                if (this.children.get(i).nonEmpty()) {

                    indicesOfSubstitution.add(i);
                }
            }

            if (this.definition.parameters.size() <= indicesOfSubstitution.size()) {

                if (this.parameter == null) {

                    previousPart.add(this.write());
                    return this.definition.expression.writeEvaluation(previousPart);
                }

                previousPart.addAll(this.writeSubEvaluation());

                if (this.children.size() > 0) {

                    return this.definition.expression.writeEvaluation(previousPart);
                }
                return previousPart;
            }

            if (children.size() == 0) {

                previousPart.add(this.write());
                return previousPart;
            }
        }

        previousPart.add(this.write());

        if (this.definition == null) {

            ArrayList<String> childrenValues = new ArrayList<>();
            for (int i = 0; i < this.children.size(); i++) {

                Expression child = this.children.get(i);

                if (child.value != null && child.nontrivialEvaluation()) {

                    ArrayList<String> childEvaluation = child.writeEvaluation();
                    for (int j = 1; j < childEvaluation.size(); j++) {

                        String line = childEvaluation.get(j);
                        StringBuilder retLine = new StringBuilder(this.elementaryDefinition + "(");
                        for (String childValue : childrenValues) {

                            retLine.append(childValue).append(", ");
                        }
                        retLine.append(line);
                        for (int k = i + 1; k < this.children.size(); k++) {

                            retLine.append(", ").append(this.children.get(k).write());
                        }
                        retLine.append(")");

                        previousPart.add(retLine.toString());
                    }
                    childrenValues.add(child.value.write());
                }
                else {

                    childrenValues.add(child.write());
                }
            }
        }

        if (this.value != null && !this.value.write().equals(this.write())) {

            previousPart.add(this.value.write());
        }
        else if (!this.elementaryDefinition.equals(this.write())) {

            previousPart.add(this.elementaryDefinition);
        }

        return previousPart;
    }

    ArrayList<String> writeEvaluation() {

        return this.writeEvaluation(new ArrayList<>());
    }

    public static void main(String[] args) {

        //String s = "{a,x.{r,y.a({z.r(r, z)}, y)}({r,y.a({z.r(r, z)}, y)}, x)}({f,n.if(=(n, 0), 1, *(n, f(-(n, 1))))}, 5)";
        String s = "[{a.{t.a(t(t))}({t.a(t(t))})}({f,n.if(=(n, 0), 1, *(n, f(-(n, 1))))})](5)";
        //String s = "{a,b,c,d,e,f.a(b(c, d), e(f))}(,, 2,,,3)";
        Expression expression = read(s);
        expression.evaluate();
        ArrayList<String> evaluation = expression.writeEvaluation();
        for (String line : evaluation) { System.out.println(line); }
    }
}
