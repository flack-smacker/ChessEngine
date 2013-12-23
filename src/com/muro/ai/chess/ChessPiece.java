package com.muro.ai.chess;

public class ChessPiece {

	public ChessPiece(int value, int location, int owner) {
		this.value = value;
		this.location = location;
		this.owner = owner;
		isCaptured = false;
		nMoves = 0;
	}

	public ChessPiece(ChessPiece toCopy) {
		this.value = toCopy.value;
		this.location = toCopy.location;
		this.owner = toCopy.owner;
		isCaptured = toCopy.isCaptured;
		nMoves = toCopy.nMoves;
	}

	/**
	 * Represents the piece type (pawn=1, knight=2, king=3, bishop=5, rook=6, queen=7).
	 */
	protected int value = -1;

	/**
	 * Represents the index into the 0x88 board where this piece is located.
	 */
	protected int location = -1;

	/**
	 * Represents the player who owns this piece.
	 */
	protected int owner = 0;

	/**
	 * Represents the number of times this piece has been moved.
	 */
	protected int nMoves = 0;

	/**
	 * Indicates whether this piece has been captured.
	 */
	protected boolean isCaptured = false;

	/**
	 * Symbols for each piece in algebraic notation.
	 */
	public static final char[] PIECE_SYMBOLS = {
		'P', 'N', 'K', 'B', 'R', 'Q'
	};

	protected static final int PAWN = 0;
	protected static final int KNIGHT = 1;
	protected static final int KING = 2;
	protected static final int BISHOP = 3;
	protected static final int ROOK = 4;
	protected static final int QUEEN = 5;
}
