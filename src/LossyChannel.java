import java.io.*; 
import java.net.*; 
import java.util.*;

public class LossyChannel {
    private InetAddress IPaddress_m;
    private DatagramSocket socket_m = null;
    private int localport_m = 0;
    private int remoteport_m = 0;
	public static int lossyRateController=3; 

    private byte[] receiveBuffer_m = new byte[Packet.Max_packet_size]; 
    private TransportLayer transportLayer_m = null;
    
    public LossyChannel(int localport, int remoteport) {
		localport_m = localport;
		remoteport_m = remoteport;
		try {
			IPaddress_m = InetAddress.getByName("localhost"); 
			socket_m = new DatagramSocket(localport); 
		} catch(Exception e) {
			System.out.println("Cannot create UDP socket: "+e);
		}


	new ReadThread().start();
    }

    public void setTransportLayer(TransportLayer tl) {
		transportLayer_m = tl;
    }

    public void send(byte[] payload) {
	//
	// 
		Random rand = new Random();
		int randnum = rand.nextInt(10); 
		if(randnum < 3)
			return; 

		try {
			DatagramPacket p = new DatagramPacket(payload, payload.length, IPaddress_m, remoteport_m); 
	    	socket_m.send(p);
		} catch(Exception e) {
			System.out.println("Error sending packet: "+e);
		}
    }

   
    public byte[] receive() {
		return receiveBuffer_m;
    }

  
    public class ReadThread extends Thread {
		public void run() {
			while(true) {
				DatagramPacket p = new DatagramPacket(receiveBuffer_m, receiveBuffer_m.length); 
		
				try {
					socket_m.receive(p); 
				} catch(Exception e) {
					System.out.println("Cannot receive from socket: "+e);
				}
		
		byte[] receivedData = p.getData();
		
		if(transportLayer_m != null)
		    transportLayer_m.onPacketArrival();
	    }
	}
    }
}
