package back;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

public class Game
{
	private Board board;
	Stack<GameStatus> stack;
	private AI cpu_player;
	private boolean prune;
	private AI stupid_player;
	
	public Game(int board_size)
	{
		board = new Board(board_size, ConstantValues.WHITE);
		stack = new Stack<>();
		stack.push(new GameStatus(board.cloneBoard(), false, false, board.currentPlayer()));
		prune = true;
		cpu_player = new AI(1, 5, prune);
		stupid_player = new AI(1, 3, false);
	}
	
	public int getCurrentPlayer()
	{
		return board.currentPlayer();
	}
	
	// Pasa al proximo turno. Actualiza flags y retorna false si el proximo jugador no puede jugar, y actualiza los flags
	public boolean nextTurn()
	{
		board.switchPlayer();
		if(!board.currentPlayerCanMove())
		{
			if(board.isInactivePlayerStuck())
			{
				board.endGame();
				stack.push(new GameStatus(board.cloneBoard(), board.isWhitePlayerStuck(), board.isBlackPlayerStuck(), board.currentPlayer()));
				System.out.println("GAME OVER! Black: " +board.getBlackScore() +" - White: " +board.getWhiteScore());
				return false;
			}
			else
			{
				board.stopCurrentPlayer();
				if(board.currentPlayer() == ConstantValues.BLACK)
					System.out.println("Black player is stuck!");
				else
					System.out.println("White player is stuck!");
				return false;
			}
		}
		else
		{
			board.enableCurrentPlayer();
		}
		stack.push(new GameStatus(board.cloneBoard(), board.isWhitePlayerStuck(), board.isBlackPlayerStuck(), board.currentPlayer()));
		return true;
	}
	
	// Devuelve false si hay GAME OVER
	public boolean gameIsNotOver()
	{
		return !board.isGameOver();
	}
	
	// Devuelve el Board. Si querés la matriz, hacé getBoard().getBoard()
	public Board getBoard()
	{
		return board;
	}
	
	// Reemplaza el board actual con el recibido por parámetro
	public void setBoard(Board b)
	{
		board = b.cloneBoard();
	}
	
	// Si logra meter una ficha en esa coordenada devuelve true y cambia de turno
	public boolean placeChip(int row, int column)
	{
		if(board.placeChip(row, column, board.currentPlayer()))
		{
			nextTurn();
			return true;
		}
		return false;
	}

	// El CPU realiza un movimiento por current_player
	public void cpuMove()
	{
		cpu_player.makeAMove(board, board.currentPlayer());
		if(!nextTurn())
			nextTurn();
	}
	
	private String getLastTree()
	{
		return cpu_player.getLastTree();
	}
	
	// Exporta el último árbol de decisiones del AI a filename con la extensión .dot
	public void exportLastTree(String filename)
	{
		PrintWriter writer;
		try
		{
			writer = new PrintWriter(filename +".dot", "UTF-8");
			writer.println(getLastTree());
			writer.close();
		}
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	// ELIMINAR
	public void exportLastIdiotTree(String filename)
	{
		PrintWriter writer;
		try
		{
			writer = new PrintWriter(filename +".dot", "UTF-8");
			writer.println(stupid_player.getLastTree());
			writer.close();
		}
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}
	
	// El CPU realiza una jugada para el jugador recibido por parámetro
	public void cpuMove(int player)
	{
		cpu_player.makeAMove(board, player);
	}
	
	// FOR DEBUG ONLY. Igual a CPU Move pero hecho por un AI mas tonto
	public void idiotMove()
	{
		stupid_player.makeAMove(board, board.currentPlayer());
		if(!nextTurn())
			nextTurn();
	}
	
	// Retorna true si el jugador actual tiene jugadas posibles
	public boolean currentPlayerCanMove()
	{
		return board.currentPlayerCanMove();
	}
	
	// Retorna true si se puede volver atrás
	public boolean canUndo()
	{
		return (stack.size() > 1);
	}
	
	// Deshace la última jugada
	public boolean undo()
	{
		if(!canUndo())
			return false;
		GameStatus status = stack.pop();
		status = stack.peek();
		board = status.board;
		return true;
	}
	
	/*
	GAME
	public void saveBoard(ObjectOutputStream out) throws IOException {
        out.writeObject(board);
        out.close();
    }

    public void loadBoard(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        board = (BoardGame) ois.readObject();
        ois.close();
    }
        
    BOARD    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeObject(players);
        out.writeObject(boxes);
        out.writeInt(turn);
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        players = (ArrayList<Player>)ois.readObject();
        boxes = (ArrayList<Box>)ois.readObject();
        turn = ois.readInt();
    }
    
    VIEWCONTROLLER
    {
            save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showSaveDialog(window);
                if (file != null) {
                    try {
                        saveData(file);
                        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Información guardada con éxito.").show();
                    } catch (FileNotFoundException ex) {
                        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Error al encontrar el archivo.").show();
                    } catch (IOException ex) {
                        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Error al guardar el archivo.").show();
                    }
                }
            }
        });
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(window);
                if (file != null) {
                    try {
                        loadData(file);
                        board = new BoardPanel(gameState.getOutputData());
                        verticalSplitPane.getItems().addAll(board, layout2);
                        gameScene = new Scene(verticalSplitPane,1200,600);
                        window.setScene(gameScene);
                        disableAllButtons();
                        move.setDisable(false);
                        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Información cargada con éxito.").show();
                    } catch (Exception ex) {
                        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Error al cargar el archivo.").show();
                    }
                }
            }
        });
    }
    private void saveData(File file) throws FileNotFoundException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        gameState.saveBoard(out);
    }
    private void loadData(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        gameState = new GameState(ois);
    }
    }*/
	
	public void saveGame(String filename) throws IOException
	{
		FileOutputStream saveFile = new FileOutputStream(filename);
		ObjectOutputStream save = new ObjectOutputStream(saveFile);
		save.writeObject(board);
		save.close();
    }
	
	public void loadGame(String filename) throws IOException, ClassNotFoundException
	{
		FileInputStream saveFile = new FileInputStream(filename);
		ObjectInputStream restore = new ObjectInputStream(saveFile);
		board = (Board) restore.readObject();
		restore.close();
    }
	
	private class GameStatus
	{
		private Board board;
		
		public GameStatus(Board board, boolean white_stuck, boolean black_stuck, int current_player)
		{
			this.board = board;
		}
	}
}
