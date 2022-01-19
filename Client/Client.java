import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
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
	BorderLayout borderLayout = new BorderLayout();
	JPanel panel = new JPanel(borderLayout);
	JTextField textField = new JTextField();
	JTextArea chatArea = new JTextArea();
	Socket socket = null;
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	int width = gd.getDisplayMode().getWidth();
	int height = gd.getDisplayMode().getHeight();
	
	public Client()
	{
		textField.setEditable(false);
		chatArea.setEditable(false);
		textField.addActionListener(e -> { send(); });
		panel.add(new JScrollPane(chatArea), borderLayout.CENTER);
		panel.add(textField, borderLayout.SOUTH);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(width/3, height/3);
		frame.setLocationRelativeTo(null);
	}

	private void send()
	{
		if(textField.getText().trim().length() > 0)
		{
			out.println(textField.getText());
			textField.setText("");
		}
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
			else if (line.startsWith("NAMEACCEPTED")) textField.setEditable(true);
			else if (line.startsWith("MESSAGE")) { chatArea.append(line.substring(8) + "\n"); }
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length > 0) port = Integer.parseInt(args[0]);
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		} catch (Exception ex)
		{
			System.out.println("Port number is incorrect");
			return;
		}
		Client client = new Client();
		client.run();
	}
}
