package front;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class PlayerView {
    private Color color;
    private int score;
    private IntegerProperty scoreProperty;
    private String name;

    public PlayerView(String name, Color color){
        scoreProperty = new SimpleIntegerProperty(score);
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }

    public void setScore(int points){
        scoreProperty.set(points);
    }
}
