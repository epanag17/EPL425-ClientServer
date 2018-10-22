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

	public static double getProcessCpuLoad() throws Exception {

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

		if (list.isEmpty())
			return Double.NaN;

		Attribute att = (Attribute) list.get(0);
		Double value = (Double) att.getValue();

		// usually takes a couple of seconds before we get real values
		if (value == -1.0)
			return Double.NaN;
		// returns a percentage value with 1 decimal point precision
		return ((int) (value * 1000) / 10.0);
	}

	public static double getMemoryUtilization() throws Exception {

		// TotalPhysicalMemorySize
		// FreePhysicalMemorySize

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name,
				new String[] { "FreePhysicalMemorySize", "TotalPhysicalMemorySize" });

		if (list.isEmpty())
			return Double.NaN;

		Attribute att1 = (Attribute) list.get(0);
		Attribute att2 = (Attribute) list.get(1);
		Long value1 = (Long) att1.getValue();
		Long value2 = (Long) att2.getValue();

		// usually takes a couple of seconds before we get real values
		if (value1 == -1.0 || value2 == -1.0)
			return Double.NaN;

		return (double) ((value2 - value1)) / (double) value2;

	}

	public static void main(String[] args) {

		if (args.length < 1)

			return;

		try {
			PrintWriter writer_output_file = new PrintWriter("Server_Output.txt", "UTF-8");

			int port = Integer.parseInt(args[0]);

			long startTime = System.nanoTime();
			long endTime = System.nanoTime();

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

					endTime = System.nanoTime();

					if (endTime - startTime > 10 * 1000 * 1000) {

						writer_output_file.flush();
						
						startTime = System.nanoTime();

						double cpuLoad = -1;
						try {
							cpuLoad = getProcessCpuLoad();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (cpuLoad > 0)
							writer_output_file.println(cpuLoad + "	");
						else
							writer_output_file.println("		");

						double memUtil = -1;
						try {
							memUtil = getMemoryUtilization();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (memUtil > 0)
							writer_output_file.println("	" + memUtil + "	");
						else
							writer_output_file.println("		");

					}

					for (int j = 0; j < Thread_list.size(); j++) {

						ServerThread thread = Thread_list.get(j);

						if (thread.isAlive())
							continue;

						long total_throughput_per_thread = 0;
						for (int i = 0; i < thread.throughput_list.size(); i++) {
							total_throughput_per_thread += thread.throughput_list.get(i);
						}
						double throughput_per_thread = ((double) total_throughput_per_thread)
								/ thread.throughput_list.size();
						writer_output_file.println("		" + throughput_per_thread);

						Thread_list.remove(j);
						j = j - 1;
					}

					// socket.close();
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			writer_output_file.close();
		} catch (IOException ex) {

			System.out.println("Server exception: " + ex.getMessage());

			ex.printStackTrace();

		}

	}
}