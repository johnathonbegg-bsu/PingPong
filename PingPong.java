import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class PingPong {

	private InputStream in;
	private ObjectInputStream oin;
	private OutputStream out;
	private ObjectOutputStream oout;
	private ServerSocket serverSock;
	private Socket sock;
	private int numConnectedClients;
	private int maxConnections;
	private Random rand = new Random();
	private boolean sender;
	private boolean running = true;

	public static void main(String[] args) {
		System.out.println("java PingPong <client|server> <serverHost> <port#>");
		String host = args[1];
		int port = Integer.parseInt(args[2]);

		if (args[0].equals("server")) {
			System.out.println("attempting to create server");
			PingPong myServer = new PingPong(port);
			myServer.serverConnect();
		} else if (args[0].equals("client")) {
			System.out.println("attempting to connect to server");
			PingPong myClient = new PingPong(host, port);
			myClient.clientPlayPingPong();

		}

	}

	/**
	 * Build Server
	 * 
	 * @param port
	 */
	public PingPong(int port) {
		try {
			serverSock = new ServerSocket(port);
			System.out.println("PingPong gerver up and running on port:" + port);
			numConnectedClients = 0;
			// limit number of threads
			maxConnections = 1;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("failed to establish server at port:" + port);
			e.printStackTrace();
		}
	}

	/**
	 * Build
	 * 
	 * @param host
	 * @param port
	 */
	public PingPong(String host, int port) {
		// CLIENT
		try {
			System.out.println("establishing connection");
			sock = new Socket(host, port);
			out = sock.getOutputStream();
			in = sock.getInputStream();

			oout = new ObjectOutputStream(out);
			oin = new ObjectInputStream(in);

			// COIN TOSS
			Integer clientCoin = new Integer(rand.nextInt());
			oout.writeObject(clientCoin);
			Integer serverCoin = (Integer) oin.readObject();

			if (serverCoin.compareTo(clientCoin) >= 0)
				sender = false;
			else
				sender = true;

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void serverConnect() {
		Socket sock;
		System.out.println("waiting for clients");
		while (true) {
			try {
				// if there is still room to connect
				// then accept the connection
				if (numConnectedClients < maxConnections) {
					sock = serverSock.accept();
					numConnectedClients++;

					out = sock.getOutputStream();
					in = sock.getInputStream();

					oout = new ObjectOutputStream(out);
					oin = new ObjectInputStream(in);

					// DO COIN TOSS
					Integer clientCoin = (Integer) oin.readObject();
					Integer serverCoin = new Integer(rand.nextInt());
					oout.writeObject(serverCoin);

					if (serverCoin.compareTo(clientCoin) >= 0)
						sender = true;
					else
						sender = false;

					serverPlayPingPong(sock);
					//running = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void serverPlayPingPong(Socket sock) throws IOException, ClassNotFoundException {
		while (running){
			if(sender){
				Ball ball = new Ball();
				oout.writeObject(ball);
				System.out.println("ping");
				
				
			}else{
				Ball ball = (Ball) oin.readObject();
				//System.out.println("ping");

				oout.writeObject(new Ball());
				System.out.println("pong");
			}
			
		}
		

	}

	// CLIENT CODE

	public void clientPlayPingPong() {
		// TODO Auto-generated method stub
		while (running) {
			try {

				if(sender){
					Ball ball = new Ball();
					oout.writeObject(ball);
					System.out.println("ping");
					
					
				}else{
					Ball ball = (Ball) oin.readObject();
					//System.out.println("ping");

					oout.writeObject(new Ball());
					System.out.println("pong");
				}

				// sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			}
		}
	}

}
