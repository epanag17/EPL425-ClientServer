import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import java.io.*;

public class Client {

	//public static int N = 10;
	public static ArrayList RTT_list = new ArrayList();
	public static long RTT_array[];
	
	
	public static ArrayList<Thread> Thread_list= new ArrayList();
	
	public static void main(String[] args) {

		if (args.length < 3)

			return;

		long Sum_array[];
		String hostname = args[0];

		int port = Integer.parseInt(args[1]);

		int repetitions = Integer.parseInt(args[2]);

		try {

			for (int N = 10; N < 100; N += 10) {//users
				
				RTT_array = new long[N];
				Sum_array = new long[N];
				
				for (int k = 0; k < repetitions; k++) {//repetitions

					Thread_list.clear();
					
					for (int i = 0; i < N; i++) {
						Socket socket = new Socket(hostname, port);
						ClientThread client = new ClientThread(socket, i, RTT_list, N);
						client.start();
						Thread_list.add(client);
					}

					for (int i = 0; i < N; i++) {
					
						try {
							Thread_list.get(i).join();
							System.out.println("Thread "+i+", AVERAGE RTT: " + RTT_array[i]);
							Sum_array[i] += RTT_array[i];
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
			
						
				}
				
				long Average_array[] = new long[N];
				for( int i=0; i< N; i++ ){
					Average_array[i] = Sum_array[i] / repetitions;
					System.out.println("AVERAGE ARRAY "+i+": "+Average_array[i]);
				}
				
				return;
			}

			// https://stackoverflow.com/questions/702415/how-to-know-if-other-threads-have-finished

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());

		}

	}

}