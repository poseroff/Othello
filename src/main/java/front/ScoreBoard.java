package front;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import game.OthelloGameApp;

import javax.script.Bindings;

public class ScoreBoard extends VBox {

    private PlayerView[] playerViews;
    private HBox plays;
    private OthelloGameApp othelloGameApp;

    public ScoreBoard(PlayerView player1, PlayerView player2, StringProperty turn){
        this.getChildren().add(new Text("Score"));
        this.setWidth(100);
        playerViews = new PlayerView[2];
        playerViews[0] = player1;
        playerViews[1] = player2;
        HBox player1ScoreBoard = new HBox();
        Text player1Name = new Text(player1.getName());
        Text player1Score = new Text();
        player1Score.textProperty().bind(player1.scoreProperty().asString());
        player1ScoreBoard.getChildren().addAll(player1Name, new Text(" "), player1Score);
        this.getChildren().add(player1ScoreBoard);
        HBox player2ScoreBoard = new HBox();
        Text player2Name = new Text(player2.getName());
        Text player2Score = new Text();
        player2Score.textProperty().bind(player2.scoreProperty().asString());
        player2ScoreBoard.getChildren().addAll(player2Name, new Text(" "), player2Score);
        Text text = new Text();
        text.textProperty().bind(turn);
        plays = new HBox();
        plays.getChildren().addAll(new Text("Plays "), text);
        this.getChildren().add(player2ScoreBoard);
        this.getChildren().add(plays);
        Button undo = new Button();
        undo.setText("Undo");
        undo.setOnMousePressed(event -> {
            othelloGameApp.undo();
        });
        Button save = new Button();
        save.setText("Save");
        save.setOnMousePressed(event -> {
            othelloGameApp.save();
        });
        Button export = new Button();
        export.setText("Export Tree");
        export.setOnMousePressed(event -> {
            othelloGameApp.export();
        });
        this.getChildren().addAll(undo, save, export);
    }

    public void finish(PlayerView playerView){
        plays.getChildren().clear();
        plays.getChildren().add(new Text(playerView.getName() + " Won"));
    }
}
