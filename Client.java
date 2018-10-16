import java.net.*;
import java.util.Scanner;
import java.io.*;

class ClientThread extends Thread {
    private Socket socket;
    private int user_id;
 
    public ClientThread(Socket socket, int user_id) {
        this.socket = socket;
        this.user_id = user_id;
    }
 
    public void run() {
    	OutputStream output;
		try {
			InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			output = socket.getOutputStream();
			//SEND REQUEST TO THE SERVER
			PrintWriter writer = new PrintWriter(output, true);
			String hostname= socket.getLocalAddress().toString();
			int port = socket.getLocalPort();
			System.out.println(hostname+" "+port);
			writer.println("HELLO "+hostname+" "+port);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
      /*  try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
 
            String text;
 
            do {
                text = reader.readLine();
                String reverseText = new StringBuilder(text).reverse().toString();
                writer.println("Server: " + reverseText);
 
            } while (!text.equals("bye"));
 
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        */
    }
}

public class Client {

	public static final int N = 10;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length < 2)
			return;

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		System.out.println("hostname: " + hostname);
		System.out.println("port: "+ port);
		
	for(int i=0;i<10;i++){
			
			
		
		try (Socket socket = new Socket(hostname, port)) {

			System.out.println("Client Thread "+i+", Local Socket Address: "+ socket.getLocalSocketAddress() + ", Remote Socket Address: "+socket.getRemoteSocketAddress());
			
			
			new ClientThread(socket, i);
			
			
			
			

			/*Scanner scan = new Scanner(System.in);
			String text;

			do {
				System.out.print("HELLO");
				text = scan.nextLine();

				//writer.println(text);

				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				String time = reader.readLine();

				System.out.println(time);

			} while (!text.equals("bye"));

			scan.close();*/

			socket.close();

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
		
	}
	}

}
