package game;

public enum AI {
    DISABLED(0),
    MOVES_FIRST(1),
    MOVES_LAST(2);

    private int code;

    AI(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    static AI getIA(int code){
        for (AI ai : AI.values()){
            if (ai.code == code){
                return ai;
            }
        }
        throw new RuntimeException();
    }
}
