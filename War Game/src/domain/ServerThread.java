package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

//TODO: Cite the code taken from PS

public class ServerThread extends Thread {
	
	private ArrayList<Socket> clientSockets;
    protected ArrayList<BufferedReader> inputStreams;
    protected ArrayList<PrintWriter> outputStreams;
    private String fullMessageFromClient = new String();
    String actionCodeFromClient = new String();
    private int NUMBER_OF_PLAYERS;
	
	public ServerThread(ArrayList<Socket> clientSockets) {
		inputStreams = new ArrayList<BufferedReader>();
		outputStreams = new ArrayList<PrintWriter>();
		this.clientSockets = clientSockets;
		this.NUMBER_OF_PLAYERS = clientSockets.size();
		
	}
	
	
	public void communicateWithClients() throws IOException {
		
		BufferedReader inputStream;
		PrintWriter outputStream;
		String fullMessageFromClient;
		
		for(int i = 0; i < this.NUMBER_OF_PLAYERS; i++) {
			
			inputStream = inputStreams.get(i);
			outputStream = outputStreams.get(i);
		
			outputStream.println("Your Turn Client " + i);
			
			while(true) {
				
				fullMessageFromClient = inputStream.readLine();
				
				if (fullMessageFromClient != null) {
					System.out.println("New Message From Client: " + fullMessageFromClient);
					break;
				}		
			}

		}
		
		
		
		
	}

	
	public void run() {
		
        try
        {

        }
        catch (IOException e)
        {
            System.err.println("Server Thread. Run. IO error in server thread");
        }
        
        try
        {
        	fullMessageFromClient = bufferedReader.readLine();
        	

        	while(!fullMessageFromClient.equals("EXIT")) {
        		
        		// Decode the message in here!

        		System.out.println("New Message from Client ?: " + fullMessageFromClient);
        		
        		
        		
        	}
        } catch(IOException e) {
        	
        }

		
		
	}
}
