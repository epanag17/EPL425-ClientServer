import java.net.*;
import java.util.Random;
import java.util.Scanner;

import java.io.*;

public class Client {

	public static final int N = 10;

	public static void main(String[] args) {

		if (args.length < 2)

			return;

		String hostname = args[0];

		int port = Integer.parseInt(args[1]);

		try {

			for (int i = 0; i < N; i++) {
				Socket socket = new Socket(hostname, port);
				ClientThread client = new ClientThread(socket, i);
				client.start();
			}

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());

		}

	}

}