package trisc.ondrej.oraculum;

import java.util.ArrayList;

class Tree {

    static ArrayList<Tree> T = new ArrayList<Tree>();

    private int n;
    private ArrayList<Tree> children;

    Tree(ArrayList<Tree> trees) {

        this.n = trees.size();
        this.children = trees;
        Tree.T.add(this);
    }

    public static void main(String[] args) {

        Tree leaf = new Tree(new ArrayList<Tree>());
    }
}
