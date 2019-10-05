package trisc.ondrej.oraculum;

import java.util.ArrayList;

class Tree {

    static ArrayList<Tree> T;

    private int n;
    private ArrayList<Tree> children;

    Tree(ArrayList<Tree> trees) {

        this.n = trees.size();
        this.children = trees;
        T.add(this);
    }

    public static void main(String[] args) {

    }
}
