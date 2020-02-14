import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class PingPong extends Thread {

	private ObjectInputStream oin;
	private ObjectOutputStream oout;
	@SuppressWarnings("unused")
	private Socket sock;
	private static int numConnectedClients;
	private static int maxConnections =10;
	private Random rand = new Random();
	private boolean sender;
	private boolean running = true;
	private int delay = 500;
	private int gameID;

	public static void main(String[] args) {
		System.out.println("java PingPong <client|server> <serverHost> <port#>");
		String host = args[1];
		int port = Integer.parseInt(args[2]);

		ServerSocket serverSock;

		if (args[0].equals("server")) {
			System.out.println("attempting to create server");
			try {
				serverSock = new ServerSocket(port);
				System.out.println("PingPong gerver up and running on port:" + port);

				Socket sock = null;
				while (true) {
					if( numConnectedClients >= maxConnections){
						System.out.println("max connections reached.");
					}
					sock = serverSock.accept();
					numConnectedClients +=1;
					
					Thread myServer = new PingPong(sock);
					((PingPong)myServer).ServerDoCoinToss();
					myServer.start();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (args[0].equals("client")) {

			try {
				System.out.println("attempting to connect to server");
				System.out.println("establishing connection");
				Socket sock = new Socket(host, port);

				Thread myClient = new PingPong(sock);

				((PingPong) myClient).ClientDoCoinToss();
				myClient.start();

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public PingPong(Socket sock) {
		this.sock = sock;
		try {
			this.oout = new ObjectOutputStream(sock.getOutputStream());
			this.oin = new ObjectInputStream(sock.getInputStream());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void ClientDoCoinToss() {

		try {
			Integer cint = new Integer(rand.nextInt());
			Integer sint = (Integer)oin.readObject();
			oout.writeObject(cint);
			Integer gID = (Integer)oin.readObject();
			gameID = gID.intValue();
					
			if (sint.compareTo(cint) < 0)
				sender = true;
			else
				sender = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void ServerDoCoinToss() {
		try {
			
			Integer sint = new Integer(rand.nextInt());
			gameID = numConnectedClients;
			Integer gID = new Integer(gameID);
			oout.writeObject(sint);
			Integer cint = (Integer)oin.readObject();
			oout.writeObject(gID);

			if (sint.compareTo(cint) >=0)
				sender = true;
			else
				sender = false;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			PlayPingPong();
		}
	}

	public void PlayPingPong() {
		while (running) {
			try {

				if (sender) {
					// send object
					Thread.sleep(delay);
					Ball ball = new Ball();
					oout.writeObject(ball);
					System.out.println("ping game: " + gameID);

					// recieve object
					ball = (Ball) oin.readObject();

				} else {
					// recieve object
					Ball ball = (Ball) oin.readObject();

					// send object
					Thread.sleep(delay);
					oout.writeObject(new Ball());
					System.out.println("pong game: " + gameID );
				}

				// sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
