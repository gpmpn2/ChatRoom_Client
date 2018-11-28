package server;

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
public class ServerConnection implements Runnable {
	private Socket socket;
	private String username;
	private String password;
	private String loginCommand;
	private final LinkedList<String> messagesToSend;
	private boolean hasMessageToLoad;
	
	public void setUsername(String userName) {
		this.username = userName;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public LinkedList<String> getMessagesToSend() {
		return this.messagesToSend;
	}
	
	public void setLoginCommand(String loginCommand) {
		this.loginCommand = loginCommand;
	}
	
	public String getLoginCommand() {
		return this.loginCommand;
	}
	
	public ServerConnection(Socket socket, String userName, String password, String loginCommand) {
		setSocket(socket);
		setUsername(userName);
		setPassword(password);
		setLoginCommand(loginCommand);
		messagesToSend = new LinkedList<>();
		hasMessageToLoad = false;
	}
	
	public void pushMessage(String input) {
		String message = "";
		String command = "";
		
		String values[] = input.split(" ");
		if (values.length < 2) {
			System.out.println("[Server] Denied. Invalid input.");
			return;
		}
		
		command = values[0];
		message = message.concat(command + " " + getUsername());
		
		for(int i = 1;i < values.length; i++) {
			message = message.concat(" " + values[i]);
		}
		
		if (!checkCommand(command)) {
			System.out.println("[Server] Denied. Invalid input.");
			return;
		}
		
		synchronized (getMessagesToSend()) {
			hasMessageToLoad = true;
			getMessagesToSend().push(message);
		}
	}
	
	private static boolean checkCommand(String command) {
		switch (command) {
		case "send":
		case "logout":
			return true;
			default:
				return false;
		}
	}
	
	@Override
	public void run() {
		System.out.println("Successfully logged in as " + getUsername()
								+ "\nLocal Port: " + getSocket().getLocalPort()
								+ "\nServer Address: " + getSocket().getRemoteSocketAddress()
								+ "\nServer Port: " + getSocket().getPort());
		
		Scanner scanner = null;
		
		try {
			PrintWriter outputLocation = new PrintWriter(getSocket().getOutputStream(), false);
			InputStream inputLocation = getSocket().getInputStream();
			scanner = new Scanner(inputLocation);
			
			outputLocation.println(getLoginCommand() + " " + getUsername() + " " + getPassword());
			outputLocation.flush();
			
			while(!getSocket().isClosed()) {
				if (inputLocation.available() > 0) {
					if (scanner.hasNextLine()) {
						System.out.println(scanner.nextLine());
					}
				}
				if (hasMessageToLoad) {
					String messageToSend = "";
					synchronized (getMessagesToSend()) {
						messageToSend = getMessagesToSend().pop();
						hasMessageToLoad = !getMessagesToSend().isEmpty();
					}
					outputLocation.println(messageToSend);
					outputLocation.flush();
				}
			}
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}
