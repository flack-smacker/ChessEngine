package com.muro.ai.chess;

public class ChessPiece {
	
	/**
	 * Represents the piece type (pawn=1, knight=2, king=3, bishop=5, rook=6, queen=7).
	 */
	protected int value = -1;
	
	/**
	 * Represents the index into the 0x88 board where this piece is located.
	 */
	protected int location = -1;
	
	/**
	 * Represents owner of this piece (PLAYER = 1 and OPPONENT = 2).
	 */
	protected int owner;
	
	/**
	 * Represents the number of times this piece has been moved.
	 */
	protected int nMoves = 0;
	
	public enum PieceValues {
		PAWN, KNIGHT, KING, BISHOP, ROOK, QUEEN
	};
}
