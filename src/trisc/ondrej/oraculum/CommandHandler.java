package trisc.ondrej.oraculum;

class CommandHandler {

    private String result;

    void handleCommand(String cmd) {

        System.out.println(cmd);
        result = StringTree.read(cmd).evaluate();
    }

    String getResult() { return result; }
}
