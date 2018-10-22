import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import java.io.*;

public class Client {

	// public static int N = 10;
	// public static ArrayList RTT_list = new ArrayList();
	public static long RTT_array[];

	public static ArrayList<Thread> Thread_list = new ArrayList();

	public static void main(String[] args) {

		if (args.length < 3)

			return;

		long Sum_array[];
		ArrayList Total_latency_list = new ArrayList();// each cell contains the
														// total latency for
		// current loop (N) e.g.
		// Total_latency_array[0] contains the total
		// delay for he first 10 users,
		// Total_latency_array[1] contains the total
		// delay for he first 20 users etc
		String hostname = args[0];

		int port = Integer.parseInt(args[1]);

		int repetitions = Integer.parseInt(args[2]);

		long Total_latency = 0;

		try {

			PrintWriter writer_output_file = new PrintWriter("RTT_output.txt", "UTF-8");

			int N = 10;

			writer_output_file.println("USERS: " + N);

			RTT_array = new long[N];
			Sum_array = new long[N];// sum of RTT between repetitions

			for (int k = 0; k < repetitions; k++) {// repetitions

				System.out.println("Repetition "+ k);
				
				Thread_list.clear();

				for (int i = 0; i < N; i++) {
					Socket socket = new Socket(hostname, port);
					ClientThread client = new ClientThread(socket, i, N);
					client.start();
					Thread_list.add(client);
				}

				for (int i = 0; i < N; i++) {

					try {
						Thread_list.get(i).join();// waits the thread to
													// terminate
						// System.out.println("Thread " + i + ", AVERAGE
						// RTT: " + RTT_array[i]);
						Sum_array[i] += RTT_array[i];

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			long Average_array[] = new long[N];// save the average of RTT
												// for each user

			for (int i = 0; i < N; i++) {
				Average_array[i] = Sum_array[i] / repetitions;
				System.out.println("AVERAGE ARRAY " + i + ": " + Average_array[i]);
			}

			for (int j = 0; j < N; j++) {// gia na prosthetei to sum
				Total_latency += Average_array[j];
			}
			Total_latency_list.add(Total_latency);
			Total_latency = 0;

			writer_output_file.println("TOTAL LATENCY: " + Total_latency_list);

			// https://stackoverflow.com/questions/702415/how-to-know-if-other-threads-have-finished

			writer_output_file.close();

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());

		}

	}

}