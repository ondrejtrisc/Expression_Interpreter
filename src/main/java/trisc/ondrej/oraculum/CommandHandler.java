package trisc.ondrej.oraculum;

import java.util.ArrayList;

class CommandHandler {

    private ArrayList<String> result;

    void handleCommand(String cmd) {

        System.out.println(cmd);
        Expression expression = Expression.read(cmd);
        expression.evaluate();
        result = expression.writeEvaluation();
    }

    ArrayList<String> getResult() { return result; }
}
