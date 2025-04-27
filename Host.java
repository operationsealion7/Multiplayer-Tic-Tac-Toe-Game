import java.io.*;
import java.net.*;

public class Host {
	static String[] gameboard = {" ", " ", " ", " ", " ", " ", " ", " ", " "};
	static String condensed_gameboard;
	static Socket clientSocket1;
	static Socket clientSocket2;
	static BufferedReader in1, in2;
	static PrintWriter out1, out2;
	

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(12345);
		System.out.println("Waiting for 1st client to connect...");
		clientSocket1 = serverSocket.accept();
		System.out.println("1st client successfully connected");
		System.out.println("Waiting for 2nd client to connect...");
		clientSocket2 = serverSocket.accept();
		System.out.println("2nd client successfully connected");

		out1 = new PrintWriter(clientSocket1.getOutputStream(), true);
		out2 = new PrintWriter(clientSocket2.getOutputStream(), true);
		in1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
		in2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));

		String client_move;

	
		gameLoop: while(true) {
			System.out.println("Client 1's turn");
			send_information(1, "true");
			send_information(2, "false");
			client_move = request_information(1);

			if(client_move.equals("exit")) {
				end_game(1);
				break;
			}
			else {	
				send_information(2, "Game continues");
			}

			while(check_move(client_move) == false) {
				send_information(1, "false");
				client_move = request_information(1);
				if(client_move.equals("exit")) {
					end_game(1);
					break gameLoop;
				}
				else {	
					send_information(2, "Game continues");
				}
			}
			send_information(1, "true");
			update_board (1, Integer.parseInt(client_move) - 1);
			send_information(1, condensed_gameboard);
			send_information(2, condensed_gameboard);

			if(check_winner(1, Integer.parseInt(client_move)) == true) {
				send_information(1, "Winner");
				send_information(2, "Loser");
				end_game(0);
			}
			else {
				send_information(1, "Neither");
				send_information(2, "Neither");
			}
				
			System.out.println("Client 2's turn");
			send_information(1, "false");
			send_information(2, "true");
			client_move = request_information(2);

			if(client_move.equals("exit")) {
				end_game(2);
				break;
			}
			else {	
				send_information(1, "Game continues");
			}

			while(check_move(client_move) == false) {
				send_information(2, "false");
				client_move = request_information(2);
				if(client_move.equals("exit")) {
					end_game(2);
					break gameLoop;
				}
				else {	
					send_information(1, "Game continues");
				}
			}
			send_information(2, "true");
			update_board (2, Integer.parseInt(client_move) - 1);
			send_information(1, condensed_gameboard);
			send_information(2, condensed_gameboard);
			
			if(check_winner(2, Integer.parseInt(client_move)) == true) {
				send_information(1, "Loser");
				send_information(2, "Winner");
				end_game(0);
			}
			else {
				send_information(1, "Neither");
				send_information(2, "Neither");
			}
			
		}
	}

	public static void send_information(int client, String information) throws IOException {
		if(client == 1) {
			out1.println(information);
		}
		else {
			out2.println(information);
		}
	}

	public static String request_information(int client) throws IOException {
		String information;
		if(client == 1) {
			information = in1.readLine();
		}
		else {
			information = in2.readLine();
		}
		return information;
	}
	
	public static boolean check_move(String position) {
		int num_position;
		try {
			num_position = Integer.parseInt(position);
		}
		catch (NumberFormatException e) {
			return false;
		}
		if(num_position >= 1 && num_position <= 9) {
			if(gameboard[num_position - 1].equals(" ")) {
				System.out.println("Client move is allowed");
				return true;		
			}	
		}
		System.out.println("Client move is illegal");
		return false;
		
	}

	public static void update_board(int client, int position) {
		condensed_gameboard = "";

		if(client == 1) {
			gameboard[position] = "X";
		}
		else {
			gameboard[position] = "O";
		}
		
		for(int i = 0; i < 9; i++) {
			condensed_gameboard += gameboard[i];
		}
	}
	
	public static void end_game(int client) throws IOException {
		if(client == 1) {
			send_information(2, "Opponent has quit the game");
		}
		else if(client == 2) {
			send_information(1, "Opponent has quit the game");
		}
		in1.close();
		out1.close();	
		clientSocket1.close();
		in2.close();
		out2.close();
		clientSocket2.close();
		System.out.println("Game ended");
	}

	public static boolean check_winner(int client, int recent_move) {
		recent_move -= 1;
		
  		String designation = "";
    		if (client == 1) {
        		designation = "X";
    		} 
		else {
        		designation = "O";
    		}

    		int count = 0;
    
    		for (int i = 0; i <= 6; i += 3) { // start of each row
        		count = 0;
        		for (int k = i; k <= i + 2; k++) { // across the row
            		if (gameboard[k].equals(designation)) {
                		count += 1;
            			}
        		}
        		if (count == 3) {
            			return true;
        		}
   		 }
    
    		for (int i = 0; i <= 2; i++) { // start of each column
        		count = 0;
        		for (int k = i; k <= i + 6; k += 3) { // down the column
            			if (gameboard[k].equals(designation)) {
                			count += 1;
            			}
        		}
        		if (count == 3) {
            			return true;
        		}
    		}
    
    		// Check main diagonal (0,4,8)
    		count = 0;
    		for (int i = 0; i <= 8; i += 4) {
        		if (gameboard[i].equals(designation)) {
            			count += 1;
        		}
   		}
    		if (count == 3) {
        		return true;
   		}
    
    		// Check anti-diagonal (2,4,6)
    		count = 0;
    		for (int i = 2; i <= 6; i += 2) {
        		if (gameboard[i].equals(designation)) {
            			count += 1;
        		}
    		}
    		if (count == 3) {
        		return true;
    		}

    		return false;
	}

		
}





		
		
		