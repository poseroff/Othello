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
	Stack<Board> stack;
	private AI cpu_player;


	
	//hay q hacer bien lo de inicializar el juego despues
	public Game(int board_size, int ai, String mode, int param, String prune_op) throws RuntimeException
	{
		board = new Board(board_size, ConstantValues.WHITE);
		stack = new Stack<>();
		stack.push(board.cloneBoard());
		boolean prune=false;
		if(prune_op.equals("on"))
			prune = true;
		if(ai==1 || ai==2)
			cpu_player = new AI(mode, param, prune);
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
				stack.push(board.cloneBoard());
				return false;
			}
			else
			{
				board.stopCurrentPlayer();
				return false;
			}
		}
		else
		{
			board.enableCurrentPlayer();
		}
		stack.push(board.cloneBoard());
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
			//nextTurn();
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
	
	// El CPU realiza una jugada para el jugador recibido por parámetro
	public void cpuMove(int player)
	{
		cpu_player.makeAMove(board, player);
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
		Board status = stack.pop();
		status = stack.peek();
		board = status;
		return true;
	}
	
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
}
