package com.muro.ai.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class ChessGame {

	public static void main(String[] args) throws IOException {
		
		if (args.length < 3) {
			System.out.println("Invalid argument list. Please enter a game ID, team number, and team secert.");
			System.out.println("For example:\n  ChessGame <gameId> <teamNumber> <teamSecret>");
			System.exit(-1);
		}
		
		ChessGame theGame = new ChessGame(args[0], args[1], args[2]);
		Scanner in = new Scanner(System.in);
		String userInput = null;
		String result = null;
		boolean valid = false;
		
		/** The Game Loop **/
		while (!theGame.isGameOver) {
			// Sync our local game state with the server.
			theGame.updateGameState();
			// Display the game state on standard output.
			System.out.println(theGame);
			
			// Check if it is our turn.
			if (theGame.isPlayersMove) {
				valid = false;
				// Loop until the user provides a valid move string.
				while (!valid) {
					System.out.print("Your move: ");
					userInput = in.nextLine().trim();
					result = theGame.sendMove(userInput);
					
					if (!result.equalsIgnoreCase("valid")) {
						System.out.println("Invalid move. " + result);
					} else {
						valid = true;
					}
				}
			} else { 
				// Sleep for five seconds.
				ChessGame.waitPatiently(5 * MILLIS_PER_SEC);
			}
		}
		
		JSONObject jsonObj = new JSONObject(theGame.pollServer());
		int winner = jsonObj.getInt("winner");
		System.out.println("Game Over. Player " + winner + " won.");
		in.close();
	}

	/**
	 * Constructs a new chess game initialized to the starting state.
	 * 
	 * @param gameId a string identifying the game
	 * @param teamNumber a String specifying the player's team number
	 * @param teamSecret a string used to communicate with the game server
	 */
	public ChessGame(String gameId, String teamNumber, String teamSecret) {
		
		// Build the URL's required for polling the server and passing move strings.
		pollUrl = SERVER_URL + "poll/" + gameId + "/" + teamNumber + "/" + teamSecret + "/";
		moveUrl = SERVER_URL + "move/" + gameId + "/" + teamNumber + "/" + teamSecret + "/";
					
		// Create a new game board initialized to the default state.
		gameBoard = new ChessBoard();
		
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
	public String sendMove(String moveString) {
		
		String response = sendRequest(moveUrl + moveString + "/");
		
		// Use a JSONObject to parse the JSON string.
		JSONObject jsonObj = new JSONObject(response);
		boolean isValid = jsonObj.getBoolean("result");
		
		if (isValid) {
			return "valid";
		} else {
			return "invalid: " + jsonObj.getString("message");
		}
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
		
		try {
			// Construct the URL.
			serverConn = new URL(urlString);
			// Open the connection and send the HTTP request.
			fromServer = new BufferedReader(new InputStreamReader(serverConn.openStream()));
			// Read the response from the server.
			response = fromServer.readLine();
			// Cleanup resources
			fromServer.close();
		} catch (MalformedURLException e) {
			System.err.println("Could not connect to the specified URL. Please verify the gameId, team number, and team secret.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	private ChessBoard gameBoard = null;
	
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
	 * The default wait time.
	 */
	private static final int MILLIS_PER_SEC = 1000;
}
