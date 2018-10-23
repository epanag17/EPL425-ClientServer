import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class Server {

	public static ArrayList<ServerThread> Thread_list = new ArrayList();

	public static int threadID = 0;
	public static ArrayList<Double> throughputAllThreadsList;
	
	
	
	
	public static ArrayList<Double> cpuLoad_list = new ArrayList<Double>();
	public static ArrayList<Long> memUtil_list = new ArrayList<Long>();
	
	
	public static int totalRequests = 0;
	
	public static double getProcessCpuLoad() throws Exception {

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

		if (list.isEmpty())
			return -1;

		Attribute att = (Attribute) list.get(0);
		Double value = (Double) att.getValue();

		// usually takes a couple of seconds before we get real values
		if (value == -1.0)
			return -1;
		// returns a percentage value with 1 decimal point precision
		return ((int) (value * 1000) / 10.0);
	}

	public static long getMemoryUtilization() throws Exception {

		// TotalPhysicalMemorySize
		// FreePhysicalMemorySize

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name,
				new String[] { "FreePhysicalMemorySize", "TotalPhysicalMemorySize" });

		if (list.isEmpty())
			return -1;

		Attribute att1 = (Attribute) list.get(0);
		Attribute att2 = (Attribute) list.get(1);
		Long freeMem = (Long) att1.getValue();
		Long totalMem = (Long) att2.getValue();

		// usually takes a couple of seconds before we get real values
		if (freeMem == -1.0 || totalMem == -1.0)
			return -1;

		Long usedMemory = totalMem - freeMem;
		
		// Panta prepei na diairoume me 10 gia na exoume swsta apotelesmata
		return usedMemory / 10;
	}

	public static void main(String[] args) {

		if (args.length < 1)

			return;
		
		
		
		int N = 10;
		
		// Mistiko input gia arithmo xristwn
		if( args.length == 2 )
			N = Integer.parseInt( args[1] );

		
		boolean firstTime = true;
		
		try {
			PrintWriter writer_output_file = new PrintWriter("Server_Output_"+N+"users.txt", "UTF-8");

			int port = Integer.parseInt(args[0]);


			try (ServerSocket serverSocket = new ServerSocket(port)) {

				System.out.println("Server is listening on port " + port);

				long startTime = System.nanoTime();


				
				do {
					
					// Tin prwti fora perimene 10 sec
					// Tis epwmenes na diakopteis ka8e 1 sec gia na elegxeis ta CPU load kai Mem. Utilization
					if(firstTime){
						serverSocket.setSoTimeout(10000);
						firstTime = false;
					}
					else
						serverSocket.setSoTimeout(3000);
					

					try {

						while (true) {

							
							// Bres poia threads exoun psofisei kai bgale ta apo tin lista
							// Episis piase ta requests pou exoun e3ipiretisei
							for (int j = 0; j < Thread_list.size(); j++) {
								ServerThread thread = Thread_list.get(j);

								if (thread.isAlive())
									continue;
								
								totalRequests += thread.requests_served;
										
								Thread_list.remove(j);
								j = j - 1;
							}
							
							Socket socket = serverSocket.accept();
							

							ServerThread serverThread = new ServerThread(socket, threadID);
							serverThread.start();
							threadID++;
							Thread_list.add(serverThread);
							
							System.out.println("  New client connected. ServerThread id "+threadID);
						}

					} catch (SocketTimeoutException e1) {
						//System.out.println("---SERVER TIMEOUT---");
					}

					// Piase metrikes cpu load ke mem utilization
					try {
						
						double cpuLoad = getProcessCpuLoad();
						if (cpuLoad > 0)
							cpuLoad_list.add(cpuLoad);
	
						long memUtil = getMemoryUtilization();
						if (memUtil > 0)
							memUtil_list.add(memUtil);			
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				} while (!Thread_list.isEmpty());
				

				long endTime = System.nanoTime();
				// Sinilikos xronos se defterolepta
				double totalTime = ((double)(endTime - startTime))/Math.pow(10, 9);
				
				//Typwse to throughput ana sec.
				double throughput = totalRequests / totalTime;
				writer_output_file.println("Throughput: " + throughput + " requests/sec");
				
				
				// Typwse kai ton xrono ektelesis
				writer_output_file.println("Total time: " + totalTime + "sec");
				
				// Typwse CPU load kai Memory Utilization
				double cpuLoad_sum = 0;
				for( int i=0; i<cpuLoad_list.size(); i++ )
					cpuLoad_sum += cpuLoad_list.get(i);
				double cpuLoad_avg = cpuLoad_sum / cpuLoad_list.size();
				writer_output_file.println("CPU Load: " + cpuLoad_avg + "%");
				
				
				long memUtil_sum = 0;
				for( int i=0; i<memUtil_list.size(); i++ )
					memUtil_sum += memUtil_list.get(i);
				long memUtil_avg = memUtil_sum / memUtil_list.size();			
				writer_output_file.println("Mem. Utilization: " + (memUtil_avg/1024) + "KiB");	
				
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			writer_output_file.close();

			System.out.println("!--- SERVER SHUTDOWN ---!");
			
			
			
			
		} catch (IOException ex) {

			System.out.println("Server exception: " + ex.getMessage());

			ex.printStackTrace();

		}

	}
}