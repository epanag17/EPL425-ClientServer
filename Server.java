import java.io.*;

import java.net.*;
import java.util.Random;



class ServerThread extends Thread {

	private Socket socket;



	public ServerThread(Socket socket) {

		this.socket = socket;

	}


	public static int CalculatePayloadSize(int minRange, int maxRange) {
		Random random = new Random();
		random.setSeed(System.nanoTime());
		
		int range = (maxRange - minRange)+1;
		int size = (int) (random.nextDouble() * range) + minRange;
		size = size * 1024;
		return size;			
	}
	
	public static byte[] CalculatePayloadValue(int payloadSize) {
		byte[] payloadValue= new byte[payloadSize];
		//arxikopoisi payloadValue me 0 stis zyges theseis kai 1 stis mones theseis
		for(int i=0;i<payloadValue.length;i++) {
			if(i % 2 ==0)
				payloadValue[i] = 65;
			else 
				payloadValue[i] = 68;
		}
		return payloadValue;
	}

	public void run() {

		try {

			// Read data from the client

			InputStream input = socket.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));



			OutputStream output = socket.getOutputStream();

			PrintWriter writer = new PrintWriter(output, true);

			

			String request = reader.readLine();



			String[] tokens = request.split(" ");

			int user_id = Integer.parseInt(tokens[3]);

			// reply to the client

			writer.println("WELCOME " + user_id);


			int payloadSize = CalculatePayloadSize(300,2000);
			byte[] payload = CalculatePayloadValue(payloadSize);

			DataOutputStream dOut = new DataOutputStream(output);

			System.out.println("Send to user "+user_id+", Payload size: "+payload.length);
			dOut.writeInt(payloadSize);
			dOut.write(payload);
			

			socket.close();

		} catch (IOException ex) {

			System.out.println("Server exception: " + ex.getMessage());

			ex.printStackTrace();

		}

	}

}



public class Server {


	public static void main(String[] args) {

		if (args.length < 1)

			return;



		int port = Integer.parseInt(args[0]);



		try (ServerSocket serverSocket = new ServerSocket(port)) {



			System.out.println("Server is listening on port " + port);



			while (true) {

				Socket socket = serverSocket.accept();

				System.out.println("New client connected");



				new ServerThread(socket).start();

			}



		} catch (IOException ex) {

			System.out.println("Server exception: " + ex.getMessage());

			ex.printStackTrace();

		}



	}



}
