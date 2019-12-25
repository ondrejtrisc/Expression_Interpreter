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

        ArrayList<StringTree> children = new ArrayList<StringTree>();

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

        ArrayList<Integer> branchEnds = new ArrayList<Integer>();
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
        for (int j = 0; j < branchEnds.size() - 1; j++) {

            String childString = s.substring(branchEnds.get(j) + 1, branchEnds.get(j + 1));
            StringTree child = read(childString);
            children.add(child);
        }

        return new StringTree(content, children);
    }

    public String toString() {

        if (children.size() == 0) { return content; }

        String s = "";
        for (int i = 0; i < children.size(); i++) {

            if (i == children.size() - 1) { s = s + children.get(i).toString(); }
            else { s = s + children.get(i).toString() + ", "; }
        }
        return content + "(" + s + ")";
    }

    public static void main(String[] args) {

        StringTree t = read("a(b, c(d, e({f,g(h)}), g(h, i(j(k)))))");
        System.out.println(t);
    }
}
