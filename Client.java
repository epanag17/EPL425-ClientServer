import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {

	public static final int N = 10;

	public static void main(String[] args) {

		if (args.length < 2)
			return;

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);

		for (int i = 0; i < 10; i++) {

			try (Socket socket = new Socket(hostname, port)) {

				OutputStream output;
				try {
					InputStream input = socket.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));

					output = socket.getOutputStream();
					// SEND REQUEST TO THE SERVER
					PrintWriter writer = new PrintWriter(output, true);
					String IPaddress = socket.getLocalAddress().toString();

					writer.println("HELLO " + IPaddress + " " + port + " " + i);
					System.out.println("response to the client: " + reader.readLine());

				} catch (IOException e) {
					e.printStackTrace();
				}

				socket.close();

			} catch (UnknownHostException ex) {

				System.out.println("Server not found: " + ex.getMessage());

			} catch (IOException ex) {

				System.out.println("I/O error: " + ex.getMessage());
			}

		}
	}

}
