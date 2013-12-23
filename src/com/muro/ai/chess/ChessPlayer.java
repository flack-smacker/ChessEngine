package com.muro.ai.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

import com.muro.ai.chess.ChessGame.ChessMove;

public class ChessPlayer {

	public static void main(String[] args) throws IOException {

		if (args.length < 4) {
			System.out.println("Please enter a color, game ID, team number, and team secert in the following format.");
			System.out.println("    ChessPlayer <w/b> <gameId> <teamNumber> <teamSecret>");
			System.out.println("For example: ChessPlayer w 99 15 abc123ae");
			System.exit(-1);
		}

		ChessPlayer theGame = new ChessPlayer(args[0], args[1], args[2], args[3]);

		/** The Game Loop **/
		while (!theGame.isGameOver) {

			// Sync our local game state with the server.
			theGame.updateGameState();

			// Display the game state on standard output.
			System.out.println(theGame);
			System.out.println();
			System.out.println(theGame.gameBoard + "\n");

			// Check if it is our turn.
			if (theGame.isPlayersMove) {
				theGame.takeTurnRobot();
			} else {
				// Wait for our opponent to make a move...
				ChessPlayer.waitPatiently(5 * MILLIS_PER_SEC);
			}
		}

		// Determine who the winner was and output the result.
		JSONObject jsonObj = new JSONObject(theGame.pollServer());
		System.out.println("Game Over. Player " + jsonObj.getInt("winner") + " won.");

		// Clean up system resources
		in.close();
	}

	/**
	 * Constructs a new chess game initialized to the starting state.
	 * 
	 * @param teamColor a String representing the team number.
	 * @param gameId a string identifying the game
	 * @param teamNumber a String specifying the player's team number
	 * @param teamSecret a string used to communicate with the game server
	 */
	public ChessPlayer(String teamColor, String gameId, String teamNumber, String teamSecret) {

		// Build the URL's required for polling the server and passing move strings.
		pollUrl = SERVER_URL + "poll/" + gameId + "/" + teamNumber + "/" + teamSecret + "/";
		moveUrl = SERVER_URL + "move/" + gameId + "/" + teamNumber + "/" + teamSecret + "/";

		// Store the team number.
		if (teamColor.equalsIgnoreCase("b")) {
			this.playerColor = ChessGame.BLACK_PLAYER;
		} else {
			this.playerColor = ChessGame.WHITE_PLAYER;
		}

		// Create a new game board initialized to the default state.
		gameBoard = new ChessGame();

	}

	/**
	 * Allows us to play chess from the command line.
	 */
	public void takeTurnHuman() {
		boolean valid = false;
		String userInput = null;

		// Loop until the user provides a valid move string.
		while (!valid) {

			// Prompt the user for a move.
			System.out.print("Your move: ");
			userInput = in.nextLine().trim();

			// Send the move to the server.
			if (!sendMove(userInput)) {
				System.out.println("Invalid move. Try again");
			} else {
				gameBoard.performMove(userInput);
				valid = true;
			}
		}
	}


	/**
	 * Let's the computer play.
	 */
	public void takeTurnRobot() {

		// Calculate how long we should search for.
		int searchTime;

		// How much time do we have in seconds?
		int timeLeft = (int)timeRemaining;

		if (timeLeft < 120 && moveCount < 20 ) {
			// Pick up the pace...
			searchTime = 5;
		} else if (moveCount < 15){ // think about it...
			searchTime = MINIMUM_TIME_PER_MOVE + 30;
		} else {
			searchTime = MINIMUM_TIME_PER_MOVE + 45;
		}

		ChessMove nextBest = ChessSearch.findNextMove(gameBoard, playerColor, searchTime, 4);

		// Send the move to the server.
		boolean valid = sendMove(nextBest.toString());

		while(!valid) {

			System.out.println("Invalid move selected. Try again.");

			if (playerColor == ChessGame.WHITE_PLAYER) {
				valid = sendMove(ChessSearch.randomWhiteMove(gameBoard).toString());
			} else {
				valid = sendMove(ChessSearch.randomBlackMove(gameBoard).toString());
			}
		}

		gameBoard.performMove(nextBest.toString());
		System.out.println("Move selected: " + nextBest.toString());
		System.out.println();
	}

	/**
	 * Queries the server for the game state and updates the local game state appropriately. 
	 */
	public void updateGameState() {

		// Query the server for the game state.
		String response = this.pollServer();

		// Use a JSONObject to parse the JSON string.
		JSONObject jsonObj = new JSONObject(response);
		isPlayersMove = jsonObj.getBoolean("ready");
		timeRemaining = jsonObj.getDouble("secondsleft");
		moveCount = jsonObj.getInt("lastmovenumber");

		// If it is currently our turn AND this is NOT the first turn of the game.
		if (isPlayersMove && moveCount > 0) {
			lastMove = jsonObj.getString("lastmove");
			// Update the game board with our opponent's last move.
			gameBoard.performMove(jsonObj.getString("lastmove"));
		} else { // Its not our turn.
			// Check if the game is over.
			if (jsonObj.has("gameover") && jsonObj.getBoolean("gameover")) {
				isGameOver = true;
			}
		}
	}

	/**
	 * Sends a move to the server. Returns a JSON string containing the result of the move.
	 * 
	 * @param moveString a String representing a chess move in a modified form of algebraic chess notation.
	 * 
	 * @return the response string received from the server
	 */
	public boolean sendMove(String moveString) {

		// Send the move string to the server.
		String response = sendRequest(moveUrl + moveString + "/");

		// Use a JSONObject to parse the response.
		JSONObject jsonObj = new JSONObject(response);

		boolean isValid = false;

		if (jsonObj.has("result")) {
			isValid = jsonObj.getBoolean(("result"));
		}

		return isValid;
	}

	/**
	 * 
	 */
	public String toString() {
		return "time remaining: " + timeRemaining + " move count: " + moveCount + " opponents last move: " + lastMove;
	}

	/**
	 * Polls the server for the game state. Returns a JSON string containing the game state.
	 * 
	 * @return the response string received from the server
	 */
	private String pollServer() {
		return sendRequest(pollUrl);
	}

	/**
	 * Makes a HTTP GET request to the specified URL. 
	 * 
	 * @param urlString a string representing a valid URL.
	 * 
	 * @return a JSON string containing the server's response
	 */
	private String sendRequest(String urlString) {
		URL serverConn;
		String response = null;
		BufferedReader fromServer = null;
		boolean connected = false;
		int attempts = 5;

		while (!connected && attempts > 0) {
			try {
				// Construct the URL.
				serverConn = new URL(urlString);
				// Open the connection and send the HTTP request.
				fromServer = new BufferedReader(new InputStreamReader(serverConn.openStream()));
				// Read the response from the server.
				response = fromServer.readLine();
				// Indicate success
				connected = true;
				// Cleanup resources
				fromServer.close();
			} catch (MalformedURLException e) {
				System.err.println("Could not connect to the specified URL. Please verify the gameId, team number, and team secret.");
			} catch (IOException e) {
				System.err.println("Connection attempt failed.");
				System.err.println("Reason: " + e.getMessage());
				System.err.println("Waiting five seconds and trying again.");

				attempts--;

				// Give the server some time to catch up...
				try {
					Thread.sleep(5 * MILLIS_PER_SEC);
				} catch (InterruptedException e1) {
					System.err.println("Interrupted while sleeping. C'mon bro.");
					System.err.println("Reason: " + e.getMessage());
				}
			}
		}

		return response;
	}

	/**
	 * A utility method that causes the current thread to sleep for the specified duration.
	 * This is used while we our waiting for our opponent to make a move.
	 * 
	 * @param duration an integer representing the wait time in milliseconds
	 */
	private static void waitPatiently(int duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			System.out.println("An error occurred while we were waiting patiently for our turn.");
			e.printStackTrace();
		}
	}

	/**
	 * Used to track the pieces and their locations.
	 */
	private ChessGame gameBoard = null;

	/**
	 * Indicates whether it is the player's turn (true) or the opponents (false).
	 */
	private boolean isPlayersMove = false;

	/**
	 * Used to send a move to the server.
	 */
	private String moveUrl = null;

	/**
	 * Used to poll the server during the opponent's turn.
	 */
	private String pollUrl = null;
	
	/**
	 * A constant representing the team number.
	 */
	private int playerColor = -1;
	
	
	/**
	 * The total amount of time available to the player for deciding upon a move.
	 */
	private double timeRemaining = 0;

	/**
	 * The total number of moves made by both players.
	 */
	private int moveCount = 0;

	/**
	 * The last move made by the opponent.
	 */
	private String lastMove = null;
	
	/**
	 * A flag indicating whether the game is over.
	 */
	private boolean isGameOver = false;

	/**
	 * The URL of the game server.
	 */
	private static String SERVER_URL = "http://www.bencarle.com/chess/";

	/**
	 * To convert from milliseconds to seconds.
	 */
	private static final int MILLIS_PER_SEC = 1000;

	/**
	 * Represents a minimum number of seconds to take for each move.
	 */
	private static final int MINIMUM_TIME_PER_MOVE = 30;
	
	/**
	 * Used to get input from the user.
	 */
	public static final Scanner in = new Scanner(System.in);
}
