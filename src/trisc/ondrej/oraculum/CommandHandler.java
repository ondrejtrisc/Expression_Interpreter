package trisc.ondrej.oraculum;

class CommandHandler {

    private String result;

    void handleCommand(String cmd) {

        System.out.println(cmd);
        result = Expression.read(cmd).evaluate().write();
    }

    String getResult() { return result; }
}
