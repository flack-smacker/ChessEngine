package com.muro.ai.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoard {

	/**
	 * Constructs a new chess game initialized to the starting state.
	 */
	public ChessBoard() {
		// Create a new blank board.
		theBoard = new ChessPiece[_0x88_BOARD_SIZE];
		// Initialize white's piece list.
		// Initialize black's piece list.
		// Place each piece on the board.
	}

	/**
	 * Constructs a new chess game initialized to the state of the specified ChessGame.
	 * 
	 * @param toCopy a ChessGame
	 */
	public ChessBoard(ChessGame toCopy) {

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
	 */
	public void performMove(String moveString) {
		// TODO
	}

	private void initWhite() {
		whitePieces = new ArrayList<ChessPiece>(STARTING_LOCATIONS_WHITE.length);
	}

	private void initBlack() {
		blackPieces = new ArrayList<ChessPiece>(STARTING_LOCATIONS_BLACK.length);
	}

	private void initGameBoard() {

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
	 * A mapping from file identifiers (a-h) to integers, which are used to index the game board array.
	 */
	private static final Map<String, Integer> files = new HashMap<String, Integer>();
	
	static {
		files.put("a", 1);
		files.put("b", 2);
		files.put("c", 3);
		files.put("d", 4);
		files.put("e", 5);
		files.put("f", 6);
		files.put("g", 7);
		files.put("h", 8);
	}
}
