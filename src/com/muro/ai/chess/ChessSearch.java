package com.muro.ai.chess;

import java.util.List;
import java.util.ArrayList;

import com.muro.ai.chess.ChessGame.ChessMove;

public class ChessSearch {

	public static void main(String[] args) {

	}

	public static ChessMove findNextMove(ChessGame gameState, int teamNumber, int timeLimit, int depthLimit) {
		ChessSearch theSearch = new ChessSearch(gameState);
		return theSearch.alphaBetaSearch(timeLimit, depthLimit, teamNumber);
	}

	public static ChessMove randomWhiteMove(ChessGame gameState) {
		ChessSearch theSearch = new ChessSearch(gameState);
		List<ChessMove> moveList = theSearch.generateWhiteMoves();
		int randomMove = (int) (Math.random() * moveList.size());
		return moveList.get(randomMove);
	}

	public static ChessMove randomBlackMove(ChessGame gameState) {
		ChessSearch theSearch = new ChessSearch(gameState);
		List<ChessMove> moveList = theSearch.generateBlackMoves();
		int randomMove = (int) (Math.random() * moveList.size());
		return moveList.get(randomMove);
	}


	/**
	 * Constructs a new ChessSearch to be performed on the specified ChessGame.
	 * 
	 * @param toSearch a ChessGame
	 */
	ChessSearch(ChessGame toSearch) {
		chessGame = new ChessGame(toSearch);
		globalMax = null;
		globalMin = null;
	}

	/**
	 * Generates a chess move by performing alpha-beta search on the current game state. The team parameter is used to specify
	 * the perspective from which to perform the search. A value of 2 for the team number specifies a search for the black player and a value of 1
	 * is for the white player. 
	 * 
	 * @param timeLimit the maximum duration to execute the search
	 * @param depthLimit the maximum search depth
	 * @param team an integer (1 or 2) representing the white or black team
	 * 
	 * @return a ChessMove specifying the best move
	 */
	private ChessMove alphaBetaSearch(int timeLimit, int depthLimit, int team) {

		// Set the search duration value.
		this.timeLimit = timeLimit * NANOS_PER_SECOND;
		// Set the depth limit.
		this.depthLimit = depthLimit;
		// Mark the start time.
		startTime = System.nanoTime();

		// Perform the search.
		if (team == ChessGame.WHITE_PLAYER) {
			alphaSearch(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			return globalMax;
		} else {
			betaSearch(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			return globalMin;
		}
	}

	private int alphaSearch(int alpha, int beta, int depth) {

		// Have we exceeded the time limit?
		if (System.nanoTime() - startTime > timeLimit || depth > depthLimit) { 
			return evaluate(chessGame);
		}

		// Generate all moves for white...
		List<ChessMove> whiteMoves = generateWhiteMoves();

		// We haven't seen anything great thus far.
		int currentBest = Integer.MIN_VALUE;

		for (ChessMove currentMove : whiteMoves) {

			// Make the move.
			chessGame.doMove(currentMove);
			chessGame.moveHistory.addFirst(currentMove);

			// Check if this move results in a value greater than our current max.
			currentBest = Math.max(currentBest, betaSearch(alpha, beta, depth+1));

			// Check if MIN will allow us to use this value.
			if (currentBest >= beta) {
				// MIN has seen a worse value so we prune.
				chessGame.undoMove(currentMove);
				chessGame.moveHistory.removeFirst();
				return currentBest;
			}

			// Check if this move results in a better ALPHA value.
			if ( currentBest > alpha ) {
				alpha = currentBest;
				if (depth == 0) {
					globalMax = currentMove;
				}
			}

			// Undo the last move and loop again..
			chessGame.undoMove(currentMove);
			chessGame.moveHistory.removeFirst();
		}

		return currentBest;
	}

	private int betaSearch(int alpha, int beta, int depth) {

		// Have we reached the depth limit or exceeded our allotted search time?
		if (System.nanoTime() - startTime > timeLimit|| depth > depthLimit) { 
			return evaluate(chessGame);
		}

		// Generate all possible moves for black.
		List<ChessMove> blackMoves = generateBlackMoves();

		// We haven't seen anything great thus far.
		int currentMin = Integer.MAX_VALUE;

		for (ChessMove currentMove : blackMoves) {

			// Make the move.
			chessGame.doMove(currentMove);
			chessGame.moveHistory.addFirst(currentMove);

			// Check if this move results in a value that is less than our current minimum.
			currentMin = Math.min(currentMin, alphaSearch(alpha, beta, depth+1));

			// Check if MAX will allow us to use this value.
			if (currentMin <= alpha) {
				// MAX has already seen a better value so we prune.
				chessGame.undoMove(currentMove);
				chessGame.moveHistory.removeFirst();
				return currentMin;
			}

			// Check if this move resulted in a lower BETA value.
			if ( currentMin < beta ) {
				beta = currentMin;

				if (depth == 0) {
					globalMin = currentMove;
				}
			}

			// Undo the last move and loop again.
			chessGame.undoMove(currentMove);
			chessGame.moveHistory.removeFirst();
		}

		return currentMin;
	}

	/**
	 * A simple evaluation function that returns the difference between white and black's material score.
	 * 
	 * @param aGame a ChessGame
	 * @return an integer
	 */
	private int evaluate(ChessGame aGame) {

		// Calculate white's material score.
		int whiteMaterialScore = 0;
		int whiteMoveScore = 0;

		for (ChessPiece whitePiece : aGame.whitePieces) {
			if (!whitePiece.isCaptured) {

				if (whitePiece.value == ChessPiece.PAWN && whitePiece.nMoves >= 2 && chessGame.moveHistory.size() < 10) {
					whiteMoveScore += (10 * whitePiece.nMoves);
				}

				if (whitePiece.value != ChessPiece.PAWN && whitePiece.value != ChessPiece.KING && chessGame.moveHistory.size() < 15) {
					whiteMoveScore += (15 * whitePiece.nMoves);
				}

				whiteMaterialScore += ChessSearch.MATERIAL_VALUES[whitePiece.value];
			}
		}

		// Calculate black's material score.
		int blackMaterialScore = 0;
		int blackMoveScore = 0;
		for (ChessPiece blackPiece : aGame.blackPieces) {
			if (!blackPiece.isCaptured) {

				if (blackPiece.value == ChessPiece.PAWN && blackPiece.nMoves >= 2 && chessGame.moveHistory.size() < 10) {
					blackMoveScore += (10 * blackPiece.nMoves);
				}

				if (blackPiece.value != ChessPiece.PAWN && blackPiece.value != ChessPiece.KING && chessGame.moveHistory.size() < 15) {
					blackMoveScore += (15 * blackPiece.nMoves);
				}

				blackMaterialScore += ChessSearch.MATERIAL_VALUES[blackPiece.value];
			}
		}

		int materialScore = whiteMaterialScore - blackMaterialScore;

		int moveScore = whiteMoveScore - blackMoveScore;

		return materialScore + moveScore;
	}

	@SuppressWarnings("unused")
	private boolean isChecked(ChessPiece aKing) {
		return false;
	}

	/**
	 * Generates all possible moves available to white given the current state.
	 * 
	 * @return a list of ChessMoves
	 */
	protected List<ChessMove> generateWhiteMoves() {
		List<ChessMove> moves = new ArrayList<ChessMove>();

		for (ChessPiece whitePiece : chessGame.whitePieces) {
			if (!whitePiece.isCaptured) {
				moves.addAll(generateMoves(whitePiece, ChessGame.WHITE_PLAYER));
			}
		}

		return moves;
	}

	/**
	 * Generates all possible moves to black given the current state.
	 * 
	 * @return a list of ChessMoves
	 */
	private List<ChessMove> generateBlackMoves() {
		List<ChessMove> moves = new ArrayList<ChessMove>();

		for (ChessPiece blackPiece : chessGame.blackPieces) {
			if (!blackPiece.isCaptured) {
				moves.addAll(generateMoves(blackPiece, ChessGame.BLACK_PLAYER));
			}
		}

		return moves;
	}

	/**
	 * Generates all psuedo-legal moves for the specified player's piece.  A psuedo-legal move is a move that possibly 
	 * results in leaving the specified player's king in check.
	 *
	 * @param aPiece
	 * @param player
	 * @return
	 */
	List<ChessMove> generateMoves(ChessPiece aPiece, int player) {

		List<ChessMove> moves = null;

		int[][] deltas = null;

		if (player == ChessGame.BLACK_PLAYER) {
			deltas = ChessSearch.BLACK_PIECE_DELTAS;
		} else {
			deltas = ChessSearch.WHITE_PIECE_DELTAS;
		}

		switch (aPiece.value) {

		case ChessPiece.PAWN:
			moves = doGeneratePawnMoves(aPiece, deltas[ChessPiece.PAWN]);
			break;
		case ChessPiece.KNIGHT:
			moves = doGenerateMoves(aPiece, deltas[ChessPiece.KNIGHT]);
			break;
		case ChessPiece.KING:
			moves = doGenerateMoves(aPiece, deltas[ChessPiece.KING]);
			break;
		case ChessPiece.BISHOP:
			moves = doGenerateMovesSliding(aPiece, deltas[ChessPiece.BISHOP]);
			break;
		case ChessPiece.ROOK:
			moves = doGenerateMovesSliding(aPiece, deltas[ChessPiece.ROOK]);
			break;
		case ChessPiece.QUEEN:
			moves = doGenerateMovesSliding(aPiece, deltas[ChessPiece.QUEEN]);
			break;
		}

		return moves;
	}

	/**
	 * Generates all possible moves for the specified chess piece.
	 *
	 * @param aPiece a non-sliding ChessPiece
	 * @return a List of chess moves specified in algebraic notation
	 */
	private List<ChessMove> doGenerateMoves(ChessPiece aPiece, int[] deltas) {
		List<ChessMove> moves = new ArrayList<ChessMove>();

		for (int delta: deltas) {
			// Move the piece to the square represented by delta.
			int currentIndex = aPiece.location + delta;
			// If the square is on the board...
			if ((currentIndex & 0x88) == 0) {
				// Get the piece at the current square.
				ChessPiece piece = chessGame.theBoard[currentIndex];
				// Is this square empty?
				if (piece == null) {
					// Its an empty square so this move is valid.
					moves.add(new ChessMove(aPiece, aPiece.location, currentIndex, false, false));
				} else if (piece.owner != aPiece.owner) {
					// The square is occupied by an enemy piece so this move is valid.
					moves.add(new ChessMove(aPiece, aPiece.location, currentIndex, true, false));
				}
			}
		}

		return moves;
	}

	/**
	 * Generates all possible moves for the specified chess piece.
	 *
	 * @param aPiece a sliding ChessPiece
	 * @return a list of String representing chess moves in algebraic notation
	 */
	private List<ChessMove> doGenerateMovesSliding(ChessPiece aPiece, int[] deltas) {

		List<ChessMove> moves = new ArrayList<ChessMove>();
		// For every possible direction in which this piece can move...
		for (int delta : deltas) {
			// Reset the piece to its starting location.
			int currentIndex = aPiece.location;
			// Move the piece one square in the current direction
			currentIndex += delta;
			// Indicates whether this piece is blocked.
			boolean isBlocked = false;
			// Slide the piece until it is off the board or until another piece is encountered.
			while ((currentIndex & 0x88) == 0 && !isBlocked) {
				// Get the piece at the current square.
				ChessPiece piece = chessGame.theBoard[currentIndex];
				// Is this square empty?
				if (piece == null) {
					// Its an empty square so this move is valid.
					moves.add(new ChessMove(aPiece, aPiece.location, currentIndex, false, false));
				} else { // We encountered another piece.
					// Check if we can capture the piece.
					if (piece.owner != aPiece.owner) {
						// The square is occupied by an enemy piece so this move is valid.
						moves.add(new ChessMove(aPiece, aPiece.location, currentIndex, true, false));
					}
					// Stop moving.
					isBlocked = true;
				}
				// Move the piece one square in the current direction
				currentIndex += delta;
			}
		}
		return moves;
	}

	private List<ChessMove> doGeneratePawnMoves(ChessPiece aPawn, int[] deltas) {
		List<ChessMove> moves = new ArrayList<ChessMove>();

		int startIndex = aPawn.location;

		// Can we move the pawn forward one square?
		int upOne = startIndex + deltas[0];

		if (((upOne & 0x88) == 0) && chessGame.theBoard[upOne] == null) {
			moves.add(new ChessMove(aPawn, aPawn.location, upOne, false, false));
		}

		// Check for attack moves...
		ChessPiece toAttack = null;

		// Can the pawn attack left?
		int attackLeft = startIndex + deltas[1];
		if (((attackLeft & 0x88) == 0)) {
			// Is there an enemy piece to attack?
			toAttack = chessGame.theBoard[attackLeft];	
			if (toAttack != null && toAttack.owner != aPawn.owner) {
				moves.add(new ChessMove(aPawn, aPawn.location, attackLeft, true, false));
			}
		}

		// TODO: Can the pawn perform an en passant attack left?

		// Can the pawn attack right?
		int attackRight = startIndex + deltas[2];		
		if ((attackRight& 0x88) == 0) {
			toAttack = chessGame.theBoard[attackRight];
			if (toAttack != null && toAttack.owner != aPawn.owner) {
				moves.add(new ChessMove(aPawn, aPawn.location, attackRight, true, false));
			}
		}

		// TODO: Can the pawn perform an en passant attack right?

		// Can the pawn move forward two squares?
		int doubleMove = startIndex + deltas[3];
		if ( ((doubleMove & 0x88) == 0) && 
				chessGame.theBoard[doubleMove] == null &&
				aPawn.nMoves == 0) {
			moves.add(new ChessMove(aPawn, aPawn.location, doubleMove, false, false));
		}

		// Check each move to see if it results in a promotion.
		for (ChessMove move : moves) {
			// Check if moving the piece forward one square would move it off the board.
			if (((move.source + deltas[0]) & 0x88) != 0) {
				move.isPromotion = true;
			}
		}

		return moves;
	}

	/**
	 * Determines if the specified piece is vulnerable to attack.
	 * 
	 * @param location an index into the game board.
	 * 
	 * @return true if this square can be attacked by an enemy piece.
	 */
	@SuppressWarnings("unused")
	private boolean isAttacked(ChessPiece aPiece) {

		// Check trivial cases.
		if (aPiece == null || aPiece.isCaptured) {
			return false;
		}

		// Where is this piece located on the board?
		int pieceIndex = aPiece.location;

		// Get all the opponent's pieces.
		List<ChessPiece> attackers = new ArrayList<ChessPiece>();
		if (aPiece.owner == ChessGame.BLACK_PLAYER) {
			attackers = chessGame.whitePieces;
		} else {
			attackers = chessGame.blackPieces;
		}

		// For each of the opponent's pieces.
		for (ChessPiece attacker : attackers) {
			// Check if this piece is still on the board.
			if (!attacker.isCaptured) {
				if (ChessSearch.canAttack(attacker, pieceIndex)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines if the specified piece can attack the specified index.
	 * 
	 * @param attacker a ChessPiece
	 * @param pieceIndex an integer representing a location the board.
	 */
	private static boolean canAttack(ChessPiece attacker, int pieceIndex) {

		// Where is the attacker located relative to the square to be attacked?
		//int delta = attacker.location - pieceIndex;

		return true;

	}

	/**
	 * The chess game on which the search is to be performed.
	 */
	private ChessGame chessGame = null;

	/**
	 * The best move seen by max thus far.
	 */
	private ChessMove globalMax = null;

	/**
	 * The best move seen by min thus far.
	 */
	private ChessMove globalMin = null;

	/**
	 * Represents the maximum search depth.
	 */
	private int depthLimit = -1;

	/**
	 * The time this search started.
	 */
	private long startTime = 0;

	/**
	 * Represents the alloted search time.
	 */
	private long timeLimit = -1;


	/**
	 * Used in the evaluation function.
	 */
	public static final int[] MATERIAL_VALUES = {
		100, // pawn
		300, // knight
		1_000_000, // king
		325, // bishop
		500, // rook
		900 // queen
	};

	public static final int[][] WHITE_PIECE_DELTAS = {
		// Pawn
		{
			16,  // up one
			15, 17, // up-left, up-right (attack moves only)
			32 // up two squares (first move only)
		},
		// Knight
		{
			31, 33,
			18, -14, // right two up one, right two down one
			14, -18, // left two up one, left two down one
			-33, -31 // down two left one, down two right one
		},
		// King
		{
			16, -16, // forward, backward
			-1, 1, // left, right
			15, 17,  // up-left, up-right
			-17, -15 // down-left, down-right
		},
		// Bishop
		{
			15, 17, // up-left, up-right
			-17, -15 // down-left, down-right
		},
		// Rook
		{
			16, -16,  // up, down
			-1, 1, // left, right
		},
		// Queen
		{
			16, -16, // up, down
			-1, 1, // left, right
			15, 17, // up-left, up-right
			-17, -15  // down-left, down-right
		}
	};

	public static final int[][] BLACK_PIECE_DELTAS = {
		// Pawn
		{
			-16,  // up one
			-15, -17, // up-left, up-right (attack moves only)
			-32 // up two squares (first move only)
		},
		// Knight
		{
			-31, -33,
			-18, 14, // right two up one, right two down one
			-14, 18, // left two up one, left two down one
			33, 31 // down two left one, down two right one
		},
		// King
		{
			-16, 16, // forward, backward
			1, -1, // left, right
			-15, -17,  // up-left, up-right
			17, 15 // down-left, down-right
		},
		// Bishop
		{
			-15, -17, // up-left, up-right
			17, 15 // down-left, down-right
		},
		// Rook
		{
			-16, 16,  // up, down
			1, -1, // left, right
		},
		// Queen
		{
			-16, 16, // up, down
			1, -1, // left, right
			-15, -17, // up-left, up-right
			17, 15  // down-left, down-right
		}
	};

	public static final int[][] POSITION_VALUES = {
		// PAWN
		{
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			50, 50, 50, 50, 50, 50,50, 50, 0, 0, 0, 0, 0, 0, 0, 0,
			10,10, 20, 30, 30, 20, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0,
			5, 5, 10, 25, 25, 10, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0,  0, 20, 20,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			5,-5,-10,  0, 0,-10, -5, 5, 0, 0, 0, 0, 0, 0, 0, 0,
			5, 10, 10,-20,-20, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		},
		// KNIGHT
		{
			-50,-40,-30,-30,-30,-30,-40,-50, 0, 0, 0, 0, 0, 0, 0, 0,
			-40,-20,  0,  0,  0,  0,-20,-40, 0, 0, 0, 0, 0, 0, 0, 0,
			-30,  0, 10, 15, 15, 10,  0,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-30,  5, 15, 20, 20, 15,  5,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-30,  0, 15, 20, 20, 15,  0,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-30,  5, 10, 15, 15, 10,  5,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-40,-20,  0,  5,  5,  0,-20,-40, 0, 0, 0, 0, 0, 0, 0, 0,
			-50,-40,-30,-30,-30,-30,-40,-50, 0, 0, 0, 0, 0, 0, 0, 0
		},
		// KING
		{
			-30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
			-20,-30,-30,-40,-40,-30,-30,-20, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,-20,-20,-20,-20,-20,-20,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			20, 20,  0,  0,  0,  0, 20, 20, 0, 0, 0, 0, 0, 0, 0, 0,
			20, 30, 10,  0,  0, 10, 30, 20, 0, 0, 0, 0, 0, 0, 0, 0
		},
		// BISHOP
		{
			-20,-10,-10,-10,-10,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  0,  0,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  0,  5, 10, 10,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  5,  5, 10, 10,  5,  5,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  0, 10, 10, 10, 10,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-10, 10, 10, 10, 10, 10, 10,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  5,  0,  0,  0,  0,  5,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-20,-10,-10,-10,-10,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0
		},
		// ROOK
		{
			0,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0,
			5, 10, 10, 10, 10, 10, 10,  5, 0, 0, 0, 0, 0, 0, 0, 0,
			-5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			0,  0,  0,  5,  5,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0
		},
		// QUEEN
		{
			-20,-10,-10, -5, -5,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  0,  0,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  0,  5,  5,  5,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-5,  0,  5,  5,  5,  5,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			0,  0,  5,  5,  5,  5,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  5,  5,  5,  5,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-10,  0,  5,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
			-20,-10,-10, -5, -5,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0
		}
	};


	private static final long NANOS_PER_SECOND = 1_000_000_000;
}
