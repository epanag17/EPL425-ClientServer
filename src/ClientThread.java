import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {

	private Socket socket;
	private int user_id;
	public static final int REQUESTS = 300;
	private int N;

	public ClientThread(Socket socket, int user_id, int N) {

		this.socket = socket;
		this.user_id = user_id;
		this.N = N;

	}

	
	public byte[] payload_table = new byte[2000*1024];
	
	public void run() {

		try {

			long sum = 0;

			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);
			DataInputStream dIn = new DataInputStream(input);
			
			for (int i = 0; i < REQUESTS; i++) {
				
				String IPaddress = socket.getLocalAddress().toString();
				long startTime = System.nanoTime();
				writer.println("HELLO " + IPaddress + " " + socket.getPort() + " " + user_id + " fora " + i);
				//System.out.println("HELLO " + IPaddress + " " + socket.getPort() + " " + user_id + " fora " + i);

				String response = reader.readLine();
				//System.out.println("response to the client: " + response);

				String payload = reader.readLine();

				// calculate the RTT time
				long finalTime = System.nanoTime();
				long RTT = finalTime - startTime;

				sum += RTT;

			}

			long averageRTT = sum / 300; // average RTT for one user

			Client.RTT_array[this.user_id] = averageRTT;
			
			socket.close();

		} catch (IOException ex) {

			System.out.println("Client exception: " + ex.getMessage());

			ex.printStackTrace();

		}

	}

}
