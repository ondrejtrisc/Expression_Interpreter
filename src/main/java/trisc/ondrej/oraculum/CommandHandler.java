package trisc.ondrej.oraculum;

import java.util.ArrayList;

class CommandHandler {

    private ArrayList<String> result;

    void handleCommand(String cmd) {

        System.out.println(cmd);
        try {

            Expression expression = Expression.read(cmd);
            expression.evaluate();
            result = expression.writeEvaluation();
        }
        catch (Exception e) {

            ArrayList<String> ret = new ArrayList<>();
            ret.add("error");
            result = ret;
        }
    }

    ArrayList<String> getResult() {

        return result;
    }
}
