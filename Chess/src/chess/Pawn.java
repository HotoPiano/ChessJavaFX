package Chess;

public class Pawn extends Piece
{

    public Pawn(String piece, boolean isWhite)
    {
        this(piece, isWhite, false);
    }

    public Pawn(String piece, boolean isWhite, boolean hasMoved)
    {
        super(piece, isWhite, hasMoved);
    }

    @Override
    public boolean isLegalMove(int x, int y, int i, int j)
    {
        if(!this.getIsWhite())
            if(i == x && (j-1 == y || (j-2 == y && !this.getHasMoved())) || (j == y+1 && (i == x-1 || i == x+1)))
                return true;
        if(this.getIsWhite())
            if(i == x && (j+1 == y || (j+2 == y && !this.getHasMoved())) || (j == y-1 && (i == x-1 || i == x+1)))
                return true;
        return false;
    }

    @Override
    public String getPieceName()
    {
        return "P";
    }

    @Override
    public int getValue()
    {
        return 1;
    }
}
