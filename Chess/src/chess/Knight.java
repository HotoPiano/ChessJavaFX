package Chess;

public class Knight extends Piece
{

    public Knight(String piece, boolean isWhite)
    {
        super(piece, isWhite);
    }

    @Override
    public boolean isLegalMove(int x, int y, int i, int j)
    {
        return ((i == x+1 || i == x-1) && (j == y+2 || j == y-2)) || ((i == x+2 || i == x-2) && (j == y+1 || j == y-1));
    }

    @Override
    public String getPieceName()
    {
        return "K";
    }

    @Override
    public int getValue()
    {
        return 3;
    }
}
