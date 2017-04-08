package Chess;

import java.sql.Date;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Run extends Application
{
    // Declaring gameplay variables
    private ImageView[][] piece;
    private Board b;
    private boolean isActive;
    private Node activeNode;
    private boolean isWhitePlayer;
    private int roundId;
    private int opponent;
    // Declaration components of the scene, visual structure
    private Group root;
    private Scene scene;
    private GridPane squarePane;
    private GridPane nodePane;
    private HBox bottomPane;
    private ArrayList<Rectangle> legalMovesRect;
    // Declaration and initiating variables for text, rectangles and images
    private final Label BOTTOMTXT = new Label();
    private final Rectangle FROMSQUARERECT = new Rectangle(100, 100, Color.LIGHTCORAL);
    private final Rectangle TOSQUARERECT = new Rectangle(100, 100, Color.RED);
    private final Image WHITEPAWN = new Image("/img/white_pawn.png");
    private final Image BLACKPAWN = new Image("/img/black_pawn.png");
    private final Image WHITEKING = new Image("/img/white_king.png");
    private final Image BLACKKING = new Image("/img/black_king.png");
    private final Image WHITEQUEEN = new Image("/img/white_queen.png");
    private final Image BLACKQUEEN = new Image("/img/black_queen.png");
    private final Image WHITEROOK = new Image("/img/white_rook.png");
    private final Image BLACKROOK = new Image("/img/black_rook.png");
    private final Image WHITEKNIGHT = new Image("/img/white_knight.png");
    private final Image BLACKKNIGHT = new Image("/img/black_knight.png");
    private final Image WHITEBISHOP = new Image("/img/white_bishop.png");
    private final Image BLACKBISHOP = new Image("/img/black_bishop.png");
    private final Image OPENFIELD = new Image("/img/open_field.png");
    // Declaration of time variables
    private Timeline timer;
    private String timerTxt;
    private int minutes;
    private int seconds;
    
    /**
     * Initiating the scene and its components structure.
     * @param primaryStage 
     */
    @Override
    public void start(Stage primaryStage)
    {
        piece  = new ImageView[8][8];
        root = new Group();
        scene = new Scene(root, 790, 860);
        scene.setFill(Color.SNOW);
        squarePane = squares();
        nodePane = pieces();
        bottomPane = bottom();
        root.getChildren().addAll(squarePane, nodePane, bottomPane);
        // Small featureadjustments to visual components
        FROMSQUARERECT.setOpacity(0.7);
        TOSQUARERECT.setOpacity(0.7);
        BOTTOMTXT.setFont(Font.font("Verdana", 20));
        bottomPane.getChildren().add(BOTTOMTXT);

        primaryStage.setTitle("Chess");
        primaryStage.getIcons().add(new Image("/img/chess.jpg"));
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Starting the game. Some variables are refreshed in case a game was already 
     * running, and calling private method for every clickable square in the 
     * gridpane.
     */
    private void game()
    {
        b = new Board();
        updateBoard();
        isActive = false;
        isWhitePlayer = true;
        legalMovesRect = new ArrayList();
        nodePane.setDisable(false);
        roundId = 1;
        minutes = 0;
        seconds = 0;
        timer();
        
        for(Node n : nodePane.getChildren())
            interaction(n);
    }

    /**
     * Giving parameter object in the gridpane a clickable function.
     * @param n the object which recieves the clickable function.
     */
    private void interaction(Node n)
    {
        n.setOnMouseClicked(e ->
        {
            
            if(roundId % 2 == 1)
                isWhitePlayer = true;
            else
                isWhitePlayer = false;
            {
                // Position of pressed node
                int x = GridPane.getColumnIndex(n);
                int y = GridPane.getRowIndex(n);

                // If a piece is already pressed
                if (isActive)
                {
                    // Position of already activated node
                    int i = GridPane.getColumnIndex(activeNode);
                    int j = GridPane.getRowIndex(activeNode);

                    // If the player clicks on a square with an allied piece
                    if (b.getPiece(x, y) != null && b.getPiece(x, y).getIsWhite() == b.getPiece(i, j).getIsWhite())
                    {
                        // Same piece is clicked, deactivate it
                        if (n == activeNode)
                        {
                            n.setOpacity(1);
                            isActive = false;
                            squarePane.getChildren().removeAll(legalMovesRect);
                            legalMovesRect.clear();
                        }
                        // Another allied piece is clicked, activate that one instead
                        else
                        {
                            activeNode.setOpacity(1);
                            n.setOpacity(0.5);
                            activeNode = n;
                            squarePane.getChildren().removeAll(legalMovesRect);
                            legalMovesRect.clear();
                            showLegalMoves(x, y);
                        }
                    }
                    // Pressed square is either empty or occupied by enemy, try the move.
                    // If this part is reached, but the move was illegal, nothing happens.
                    else if (b.movePieceIfLegal(i, j, x, y))
                    {
                        // The move was performed. Deactivate node and highlighted options
                        activeNode.setOpacity(1);
                        isActive = false;
                        squarePane.getChildren().removeAll(legalMovesRect);
                        legalMovesRect.clear();
                        
                        // Round done
                        updateBoard();
                        if(b.getGameOver() > 0)
                        {
                            gameDone();
                            return;
                        }
                        roundId++;
                        
                        // If promotion triggered, method to move AI is NOT called, as AI 
                        // must wait for the player to pick a new piece to replace the pawn
                        if(b.getPiece(x, y).getPieceName().equals("P") && (y == 0 || y == 7))
                            promotionBtns(x, y);
                        // If game is not over, and opponent is easy AI, automatically make a move for black player.
                        else if(opponent == 1)
                        {
                            //long m1 = System.currentTimeMillis() % 1000;
                            moveEasyAI();
                            //long m2 = System.currentTimeMillis() % 1000;
                            //System.out.println(m2-m1);
                            // Round finished, white player can now move. Also a check if game over
                            // to see if the human player was defeated.
                            updateBoard();
                            if(b.getGameOver() > 0)
                                gameDone();
                            roundId++;
                        }
                    }
                }
                // Activate pressed node
                else if (b.getPiece(x, y) != null && b.getPiece(x, y).getIsWhite() == isWhitePlayer)
                {
                    activeNode = n;
                    n.setOpacity(0.5);
                    isActive = true;
                    
                    showLegalMoves(x, y);
                }
            }
        });
    }
    
    /**
     * Calls method from board to automatically move a piece.
     */
    private void moveEasyAI()
    {
        int[] tab = b.easyAIRound();
        int x = tab[0];
        int y = tab[1];
        int i = tab[2];
        int j = tab[3];
        
        // Add colored rectangle to the board showing the AI's move
        squarePane.getChildren().removeAll(FROMSQUARERECT, TOSQUARERECT);
        squarePane.add(FROMSQUARERECT, x, y);
        squarePane.add(TOSQUARERECT, i, j);
        
        // Automatically promote to queen if AI pawn has reached the bottom
        if(b.getPiece(i, j).getValue() == 1 && (j == 7))
            b.promotion(i, j, "bQueen");
    }
    
    /**
     * Checks every spot on the board, if clicked piece can move there. Adds 
     * a rectangle to the view for every legal move.
     * @param x position of piece
     * @param y position of piece
     */
    private void showLegalMoves(int x, int y)
    {
        int count = 0;
        for (int i=0; i<8; i++)
        {
            for (int j=0; j<8; j++)
            {
                if (b.isLegalMove(x, y, i, j))
                {
                    legalMovesRect.add(new Rectangle(100, 100, Color.TURQUOISE));
                    squarePane.add(legalMovesRect.get(count++), i, j);
                    legalMovesRect.get(count - 1).setOpacity(0.7);
                }
            }
        }
    }
    
    /**
     * Check every node in the gridpane to see if they corresponds to the 
     * chess board matrix, if not - update node image to the right piece
     */
    public void updateBoard()
    {
        for(int x=0; x<8; x++)
            for(int y=0; y<8; y++)
            {
                if(b.getPiece(x, y) == null)
                {
                    piece[x][y].setImage(OPENFIELD);
                    piece[x][y].setId("openField");
                }
                else if(!b.getPiece(x, y).getPiece().equals(piece[x][y].getId()))
                {
                    piece[x][y].setId(b.getPiece(x, y).getPiece());
                    switch (b.getPiece(x, y).getPiece())
                    {
                        case "wPawn":
                            piece[x][y].setImage(WHITEPAWN);
                            break;
                        case "bPawn":
                            piece[x][y].setImage(BLACKPAWN);
                            break;
                        case "wRook":
                            piece[x][y].setImage(WHITEROOK);
                            break;
                        case "bRook":
                            piece[x][y].setImage(BLACKROOK);
                            break;
                        case "wKnight":
                            piece[x][y].setImage(WHITEKNIGHT);
                            break;
                        case "bKnight":
                            piece[x][y].setImage(BLACKKNIGHT);
                            break;
                        case "wBishop":
                            piece[x][y].setImage(WHITEBISHOP);
                            break;
                        case "bBishop":
                            piece[x][y].setImage(BLACKBISHOP);
                            break;
                        case "wKing":
                            piece[x][y].setImage(WHITEKING);
                            break;
                        case "bKing":
                            piece[x][y].setImage(BLACKKING);
                            break;
                        case "wQueen":
                            piece[x][y].setImage(WHITEQUEEN);
                            break;
                        case "bQueen":
                            piece[x][y].setImage(BLACKQUEEN);
                            break;
                        default:
                            break;
                    }
                }
            }
    }

    private GridPane squares()
    {
        final GridPane brett = new GridPane();
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if ((i + j) % 2 == 0)
                    brett.add(new Rectangle(100, 100, Color.IVORY), i, j);
                if ((i + j) % 2 == 1)
                    brett.add(new Rectangle(100, 100, Color.BISQUE), i, j);
            }
        }
        brett.setGridLinesVisible(true);
        return brett;
    }

    private GridPane pieces()
    {
        GridPane piecePane = new GridPane();

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(12.5);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(12.5);
        piecePane.getColumnConstraints().addAll(cc, cc, cc, cc, cc, cc, cc, cc);
        piecePane.getRowConstraints().addAll(rc, rc, rc, rc, rc, rc, rc, rc);
        
        for(int i=0; i<8; i++)
            for(int j=0; j<8; j++)
            {
                piece[i][j] = new ImageView();
                piecePane.add(piece[i][j], i, j);
            }

        for(Node n : piecePane.getChildren())
            n.setPickOnBounds(true);
        return piecePane;
    }

    private HBox bottom()
    {
        bottomPane = new HBox();
        bottomPane.setLayoutX(50);
        bottomPane.setLayoutY(822.5);
        bottomPane.setSpacing(70);
        Button newGameBtn = new Button("New game");
        newGameBtn.setScaleX(2);
        newGameBtn.setScaleY(2);
        bottomPane.getChildren().add(newGameBtn);
        newGameBtn.setDisable(true);
        newGameBtn.setFocusTraversable(false);
        
        Button[] buttons = new Button[2];
        buttons[0] = new Button("Against local player");
        buttons[1] = new Button("Against easy AI");
        
        for (Button bu : buttons)
        {
            bu.setScaleX(1.5);
            bu.setScaleY(1.5);
            bu.setFocusTraversable(false);
            bottomPane.getChildren().add(bu);
            bu.setOnAction(f ->
            {
                newGameBtn.setDisable(false);
                bottomPane.getChildren().removeAll(buttons);
                if(bu.getText().contains("local"))
                    opponent = 0;
                else if(bu.getText().contains("easy"))
                    opponent = 1;
                game();
            });
        }
        
        newGameBtn.setOnMouseClicked(e ->
        {
            BOTTOMTXT.setText("");
            if(timer != null)
                timer.stop();
            newGameBtn.setDisable(true);
            nodePane.setDisable(true);
            for(Node n : nodePane.getChildren())
                n.setOpacity(1);
            bottomPane.getChildren().addAll(buttons);
            squarePane.getChildren().removeAll(legalMovesRect);
            squarePane.getChildren().removeAll(FROMSQUARERECT, TOSQUARERECT);
            legalMovesRect.clear();
            //nodePane = pieces();
        });
        return bottomPane;
    }

    private void gameDone()
    {
        timer.stop();
        squarePane.getChildren().removeAll(FROMSQUARERECT, TOSQUARERECT);
        if(b.gameOver == 3)
            BOTTOMTXT.setText("Game done, draw! Time: " + timerTxt);
        else if(b.gameOver == 1)
            BOTTOMTXT.setText("Checkmate, white player wins! Time: " + timerTxt);
        else if(b.gameOver == 2)
            BOTTOMTXT.setText("Checkmate, black player wins! Time: " + timerTxt);
        nodePane.setDisable(true);
    }
    
    /**
     * Triggered when the interaction method moves a piece, and the piece is 
     * a pawn that hits the top or the bottom of the board.
     * @param x
     * @param y
     */
    private void promotionBtns(int x, int y)
    {
        Button[] buttons = new Button[4];
        buttons[0] = new Button("Bishop");
        buttons[1] = new Button("Knight");
        buttons[2] = new Button("Rook");
        buttons[3] = new Button("Queen");
        nodePane.setDisable(true);
        for (Button bu : buttons)
        {
            bu.setScaleX(2);
            bu.setScaleY(2);
            bottomPane.getChildren().add(bu);
            bu.setOnMouseClicked(e ->
            {
                if(y == 0)
                    b.promotion(x, y, "w" + bu.getText());
                else if(y == 7)
                    b.promotion(x, y, "b" + bu.getText());
                updateBoard();
                bottomPane.getChildren().removeAll(buttons);
                nodePane.setDisable(false);
                if(opponent == 1)
                    moveEasyAI();
            });
        }
    }
    
    private void timer()
    {
        BOTTOMTXT.setText("00:00");
        timer = new Timeline(new KeyFrame(Duration.millis(1000), e -> 
        {
            seconds++;
            if(seconds == 60)
            {
                seconds = 0;
                minutes++;
            }
            timerTxt = minutes + ":" + seconds;
            if(seconds < 10)
                timerTxt = minutes + ":" + "0" + seconds;
            if(minutes < 10)
                timerTxt = "0" + timerTxt;
            BOTTOMTXT.setText(timerTxt);

        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
