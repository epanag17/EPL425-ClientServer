import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

	private Socket socket;
	private int user_id;

	public ClientThread(Socket socket, int user_id) {

		this.socket = socket;
		this.user_id = user_id;

	}

	public void run() {

		try {

			/*OutputStream output = socket.getOutputStream();

			PrintWriter writer = new PrintWriter(output, true);
			writer.println("hello");
			*/
			InputStream input = socket.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();

			PrintWriter writer = new PrintWriter(output, true);

			String IPaddress = socket.getLocalAddress().toString();

			writer.println("HELLO " + IPaddress + " " + socket.getPort() + " " + user_id);

			System.out.println("response to the client: " + reader.readLine());

			int payloadSize = Integer.parseInt(reader.readLine());
			System.out.println("payload size: "+payloadSize);
			
			byte[] payload_table = reader.readLine().getBytes();
			for(int i=0;i<payload_table.length;i++){
				System.out.print(payload_table[i]);
			}
			/*DataInputStream dIn = new DataInputStream(input);
			int payloadSize = dIn.readInt();
			System.out.println("Client " + user_id + ": Payload size: " + payloadSize);
			if (payloadSize > 0) {
				byte[] payload = new byte[payloadSize];
				dIn.readFully(payload, 0, payload.length); // read the
															// message
				System.out.println("Client " + user_id + ": Last character: " + (int) payload[payload.length - 1]);
			}
*/
			input.close();
			reader.close();
			output.close();
			writer.close();
			// socket.close();

			
			socket.close();
		} catch (IOException ex) {

			System.out.println("Client exception: " + ex.getMessage());

			ex.printStackTrace();

		}

		
	}

}
