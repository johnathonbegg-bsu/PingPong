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
	private Boolean sender; //true == ping, false == pong
	private Random rand; 
	
	
	public static void main(String[] args)
	{
		System.out.println("java PingPong <client|server> <serverHost> <port#>");
		String host = args[1];
		int port = Integer.parseInt(args[2]);
		
		if(args[0].equals("server")) {
			System.out.println("attempting to create server");
			PingPong myServer = new PingPong(port);
			myServer.serverConnect();
		}
		else if(args[0].equals("client")) {
			System.out.println("attempting to connect to server");
			
			while(true) {
				PingPong myClient = new PingPong(host,port);
				myClient.clientPlayPingPong();
			}
			
		}
				
	}
	
	//SERVER CODE
	 public PingPong(int port) {
		 try {
			serverSock = new ServerSocket(port);
			System.out.println("PingPong gerver up and running on port:" + port);
			numConnectedClients = 0;
			//limit number of threads
			maxConnections = 1; 
			
		} catch (IOException e) {
			System.out.println("failed to establish server at port:" + port);
			e.printStackTrace();
		}
		 
	 }
	 
	 public void serverConnect() {
		 Socket sock;
		 System.out.println("waiting for clients");
		 while (true) {
			 try {
				 //if there is still room to connect
				 // then accept the connection
				 if (numConnectedClients <=maxConnections) {
					sock = serverSock.accept();
					 
					System.out.println("Client found");
					  
					numConnectedClients ++;
					oout = new ObjectOutputStream(out);
					oin = new ObjectInputStream(in);
					//TODO coin toss
					System.out.println("doing coin toss");
					Integer stemp = new Integer(rand.nextInt());
					oout.writeObject(stemp);
					Integer ctemp = (Integer) oin.readObject();
					
					if(stemp.compareTo(ctemp) >=0){sender = true;}
					else {sender = false;}
					
					serverPlayPingPong(sock);	
				 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }
	 private void serverPlayPingPong(Socket sock) throws IOException, ClassNotFoundException {
		
			
			if(sender)
			{
				System.out.println("sending");
				System.out.println("ping");
				oout.writeObject(new Ball());
				oout.flush();
				
				Ball ball = (Ball) oin.readObject();
				System.out.println("pong");
			}
			else
			{
				System.out.println("recieving");
				Ball ball = (Ball) oin.readObject();
				System.out.println("ping");
				
			
				oout.writeObject(new Ball());
				oout.flush();
				System.out.println("pong");
			}
			
			
			serverPlayPingPong(sock);
		 
	 }
	 
	//CLIENT CODE
	public  PingPong(String host, int port) {
	
		 try {
			 System.out.println("establishing connection");
			sock = new Socket(host, port);
			out = sock.getOutputStream();
			in = sock.getInputStream(); 
			oout = new ObjectOutputStream(out);
			oin = new ObjectInputStream(in);
			//* TODO coin toss
			Integer stemp = (Integer) oin.readObject();
			Integer ctemp = new Integer(rand.nextInt());
			oout.writeObject(ctemp);
			//s > c ==1, s = c ==0; s < c == -1
			if(stemp.compareTo(ctemp) >=0) {sender = false;
			System.out.println("I am the sender");}
			else {sender = true;
			System.out.println("I am the reciever");}
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	 }
	
	public void clientPlayPingPong() {
		// TODO Auto-generated method stub
		
		try {
			 
			if(sender)
			{
				System.out.println("sending");
				System.out.println("ping");
				oout.writeObject(new Ball());
				oout.flush();
				
				Ball ball = (Ball) oin.readObject();
				System.out.println("pong");
			}
			else
			{
				System.out.println("recieving");
				Ball ball = (Ball) oin.readObject();
				System.out.println("ping");
				
			
				oout.writeObject(new Ball());
				oout.flush();
				System.out.println("pong");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e){
		
			e.printStackTrace();
		}
		clientPlayPingPong();
	}
	
	
	}
