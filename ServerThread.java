import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class ServerThread extends Thread {

	private Socket socket;
	public static final int REQUESTS = 300;
	public static int counter = 0;// how many requests satisfies from the
									// server
	public static final double INTERVAL = Math.pow(10, 9);

	public ServerThread(Socket socket) {

		this.socket = socket;

	}

	public static int CalculatePayloadSize(int minRange, int maxRange) {
		Random random = new Random();
		random.setSeed(System.nanoTime());

		int range = (maxRange - minRange) + 1;
		int size = (int) (random.nextDouble() * range) + minRange;
		size = size * 1024;
		return size;
	}

	public static byte[] CalculatePayloadValue(int payloadSize) {
		byte[] payloadValue = new byte[payloadSize];
		// arxikopoisi payloadValue me 0 stis zyges theseis kai 1 stis mones
		// theseis
		for (int i = 0; i < payloadValue.length; i++) {
			if (i % 2 == 0)
				payloadValue[i] = 'A';
			else
				payloadValue[i] = 'D';
		}
		return payloadValue;
	}

	public void run() {
		boolean flag = false;
		try {
			PrintWriter writer_output_file = new PrintWriter("througput_output.txt", "UTF-8");

			// Read data from the client
			long startTime = System.nanoTime();

			for (int i = 0; i < REQUESTS; i++) {
				InputStream input = socket.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				OutputStream output = socket.getOutputStream();

				PrintWriter writer = new PrintWriter(output, true);

				String request = reader.readLine();

				// System.out.println("REQUEST: " + request);
				String[] tokens = request.split(" ");

				int user_id = Integer.parseInt(tokens[3]);

				// reply to the client

				writer.println("WELCOME " + user_id);

				int payloadSize = CalculatePayloadSize(300, 2000);
				byte[] payload = CalculatePayloadValue(payloadSize);

				DataOutputStream dOut = new DataOutputStream(output);

				writer.println(payloadSize);

				writer.println(payload);

				/*long endTime = System.nanoTime();
				if ((endTime - startTime) <= INTERVAL) {
					counter++;
				} else {
					flag = true;
					// System.out.println("the amount of requests that a server
					// satisfied in " + INTERVAL
					// + " nanoseconds are " + counter);
					startTime = System.nanoTime();
					counter = 0;
				}*/

			}
			long endTime = System.nanoTime();
			long time_satisfied_req_user = endTime-startTime;
			

			// System.out.println("Payload size: "+payloadSize);

			/*
			 * System.out.println("Send to user " + user_id + ", Payload size: "
			 * + payload.length); dOut.writeInt(payloadSize);
			 * dOut.write(payload);
			 */

			/*if (flag == false) {
				// System.out.println("the amount of requests that a server
				// satisfied in " + INTERVAL
				// + " nanoseconds are " + counter);
			}*/

			socket.close();
			writer_output_file.close();
		} catch (IOException ex) {

			System.out.println("Server exception: " + ex.getMessage());

			ex.printStackTrace();

		}

	}

}