package Chess;

public class Bishop extends Piece
{
    public Bishop(String piece, boolean isWhite)
    {
        super(piece, isWhite);
    }

    @Override
    public boolean isLegalMove(int x, int y, int i, int j)
    {
        return (x != i || y != j) && (x-i == j-y || i-j == x-y);
    }
    
    @Override
    public String getPieceName()
    {
        return "B";    
    }

    @Override
    public int getValue()
    {
        return 3;
    }
}
