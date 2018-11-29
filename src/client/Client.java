package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

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
	
	/*
	 * Getters and Setters
	 */
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
	
	/**
	 * Main method that starts up the Client when the jar is run
	 * @param args
	 */
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

	/**
	 * Reaches and creates a connection to the main Server listener.
	 * @throws IOException
	 */
	private void createConnection() throws IOException {
		try {
			setSocket(new Socket(HOST, PORT)); //Making a connection to the server
		} catch (ConnectException ce) {
			System.out.println("[Server] Chat Room is currently full!");
			return;
		}
		
		setInput(new BufferedReader(new InputStreamReader(getSocket().getInputStream())));

		System.out.println("[Server] If no response after 3 seconds, the Chat Room is full!");
		System.out.println(getInput().readLine());
		
		ClientHandler connection = new ClientHandler(getSocket());
		connection.start();
		
		//Printing outputs to the client as they come in, server messages, other member messages, error message etc.
		while(true) {
			try {
				String message = getInput().readLine();
				
				if (message != null) {
					System.out.print(message + "\n> ");
				}
			} catch (IOException io) {
				System.err.println("[Error] Failed to read input.");
				io.printStackTrace();
			}
		}
	}
}
