package back;

import java.util.List;

public class AI
{
	private int max_depth;
	private boolean depth_mode;
	private boolean pruning;
	private Board bestBoard;
	private DotBuilder tree;
	private int max_time;

	
	public AI(String mode, int param, boolean pruning)
	{
		if(mode.equals("depth"))
			depth_mode = true;
		else
			depth_mode = false;
		if(depth_mode) {
			max_depth = param;
			max_time= -1000;
		}
		else {
			this.max_depth = 10;
			this.max_time = param;
		}
		this.pruning = pruning;
		this.tree = new DotBuilder();
	}
	
	private int minimax(long end, Board board, int depth, boolean maximizing_player, int alpha, int beta)
	{
		if(board.boardIsFull())
		{
			int score;
			if(board.getScoreDifference() > 0)
			{
				tree.addVictory(board, "BLACK");
				score = Integer.MAX_VALUE;
			}
			else
			{
				tree.addVictory(board, "WHITE");
				score = Integer.MIN_VALUE;
			}
			return score;
		}
		
		List<Board> moves_list;
		if(maximizing_player)
			moves_list = board.getPossibleFollowups(ConstantValues.BLACK);
		else
			moves_list = board.getPossibleFollowups(ConstantValues.WHITE);
		
		// if I get stuck here
		if(moves_list.size() == 0)
		{
			if(maximizing_player)
			{
				// if nobody can move
				if(board.getPossibleFollowups(ConstantValues.WHITE).size()==0)
				{
					if(board.getScoreDifference()>0)
					{
						tree.addVictory(board, "BLACK");
						return Integer.MAX_VALUE;
					}
					else
					{
						tree.addLoss(board, "BLACK");
						return Integer.MIN_VALUE;
					}
				}
				else if(depth < max_depth)
				{
					// if WHITE can still move
					tree.addPlayerSkipped(board, "BLACK");
					Board b = board.cloneBoard();
					b.updateValidSpaces(ConstantValues.WHITE);
					int score = minimax(end, b, depth+1, false, alpha, beta);
					tree.addConnection(board, b, score);
					tree.highlightNode(b);
					return score;
				}
			}
			else
			{
				// if nobody can move, declare winner
				if(board.getPossibleFollowups(ConstantValues.BLACK).size()==0)
				{
					if(board.getScoreDifference()>0)
					{
						tree.addLoss(board, "WHITE");
						return Integer.MAX_VALUE;
					}
					else
					{
						tree.addVictory(board, "WHITE");
						return Integer.MIN_VALUE;
					}
				}
				else if(depth < max_depth)
				{
					// if BLACK can still move
					tree.addPlayerSkipped(board, "WHITE");
					Board b = board.cloneBoard();
					b.updateValidSpaces(ConstantValues.BLACK);
					int score = minimax(end, b, depth+1, true, alpha, beta);
					tree.addConnection(board, b, score);
					tree.highlightNode(b);
					return score;
				}
			}
		}
		if(depth == max_depth)
		{
			if(maximizing_player)
				tree.addDeclaration(board, "BLACK");
			else
				tree.addDeclaration(board, "WHITE");
			return board.getHeuristicScore();
		}
		// if I'm black and can move
		if(maximizing_player)
		{
			int top = 0;
			int i=0;
			int max_eval = Integer.MIN_VALUE;
			boolean prune_flag = false;
			tree.addDeclaration(board, "BLACK");
			
			for(Board b: moves_list)
			{	
				if(!depth_mode && System.currentTimeMillis() > end) {
					break;
				}
				if(prune_flag)
				{
					tree.addPrunedDeclaration(b, "WHITE");
					tree.addConnection(board, b);
				}
				else
				{
					int score = minimax(end, b, depth+1, false, alpha, beta);
					tree.addConnection(board, b, score);
					// alpha is the new max, top is the index of the best board so far
					if(score > max_eval)
					{
						max_eval = score;
						top=i;
					}
					if(score>alpha)
					{
						alpha=score;
					}
					// if the new max is better than the previous one, stop looking for a better one
					if(pruning && alpha>=beta)
						prune_flag = true;
					i++;
				}
			}
			
			tree.highlightNode(moves_list.get(top));
			if(depth==0)
				bestBoard = moves_list.get(top);
			return max_eval;
		}
		// if I'm white and can move
		else
		{
			int top = 0;
			int i=0;
			int min_eval = Integer.MAX_VALUE;
			boolean prune_flag = false;
			tree.addDeclaration(board, "WHITE");
			for(Board b: moves_list)
			{	
				if(!depth_mode && System.currentTimeMillis() > end) {
					break;
				}
				if(prune_flag)
				{
					tree.addPrunedDeclaration(b, "WHITE");
					tree.addConnection(board, b);
				}
				else
				{
					int score = minimax(end, b, depth+1, true, alpha, beta);
					tree.addConnection(board, b, score);
					// beta is the new min, board is not saved since this is just an intermediate step
					if(score < min_eval)
					{
						min_eval = score;
						top=i;
					}
					if(score<beta)
					{
						beta=score;
					}
					if(pruning && alpha>=beta)
						prune_flag = true;
					i++;
				}
				
			}
			
			tree.highlightNode(moves_list.get(top));
			if(depth==0)
				bestBoard = moves_list.get(top);
			return min_eval;
		}
	}
	

	// it's a good idea to order the lists on how good they are ()
	public void makeAMove(Board board, int player)
	{
		tree.open();
		long start = System.currentTimeMillis();
		long end = start + max_time*1000;
		System.out.println("Realiza movimiento de puntaje " +minimax(end, board, 0, (player==ConstantValues.BLACK), Integer.MIN_VALUE, Integer.MAX_VALUE) + " en " + (System.currentTimeMillis()-start)/1000.0 + " segs ");
		board.setState(bestBoard);
		tree.close();
	}
	
	public String getLastTree()
	{
		return tree.getTree();
	}
}