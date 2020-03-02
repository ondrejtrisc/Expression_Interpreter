package trisc.ondrej.oraculum;

import java.util.ArrayList;

class StringTree {

    String content;
    ArrayList<StringTree> children;

    private StringTree(String content, ArrayList<StringTree> children) {

        this.content = content;
        this.children = children;
    }

    static StringTree read(String s) {

        s = s.replaceAll("\\s","");

        ArrayList<StringTree> children = new ArrayList<>();

        if (s.isEmpty()) { return new StringTree(s, children); }

        if (s.charAt(s.length() - 1) != ')') { return new StringTree(s, children); }

        //read the root
        int bracesDepth = 0;
        int i = 0;
        while (s.charAt(i) != '(' || bracesDepth != 0) {

            if (s.charAt(i) == '{') { bracesDepth++; }
            else if (s.charAt(i) == '}') { bracesDepth--; }
            i++;
        }
        String content = s.substring(0, i);

        ArrayList<Integer> branchEnds = new ArrayList<>();
        branchEnds.add(i);

        //count the children
        while (s.charAt(i) != ')') {

            i++;
            int bracketDepth = 0;
            bracesDepth = 0;
            while (bracketDepth > 0 || bracesDepth > 0 || (s.charAt(i) != ',' && s.charAt(i) != ')')) {

                if (s.charAt(i) == '(') { bracketDepth++; }
                else if (s.charAt(i) == ')') { bracketDepth--; }
                else if (s.charAt(i) == '{') { bracesDepth++; }
                else if (s.charAt(i) == '}') { bracesDepth--; }
                i++;
            }
            branchEnds.add(i);
        }

        //read the children
        for (i = 0; i < branchEnds.size() - 1; i++) {

            String childString = s.substring(branchEnds.get(i) + 1, branchEnds.get(i + 1));
            StringTree child = read(childString);
            children.add(child);
        }

        return new StringTree(content, children);
    }

    public String toString() {

        if (children.size() == 0) { return content; }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {

            if (i == children.size() - 1) { s.append(children.get(i).toString()); }
            else { s.append(children.get(i).toString()).append(", "); }
        }
        return content + "(" + s + ")";
    }

    public String evaluate() {

        ArrayList<String> ins = new ArrayList<>();
        for (StringTree child: children) { ins.add(child.evaluate()); }


        switch(content) {

            case "id":

                return ins.get(0);

            case "!":

                if (ins.get(0).equals("true")) {

                    return "false";
                }
                else if (ins.get(0).equals("false")) {

                    return "true";
                }
                return "error";

            case "&":

                String ret = "true";
                for (String in: ins) {

                    if (in.equals("false")) {

                        ret = "false";
                        break;
                    }
                    else if (in.equals("true")) {

                        continue;
                    }
                    ret = "error";
                    break;
                }
                return ret;

            case "||":

                ret = "false";
                for (String in: ins) {

                    if (in.equals("true")) {

                        ret = "true";
                        break;
                    }
                    else if (in.equals("false")) {

                        continue;
                    }
                    ret = "error";
                    break;
                }
                return ret;

            case "=":

                try {

                    String s = String.valueOf(Double.parseDouble(ins.get(0)));
                    for (int i = 1; i < ins.size(); i++) {

                        if (!s.equals(String.valueOf(Double.parseDouble(ins.get(i))))) {

                            return "false";
                        }
                    }
                    return "true";
                }
                catch (NumberFormatException e) {

                    for (int i = 1; i < ins.size(); i++) {

                        if (!ins.get(0).equals(ins.get(i))) {

                            return "false";
                        }
                    }
                    return "true";
                }

            case "<=":

                try {

                    double d1 = Double.parseDouble(ins.get(0));
                    double d2 = Double.parseDouble(ins.get(1));
                    if (d1 <= d2) {

                        return "true";
                    }
                    return "false";
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            case ">=":

                try {

                    double d1 = Double.parseDouble(ins.get(0));
                    double d2 = Double.parseDouble(ins.get(1));
                    if (d1 >= d2) {

                        return "true";
                    }
                    return "false";
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            case "<":

                try {

                    double d1 = Double.parseDouble(ins.get(0));
                    double d2 = Double.parseDouble(ins.get(1));
                    if (d1 < d2) {

                        return "true";
                    }
                    return "false";
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            case ">":

                try {

                    double d1 = Double.parseDouble(ins.get(0));
                    double d2 = Double.parseDouble(ins.get(1));
                    if (d1 > d2) {

                        return "true";
                    }
                    return "false";
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            case "if":

                if (ins.get(0).equals("true")) {

                    return ins.get(1);
                }
                return ins.get(2);

            case "+":

                try {
                    double retNum = 0;
                    for (String in: ins) {
                        retNum += Double.parseDouble(in);
                    }
                    return String.valueOf(retNum);
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            case "-":

                try {
                    return String.valueOf(Double.parseDouble(ins.get(0)) - Double.parseDouble(ins.get(1)));
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            case "*":

                try {

                    double retNum = 1;
                    for (String in: ins) {
                        retNum *= Double.parseDouble(in);
                    }
                    return String.valueOf(retNum);
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            case "/":

                try {

                    double denominator = Double.parseDouble(ins.get(1));
                    if (denominator == 0) {

                        return "error";
                    }
                    return String.valueOf(Double.parseDouble(ins.get(0)) / denominator);
                }
                catch (NumberFormatException e) {

                    return "error";
                }

            default:

                if (ins.size() == 0) { return content; }

                StringBuilder s = new StringBuilder();
                for (int i = 0; i < ins.size(); i++) {

                    if (i == ins.size() - 1) { s.append(ins.get(i)); }
                    else { s.append(ins.get(i)).append(", "); }
                }
                return content + "(" + s + ")";
        }
    }

    public static void main(String[] args) {

        StringTree t = read("if(!(=(+(*(2, 3), 4), -(/(21, 2), 0.5))), fail, success)");
        System.out.println(t.evaluate());
    }
}
