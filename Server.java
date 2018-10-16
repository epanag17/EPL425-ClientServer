import java.io.*;
import java.net.*;

class ServerThread extends Thread {
    private Socket socket;
 
    public ServerThread(Socket socket) {
        this.socket = socket;
    }
 
    public void run() {
        try {
        	//Read data from the client
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            //String line = reader.readLine(); // reads a line of text
            
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
 
			String request = reader.readLine();
			if (request == null) {
				System.out.println("Server thread: Got null");
				request = reader.readLine();
				if(request == null)
					System.out.println("Server thread: Got null again");
				else
					System.out.println(request);
				
			} else {
				// String reverseText = new
				// StringBuilder(request).reverse().toString();
				String[] tokens = request.split(" ");
				int user_id = Integer.parseInt(tokens[3]);
				// reply to the client
				writer.println("WELCOME " + user_id);
			}
            
 
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

public class Server {

	public static void main(String[] args) {
		if (args.length < 1) return;
		 
        int port = Integer.parseInt(args[0]);
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();
                //System.out.println("New client connected: "+ socket.getRemoteSocketAddress() + ", " + socket.getPort() );
    			System.out.println("New Server Thread , Local Socket Address: "+ socket.getLocalSocketAddress() + ", Remote Socket Address: "+socket.getRemoteSocketAddress());
    			
                new ServerThread(socket).start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }

	}

}
