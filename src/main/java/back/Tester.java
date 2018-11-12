package back;

public class Tester {
	public static void main(String[] args) throws Exception
	{
		Game game = new Game(4);
		game.getBoard().printBoard();
		int turn=1;

		while(game.gameIsNotOver())
		{
			if(game.getCurrentPlayer() == ConstantValues.WHITE)
			{
				System.out.println("\n==" +turn +" - Hilda's turn==");
				game.cpuMove();
				game.getBoard().printBoard();
				game.exportLastTree("hello" +turn);
			}
			else
			{
				System.out.println("\n==" +turn +" - Hilbert's turn==");
				game.idiotMove();
				game.getBoard().printBoard();
				game.exportLastIdiotTree("hello" +turn);
			}
			turn++;
		}
		
		/*
		for(int i=0; i<args.length; i++)
		{
			switch(args[i])
			{
			case "-ai":
				System.out.println("AI Turn: " +args[++i]);
				break;
			case "-mode":
				System.out.println("AI Mode: " +args[i+1] +"=" +args[i+3]);
				i+=3;
				break;
			case "-prune":
				System.out.println("Prune: " +args[++i]);
				break;
			case "-load":
				System.out.println("Loading: " +args[++i]);
				break;
			}
		}
		*/
	}
}
