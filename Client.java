import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;

public class Client
{
	private static int port = 9009;
	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("CHATK");
	JTextField inputField = new JTextField();
	JTextArea messageArea = new JTextArea();
	Socket socket = null;
	
	public Client()
	{
		inputField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(inputField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(500, 900);
		
		ActionListener enterListener = e ->
		{
			out.println(inputField.getText());
			inputField.setText("");
		};
		inputField.addActionListener(enterListener);
	}

	private String getServerAddress()
	{
        return JOptionPane.showInputDialog
		(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to CHATK",
            JOptionPane.QUESTION_MESSAGE
		);
    }


    private String getName()
	{
        return JOptionPane.showInputDialog
		(
            frame,
            "Enter your name:",
            "Welcome to CHATK",
            JOptionPane.QUESTION_MESSAGE
		);
    }

	private void run()
	{
		String serverAddress = getServerAddress();
		try
		{
			socket = new Socket(serverAddress, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true); // autoflush: true
		} catch (Exception ex)
		{
			System.out.println("Could not connect to the server");
		}

		while (socket.isConnected())
		{
			String line = "";
			try { line = in.readLine(); } catch (IOException e) { e.printStackTrace(); }
			if (line.startsWith("SUBMITNAME")) out.println(getName());
			else if (line.startsWith("NAMEACCEPTED")) inputField.setEditable(true);
			else if (line.startsWith("MESSAGE")) { messageArea.append(line.substring(8) + "\n"); }
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length > 0) port = Integer.parseInt(args[0]);
		} catch (Exception ex)
		{
			System.out.println("Port number is incorrect");
			return;
		}
		Client client = new Client();
		client.run();
	}
}
