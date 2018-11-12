package back;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable
{
	private static final long serialVersionUID = 2L;
	private enum Direction {NORTHWEST, NORTH, NORTHEAST, WEST, EAST, SOUTHWEST, SOUTH, SOUTHEAST};
	
	private int size;
	private int[][] board;
	private int black_score;
	private int white_score;
	private boolean game_is_active;
	private boolean white_player_stuck;
	private boolean black_player_stuck;
	private int current_player;
	private List<Coordinates> usable_squares = new ArrayList<>();
	
	public Board(int N, int first_player) throws RuntimeException
	{
		if(N<4 || N%2!=0 || N>10)
			throw new RuntimeException();
		this.size = N;
		board = new int[N][N];
		board[(N-1)/2][(N-1)/2] = ConstantValues.BLACK;
		board[(N-1)/2][N/2] = ConstantValues.WHITE;
		board[N/2][(N-1)/2] = ConstantValues.WHITE;
		board[N/2][N/2] = ConstantValues.BLACK;
		black_score = 2;
		white_score = 2;
		usable_squares = new ArrayList<>();
		game_is_active = true;
		white_player_stuck = false;
		black_player_stuck = false;
		current_player = first_player;
		updateValidSpaces(first_player);
	}
	
	public Board(int N, int[][] status, int black_score, int white_score, boolean game_is_active, boolean white_stuck, boolean black_stuck, int current_player)
	{
		this.size = N;
		this.board = new int[N][N];
		for(int i=0; i<size; i++)
			for(int j=0; j<size; j++)
				board[i][j] = status[i][j];
		this.black_score = black_score;
		this.white_score = white_score;
		this.game_is_active = game_is_active;
		this.white_player_stuck = white_stuck;
		this.black_player_stuck = black_stuck;
		this.current_player = current_player;
	}
	
	public void printBoard()
	{
		for(int i=0; i<size; i++)
		{
			for(int j=0; j<size; j++)
			{
				if(board[i][j] == ConstantValues.BLACK)
					System.out.print('■');
				else if(board[i][j] == ConstantValues.WHITE)
					System.out.print('o');
				else if(board[i][j] == ConstantValues.AVAILABLE)
					System.out.print('-');
				else
					System.out.print(' ');
			}
			System.out.println();
		}
	}
	
	public int getSize()
	{
		return size;
	}
		
	public int[][] getBoard()
	{
		return board;
	}
	
	public int getWhiteScore()
	{
		return white_score;
	}
	
	public int getBlackScore()
	{
		return black_score;
	}
	
	public List<Coordinates> getPossibleMoves()
	{
		return usable_squares;
	}
	
	public List<Coordinates> getPossibleMoves(int player)
	{
		updateValidSpaces(player);
		return usable_squares;
	}
	
	public Board cloneBoard()
	{
		return new Board(size, board, black_score, white_score, game_is_active, white_player_stuck, black_player_stuck, current_player);
	}
	
	public void updateValidSpaces(int player)
	{
		usable_squares.clear();
		for(int i=0; i<size; i++)
		{
			for(int j=0; j<size; j++)
			{
				if(board[i][j] != ConstantValues.BLACK && board[i][j] != ConstantValues.WHITE)
				{
					if(isUsableSpace(i,j,player))
					{
						board[i][j] = ConstantValues.AVAILABLE;
						usable_squares.add(new Coordinates(i,j));
					}
					else
						board[i][j] = ConstantValues.EMPTY;
				}
			}
		}
	}
	
	public int getScoreDifference()
	{
		return black_score - white_score;
	}
	
	public int getHeuristicScore() {
		int score=0;
		for(int i=0; i<size; i++)
		{
			for(int j=0; j< size; j++) {
				score= score + board[i][j]*getHeuristicValue(i,j);
			}
		}
		return score*5 + 2*getScoreDifference() + 3*(usable_squares.size());
	}
	
	public List<Board> getPossibleFollowups(int player)
	{
		List<Board> possible_followups = new ArrayList<>();
		ArrayList<Coordinates> player_usable_squares = new ArrayList<>();
		for(int i=0; i<size; i++)
		{
			for(int j=0; j<size; j++)
			{
				if(isUsableSpace(i, j, player))
				{
					player_usable_squares.add(new Coordinates(i, j));
				}
			}
		}
		Board aux = cloneBoard();
		aux.updateValidSpaces(player);
		for(Coordinates c: player_usable_squares)
		{
			Board b = aux.cloneBoard();
			b.placeChip(c.getX(), c.getY(), player);
			if(player == ConstantValues.BLACK)
				b.updateValidSpaces(ConstantValues.WHITE);
			else
				b.updateValidSpaces(ConstantValues.BLACK);
			possible_followups.add(b);
		}
		return possible_followups;
	}
	
	public void setState(Board b)
	{
		size = b.getSize();
		board = b.getBoard();
		black_score = b.getBlackScore();
		white_score = b.getWhiteScore();
		usable_squares = b.getPossibleMoves();
	}
	
	public boolean isWhitePlayerStuck()
	{
		return white_player_stuck;
	}
	
	public boolean isBlackPlayerStuck()
	{
		return black_player_stuck;
	}
	
	public int currentPlayer()
	{
		return current_player;
	}
	
	public boolean isGameOver()
	{
		return !game_is_active;
	}
	
	public void switchPlayer()
	{
		if(current_player == ConstantValues.BLACK)
			current_player = ConstantValues.WHITE;
		else
			current_player = ConstantValues.BLACK;
		updateValidSpaces(current_player);
	}
	
	public boolean currentPlayerCanMove()
	{
		return (getPossibleMoves().size() > 0);
	}

	public void endGame()
	{
		game_is_active = false;
	}
	
	public void stopWhitePlayer()
	{
		white_player_stuck = true;
	}
	
	public void stopBlackPlayer()
	{
		black_player_stuck = true;
	}
	
	public void enableWhitePlayer()
	{
		white_player_stuck = false;
	}
	
	public void enableBlackPlayer()
	{
		black_player_stuck = false;
	}
	
	public void stopInactivePlayer()
	{
		if(current_player == ConstantValues.BLACK)
			white_player_stuck = true;
		else
			black_player_stuck = true;
	}
	
	public boolean isInactivePlayerStuck()
	{
		if(current_player == ConstantValues.BLACK)
			return white_player_stuck;
		else
			return black_player_stuck;
	}
	
	public void enableInactivePlayer()
	{
		if(current_player == ConstantValues.BLACK)
			white_player_stuck = false;
		else
			black_player_stuck = false;
	}
	
	public void enableCurrentPlayer()
	{
		if(current_player == ConstantValues.WHITE)
			white_player_stuck = false;
		else
			black_player_stuck = false;
	}
	
	public void stopCurrentPlayer()
	{
		if(current_player == ConstantValues.WHITE)
			white_player_stuck = true;
		else
			black_player_stuck = true;
	}
	
	public boolean boardIsFull()
	{
		return (white_score+black_score == size*size);
	}
	
	private boolean isUsableSpace(int x, int y, int player)
	{
		int enemy;
		if(player== ConstantValues.WHITE)
			enemy = ConstantValues.BLACK;
		else
			enemy = ConstantValues.WHITE;
		if(board[x][y]==ConstantValues.EMPTY || board[x][y]==ConstantValues.AVAILABLE)
		{
			// if a neighbor is an enemy and following that neighbor reaches a friend, return true
			if(x>=1 && y>=1 && board[x-1][y-1] == enemy && isValidMove(x,y,Direction.NORTHWEST, player))
				return true;
			if(x>=1 && board[x-1][y] == enemy && isValidMove(x,y,Direction.WEST, player))
				return true;
			if(x>=1 && y<size-1 && board[x-1][y+1] == enemy && isValidMove(x,y,Direction.SOUTHWEST, player))
				return true;
			if(y>=1 && board[x][y-1] == enemy && isValidMove(x,y,Direction.NORTH, player))
				return true;
			if(y<size-1 && board[x][y+1] == enemy && isValidMove(x,y,Direction.SOUTH, player))
				return true;
			if(x<size-1 && y>=1 && board[x+1][y-1] == enemy && isValidMove(x,y,Direction.NORTHEAST, player))
				return true;
			if(x<size-1 && board[x+1][y] == enemy && isValidMove(x,y,Direction.EAST, player))
				return true;
			if(x<size-1 && y<size-1 && board[x+1][y+1] == enemy && isValidMove(x,y,Direction.SOUTHEAST, player))
				return true;
		}
		return false;
	}
	
	private boolean isValidMove(int x, int y, Direction direction, int player)
	{
		switch(direction)
		{
			case NORTHWEST:
				x--;
				y--;
				while(x>=0 && y>=0)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					x--;
					y--;
				}
				break;
			case NORTH:
				y--;
				while(y>=0)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					y--;
				}
				break;
			case NORTHEAST:
				x++;
				y--;
				while(x<size && y>=0)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					x++;
					y--;
				}
				break;
			case WEST:
				x--;
				while(x>=0)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					x--;
				}
				break;
			case EAST:
				x++;
				while(x<size)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					x++;
				}
				break;
			case SOUTHWEST:
				x--;
				y++;
				while(x>=0 && y<size)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					x--;
					y++;
				}
				break;
			case SOUTH:
				y++;
				while(y<size)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					y++;
				}
				break;
			case SOUTHEAST:
				x++;
				y++;
				while(x<size && y<size)
				{
					if(board[x][y] == 0 || board[x][y] == 2)
						return false;
					if(board[x][y] == player)
						return true;
					x++;
					y++;
				}
				break;
		}
		return false;
	}
	
	public boolean placeChip(int row, int column, int player)
	{
		if(board[row][column] == ConstantValues.AVAILABLE)
		{
			board[row][column] = player;
			if(player == ConstantValues.BLACK)
				black_score++;
			else
				white_score++;
			flipNeighborEnemies(row, column, player);
			return true;
		}
		return false;
	}
	
	private void flipNeighborEnemies(int row, int column, int player)
	{
		for(Direction d: Direction.values())
		{
			if(isValidMove(row, column, d, player))
				flipEnemiesInRow(row, column, d, player);
		}
	}
	
	private int getHeuristicValue(int row, int column)
	{	
		if(row==0 && column==0 || row==0 && column==size-1 || row==size-1 && column==0 || row==size-1 && column==size-1)
			return 5;
		else 
			return 1;
	}
	
	private void flipEnemiesInRow(int x, int y, Direction direction, int player)
	{
		int enemy;
		if(player==ConstantValues.BLACK)
			enemy = ConstantValues.WHITE;
		else
			enemy = ConstantValues.BLACK;
		switch(direction)
		{
			case NORTHWEST:
				x--;
				y--;
				while(x>=0 && y>=0)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					x--;
					y--;
				}
				break;
			case NORTH:
				y--;
				while(y>=0)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					y--;
				}
				break;
			case NORTHEAST:
				x++;
				y--;
				while(x<size-1 && y>=0)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					x++;
					y--;
				}
				break;
			case WEST:
				x--;
				while(x>=0)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					x--;
				}
				break;
			case EAST:
				x++;
				while(x<size-1)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					x++;
				}
				break;
			case SOUTHWEST:
				x--;
				y++;
				while(x>=0 && y<size-1)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					x--;
					y++;
				}
				break;
			case SOUTH:
				y++;
				while(y<size-1)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					y++;
				}
				break;
			case SOUTHEAST:
				x++;
				y++;
				while(x<size-1 && y<size-1)
				{
					if(board[x][y] == enemy)
						flipChip(x,y);
					else if(board[x][y] == player)
						return;
					x++;
					y++;
				}
				break;
		}
	}
	
	private void flipChip(int row, int column)
	{
		int enemy;
		if(board[row][column] == ConstantValues.BLACK)
			enemy = ConstantValues.WHITE;
		else if(board[row][column] == ConstantValues.WHITE)
			enemy = ConstantValues.BLACK;
		else
			return;
		board[row][column] = enemy;
		if(enemy == ConstantValues.BLACK)
		{
			black_score++;
			white_score--;
		}
		else
		{
			white_score++;
			black_score--;
		}
	}
	
	@Override
	public int hashCode()
	{
		return board.hashCode();
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeInt(size);
        out.writeObject(board);
        out.writeInt(black_score);
        out.writeInt(white_score);
        out.writeBoolean(game_is_active);
        out.writeBoolean(white_player_stuck);
        out.writeBoolean(black_player_stuck);
        out.writeInt(current_player);
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        size = (int) ois.readInt();
        board = (int[][]) ois.readObject();
        black_score = (int) ois.readInt();
        white_score = (int) ois.readInt();
        game_is_active = (boolean) ois.readBoolean();
        white_player_stuck = (boolean) ois.readBoolean();
        black_player_stuck = (boolean) ois.readBoolean();
        current_player = (int) ois.readInt();
    }
	
	@Override
	public String toString()
	{
		String s = "";
		for(int i=0; i<size; i++)
		{
			for(int j=0; j<size; j++)
			{
				if(board[i][j] == ConstantValues.BLACK)
					s = s.concat("X");
				else if(board[i][j] == ConstantValues.WHITE)
					s = s.concat("O");
				else if(board[i][j] == ConstantValues.AVAILABLE)
					s = s.concat("-");
				else
					s = s.concat(" ");
			}
			s = s.concat("\n");
		}
		return s;
	}
}
