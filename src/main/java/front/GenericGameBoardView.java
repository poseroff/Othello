package front;

import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public abstract class GenericGameBoardView extends GridPane implements GameBoardView {

    //Sets Board's prefered dimentions
    private static final int PREFERED_WIDTH = 600;
    private static final int PREFERED_HEIGHT = 600;

    protected int rows;
    protected int cols;
    protected GenericBoardTile[][] boardTiles;

    public GenericGameBoardView(int rows, int cols){
        this(rows, cols, PREFERED_WIDTH, PREFERED_HEIGHT);
    }

    public GenericGameBoardView(int rows, int cols, int pref_width, int pref_height){
        this.rows = rows;
        this.cols = cols;
        boardTiles = new GenericBoardTile[rows][cols];
        this.setPrefSize(pref_width, pref_height);
        int i, j;
        for (i = 0; i < rows; i++){
            for (j = 0; j < cols; j++){
                GenericBoardTile tile = tileDefinition(i, j);
                boardTiles[i][j] = tile;
                this.add(tile, i, j);
            }
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100 / (double) rows);
            this.getRowConstraints().add(rowConstraints);
        }
        for (j = 0; j < cols; j++){
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100 / (double) cols);
            this.getColumnConstraints().add(columnConstraints);
        }
    }

    protected abstract class GenericBoardTile extends StackPane {}
}
