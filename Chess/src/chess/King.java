package Chess;

public class King extends Piece
{

    public King(String piece, boolean isWhite, boolean castling)
    {
        super(piece, isWhite, castling);
    }

    @Override 
    public boolean isLegalMove(int x, int y, int i, int j)
    {
        
            return true;
    }

    @Override
    public String getPieceName()
    {
        return "g";
    }

    @Override
    public int getValue()
    {
        return 0;
    }
}
