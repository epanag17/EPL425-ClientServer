import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Server {

	public static ArrayList<ServerThread> Thread_list = new ArrayList();

	public static int threadID = 0;


	public static void main(String[] args) {

		if (args.length < 1)

			return;

		try {
			int port = Integer.parseInt(args[0]);


			try (ServerSocket serverSocket = new ServerSocket(port)) {

				System.out.println("Server is listening on port " + port);

				startTime = System.nanoTime();

				while (true) {

					Socket socket = serverSocket.accept();

					// System.out.println("New client connected");

					ServerThread serverThread = new ServerThread(socket, threadID);
					serverThread.start();
					threadID++;
					Thread_list.add(serverThread);

					// socket.close();
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (IOException ex) {

			System.out.println("Server exception: " + ex.getMessage());

			ex.printStackTrace();

		}

	}
}