import java.io.*;
import java.net.*;

public class Client {
	static String[] gameboard = {" ", " ", " ", " ", " ", " ", " ", " ", " "};
	static Socket socket;
	static BufferedReader in;
	static PrintWriter out;
	static BufferedReader keyboard;
	
	public static void main(String[] args) throws IOException {
		socket = new Socket("localhost", 12345);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		keyboard = new BufferedReader(new InputStreamReader(System.in));

		String winner_status;

		System.out.println("Welcome to Tic-Tac-Toe!");
		System.out.println("To make a move, enter an integer between 1 and 9, inclusive:");
		System.out.println(" _ _ _ ");
		System.out.println("|" + "1" +"|" + "2" + "|" + "3" + "|");
		System.out.println(" _ _ _ ");
		System.out.println("|" + "4" +"|" + "5" + "|" + "6" + "|");
		System.out.println(" _ _ _ ");
		System.out.println("|" + "7" +"|" + "8" + "|" + "9" + "|");
		System.out.println(" _ _ _ ");
		System.out.println("You can exit the game at any time by typing \"exit\" during your turn.");
		System.out.println("The game will begin once both clients are connected to the server.");
		
		gameLoop: while(true) {
			if(request_turn() == true) {
				String my_turn = make_turn();
				send_information(my_turn);

				if(my_turn.equals("exit")) {
					System.out.println("You have exited the game");	
					quit_game();
					break;
				}

				Boolean is_turn_valid = Boolean.parseBoolean(request_information());
				while(is_turn_valid == false) {
					System.out.println("Move invalid");
					my_turn = make_turn();
					send_information(my_turn);

					if(my_turn.equals("exit")) {
						System.out.println("You have exited the game");	
						quit_game();
						break gameLoop;
					}

					is_turn_valid = Boolean.parseBoolean(request_information());
				}
				String host_board = request_information();
				update_board(host_board);
				print_board();

				winner_status = request_information();
				if(winner_status.equals("Winner")) {
					System.out.println("You won the game!");
					quit_game();
					break;
				}
				else if(winner_status.equals("Loser")) {
					System.out.println("You have lost the game.");
					quit_game();
					break;
				}

			}
			else {
				String game_info = request_information();
				while(game_info.equals("Game continues")) {
					game_info = request_information();
				}

				if(game_info.equals("Opponent has quit the game")) {
					System.out.println("Opponent has quit the game");
					quit_game();
					break;
				}
				String host_board = game_info;
				update_board(host_board);
				print_board();
				
				winner_status = request_information();
				if(winner_status.equals("Winner")) {
					System.out.println("You won the game!");
					quit_game();
					break;
				}
				else if(winner_status.equals("Loser")) {
					System.out.println("You have lost the game.");
					quit_game();
					break;
				}
			}
		}
	}
	
	public static boolean request_turn() throws IOException {
		String is_turn;
		is_turn = in.readLine();
		
		if(Boolean.parseBoolean(is_turn) == true) {
			return true;
		}
		System.out.println("Waiting for opponents move...");
		return false;
	}

	public static String make_turn() throws IOException {
		String my_move;
		System.out.println("Your turn! Make your move:");
		my_move = keyboard.readLine();
		return my_move;
	}

	public static void quit_game() throws IOException {
		in.close();
		out.close();
		keyboard.close();
		socket.close();
		System.out.println("Connection closed.");
	}

	public static void send_information(String information) throws IOException {
		out.println(information);
	}

	public static String request_information() throws IOException {
		String information = in.readLine();
		return information;
	}

	public static void update_board(String host_board) {
		for (int i = 0; i < 9; i++) {
			gameboard[i] = String.valueOf(host_board.charAt(i));
		}
	}

	public static void print_board() {
		System.out.println(" _ _ _ ");
		System.out.println("|" + gameboard[0] +"|" + gameboard[1] + "|" + gameboard[2] + "|");
		System.out.println(" _ _ _ ");
		System.out.println("|" + gameboard[3] +"|" + gameboard[4] + "|" + gameboard[5] + "|");
		System.out.println(" _ _ _ ");
		System.out.println("|" + gameboard[6] +"|" + gameboard[7] + "|" + gameboard[8] + "|");
		System.out.println(" _ _ _ ");
	}
	
}