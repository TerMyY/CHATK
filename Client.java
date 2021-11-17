import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;

public class Client
{

	BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("CHATK");
    JTextField inputField = new JTextField();
    JTextArea messageArea = new JTextArea();
	
	public Client()
	{
		inputField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(inputField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(500, 900);
		
		ActionListener enterListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				out.println(inputField.getText());
				inputField.setText("");
			}
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
			Socket socket = new Socket(serverAddress, 9999);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true); // autoflush: true
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		while (true)
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
		Client client = new Client();
		client.run();
	}
}
