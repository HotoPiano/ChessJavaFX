package Chess;

public class Queen extends Piece
{

    public Queen(String piece, boolean isWhite)
    {
        super(piece, isWhite);
    }

    @Override
    public boolean isLegalMove(int x, int y, int i, int j)
    {
        return (x != i || y != j) && ((x-i == j-y || i-j == x-y) || (x == i || y == j));
    }

    @Override
    public String getPieceName()
    {
        return "Q";
    }

    @Override
    public int getValue()
    {
        return 9;
    }
}
