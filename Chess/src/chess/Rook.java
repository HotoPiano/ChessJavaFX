package Chess;

public class Rook extends Piece
{

    public Rook(String piece, boolean isWhite, boolean castling)
    {
        super(piece, isWhite, castling);
    }

    @Override
    public boolean isLegalMove(int x, int y, int i, int j)
    {
        return (x != i || y != j) && (x == i || y == j);
    }

    @Override
    public String getPieceName()
    {
        return "R";
    }

    @Override
    public int getValue()
    {
        return 5;
    }
}
