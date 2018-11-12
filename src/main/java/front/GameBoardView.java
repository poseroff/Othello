package front;

public interface GameBoardView {

    //Defines how a tile should look and feel
    GenericGameBoardView.GenericBoardTile tileDefinition(int i, int j);
}