package Chess;
import java.util.ArrayList;
import java.util.Random;

public class Board
{

    public static final int BOARDSIZE = 8;
    private int gameId;
    public int gameOver;
    private final Piece[][] pieces = new Piece[8][8];

    public int getGameOver()
    {
        return gameOver;
    }
    
    public Piece getPiece(int x, int y)
    {
        return pieces[x][y];
    }
    
    public Board()
    {
        this.gameId = gameId++;
        this.gameOver = 0;

        for (int i = 0; i < BOARDSIZE; i++)
        {
            pieces[i][1] = new Pawn("bPawn", false);
            pieces[i][6] = new Pawn("wPawn", true);
        }
        pieces[0][0] = new Rook("bRook", false, false);
        pieces[7][0] = new Rook("bRook", false, false);
        pieces[0][7] = new Rook("wRook", true, false);
        pieces[7][7] = new Rook("wRook", true, false);
        pieces[1][0] = new Knight("bKnight", false);
        pieces[6][0] = new Knight("bKnight", false);
        pieces[1][7] = new Knight("wKnight", true);
        pieces[6][7] = new Knight("wKnight", true);
        pieces[2][0] = new Bishop("bBishop", false);
        pieces[5][0] = new Bishop("bBishop", false);
        pieces[2][7] = new Bishop("wBishop", true);
        pieces[5][7] = new Bishop("wBishop", true);
        pieces[3][0] = new Queen("bQueen", false);
        pieces[3][7] = new Queen("wQueen", true);
        pieces[4][0] = new King("bKing", false, false);
        pieces[4][7] = new King("wKing", true, false);
    }
    
    public boolean movePieceIfLegal(int x, int y, int i, int j)
    {
        if(isLegalMove(x, y, i, j) && safeTestMove(x, y, i, j))
        {
            pieces[i][j] = pieces[x][y];
            pieces[x][y] = null;
            
            // By now, the move has been executed. Now special conditions 
            // related to castling and en passant are updated, and cantMove
            // method is checked to see if the game is over.
            specialEvents(x, y, i, j);
            pieces[i][j].setHasMoved(true);
            cantMove(!pieces[i][j].getIsWhite());
            return true;
        }
        return false;
    }
    
    private boolean safeTestMove(int x, int y, int i, int j)
    {
        // Big check to make sure attempted castling move is not legal if
        // the path is endagered
        if(illegalCastling(x, y, i, j))
            return false;
        // Try the move
        Piece tmp = pieces[i][j];
        pieces[i][j] = pieces[x][y];
        pieces[x][y] = null;
        
        // Check kingsafe method, redo the move and return
        boolean safe = kingSafe(pieces[i][j].getIsWhite());
        pieces[x][y] = pieces[i][j];
        pieces[i][j] = tmp;
        return safe;
    }
    
    /**
     * Test if an illegal castling attempt is made
     * @param x
     * @param y
     * @param i
     * @param j
     * @return 
     */
    private boolean illegalCastling(int x, int y, int i, int j)
    {
        if(pieces[x][y].getPieceName().equals("g"))
        {
            if(x > i+1)
            {
                Piece tmp = pieces[x-1][y];
                pieces[x-1][y] = pieces[x][y];
                if(!kingSafe(pieces[x][y].getIsWhite()))
                {
                    pieces[x-1][y] = tmp;
                    return true;
                }
                pieces[x-1][y] = tmp;
            }
            if(x < i-1)
            {
                Piece tmp = pieces[x+1][y];
                pieces[x+1][y] = pieces[x][y];
                if(!kingSafe(pieces[x][y].getIsWhite()))
                {
                    pieces[x+1][y] = tmp;
                    return true;
                }
                pieces[x+1][y] = tmp;
            }
        }
        return false;
    }
    
    private void specialEvents(int x, int y, int i, int j)
    {
        // If passant, remove crossed pawn
        if(pieces[i][j].getPiece().equals("bPawn") && pieces[i][j-1] != null && pieces[i][j-1].getPiece().equals("wPawn") && pieces[i][j-1].isPassantRound() && x != i && y != j)
            pieces[i][j-1] = null;
        else if(pieces[i][j].getPiece().equals("wPawn") && pieces[i][j+1] != null && pieces[i][j+1].getPiece().equals("bPawn") && pieces[i][j+1].isPassantRound() && x != i && y != j)
            pieces[i][j+1] = null;
        
        //update passant
        updatePassant();
        
        // If the piece is a pawn moved 2 squares, set passant
        if(pieces[i][j].getPieceName().equals("P") && (j == y+2 || j == y-2))
            pieces[i][j].setPassantRound(true);
        

        
        // If castling has been performed
        if(pieces[i][j].getPieceName().equals("g"))
            if(i == x-2) 
            {
                pieces[3][y] = pieces[0][y];
                pieces[0][y] = null;
                pieces[3][y].setHasMoved(true);
            }
            else if(i == x+2)
            {
                pieces[5][y] = pieces[7][y];
                pieces[7][y] = null;
                pieces[5][y].setHasMoved(true);
            }
    }
    
    private void updatePassant()
    {
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
                if(pieces[i][j] != null && pieces[i][j].getPieceName().equals("P"))
                    pieces[i][j].setPassantRound(false);
    }
    
    public boolean isLegalMove(int x, int y, int i, int j)
    {
        // Calls inner class legality check method, which is different for each piece
        if(pieces[x][y].isLegalMove(x, y, i, j))
        {
            // If it passed that test, time to check if the destination is occupied by allied piece, 
            // or if the path is blocked, or if the piece has any special moves
            // WBlack pawn, with special moves for en passant
            if(pieces[x][y].getPiece().equals("bPawn"))

                // Regular one step move to unoccupied square
                if(i == x && ((j == y+1 && pieces[i][j] == null)))
                    return true;
                // Crossing to an opponent
                else if(i != x && j != y && pieces[i][j] != null && pieces[i][j].getIsWhite() != pieces[x][y].getIsWhite())
                    return true;
                // Can move two squares if first move
                else if((j == y+2 && pieces[i][j] == null && pieces[i][j-1] == null))
                    return true;
                // Passant attempt
                else if((i == x-1 && pieces[x-1][y] != null && pieces[x-1][y].isPassantRound()) || (i == x+1 && pieces[x+1][y] != null && pieces[x+1][y].isPassantRound()))
                    return true;
            // White pawn, with special moves for en passant
            if(pieces[x][y].getPiece().equals("wPawn"))
                // Regular one step move to unoccupied square
                if(i == x && ((j == y-1 && pieces[i][j] == null)))
                    return true;
                // Crossing to an opponent
                else if(i != x && j != y && pieces[i][j] != null && pieces[i][j].getIsWhite() != pieces[x][y].getIsWhite())
                    return true;
                // Can move two squares if first move
                else if((j == y-2 && pieces[i][j] == null && pieces[i][j+1] == null))
                    return true;
                // Passant attempt
                else if((i == x-1 && pieces[x-1][y] != null && pieces[x-1][y].isPassantRound()) || (i == x+1 && pieces[x+1][y] != null && pieces[x+1][y].isPassantRound()))
                    return true;
            // Knight
            if(pieces[x][y].getPieceName().equals("K") && notAlliedPiece(x, y, i, j))
                    return true;
            // Bishop
            if(pieces[x][y].getPieceName().equals("B")  && notAlliedPiece(x, y, i, j))
                    if(!diagonalBlocked(x, y, i, j))
                        return true;
            // Rook
            if(pieces[x][y].getPieceName().equals("R") && notAlliedPiece(x, y, i, j))
                    if(!straightBlocked(x, y, i, j))
                        return true;
            // Queen
            if(pieces[x][y].getPieceName().equals("Q")  && notAlliedPiece(x, y, i, j))
                    if(!straightBlocked(x, y, i, j) && !diagonalBlocked(x, y, i, j))
                        return true;
            // King
            if(pieces[x][y].getPieceName().equals("g") && notAlliedPiece(x, y, i, j))
                // Check to see if it was not an attempted castling
                if(i <= x+1 && i >= x-1 && j <= y+1 && j >= y-1)
                    return true;
                // Castling
                else if(!pieces[x][y].getHasMoved() && i == x-2 && j == y && pieces[0][y] != null && !pieces[0][y].getHasMoved() && isLegalMove(x, y, i+1, j) && isLegalMove(0, y, 3, j))
                    return true;
                else if(!pieces[x][y].getHasMoved() && i == x+2 && j == y && pieces[7][y] != null && !pieces[7][y].getHasMoved() && isLegalMove(x, y, i-1, j) && isLegalMove(7, y, 5, j))
                    return true;
            return false;
        }
        return false;
    }
    
    private boolean alliedPiece(int x, int y, int i, int j)
    {
        return pieces[i][j] != null && (pieces[i][j].getIsWhite() == pieces[x][y].getIsWhite());
    }
        
    /**
     * Test if destination square is not occupied by allied piece. In difference to
     * allied piece check, this method also returns true if the square is empty.
     * @param x
     * @param y
     * @param i
     * @param j
     * @return
     */
    private boolean notAlliedPiece(int x, int y, int i, int j)
    {
        return pieces[i][j] == null || (pieces[i][j].getIsWhite() != pieces[x][y].getIsWhite());
    }
    

    
    private boolean straightBlocked(int x, int y, int i, int j)
    {
        // Vertical
        if(i == x && j < y)
            for(int u=j+1; u<y; u++)
                if(pieces[i][u] != null)
                    return true;
        if(i == x && j > y)
            for(int u=j-1; u>y; u--)
                if(pieces[i][u] != null)
                    return true;
        // Horizontal
        if(j == y && i < x)
            for(int v=i+1; v<x; v++)
                if(pieces[v][j] != null)
                    return true;
        if(j == y && i > x)
            for(int v=i-1; v>x; v--)
                if(pieces[v][j] != null)
                    return true;
        return false;
    }
    
    private boolean diagonalBlocked(int x, int y, int i, int j)
    {
        // Right up
        if(i > x+1 && j < y-1)
            for(int u=1; u<i-x; u++)
                if(pieces[x+u][y-u] != null)
                    return true;
        // Right down
        if(i > x+1 && j > y+1)
            for(int u=1; u<i-x; u++)
                if(pieces[x+u][y+u] != null)
                    return true;
        // Left down
        if(i < x-1 && j > y+1)
            for(int u=1; u<x-i; u++)
                if(pieces[x-u][y+u] != null)
                    return true;
        // Left up
        if(i < x-1 && j < y-1)
            for(int u=1; u<x-i; u++)
                if(pieces[x-u][y-u] != null)
                    return true;
        return false;
    }
    
    public void cantMove(boolean isWhite)
    {
        boolean notInCheck = kingSafe(isWhite);
        
        // Every round also test if there is sufficent pieces to continue, if not: draw
        int count = 0;
        boolean stopped = false;
        boolean twoBishopsOnSameSquareColor = false;
        Piece p;
        int tmpX;
        int tmpY;
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
            {
                if(pieces[i][j] != null)
                {
                    // If a queen, pawn or rook still is present, break the loops - the game can continue
                    if(pieces[i][j].getPieceName().equals("P") || pieces[i][j].getPieceName().equals("R") || pieces[i][j].getPieceName().equals("Q"))
                    {
                        stopped = true;
                        break;
                    }
                    // Count the other pieces
                    if(pieces[i][j].getPieceName().equals("B"))
                    {
                        // Temporary variables for first registered bishop.
                        // If a second one is found, check if its on the same color (x % 2), 
                        // then the game is draw if these are the only registered pieces
                        // (other than the kings of course)
                        p = pieces[i][j];
                        tmpX = i;
                        tmpY = j;
                        count++;
                        if(p != null && tmpX % 2 == i % 2 && tmpY % 2 == j)
                            twoBishopsOnSameSquareColor = true;
                    }
                    else if(pieces[i][j].getPieceName().equals("K"))
                        count++;
                }
            }
        // If the board contains 2 kings and only 1 bishop or knight, its a draw!
        // Also a draw if theres only 2 kings and 2 bishops on same color square
        if((count < 2 && !stopped) || (count == 2 && !stopped && twoBishopsOnSameSquareColor))
            gameOver = 3;
        
        // The big loop to test if the game is over by no possible moves, or check mate
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
            {
                if(pieces[i][j] != null && pieces[i][j].getIsWhite() == isWhite)
                    for(int u=0; u<8; u++)
                        for(int v=0; v<8; v++)
                            if(isLegalMove(i, j, u, v))
                            {
                                // Try the move, and see if king can be safe
                                Piece tmp = pieces[u][v];
                                pieces[u][v] = pieces[i][j];
                                pieces[i][j] = null;
                                // If he is safe, undo and continue with the game
                                if(kingSafe(isWhite))
                                {
                                    pieces[i][j] = pieces[u][v];
                                    pieces[u][v] = tmp;
                                    return;
                                }
                                pieces[i][j] = pieces[u][v];
                                pieces[u][v] = tmp;
                            }
            }
        
        // No possible moves, but king not in danger? Draw
        if(notInCheck)
            gameOver = 3;
        // White is in check wherever he goes, white player won
        else if(!isWhite)
            gameOver = 1;
        // White player is in check wherever he goes, black player won
        else if (isWhite)
            gameOver = 2;
    }
    
    public boolean kingSafe(boolean isWhite)
    {
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
            {
                if(pieces[i][j] != null && pieces[i][j].getIsWhite() != isWhite)
                {
                    for(int u=0; u<8; u++)
                        for(int v=0; v<8; v++)
                            // If its a legal move, and the piece is an opponent king, the king is in check
                            if(isLegalMove(i, j, u, v) && pieces[u][v] != null && pieces[u][v].getIsWhite() == isWhite && pieces[u][v].getPieceName().equals("g"))
                                return false;
                }
            }
        return true;
    }
    
    public void promotion(int x, int y, String piece)
    {
        if(piece.contains("Bishop"))
            pieces[x][y] = new Bishop(piece, pieces[x][y].getIsWhite());
        else if (piece.contains("Knight"))
            pieces[x][y] = new Knight(piece, pieces[x][y].getIsWhite());
        else if (piece.contains("Rook"))
            pieces[x][y] = new Rook(piece, pieces[x][y].getIsWhite(), true);
        else if (piece.contains("Queen"))
            pieces[x][y] = new Queen(piece, pieces[x][y].getIsWhite());
        cantMove(!pieces[x][y].getIsWhite());
    }
    
    public int[] easyAIRound2()
    {
        ArrayList<int[]> allMoves = new ArrayList();
        ArrayList<int[]> preferredMoves = new ArrayList();
        Random rand;
        // Make the list of all moves available
        for(int x=0; x<8; x++)
            for(int y=0; y<8; y++)
            {
                if(pieces[x][y] != null && !pieces[x][y].getIsWhite())
                {
                    for(int i=0; i<8; i++)
                        for(int j=0; j<8; j++)
                        {
                            if(isLegalMove(x, y, i, j) && safeTestMove(x, y, i, j))
                            {
                                int tab[] = {x, y, i, j};
                                allMoves.add(tab);
                            }
                        }
                }
            }
        
        int valTab[] = calculateBoardValue();
        
        int startVal = valTab[1] - valTab[0] - 10;
        int bestVal = startVal;
        boolean isInCheck = false;
        boolean firstCheck = true;

        // Go through all moves and filter out the most interesting ones
        for(int[] m : allMoves)
        {
            if(safeTestMove(m[0], m[1], m[2], m[3]))
            {
                int val = minimumValueGainedForBlack(m[0], m[1], m[2], m[3]);
                // If the move does not put the computer in a disadvantage, 
                // and threatens white king
                if(val >= bestVal && whiteThreatened(m[0], m[1], m[2], m[3]) && firstCheck)
                {
                    isInCheck = true;
                    firstCheck = false;
                    bestVal = val;
                    preferredMoves = new ArrayList();
                    preferredMoves.add(m);
                }
                // If another moves is found that is even more effective than the above, 
                // restart firstCheck test and refresh list to make room for the 
                // better move, and potential children
                else if(val > bestVal && whiteThreatened(m[0], m[1], m[2], m[3]))
                {
                    isInCheck = true;
                    firstCheck = true;
                    bestVal = val;
                    preferredMoves = new ArrayList();
                    preferredMoves.add(m);
                }
                // Does not include the firstCheck test, so it doesnt refresh the 
                // moveslist if check is found.
                else if(val >= bestVal && whiteThreatened(m[0], m[1], m[2], m[3]))
                {
                    isInCheck = true;
                    bestVal = val;
                    preferredMoves.add(m);
                }
                else if(val > bestVal) // && !isInCheck
                {
                    bestVal = val;
                    // Refresh list of moves if a more effective move is discovered
                    preferredMoves = new ArrayList();
                    preferredMoves.add(m);
                }
                // Add to random move selection list if theres more moves of same value
                else if(val == bestVal && !isInCheck)
                    preferredMoves.add(m);
            }
        }
        
        rand = new Random();
        int pos = 0;
        // If more than 1 move have been selected, choose randomly between them
        if(preferredMoves.size() > 1)
            pos = rand.nextInt(preferredMoves.size()-1);
        int x = preferredMoves.get(pos)[0];
        int y = preferredMoves.get(pos)[1];
        int i = preferredMoves.get(pos)[2];
        int j = preferredMoves.get(pos)[3];
        
        movePieceIfLegal(x, y, i, j);
        return preferredMoves.get(pos);
    }
    
    /**
     * 
     * @return integer list of coordinates
     */
    public int[] easyAIRound()
    {
        ArrayList<int[]> allMoves = makeAIMovesList();
        ArrayList<int[]> preferredMoves = filterAIMovesList(allMoves);
        
        Random rand = new Random();
        int pos = 0;
        // If more than 1 move have been selected, choose randomly between them
        if(preferredMoves.size() > 1)
            pos = rand.nextInt(preferredMoves.size()-1);
        // Fetch the coordinates of one of the selected move and execute the move
        int x = preferredMoves.get(pos)[0];
        int y = preferredMoves.get(pos)[1];
        int i = preferredMoves.get(pos)[2];
        int j = preferredMoves.get(pos)[3];
        movePieceIfLegal(x, y, i, j);
        return preferredMoves.get(pos);
    }
    
    /**
     * Creates an list and adds every possible move for the black player.
     * @return arraylist containing the integer lists of the coordinates
     */
    public ArrayList makeAIMovesList()
    {
        ArrayList<int[]> allMoves = new ArrayList();
        // Check every position on the board
        for(int x=0; x<8; x++)
            for(int y=0; y<8; y++)
            {
                if(pieces[x][y] != null && !pieces[x][y].getIsWhite())
                {
                    // when a black piece is found, again iterate through every 
                    // position on the board and check if it can move there. If 
                    // so, add the from and to coordinates to the arraylist.
                    for(int i=0; i<8; i++)
                        for(int j=0; j<8; j++)
                        {
                            if(isLegalMove(x, y, i, j) && safeTestMove(x, y, i, j))
                            {
                                int tab[] = {x, y, i, j};
                                allMoves.add(tab);
                            }
                        }
                }
            }
        return allMoves;
    }
    
    public ArrayList filterAIMovesList(ArrayList<int[]> allMoves)
    {
        ArrayList<int[]> preferredMoves = new ArrayList();
        
        int valTab[] = calculateBoardValue();
        
        int startVal = valTab[1] - valTab[0] - 10;
        int bestVal = startVal;
        boolean isInCheck = false;
        boolean firstCheck = true;

        // Go through all moves and filter out the most interesting ones
        for(int[] m : allMoves)
        {
            if(safeTestMove(m[0], m[1], m[2], m[3]))
            {
                int val = minimumValueGainedForBlack(m[0], m[1], m[2], m[3]);
                // If the move does not put the computer in a disadvantage, 
                // and threatens white king
                if(val >= bestVal && whiteThreatened(m[0], m[1], m[2], m[3]) && firstCheck)
                {
                    isInCheck = true;
                    firstCheck = false;
                    bestVal = val;
                    preferredMoves = new ArrayList();
                    preferredMoves.add(m);
                }
                // If another move is found that is even more effective than the above, 
                // restart firstCheck test and refresh list to make room for the 
                // better move, and potential children
                else if(val > bestVal && whiteThreatened(m[0], m[1], m[2], m[3]))
                {
                    isInCheck = true;
                    firstCheck = true;
                    bestVal = val;
                    preferredMoves = new ArrayList();
                    preferredMoves.add(m);
                }
                // Does not include the firstCheck test, so it doesnt refresh the 
                // moveslist if check is found.
                else if(val >= bestVal && whiteThreatened(m[0], m[1], m[2], m[3]))
                {
                    isInCheck = true;
                    bestVal = val;
                    preferredMoves.add(m);
                }
                else if(val > bestVal) // && !isInCheck
                {
                    bestVal = val;
                    // Refresh list of moves if a more effective move is discovered
                    preferredMoves = new ArrayList();
                    preferredMoves.add(m);
                }
                // Add to random move selection list if theres more moves of same value
                else if(val == bestVal && !isInCheck)
                    preferredMoves.add(m);
            }
        }
        
        return preferredMoves;
    }
    
    private int minimumValueGainedForBlack(int x, int y, int i, int j)
    {
        int tab[] = calculateBoardValue();
        int val = tab[1] - tab[0];
        // Try the move
        Piece tmp = pieces[i][j];
        pieces[i][j] = pieces[x][y];
        pieces[x][y] = null;
        
        // If the AI is trying to promote a pawn, it would mean an extra value increase of 8
        if(pieces[i][j].getPieceName().equals("P") && j == 7)
            val+=8;
        
        for(int a=0; a<8; a++)
            for(int b=0; b<8; b++)
            {
                // Find white pieces and check all moves they can do
                if(pieces[a][b] != null && pieces[a][b].getIsWhite())
                {
                    for(int c=0; c<8; c++)
                        for(int d=0; d<8; d++)
                        {
                            if(pieces[c][d] == null || (pieces[c][d] != null && !pieces[c][d].getIsWhite()))
                            {
                                if(isLegalMove(a, b, c, d) && safeTestMove(a, b, c, d))
                                {
                                    // Try the move for white
                                    int tab2[] = calculateBoardValue();
                                    int newVal = tab2[1] - tab2[0];
                                    // Subtract value if a black piece is taken
                                    if(pieces[c][d] != null)
                                        newVal -= pieces[c][d].getValue();
                                    // The iteration where the highest damage
                                    // is caused in the counter attack by white 
                                    // is stored.
                                    if(newVal < val)
                                        val = newVal;
                                }
                            }
                        }
                }
            }
        // Undo tested move
        pieces[x][y] = pieces[i][j];
        pieces[i][j] = tmp;
        return val;
    }
    
    private int[] calculateBoardValue()
    {
        int wValue = 0;
        int bValue = 0;
        // Calculate piece values, to see if the upcoming move is worth it
        for(int x=0; x<8; x++)
            for(int y=0; y<8; y++)
            {
                if(pieces[x][y] != null)
                {
                    int value = pieces[x][y].getValue();
                    if(pieces[x][y].getIsWhite())
                        wValue += value;
                    else
                        bValue += value; 
                }
            }
        
        int[] tab = {wValue, bValue};
        return tab;
    }
    
    /**
     * Performs a testmove, checks if the white king is threatened after that move.
     * @param x
     * @param y
     * @param i
     * @param j
     * @return true if white king gets threatened.
     */
    private boolean whiteThreatened(int x, int y, int i, int j)
    {
        // Try the move
        Piece tmp = pieces[i][j];
        pieces[i][j] = pieces[x][y];
        pieces[x][y] = null;
        
        // Check kingsafe method, redo the move and return, true for isWhite
        boolean safe = kingSafe(true);
        pieces[x][y] = pieces[i][j];
        pieces[i][j] = tmp;
        return !safe;
    }
}
