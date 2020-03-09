package trisc.ondrej.oraculum;

class Parameter {

    String name;
    Expression substituent;

    Parameter(String name, Expression substituent) {

        this.name = name;
        this.substituent = substituent;
    }
}
