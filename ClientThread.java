import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {

	private Socket socket;
	private int user_id;
	public static final int REQUESTS = 300;
	public static ArrayList RTT_list;
	private int N;

	public ClientThread(Socket socket, int user_id, ArrayList RTT_list, int N) {

		this.socket = socket;
		this.user_id = user_id;
		this.RTT_list = RTT_list;
		this.N = N;

	}

	public void run() {

		PrintWriter writer_output_file;
		try {
			writer_output_file = new PrintWriter("RTT_output.txt", "UTF-8");

			try {

				long sum = 0;

				for (int i = 0; i < REQUESTS; i++) {
					InputStream input = socket.getInputStream();

					BufferedReader reader = new BufferedReader(new InputStreamReader(input));

					OutputStream output = socket.getOutputStream();

					PrintWriter writer = new PrintWriter(output, true);

					String IPaddress = socket.getLocalAddress().toString();
					long startTime = System.nanoTime();
					writer.println("HELLO " + IPaddress + " " + socket.getPort() + " " + user_id + " fora " + i);
					//System.out.println("HELLO " + IPaddress + " " + socket.getPort() + " " + user_id + " fora " + i);

					String response = reader.readLine();
					//System.out.println("response to the client: " + response);

					int payloadSize = Integer.parseInt(reader.readLine());
					//System.out.println("payload size: " + payloadSize);

					byte[] payload_table = reader.readLine().getBytes();
					for (int j = 0; j < payload_table.length; j++) {
						//System.out.print(payload_table[j]);
					}
					//System.out.println();

					// calculate the RTT time
					long finalTime = System.nanoTime();
					long RTT = finalTime - startTime;

					sum += RTT;

				}

				long averageRTT = sum / 300; // average RTT for one user
				RTT_list.add(averageRTT);// contains RTT for all users

				Client.RTT_array[this.user_id] = averageRTT;
				
				socket.close();

			} catch (IOException ex) {

				System.out.println("Client exception: " + ex.getMessage());

				ex.printStackTrace();

			}

			long sumRTT = 0;
			if (RTT_list.size() == N) {// if all the RTT's for each user is
										// written to the list
				for (int i = 0; i < N; i++) {
					sumRTT = sumRTT + (long) RTT_list.get(i);// the RTT for all
																// users
				}
				writer_output_file.println("NUM OF USERS: " + N + " SUM RTT: " + sumRTT);
				writer_output_file.close();
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
