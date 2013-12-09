package com.muro.ai.chess;

public class ChessPiece {
	
	public ChessPiece(int value, int location, char owner) {
		this.value = value;
		this.location = location;
		this.owner = owner;
		isCaptured = false;
		nMoves = 0;
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
	protected char owner = 0;
	
	/**
	 * Represents the number of times this piece has been moved.
	 */
	protected int nMoves = 0;
	
	/**
	 * Indicates whether this piece has been captured.
	 */
	protected boolean isCaptured = false;
	
	protected static final int PAWN = 1;
	protected static final int KNIGHT = 2;
	protected static final int KING = 3;
	protected static final int BISHOP = 5;
	protected static final int ROOK = 6;
	protected static final int QUEEN = 7;
}
