package back;

public class DotBuilder
{
	String tree;
	
	public DotBuilder()
	{
		tree = "graph minimax_tree {\nsize=\"1,1\";\n";
	}
	
	public void open()
	{
		tree = "graph minimax_tree {\nsize=\"1,1\";\n";
	}
	
	public void close()
	{
		tree = tree + "}";
	}
	
	public void addDeclaration(Board board, String player_name)
	{
		tree = tree + "B" +board.hashCode() +" [label=\"" +player_name +" sees this:\nSCORE:" +board.getScoreDifference() +"\n" +board.toString() +"\"];\n";
	}
	
	public void addPrunedDeclaration(Board board, String player_name)
	{
		tree = tree + "B" +board.hashCode() +" [fillcolor=grey, style=filled, label=\"" +player_name +" sees this:\n" +board.toString() +"\"];\n";
	}
	
	public void addVictory(Board board, String player_name)
	{
		tree = tree + "B" +board.hashCode() +" [label=\"" +player_name +" wins:\nSCORE:" +board.getScoreDifference() +"\n" +board.toString() +"\"];\n";
	}
	
	public void addLoss(Board board, String player_name)
	{
		tree = tree + "B" +board.hashCode() +" [label=\"" +player_name +" loses:\nSCORE:" +board.getScoreDifference() +"\n" +board.toString() +"\"];\n";
	}
	
	public void addPlayerSkipped(Board board, String player_name)
	{
		tree = tree + "B" +board.hashCode() +" [label=\"" +player_name +" can't move!\nSCORE:" +board.getScoreDifference() +"\n" +board.toString() +"\"];\n";
	}
	
	public void addConnection(Board parent, Board child)
	{
		tree = tree + "B" +parent.hashCode() +" -- B" +child.hashCode() +";\n";
	}
	
	public void addConnection(Board parent, Board child, int score)
	{
		tree = tree + "B" +parent.hashCode() +" -- B" +child.hashCode() +" [label=" +score +"];\n";
	}
	
	public void highlightNode(Board board)
	{
		tree = tree + "B" +board.hashCode() +"[fillcolor=yellow, style=filled];\n";
	}
	
	public String getTree()
	{
		return tree;
	}
}
