package Chess;

/**
 * Abstract superclass that every piece uses.
 */
public abstract class Piece
{
    
    // Methods that every pieces uses, but different for every piece.
    public abstract boolean isLegalMove(int x, int y, int i, int j);
    public abstract String getPieceName();
    public abstract int getValue();
    
    protected String piece;
    protected boolean isWhite;
    protected boolean hasMoved;
    protected boolean passantRound;

    /**
     * Constructor for the piece. Calls neighbour constructor with the hasMoved 
     * value default as false.
     * @param piece
     * @param isWhite 
     */
    public Piece(String piece, boolean isWhite)
    {
        this(piece, isWhite, false);
    }
    /**
     * Constructor for the piece.
     * @param piece
     * @param isWhite
     * @param hasMoved 
     */
    public Piece(String piece, boolean isWhite, boolean hasMoved)
    {
        this.piece = piece;
        this.isWhite = isWhite;
        this.passantRound = false;
        this.hasMoved = hasMoved;
    }

    /**
     * 
     * @return 
     */
    public String getPiece()
    {
        return piece;
    }

    /**
     * 
     * @return 
     */
    public boolean getIsWhite()
    {
        return isWhite;
    }

    /**
     * 
     * @return 
     */
    public boolean getHasMoved()
    {
        return hasMoved;
    }

    /**
     * 
     * @return 
     */
    public boolean isPassantRound()
    {
        return passantRound;
    }

    /**
     * 
     * @param hasMoved 
     */
    public void setHasMoved(boolean hasMoved)
    {
        this.hasMoved = hasMoved;
    }

    /**
     * Sets the piece's passant boolean.
     * @param passantRound 
     */
    public void setPassantRound(boolean passantRound)
    {
        this.passantRound = passantRound;
    }

}
