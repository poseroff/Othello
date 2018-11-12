package game;

public enum Prune {
    ON("on"),
    OFF("off");

    private String keyword;

    Prune(String keyword){
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    static Prune getPrune(String keyword){
        for (Prune prune : Prune.values()){
            if (prune.keyword.equals(keyword)){
                return prune;
            }
        }
        throw new RuntimeException();
    }
}
