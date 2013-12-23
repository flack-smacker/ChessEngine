package com.muro.ai.chess;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessGame {

	public static void main(String[] args) {
		testPromotion();
	}

	public static void testUndo() {
		ChessGame board = new ChessGame();

		// Show the initial board state.
		System.out.println(board);

		board.performMove("Pa2a4");
		System.out.println(board);

		board.undoLastMove();
		System.out.println(board);
	}

	public static void testPromotion() {
		ChessGame board = new ChessGame();

		// Show the initial board state.
		System.out.println(board);

		// Move a white pawn to a capture position.
		board.performMove("Pa2a6");
		System.out.println(board);

		// Perform a capture.
		board.performMove("Pa6b7");
		System.out.println(board);

		// Move a white pawn to a capture position.
		board.performMove("Pb7c8Q");
		System.out.println(board);

		System.out.println("Undoing all moves.");

		while (!board.moveHistory.isEmpty()) {
			board.undoLastMove();
			System.out.println(board);
		}

	}

	public static void testEnpassant() {
		ChessGame board = new ChessGame();

		// Show the initial board state.
		System.out.println(board);

		// Move a white pawn to its fifth rank.
		board.performMove("Pa2a5");
		System.out.println(board);

		// Move a black pawn to the square adjacent to the white pawn.
		board.performMove("Pb7b5");
		System.out.println(board);

		// Attempt an en passant capture.
		board.performMove("Pa5b6");
		System.out.println(board);

		// Move a black pawn to its fifth rank.
		board.performMove("Pc7c4");
		System.out.println(board);

		// Move a white pawn to the square adjacent to the black pawn.
		board.performMove("Pb2b4");
		System.out.println(board);

		// Attempt an en passant capture.
		board.performMove("Pc4b3");
		System.out.println(board);

		System.out.println("Undoing all moves.");

		while (!board.moveHistory.isEmpty()) {
			board.undoLastMove();
			System.out.println(board);
		}

	}

	/**
	 * Constructs a new chess game initialized to the starting state.
	 */
	public ChessGame() {
		// Initialize the chess game.
		theBoard = new ChessPiece[_0x88_BOARD_SIZE]; // Create a blank board.
		moveHistory = new ArrayDeque<ChessMove>(); // Initialize the history stack.
		capturedPieces = new ArrayDeque<ChessPiece>(); // Initialize the stack of captured pieces.
		whitePieces = new ArrayList<ChessPiece>(ChessGame.STARTING_VALUES.length); 
		blackPieces = new ArrayList<ChessPiece>(ChessGame.STARTING_VALUES.length);
	}
	
	public void initGame() {
		initWhite(); // Initialize white's piece list.
		initBlack(); // Initialize black's piece list.
		initChessBoard(); // Place each piece on the board.
	}
	

	/**
	 * Constructs a new chess board initialized to the state of the specified chess board.
	 * 
	 * @param toCopy a ChessBoard
	 */
	public ChessGame(ChessGame toCopy) {

		// Creaet a new board.
		this.theBoard = new ChessPiece[_0x88_BOARD_SIZE];

		// Copy white's pieces and place them on the board.
		this.whitePieces = new ArrayList<ChessPiece>(ChessGame.STARTING_VALUES.length);
		for (ChessPiece piece : toCopy.whitePieces) {
			ChessPiece pieceCopy = new ChessPiece(piece);
			this.whitePieces.add(pieceCopy);
			// Place the piece on the new board.
			if (!piece.isCaptured) {
				this.theBoard[piece.location] = pieceCopy;
			}
		}

		// copy black's pieces
		this.blackPieces = new ArrayList<ChessPiece>(ChessGame.STARTING_VALUES.length);
		for (ChessPiece piece : toCopy.blackPieces) {
			ChessPiece pieceCopy = new ChessPiece(piece);
			this.blackPieces.add(pieceCopy);
			// place the piece on the new board.
			if (!piece.isCaptured) {
				this.theBoard[piece.location] = pieceCopy;
			}
		}

		// copy the history stack
		this.moveHistory = new ArrayDeque<ChessMove>();
		for (ChessMove move : toCopy.moveHistory) {
			this.moveHistory.addFirst(new ChessMove(move));
		}

		// copy the captured pieces stack
		this.capturedPieces = new ArrayDeque<ChessPiece>();
		for (ChessPiece piece : toCopy.capturedPieces) {
			this.capturedPieces.addFirst(new ChessPiece(piece));
		}

	}

	/**
	 * Performs the move contained in the specified <code>moveString</code>.
	 * 
	 * Moves are denoted using a modified form of algebraic notation for chess.
	 * Most moves will be denoted by five characters. 
	 * 		
	 * 		1. type of piece {K, Q, R, B, N, P}
	 *		2. beginning file {a, b, c, d, e, f, g, h}
	 *		3. beginning rank {1, 2, 3, 4, 5, 6, 7, 8}
	 *		4. ending file {a, b, c, d, e, f, g, h}
	 *		5. ending rank {1, 2, 3, 4, 5, 6, 7, 8}
	 *
	 * @param moveString a String representing a move in our modified algebraic notation.
	 * @return indicates whether the move was performed or not
	 * 
	 */
	public boolean performMove(String moveString) {

		// Translate the destination square from algebraic notation to a valid 0x88 index.
		int source = ChessGame.convertToIndex(moveString.substring(1, 3));

		// Translate the destination square from algebraic notation to a valid 0x88 index.
		int target = ChessGame.convertToIndex(moveString.substring(3, 5));

		// Determine if this move involves a pawn promotion.
		boolean isPromotion = ChessGame.isPromotion(moveString);

		// Indicates whether the move was valid.
		boolean result = false;

		// Verify that both the source and target squares are on the board.
		if (((source & 0x88) == 0) && ((target & 0x88) == 0)) {

			// Get the piece at the source square.
			ChessPiece aPiece = theBoard[source];

			// If there is a piece at the source than perform the move.
			if (aPiece != null) {
				ChessMove aMove = new ChessMove(aPiece, source, target, false, isPromotion);
				doMove(aMove);
				// Add the move to our history stack.
				moveHistory.addFirst(aMove);
				// Indicate success.
				result = true;
			}
		}

		return result;
	}

	/**
	 * Performs the specified chess move.
	 * 
	 * @param aMove a ChessMove
	 */
	protected void doMove(ChessMove aMove) {

		// Pawn moves require a separate method to handle attacks, en passant, and promotion.
		if (aMove.piece.value == ChessPiece.PAWN) {
			doPawnMove(aMove);
		} else {

			// Is there a piece at the destination square?
			ChessPiece toCapture = theBoard[aMove.target];

			// Check if the piece belong to our opponent.
			if (toCapture != null && aMove.piece.owner != toCapture.owner) {
				toCapture.isCaptured = true;
				aMove.isCapture = true;
				capturedPieces.addFirst(toCapture);
			}

			// Update the board.
			theBoard[aMove.target] = aMove.piece;
			theBoard[aMove.source] = null;

			// Update the piece information
			aMove.piece.nMoves++;
			aMove.piece.location = aMove.target;
		}
	}

	protected void undoMove(ChessMove toUndo) {

		// Where did the piece come from?
		int origin = toUndo.source;
		// Put the piece back in it's original position on the board.
		theBoard[origin] = toUndo.piece;
		toUndo.piece.nMoves--;
		// Update the board accordingly.
		theBoard[toUndo.target] = null;
		// Update the piece data.
		toUndo.piece.location = origin;

		// Did the prior move result in a capture?
		if (toUndo.isCapture) {
			// Get the piece that was captured.
			ChessPiece captured = capturedPieces.removeFirst();
			// Revert it back to its prior state.
			captured.isCaptured = false;
			// Place it back on the board.
			theBoard[captured.location] = captured;
		}

		// Did the prior move result in a promotion?
		if (toUndo.isPromotion) {
			// Demote the piece back to a pawn.
			toUndo.piece.value = ChessPiece.PAWN;
		}


	}

	/**
	 * Reverts the chess board to the state before the prior move was performed.
	 * 
	 * @return true if the last move was reverted successfully, false otherwise.
	 */
	public boolean undoLastMove() {

		ChessMove toUndo = moveHistory.removeFirst();

		if (toUndo != null) {

			// Where did the piece come from?
			int origin = toUndo.source;
			// Put the piece back in it's original position on the board.
			theBoard[origin] = toUndo.piece;
			toUndo.piece.nMoves--;
			// Update the board accordingly.
			theBoard[toUndo.target] = null;
			// Update the piece data.
			toUndo.piece.location = origin;

			// Did the prior move result in a capture?
			if (toUndo.isCapture) {
				// Get the piece that was captured.
				ChessPiece captured = capturedPieces.removeFirst();
				// Revert it back to its prior state.
				captured.isCaptured = false;
				// Place it back on the board.
				theBoard[captured.location] = captured;
			}

			// Did the prior move result in a promotion?
			if (toUndo.isPromotion) {
				// Demote the piece back to a pawn.
				toUndo.piece.value = ChessPiece.PAWN;
			}
		}

		return true;
	}

	private void doPawnMove(ChessMove aMove) {

		// Determine if this is an attack move by checking if the source and target file differ.
		if ((aMove.source %  16) != (aMove.target % 16)) {
			// Determine if this is an en passant attack.
			if (!doEnpassant(aMove)) {
				// Is there a piece at the destination square?
				ChessPiece toCapture = theBoard[aMove.target];
				// Check if the piece belong to our opponent.
				if (toCapture != null && aMove.piece.owner != toCapture.owner) {
					toCapture.isCaptured = true;
					aMove.isCapture = true;
					capturedPieces.addFirst(toCapture);
				}
			}
		}

		// Update the board.
		theBoard[aMove.target] = aMove.piece;
		theBoard[aMove.source] = null;

		// Update the piece information
		aMove.piece.nMoves++;
		aMove.piece.location = aMove.target;

		// Check if this is a promotion.
		if (aMove.isPromotion) {
			// Default the promotion to a queen.
			aMove.piece.value = ChessPiece.QUEEN;
		}
	}

	/**
	 * Determines whether the specified move is an en passant attack and, if so, performs the move.
	 * 
	 * @param aMove a ChessMove
	 * @return true if the move was determined to be an en passant attack and false otherwise.
	 */
	private boolean doEnpassant(ChessMove aMove) {

		boolean result = false;

		// the starting rank of the attacking piece
		int startRank = aMove.source / 16;

		// the ending rank and file of the attacking piece
		int endRank = aMove.target / 16;
		int endFile = aMove.target % 16;

		// Determine if this is en passant attack by checking if the pawn is on its fifth rank.
		if ( ((startRank == 3) && (endRank == 2)) || // black en passant 
				((startRank == 4) && (endRank == 5))) { // white en passant

			// Determine if there is an enemy pawn on the adjacent file
			int adjacentIndex = (startRank * 16) + endFile;
			ChessPiece adjacentPiece = theBoard[adjacentIndex];

			if (adjacentPiece != null && 
					adjacentPiece.value == ChessPiece.PAWN && 
					adjacentPiece.owner != aMove.piece.owner) {

				ChessMove priorMove = moveHistory.peekFirst();

				// Determine if the move turn immediately before ours was a double-step move
				if (priorMove.piece.value == ChessPiece.PAWN) {

					int priorRankStart = priorMove.source / 16;
					int priorRankEnd = priorMove.target / 16;

					if (Math.abs(priorRankEnd - priorRankStart) == 2) {
						adjacentPiece.isCaptured = true;
						theBoard[adjacentIndex] = null;
						capturedPieces.addFirst(adjacentPiece);
						aMove.isCapture = true;
					}
				} // End double-step check
			} // End adjacency check
		} // End en passant check

		return result;
	}

	/**
	 * Returns a string representation of this chess board.
	 */
	public String toString() {

		// Our representation of the board is twice as large as a standard chess board.
		StringBuilder toReturn = new StringBuilder(ChessGame._0x88_BOARD_SIZE / 2);
		toReturn.append(FILES + "\n");
		// For each column and row.
		int nRows = 8;
		int nColumns = 8;

		for (int i=(nRows-1); i >= 0; i-=1) { // top-to-bottom
			toReturn.append((i + 1) + " |");
			for (int j=0; j < nColumns; j+=1) { // left-to-right
				ChessPiece aPiece = theBoard[i * 16 + j];
				if (aPiece != null) {
					toReturn.append(ChessPiece.PIECE_SYMBOLS[aPiece.value]);
					toReturn.append(aPiece.owner + " ");
				} else {
					toReturn.append("-- ");
				}
			}
			toReturn.append("| " + (i + 1));
			toReturn.append("\n");
		}
		toReturn.append(FILES + "\n");
		
		return toReturn.toString();
	}

	/**
	 * Initializes white's piece list by creating 16 pieces, where each piece is initialized to it's starting location.
	 */
	private void initWhite() {
		for (int i=0; i < ChessGame.STARTING_VALUES.length; i+=1) {
			ChessPiece piece = new ChessPiece(ChessGame.STARTING_VALUES[i], ChessGame.STARTING_LOCATIONS_WHITE[i], ChessGame.WHITE_PLAYER);
			whitePieces.add(piece);
		}
	}

	/**
	 * Initializes black's piece list by creating 16 pieces, where each piece is initialized to it's starting location.
	 */
	private void initBlack() {
		for (int i=0; i < ChessGame.STARTING_VALUES.length; i+=1) {
			ChessPiece piece = new ChessPiece(ChessGame.STARTING_VALUES[i], ChessGame.STARTING_LOCATIONS_BLACK[i], ChessGame.BLACK_PLAYER);
			blackPieces.add(piece);
		}
	}

	/**
	 * Initializes the chess board by placing each piece in it's starting location.
	 */
	private void initChessBoard() {
		// Place white's pieces on the board.
		for (ChessPiece aPiece : whitePieces) {
			theBoard[aPiece.location] = aPiece;
		}
		// Place black's pieces on the board.
		for (ChessPiece aPiece : blackPieces) {
			theBoard[aPiece.location] = aPiece;
		}
	}

	/**
	 * Converts the square specified in algebraic notation into an index into our 0x88 board.
	 * 
	 * @param move a String
	 * @return an integer index
	 */
	private static int convertToIndex(String move) {
		// Convert the file (a-h) to an integer.
		int startFile = files.get((move.substring(0,1)));
		// Convert the rank to our 0x88 board representation.
		int startRank = Integer.parseInt(move.substring(1, 2)) - 1;
		// Calculate the index into the board array.
		int index = (startRank * 16) + startFile;
		// Return the result.
		return index;
	}

	/**
	 * Indicates whether the specified move results in a pawn promotion.
	 * 
	 * @param moveString a chess move in algebraic notation
	 * @return true if the specified move results in a pawn promotion.
	 */
	private static boolean isPromotion(String moveString) {
		// Right now we assume that any move of length six involving a pawn is a promotion.
		return (moveString.length() == 6 && moveString.substring(0,1).equals("P"));
	}
	/**
	 * A 0x88 representation of the chess board. Each array element contains
	 * either a null reference (empty space) or a reference to a ChessPiece.
	 */
	protected ChessPiece[] theBoard;

	/**
	 * Contains the pieces belonging to white that have not been captured.
	 */
	protected List<ChessPiece> whitePieces;

	/**
	 * Contains the pieces belonging to black that have not been captured.
	 */
	protected List<ChessPiece> blackPieces;

	/**
	 * A stack containing all of the moves made thus far.
	 */
	protected Deque<ChessMove> moveHistory = null;

	/**
	 * A stack containing all of the pieces captured thus far. 
	 */
	protected Deque<ChessPiece> capturedPieces = null;

	/**
	 * The size of the array in the 0x88 chess board representation.
	 */
	private static final int _0x88_BOARD_SIZE = 128;

	/**
	 * The pieces that each player starts with. 
	 */
	private static final int[] STARTING_VALUES = {
		ChessPiece.PAWN, ChessPiece.PAWN, ChessPiece.PAWN, ChessPiece.PAWN, 
		ChessPiece.PAWN, ChessPiece.PAWN, ChessPiece.PAWN, ChessPiece.PAWN,
		ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISHOP, ChessPiece.QUEEN,
		ChessPiece.KING, ChessPiece.BISHOP, ChessPiece.KNIGHT, ChessPiece.ROOK
	};

	/**
	 * The indices into the 0x88 representing the starting locations for each of white's pieces.
	 */
	private static final int[] STARTING_LOCATIONS_WHITE = {
		16, 17, 18, 19, 20, 21, 22, 23, // Pawns
		0, 1, 2, 3, 4, 5, 6, 7, 8 // Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
	};

	/**
	 * The indices into the 0x88 representing the starting locations for each of black's pieces.
	 */
	private static final int[] STARTING_LOCATIONS_BLACK = {
		96, 97, 98, 99, 100, 101, 102, 103, // Pawns
		112, 113, 114, 115, 116, 117, 118, 119 // Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
	};

	/**
	 * Represents the white player.
	 */
	protected static final int WHITE_PLAYER = 0;

	/**
	 * Represents the black player.
	 */
	protected static final int BLACK_PLAYER = 1;

	/**
	 * A mapping from file identifiers (a-h) to integers, which are used to index the game board array.
	 */
	private static final Map<String, Integer> files = new HashMap<String, Integer>();

	static {
		files.put("a", 0);
		files.put("b", 1);
		files.put("c", 2);
		files.put("d", 3);
		files.put("e", 4);
		files.put("f", 5);
		files.put("g", 6);
		files.put("h", 7);
	}

	/**
	 * Allows conversion of the files from the array-based representation each file to the chess notation.
	 */
	public static final char[] FILE_SYMBOLS = {
		'a', 'b', 'c', 'd',
		'e', 'f', 'g', 'h'
	};
	
	public static final String FILES = "    A  B  C  D  E  F  G  H";

	static class ChessMove {

		ChessMove(ChessPiece aPiece, int source, int target, boolean isCapture, boolean isPromotion) {
			this.piece = aPiece;
			this.source = source;
			this.target = target;
			this.isCapture = isCapture;
			this.isPromotion = isPromotion;
		}

		ChessMove(ChessMove toCopy) {
			this.piece = new ChessPiece(toCopy.piece);
			this.source = toCopy.source;
			this.target = toCopy.target;
			this.isCapture = toCopy.isCapture;
			this.isPromotion = toCopy.isPromotion;
		}

		/**
		 *  Converts the move to a String representing a move in algebraic notation.
		 */
		public String toString() {

			StringBuilder move = new StringBuilder();

			// Append the piece symbol (i.e., P, N, K, B, R, Q).
			move.append(ChessPiece.PIECE_SYMBOLS[this.piece.value]);

			// Append the starting location (e.g., a1, d4, etc.).
			move.append(ChessGame.FILE_SYMBOLS[this.source % 16]);
			move.append(this.source / 16 + 1);

			// Append the ending location (e.g., a1, d4, etc.).
			move.append(ChessGame.FILE_SYMBOLS[this.target % 16]);
			move.append(this.target / 16 + 1);

			// return the result
			return move.toString();
		}

		/**
		 * The ChessPiece that was moved.
		 */
		ChessPiece piece = null;

		/**
		 * The starting square.
		 */
		int source = -1;

		/**
		 * The ending square.
		 */
		int target = -1;

		/**
		 * Indicates whether this move resulted in a capture.
		 */
		boolean isCapture = false;

		/**
		 * Indicates whether this move resulted in a pawn promotion.
		 */
		boolean isPromotion = false;

	}
}
