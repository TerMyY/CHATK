import java.util.HashSet;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Server
{
	// server port
	private static int port = 9009;

	// list of client names
	private static final List<String> clientNames = new ArrayList<>();
	// list of print writers
	private static final List<PrintWriter> printWriters = new ArrayList<>();

	// server listen on a port and spawns client handler for each connection  
	public static void main(String[] args)
	{
		System.out.print("Launching the chat server...");
		ServerSocket serverSocket = null;
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> { System.out.print("Shutting down...(OK)"); }));

		try
		{
			if (args.length > 0) port = Integer.parseInt(args[0]);
			serverSocket = new ServerSocket(port);
			System.out.println("(OK)");
			while (!serverSocket.isClosed())
			{
				new ClientHandler(serverSocket.accept()).start();
			}
		} catch (NumberFormatException nfEx)
		{
			System.out.println("(Port number is incorrect)");
			return;
		} catch (Exception ex)
		{
			System.out.println("(Server failed to open)");
		}
		finally
		{
			if (serverSocket != null)
				try { serverSocket.close(); } catch (Exception ex) { ex.printStackTrace(); }
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
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");;
			String currentTime;
			try
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true); // autoflush: true
				while (socket.isConnected())
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
				boolean isNew = true;
				while (socket.isConnected())
				{		
					if (isNew)
					{
						currentTime = " -" + timeFormat.format(new Date()) + "- ";
						String message = " -Client Connected (" + clientName + ")- ";
						System.out.println(message);
						for (PrintWriter writer : printWriters)
						{
							writer.println("MESSAGE " + message + currentTime);
							isNew = false;
						}
					}
					
					String input = in.readLine();
					if (input == null) return;
					for (PrintWriter writer : printWriters)
					{
						currentTime = " -" + timeFormat.format(new Date()) + "- ";
						writer.println("MESSAGE " + clientName + ": " + input + currentTime);
					}
				}
			} catch (Exception ex)
			{
				String message = " -Client Disconnected (" + clientName + ")- ";
				System.out.println(message);
				for (PrintWriter writer : printWriters)
				{
					currentTime = " -" + timeFormat.format(new Date()) + "- ";
					writer.println("MESSAGE " + message + currentTime);
				}
			}
			finally
			{
				if (clientName != null) clientNames.remove(clientName);
				if (out != null) printWriters.remove(out);
				try { socket.close(); } catch (Exception ex) { ex.printStackTrace(); }
			}
		}
	}
}
