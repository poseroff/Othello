package game;

public enum Mode{
    TIME("time"),
    DEPTH("depth");

    private String keyword;

    Mode(String keyword){
        this.keyword = keyword;
    }
}
