package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 * 
 * @author Noah Roberts
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                if (r % 2 == c % 2) {
                    if (r < 3) {
                        board[r][c] = RED;
                    } else if (r > 4) {
                        board[r][c] = BLACK;
                    } else {
                        board[r][c] = EMPTY;
                    }
                } else {
                    board[r][c] = EMPTY;
                }
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;

        if (Math.abs(fromRow - toRow) == 2 && Math.abs(fromCol - toCol) == 2) {
            board[(fromRow + toRow) / 2][(fromCol + toCol) / 2] = EMPTY; // Remove jumped piece.
        }

        if (toRow == 0 && board[toRow][toCol] == RED) {
            board[toRow][toCol] = RED_KING;
        } else if (toRow == board.length - 1 && board[toRow][toCol] == BLACK) {
            board[toRow][toCol] = BLACK_KING;
        }
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        if (player != RED && player != BLACK && player != RED_KING && player != BLACK_KING) {
            return null;
        }
    
        ArrayList<CheckersMove> legalMoves = new ArrayList<>();
        boolean jumpAvailable = false;
    
        // Traverse the entire board to find all legal moves for the player.
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == player || board[row][col] == (player == RED ? RED_KING : BLACK_KING)) {
                    CheckersMove[] jumps = getLegalJumpsFrom(player, row, col);
                    if (jumps != null) {
                        legalMoves.addAll(Arrays.asList(jumps));
                        jumpAvailable = true;
                    }
                }
            }
        }
    
        if (jumpAvailable) {
            return legalMoves.toArray(new CheckersMove[0]);
        }
    
        // If no jumps, look for regular moves
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == player || board[row][col] == (player == RED ? RED_KING : BLACK_KING)) {
                    // Check for regular moves
                    CheckersMove[] regularMoves = getLegalRegularMovesFrom(player, row, col);
                    if (regularMoves != null) {
                        legalMoves.addAll(Arrays.asList(regularMoves));
                    }
                }
            }
        }
    
        // If no legal moves found, return null
        if (legalMoves.size() == 0) {
            return null;
        } else {
            return legalMoves.toArray(new CheckersMove[0]);
        }
    }
    


    /**
     * Determines if the square at the given row and col is on the board.
     */
    boolean isValidSquare(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board.length;
    }

    /**
     * Determines if the square at the given row and col is the opponent of the player.
     * 
     * Returns false if:
     * - The square is empty
     * - The square is not on the board
     * - The square is the same team as the player.
     * 
     * Returns true otherwise.
     */
    boolean isOpponent(int player, int row, int col) {
        if (!isValidSquare(row, col)) {
            return false;
        }

        if (player == RED || player == RED_KING) {
            return board[row][col] == BLACK || board[row][col] == BLACK_KING;
        } else {
            return board[row][col] == RED || board[row][col] == RED_KING;
        }
    }

    CheckersMove[] getLegalRegularMovesFrom(int player, int row, int col) {
        ArrayList<CheckersMove> legalRegularMoves = new ArrayList<>();

        int[] rowDirections;
        int[] colDirections;

        if (player == RED) {
            rowDirections = new int[]{-1, -1}; 
            colDirections = new int[]{-1, 1}; 
        } else if (player == BLACK) {
            rowDirections = new int[]{1, 1};
            colDirections = new int[]{-1, 1};
        } else if (player == RED_KING || player == BLACK_KING) {
            rowDirections = new int[]{-1, -1, 1, 1};
            colDirections = new int[]{-1, 1, -1, 1};
        } else {
            return null;
        }

        for (int i = 0; i < rowDirections.length; i++) {
            int newRow = row + rowDirections[i];
            int newCol = col + colDirections[i];
            if (isValidSquare(newRow, newCol) && board[newRow][newCol] == EMPTY) {
                legalRegularMoves.add(new CheckersMove(row, col, newRow, newCol));
            }
        }

        return legalRegularMoves.toArray(new CheckersMove[0]);
    }

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps. 
     * Each move returned in the array represents a sequence of jumps 
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        ArrayList<CheckersMove> legalJumps = new ArrayList<>();

        int[] rowDirections;
        int[] colDirections;

        if (player == RED) {
            rowDirections = new int[]{-1, -1}; 
            colDirections = new int[]{-1, 1}; 
        } else if (player == BLACK) {
            rowDirections = new int[]{1, 1};
            colDirections = new int[]{-1, 1};
        } else if (player == RED_KING || player == BLACK_KING) {
            rowDirections = new int[]{-1, -1, 1, 1};
            colDirections = new int[]{-1, 1, -1, 1};
        } else {
            return null;
        }

        for (int i = 0; i < rowDirections.length; i++) {
            int jumpRow = row + 2 * rowDirections[i];
            int jumpCol = col + 2 * colDirections[i];
            int midRow = row + rowDirections[i];
            int midCol = col + colDirections[i];
    
            // Check if the jump is within bounds and if it is a valid jump
            if (isValidSquare(jumpRow, jumpCol) && board[jumpRow][jumpCol] == EMPTY &&
                    isOpponent(player, midRow, midCol)) {
    
                // Create a move for this jump
                CheckersMove jumpMove = new CheckersMove(row, col, jumpRow, jumpCol);
                
                // Recursively check for additional jumps from the new landing position
                CheckersMove[] furtherJumps = getLegalJumpsFrom(player, jumpRow, jumpCol);
                if (furtherJumps != null && furtherJumps.length > 0) {
                    // Add subsequent jumps to the move
                    for (CheckersMove furtherJump : furtherJumps) {
                        CheckersMove multiJumpMove = jumpMove.clone();
                        multiJumpMove.addMove(furtherJump.rows.get(1), furtherJump.cols.get(1));
                        legalJumps.add(multiJumpMove);
                    }
                } else {
                    // Add the single jump move if no further jumps are available
                    legalJumps.add(jumpMove);
                }
            }
        }
    
        if (legalJumps.size() == 0) {
            return null; // No jumps available
        } else {
            return legalJumps.toArray(new CheckersMove[0]);
        }
    }

}
