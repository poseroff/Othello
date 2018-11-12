package game;

public enum Mode{
    TIME("time"),
    DEPTH("depth");

    private String keyword;

    Mode(String keyword){
        this.keyword = keyword;
    }

    static Mode getMode(String keyword){
        for (Mode mode : Mode.values()){
            if (mode.keyword.equals(keyword)){
                return mode;
            }
        }
        throw new RuntimeException();
    }
}
