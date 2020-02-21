package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
	public static final int DEFAULT_SERVER_PORT = 4444;
	private Socket socket;
	protected BufferedReader inputStream;
	protected PrintWriter outputStream;

	protected String serverIP;
	protected int serverPort;

	public Client(int serverPort, String serverIP) {

		this.serverPort = serverPort;
		this.serverIP = serverIP;
	}

	public void connect() {

		try {
			socket = new Socket(this.serverIP, this.serverPort);
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new PrintWriter(socket.getOutputStream());
			System.out.println("Successfully connected to " + this.serverIP + " on port " + this.serverPort);
		} catch (IOException e) {
			System.err.println("Error: no server has been found on " + this.serverIP + "/" + serverPort);
		}
	}

	public void communicateWithServer() throws IOException {
		String fullMessageFromServer;
		while (true) {

			fullMessageFromServer = inputStream.readLine();

			if (fullMessageFromServer != null) {

				System.out.println("New Message" + fullMessageFromServer);
				this.sendMessageToServer("I know.");

			}
		}
	}

	public void sendMessageToServer(String message) throws IOException {

		outputStream.println(message);
		outputStream.flush();

	}

	public void run() {

		try {

			communicateWithServer();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
