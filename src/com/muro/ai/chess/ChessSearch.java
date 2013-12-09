package com.muro.ai.chess;

public class ChessSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public boolean isDraw() {
		// TODO
		return false;
	}

	public boolean isCheck() {
		// TODO
		return false;
	}

	public boolean isCheckmate() {
		// TODO
		return false;
	}

	public int evaluate(ChessBoard aGame) {
		// TODO
		return -1;
	}
	
	public void generateMoves(ChessPiece aPiece) {
		
	}
	
	public static final int[][] PIECE_DELTAS = {
		{},
		{15, 16, 17}, // Pawn
		{31, 33, 18, -14, -31, -33, -18, 14}, // Knight
		{15, 16, 17, 1, -15, -16, -17, -1}, // King
		{},
		{15, 17, -15, -17}, // Bishop
		{16, 1, -16, -1}, // Rook
		{15, 16, 17, 1, -15, -16, -17, -1}  // Queen
	};

}
