package com.muro.ai.chess;

import java.util.List;

import com.muro.ai.chess.ChessGame.ChessMove;

public class TestMoveGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testWhiteKing();
		testWhitePawn();

	}

	public static void testWhiteKing() {

		// Start with an empty board.
		ChessGame emptyBoard = new ChessGame();

		// Create a new King and place it on the board.
		ChessPiece whiteKing = new ChessPiece(ChessPiece.KING, 4, ChessGame.WHITE_PLAYER);
		// Add the king to white's piece list.
		emptyBoard.whitePieces.add(whiteKing);
		// Place the king in it's starting position.
		emptyBoard.theBoard[whiteKing.location] = whiteKing;

		// Create a new pawn and place it on the board.
		ChessPiece blackPawn = new ChessPiece(ChessPiece.PAWN, 20, ChessGame.BLACK_PLAYER);
		// Add the king to white's piece list.
		//emptyBoard.whitePieces.add(whitePawn);
		// Place the pawn in a position.
		emptyBoard.theBoard[blackPawn.location] = blackPawn;
		
		List<ChessMove> moves = testPiece(whiteKing, emptyBoard);

		System.out.println("Printing available moves from the following chess game...");
		System.out.println(emptyBoard);

		for (ChessMove move : moves) {
			System.out.println(move);
		}
	}
	
	public static void testWhitePawn() {

		// Start with an empty board.
		ChessGame emptyBoard = new ChessGame();

		// Create a new King and place it on the board.
		ChessPiece whitePawn = new ChessPiece(ChessPiece.PAWN, 51, ChessGame.WHITE_PLAYER);
		// Add the king to white's piece list.
		emptyBoard.whitePieces.add(whitePawn);
		// Place the king in it's starting position.
		emptyBoard.theBoard[whitePawn.location] = whitePawn;

		// Create a new pawn and place it on the board.
		ChessPiece blackPawn1 = new ChessPiece(ChessPiece.PAWN, 66, ChessGame.BLACK_PLAYER);
		ChessPiece blackPawn2 = new ChessPiece(ChessPiece.PAWN, 68, ChessGame.BLACK_PLAYER);
		emptyBoard.blackPieces.add(blackPawn1);
		emptyBoard.blackPieces.add(blackPawn2);
		
		// Place the pawn in a position.
		emptyBoard.theBoard[blackPawn1.location] = blackPawn1;
		emptyBoard.theBoard[blackPawn2.location] = blackPawn2;
		
		List<ChessMove> moves = testPiece(whitePawn, emptyBoard);

		System.out.println("Printing available moves from the following chess game...");
		System.out.println(emptyBoard);

		for (ChessMove move : moves) {
			System.out.println(move);
		}
		
		// Move the pawn and test again.
		emptyBoard.theBoard[whitePawn.location] = null;
		whitePawn.location = 100;
		emptyBoard.theBoard[whitePawn.location] = whitePawn;
		
		moves = testPiece(whitePawn, emptyBoard);

		System.out.println("Printing available moves from the following chess game...");
		System.out.println(emptyBoard);

		for (ChessMove move : moves) {
			System.out.println(move);
		}
	
	}

	public static List<ChessMove> testPiece(ChessPiece toTest, ChessGame theBoard) {

		ChessSearch pieceTests = new ChessSearch(theBoard);

		// Test the move generator...
		return pieceTests.generateMoves(toTest, toTest.owner);
	}

}
