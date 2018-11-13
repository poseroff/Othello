package front;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import game.OthelloGameApp;

public class OthelloGameBoardView extends GenericGameBoardView {
    final static private Color AVAILABLE_COLOR = Color.YELLOW;
    final static private Color TILE_COLOR_EVEN = Color.GREEN;
    final static private Color TILE_COLOR_NOT_EVEN = Color.DARKGREEN;

    private OthelloGameApp othelloGameApp;

    public OthelloGameBoardView(int size, OthelloGameApp app){
        super(size, size);
        if (size < 4 || size % 2 != 0){
            throw new IllegalArgumentException();
        }
        othelloGameApp = app;
    }

    public void placeChip(PlayerView player, int i, int j){
        if (i >= rows || j >= cols){
            throw new IndexOutOfBoundsException();
        }
        ((OthelloBoardTile)boardTiles[i][j]).setPlayer(player);
    }

    public void setAvailable(int i, int j){
        if (i >= rows || j >= cols){
            throw new IndexOutOfBoundsException();
        }
        ((OthelloBoardTile)boardTiles[i][j]).setAvailable();
    }

    public void disableBoard(){
        for (int i= 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                ((OthelloBoardTile)boardTiles[i][j]).setDisable();
            }
        }

    }

    public void setUnavailable(int i, int j){
        if (i >= rows || j >= cols){
            throw new IndexOutOfBoundsException();
        }
        ((OthelloBoardTile)boardTiles[i][j]).setUnavailable();
    }

    @Override
    public GenericBoardTile tileDefinition(int i, int j) {
        OthelloBoardTile tile = new OthelloBoardTile(i, j);
        boardTiles[i][j] = tile;
        if (i % 2 == j % 2)
            tile.setBackground(new Background(new BackgroundFill(TILE_COLOR_EVEN, CornerRadii.EMPTY, Insets.EMPTY)));
        else
            tile.setBackground(new Background(new BackgroundFill(TILE_COLOR_NOT_EVEN, CornerRadii.EMPTY, Insets.EMPTY)));
        return tile;
    }

    private class OthelloBoardTile extends GenericBoardTile {

        private Circle piece;
        private PlayerView player;
        private boolean isAvailable = false;

        public OthelloBoardTile(int i, int j){
            piece = new Circle();
            piece.radiusProperty().bind(Bindings.min(Bindings.divide(this.heightProperty(), 2),Bindings.divide(this.widthProperty(), 2)));
            setAvailable();
            this.getChildren().add(piece);
            this.setOnMousePressed(event -> {
                if (isAvailable){
                    player = othelloGameApp.getCurrentPlayer();
                    setPlayer(player);
                    othelloGameApp.placeChip(i, j);
                }
            });
        }

        private void setPlayer(PlayerView player){
            isAvailable = false;
            piece.setVisible(true);
            piece.setFill(player.getColor());
        }

        private void setDisable(){
            isAvailable = false;
        }

        private void setAvailable(){
            isAvailable = true;
            piece.setVisible(true);
            piece.setFill(AVAILABLE_COLOR);
        }

        private void setUnavailable(){
            isAvailable = false;
            piece.setVisible(false);
            piece.setFill(AVAILABLE_COLOR);
        }
    }
}