package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import handlers.ClientHandler;

/**
 * 
 * @author Grant Maloney | gpmpn2 | 11/26/18
 *
 */
public class Client {
	private static final String HOST = "localhost";
	private static final int PORT = 14188;
	
	private Socket socket;
	private BufferedReader input;
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public void setInput(BufferedReader input) {
		this.input = input;
	}
	
	public BufferedReader getInput() {
		return this.input;
	}
	
	public static void main(String args[]) {
		System.out.println("Client booting up...");
		Client client = new Client();
		try {
			client.createConnection();
		} catch (IOException io) {
			System.err.print("[Error] Failed to connect to server.");
			io.printStackTrace();
		}
	}

	private void createConnection() throws IOException {
		setSocket(new Socket(HOST, PORT));
		setInput(new BufferedReader(new InputStreamReader(getSocket().getInputStream())));
		
		System.out.println(getInput().readLine());
		
		ClientHandler connection = new ClientHandler(getSocket());
		connection.start();
		
		while(true) {
			try {
				String message = getInput().readLine();
				
				if (message != null) {
					System.out.println(message + "\n>");
				}
			} catch (IOException io) {
				System.err.println("[Error] Failed to read input.");
				io.printStackTrace();
			}
		}
	}
}
