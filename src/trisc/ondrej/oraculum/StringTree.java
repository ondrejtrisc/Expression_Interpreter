package trisc.ondrej.oraculum;

import java.util.ArrayList;

public class StringTree {

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

            case "=":

                try {

                    String s = String.valueOf(Double.parseDouble(ins.get(0)));
                    for (int i = 1; i < ins.size(); i++) {
                        if (!s.equals(String.valueOf(Double.parseDouble(ins.get(i))))) { return "false"; }
                    }
                    return "true";
                }
                catch (NumberFormatException e) {

                    for (int i = 1; i < ins.size(); i++) {
                        if (!ins.get(0).equals(ins.get(i))) { return "false"; }
                    }
                    return "true";
                }

            case "!":

                if (ins.get(0).equals("true")) { return "false"; }
                return "true";

            case "&":

                for (String in: ins) {
                    if (in.equals("false")) { return "false"; }
                }
                return "true";

            case "||":

                for (String in: ins) {
                    if (in.equals("true")) { return "true"; }
                }
                return "false";

            case "if":

                if (ins.get(0).equals("true")) { return ins.get(1); }
                return ins.get(2);

            case "+":

                double ret = 0;
                for (String in: ins) {
                    ret += Double.parseDouble(in);
                }
                return String.valueOf(ret);

            case "-":

                return String.valueOf(Double.parseDouble(ins.get(0)) - Double.parseDouble(ins.get(1)));

            case "*":

                ret = 1;
                for (String in: ins) {
                    ret *= Double.parseDouble(in);
                }
                return String.valueOf(ret);

            case "/":

                return String.valueOf(Double.parseDouble(ins.get(0)) / Double.parseDouble(ins.get(1)));

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
