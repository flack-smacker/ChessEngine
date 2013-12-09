package com.muro.ai.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoard {

	public static void main(String[] args) {
		ChessBoard board = new ChessBoard();
		
		// Move four white pawns forward two squares.
		board.performMove("Pa2a4");
		board.performMove("Pb2b4");
		board.performMove("Pc2c4");
		board.performMove("Pd2d4");
		
		// Move four black pawns forward two squares.
		board.performMove("Pa7a5");
		board.performMove("Pb7b5");
		board.performMove("Pc7c5");
		board.performMove("Pd7d5");
		
		// Perform a move that results in a capture.
		board.performMove("Pa4b5");
		board.performMove("Pb4a5");
		board.performMove("Ra1a5");
		System.out.println(board);
	}
	
	/**
	 * Constructs a new chess game initialized to the starting state.
	 */
	public ChessBoard() {
		// Initialize the chess game.
		theBoard = new ChessPiece[_0x88_BOARD_SIZE]; // Create a blank board.
		initWhite(); // Initialize white's piece list.
		initBlack(); // Initialize black's piece list.
		initChessBoard(); // Place each piece on the board.
	}

	/**
	 * Constructs a new chess board initialized to the state of the specified chess board.
	 * 
	 * @param toCopy a ChessBoard
	 */
	public ChessBoard(ChessBoard toCopy) {
		
		// Copy the board.
		this.theBoard = Arrays.copyOf(toCopy.theBoard, toCopy.theBoard.length);
		
		// Copy white's pieces
		this.whitePieces = new ArrayList<ChessPiece>(ChessBoard.STARTING_VALUES.length);
		Collections.copy(this.whitePieces, toCopy.whitePieces);
		
		// copy black's pieces
		this.blackPieces = new ArrayList<ChessPiece>(ChessBoard.STARTING_VALUES.length);
		Collections.copy(this.blackPieces, toCopy.blackPieces);
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
	 * 
	 * @return true if the specified move was performed, false otherwise
	 */
	public boolean performMove(String moveString) {
		
		boolean validMove = false; // Indicates whether the move was valid.
		
		// Translate the algebraic notation to an index into our 0x88 board array.
		int startFile = files.get((moveString.substring(1,2)));
		int startRank = Integer.parseInt(moveString.substring(2, 3)) - 1;
		int originSquare = (startRank * 16) + startFile;
		
		// Translate the algebraic notation to an index into our 0x88 board array.
		int endFile = files.get((moveString.substring(3,4)));
		int endRank = Integer.parseInt(moveString.substring(4, 5)) - 1;
		int destinationSquare = (endRank * 16) + endFile;
		
		ChessPiece toMove = theBoard[originSquare]; // the piece to be moved
		
		// Is there a piece at the origin square?
		if (toMove != null) {
			// Make the move
			theBoard[destinationSquare] = toMove;
			theBoard[originSquare] = null;
			toMove.nMoves++;
			validMove = true;
		}
		
		return validMove;
	}
	
	/**
	 * Returns a string representation of this chess board.
	 */
	public String toString() {
		
		// Our representation of the board is twice as large as a standard chess board.
		StringBuilder toReturn = new StringBuilder(ChessBoard._0x88_BOARD_SIZE / 2);
		
		// For each column and row.
		int nRows = 8;
		int nColumns = 8;
		
		for (int i=(nRows-1); i >= 0; i-=1) { // top-to-bottom
			for (int j=0; j < nColumns; j+=1) { // left-to-right
				ChessPiece aPiece = theBoard[i * 16 + j];
				if (aPiece != null) {
					toReturn.append(ChessBoard.PIECE_SYMBOLS[aPiece.value]);
					toReturn.append(aPiece.owner + " ");
				} else {
					toReturn.append("-- ");
				}
			}
			toReturn.append("\n");
		}
		
		return toReturn.toString();
	}
	
	/**
	 * Initializes white's piece list by creating 16 pieces, where each piece is initialized to it's starting location.
	 */
	private void initWhite() {
		whitePieces = new ArrayList<ChessPiece>(ChessBoard.STARTING_VALUES.length);
		
		for (int i=0; i < ChessBoard.STARTING_VALUES.length; i+=1) {
			ChessPiece piece = new ChessPiece(ChessBoard.STARTING_VALUES[i], ChessBoard.STARTING_LOCATIONS_WHITE[i], ChessBoard.WHITE);
			whitePieces.add(piece);
		}
	}
	
	/**
	 * Initializes black's piece list by creating 16 pieces, where each piece is initialized to it's starting location.
	 */
	private void initBlack() {
		blackPieces = new ArrayList<ChessPiece>(ChessBoard.STARTING_VALUES.length);
		
		for (int i=0; i < ChessBoard.STARTING_VALUES.length; i+=1) {
			ChessPiece piece = new ChessPiece(ChessBoard.STARTING_VALUES[i], ChessBoard.STARTING_LOCATIONS_BLACK[i], ChessBoard.BLACK);
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
	 * A 0x88 representation of the chess board. Each array element contains
	 * either a null reference (empty space) or a reference to a ChessPiece.
	 */
	private ChessPiece[] theBoard;

	/**
	 * Contains the pieces belonging to white that have not been captured.
	 */
	private List<ChessPiece> whitePieces;

	/**
	 * Contains the pieces belonging to black that have not been captured.
	 */
	private List<ChessPiece> blackPieces;

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
	protected static final char WHITE = 'w';
	
	/**
	 * Represents the black player.
	 */
	protected static final char BLACK = 'b';
	
	/**
	 * 
	 */
	private static final char[] PIECE_SYMBOLS = {
		'-', 'P', 'N', 'K', '-', 'B', 'R', 'Q'
	};
	
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
}
