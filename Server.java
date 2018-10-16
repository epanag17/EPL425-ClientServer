import java.io.*;
import java.net.*;

class ServerThread extends Thread {
	private Socket socket;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			// Read data from the client
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);

			String request = reader.readLine();

			String[] tokens = request.split(" ");
			int user_id = Integer.parseInt(tokens[3]);
			// reply to the client
			writer.println("WELCOME " + user_id);

			socket.close();
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}

public class Server {

	public static void main(String[] args) {
		if (args.length < 1)
			return;

		int port = Integer.parseInt(args[0]);

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("New client connected");

				new ServerThread(socket).start();
			}

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}

	}

}
