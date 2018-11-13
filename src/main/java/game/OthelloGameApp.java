package game;

import back.ConstantValues;
import back.Game;
import front.OthelloGameBoardView;
import front.PlayerView;
import front.ScoreBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;


public class OthelloGameApp extends Application {

    private static final String appTitle = "Othello";
    private static final String appIcon = "othello.png";

    private OthelloGameBoardView othelloGameBoardView;
    private PlayerView player1;
    private PlayerView player2;
    private Game game;
    private StringProperty turn;
    private ScoreBoard scoreBoard;
    private static AI ai;
    private static Mode mode;
    private static Prune prune;
    private static Integer param;
    private static Integer size;
    private static String file;
    private static boolean ok = true;

    @Override
    public void start(Stage primaryStage) {
        boolean notDef = false;

        ai = AI.getIA(1);
        size = 4;
        prune = Prune.OFF;
        mode = Mode.DEPTH;
        param = 5;

        /*if (!ok) {
            System.exit(1);
            return;
        }
        if (size == null && file == null){
            System.out.println("Must either specify a board size or load a board");
            notDef = true;
        }
        if (size != null && file != null){
            System.out.println("Size and loading cannot be performed simultanously");
            notDef = true;
        }
        if (mode == null){
            System.out.println("Mode should be specified");
            notDef = true;
        }
        if (param == null){
            System.out.println("Must specify param");
            notDef = true;
        }
        if (ai == null){
            System.out.println("Must specify ia");
            notDef = true;
        }
        if (notDef){
            System.exit(1);
            return;
        }*/
        primaryStage.setTitle(appTitle);
        //primaryStage.getIcons().add(new Image(appIcon));
        createGame(primaryStage);
        primaryStage.show();
    }

    public void createGame(Stage primaryStage){
        game = new Game(size, ai.getCode(), mode.getKeyword(), param, prune.getKeyword());
        othelloGameBoardView = new OthelloGameBoardView(size, this);
        player1 = new PlayerView("BLACK", Color.BLACK);
        player2 = new PlayerView("WHITE", Color.WHITE);
        turn = new SimpleStringProperty();
        turn.set(player2.getName());
        HBox hBox = new HBox();
        scoreBoard = new ScoreBoard(player1, player2, turn);
        hBox.getChildren().addAll(othelloGameBoardView, scoreBoard);
        setGameBoard();
        primaryStage.setScene(new Scene(hBox));
    }

    public void undo(){
        game.undo();
    }

    public void save(){
        try {
            game.saveGame("game.oga");
        } catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("No se pudo guardar, intente de nuevo");
        }

    }

    private String getPlayerName(){
        return game.getCurrentPlayer() == ConstantValues.BLACK ? player1.getName() : player2.getName();
    }

    public void export(){
        game.exportLastTree("tree");
    }

    public void placeChip(int i, int j){
        boolean result;
        game.placeChip(i, j);
        result=game.nextTurn(); //AGREGADO VER SI SACARLO
        switch (game.getCurrentPlayer()){
            case ConstantValues.BLACK:
                turn.setValue(player1.getName());
                break;
            case ConstantValues.WHITE:
                turn.setValue(player2.getName());
                break;
        }
        player1.setScore(game.getBoard().getBlackScore());
        player2.setScore(game.getBoard().getWhiteScore());
        setGameBoard();
        System.out.println(result);
        if(!result)  //AGREGADO VER SI SACARLO
            game.nextTurn(); //AGREGADO VER SI SACARLO
        if (!game.gameIsNotOver()){
            if (game.getBoard().getBlackScore() >= game.getBoard().getWhiteScore()){
                scoreBoard.finish(player1);
            } else {
                scoreBoard.finish(player2);
            }
            System.out.println("Que no pare la fiesta");
        }

    }

    public PlayerView getCurrentPlayer(){
        PlayerView currentPlayer;
        switch (game.getCurrentPlayer()) {
            case ConstantValues.BLACK:
                currentPlayer = player1;
                break;
            case ConstantValues.WHITE:
                currentPlayer = player2;
                break;
            default:
                return null;
        }
        return currentPlayer;
    }

    public void setGameBoard(){
        int size = game.getBoard().getSize();
        int board[][] = game.getBoard().getBoard();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                switch (board[i][j]){
                    case ConstantValues.BLACK:
                        othelloGameBoardView.placeChip(player1, i, j);
                        break;
                    case ConstantValues.WHITE:
                        othelloGameBoardView.placeChip(player2, i, j);
                        break;
                    case ConstantValues.AVAILABLE:
                        othelloGameBoardView.setAvailable(i, j);
                        break;
                    default:
                        othelloGameBoardView.setUnavailable(i, j);
                        break;
                }
            }
        }
    }

    /*public Pane selectBoard(Stage primaryStage){
        VBox vBox = new VBox();
        Button four = new Button();
        four.setText("4x4");
        four.setMinWidth(300);
        four.setMaxWidth(300);
        four.setMinHeight(75);
        four.setMaxHeight(75);
        four.setOnMouseClicked(event -> {
            createGame(primaryStage, 4);
        });
        Button six = new Button();
        six.setText("6x6");
        six.setMinWidth(300);
        six.setMaxWidth(300);
        six.setMinHeight(75);
        six.setMaxHeight(75);
        six.setOnMouseClicked(event -> {
            createGame(primaryStage, 6);
        });
        Button eight = new Button();
        eight.setText("8x8");
        eight.setMinWidth(300);
        eight.setMaxWidth(300);
        eight.setMinHeight(75);
        eight.setMaxHeight(75);
        eight.setOnMouseClicked(event -> {
            createGame(primaryStage, 8);
        });
        Button ten = new Button();
        ten.setText("10x10");
        ten.setMinWidth(300);
        ten.setMaxWidth(300);
        ten.setMinHeight(75);
        ten.setMaxHeight(75);
        ten.setOnMouseClicked(event -> {
            createGame(primaryStage, 10);
        });
        vBox.setMinHeight(300);
        vBox.setMinWidth(300);
        vBox.setMaxHeight(300);
        vBox.setMaxHeight(300);
        vBox.getChildren().addAll(four, six, eight, ten);
        return vBox;
    }*/

    public static void main(String[] args){
        int i;
        for (i = 0; i < args.length && ok; i++){
            switch(args[i]){
                case "-ai":
                    i++;
                    if (i >= args.length || args[i].charAt(0) == '-'){
                        System.out.println("Wrong AI argument");
                        ok = false;
                    } else {
                        try {
                            ai = AI.getIA(Integer.valueOf(args[i]));
                        }catch (Exception e){
                            System.out.println("Wrong IA argument");
                            ok = false;
                        }
                    }
                    break;
                case "-mode":
                    i++;
                    if (i >= args.length || args[i].charAt(0) == '-'){
                        System.out.println("Wrong Mode argument");
                        ok = false;
                    } else {
                        try {
                            mode = Mode.getMode(args[i]);
                        }catch (Exception e){
                            System.out.println("Wrong Mode argument");
                            ok = false;
                        }
                    }
                    break;

                case "-prune":
                    i++;
                    if (i >= args.length || args[i].charAt(0) == '-'){
                        System.out.println("Wrong Prune argument");
                        ok = false;
                    } else {
                        try {
                            prune = Prune.getPrune(args[i]);
                        }catch (Exception e){
                            System.out.println("Wrong Prune argument");
                            ok = false;
                        }
                    }
                    break;
                case "-size":
                    i++;
                    if (i >= args.length || args[i].charAt(0) == '-'){
                        System.out.println("Wrong Size argument");
                    } else {
                        try {
                            size = Integer.valueOf(args[i]);
                            switch (size){
                                case 4:
                                case 6:
                                case 8:
                                case 10:
                                    break;
                                default:
                                    ok = false;
                                    System.out.println("Size must be 4, 6, 8 or 10");
                                    break;
                            }
                        }catch (Exception e){
                            System.out.println("Wrong Size argument");
                            ok = false;
                        }
                    }
                    break;
                case "-param":
                    i++;
                    if (i >= args.length || args[i].charAt(0) == '-'){
                        System.out.println("Wrong Param argument");
                    } else {
                        try {
                            param = Integer.valueOf(args[i]);
                            if (param <= 0){
                                System.out.println("Time must be a positive integer");
                            }
                        }catch (Exception e){
                            System.out.println("Wrong Param argument");
                            ok = false;
                        }
                    }
                    break;
                case "-load":
                    i++;
                    if (i >= args.length || args[i].charAt(0) == '-'){
                        System.out.println("Wrong Load argument");
                        ok = false;
                    } else {
                        try {
                            file = args[i];
                        }catch (Exception e){
                            System.out.println("Wrong Load argument");
                            ok = false;
                        }
                    }
                    break;
            }
        }
        if (ok) {
            launch(args);
        }
    }
}

