import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.plaf.synth.SynthSpinnerUI;

public class ServerThread extends Thread {

	private Socket socket;
	public static final int REQUESTS = 300;

	public static final double INTERVAL = Math.pow(10, 6); // Interval ana miliseconds
	
	public ArrayList<Integer> throughput_list;
	public int counter = 0;// how many requests satisfies from the
	// server
	
	public int requests_served = 0;
	public double total_served_time = 0.0; 
	
	
	public int threadID;

	
	public ServerThread(Socket socket, int threadID) {

		this.socket = socket;

		this.throughput_list = new ArrayList<Integer>();
		this.threadID = threadID;
	}

	public static int CalculatePayloadSize(int minRange, int maxRange) {
		Random random = new Random();
		random.setSeed(System.nanoTime());

		int range = (maxRange - minRange) + 1;
		int size = (int) (random.nextDouble() * range) + minRange;
		
		//int size = 300;
		size = size * 1024;
		return size;
	}


	public static String CalculatePayloadValue(int payloadSize){
		
		String payload = "";
		StringBuilder payloadBuilder = new StringBuilder(payloadSize);
		for (int i = 0; i < payloadSize; i++) {
			if (i % 2 == 0)
				payloadBuilder.append('A');
			else
				payloadBuilder.append('D');
		}
		
		return payloadBuilder.toString();
	}
	
	public void run() {
		boolean flag = false;
		try {

			// Read data from the client
			
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			OutputStream output = socket.getOutputStream();
			DataOutputStream dOut = new DataOutputStream(output);
			PrintWriter writer = new PrintWriter(output, true);
			
			for (int i = 0; i < REQUESTS; i++) {

				String request = reader.readLine();

				long startTime = System.nanoTime();
				
				// System.out.println("REQUEST: " + request);
				String[] tokens = request.split(" ");

				int user_id = Integer.parseInt(tokens[3]);

				// reply to the client
				writer.println("WELCOME " + user_id);

				int payloadSize = CalculatePayloadSize(300, 2000);
				String payload = CalculatePayloadValue(payloadSize);

				writer.println(payload);

				long currentTime = System.nanoTime();
				requests_served++;
				total_served_time+= ((double)(currentTime - startTime))/Math.pow(10, 9);

			}
			
			socket.close();
			//writer_output_file.close();
		} catch (IOException ex) {

			System.out.println("Server exception: " + ex.getMessage());

			ex.printStackTrace();

		}

	}

}
