package handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * 
 * @author Grant Maloney | gpmpn2 | 11/26/18
 *
 */
public class ClientHandler extends Thread {
	private Socket socket;
	private PrintWriter output;
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public void setOutput(PrintWriter output) {
		this.output = output;
	}
	
	public PrintWriter getOutput() {
		return this.output;
	}
	
	public ClientHandler(Socket socket) {
		setSocket(socket);
		try {
			setOutput(new PrintWriter(getSocket().getOutputStream(), true));
		} catch (IOException io) {
			System.err.println("[Error] Unable to establish output connection.");
			io.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			System.out.println(">");
				
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			getOutput().println(input);
			//scanner.close();
				
			if (input.equals("logout") || input.startsWith("logout")) {
				System.exit(0);
			}
		}
	}
}
