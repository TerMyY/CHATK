import java.util.HashSet;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	// server port
    private static final int PORT = 9999;

	// list of client names
    private static final HashSet<String> clientNames = new HashSet<>();

	private static final HashSet<PrintWriter> printWriters = new HashSet<>();

	// server listen on a port and spawns client handler for each connection  
	public static void main(String[] args)
	{
		System.out.println("Launching the chat server...");
		ServerSocket serverSocket = null;
		
		try
		{
			serverSocket = new ServerSocket(PORT);
			while (true)
			{
				new ClientHandler(serverSocket.accept()).start();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try { serverSocket.close(); } catch (IOException ex) { ex.printStackTrace(); }
		}
	}

	// ClientHandler is a thread class that handle clients
	private static class ClientHandler extends Thread
	{
		private String clientName;
        	private Socket socket;
        	private BufferedReader in;
        	private PrintWriter out;

		public ClientHandler(Socket socket)
		{
			this.socket = socket;
		}

		public void run()
		{
			try
			{
				in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
				out = new PrintWriter( new PrintWriter( socket.getOutputStream(), true ) ); // autoflush: true
				while (true)
				{
					out.println("SUBMITNAME");
					clientName = in.readLine();
					if (clientName == null) return;
					synchronized (clientNames)
					{
						if (!clientNames.contains(clientName))
						{
							clientNames.add(clientName);
							break;
						}
					}
				}
				out.println("NAMEACCEPTED");
				printWriters.add(out);
				while (true)
				{
					String input = in.readLine();
					if (input == null) return;
					for (PrintWriter writer : printWriters)
					{
						writer.println("MESSAGE " + clientName + ": " + input);
					}
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (clientName != null) clientNames.remove(clientName);
				if (out != null) printWriters.remove(out);
				try { socket.close(); } catch (IOException ex) { ex.printStackTrace(); }
			}
		}
	}

}
