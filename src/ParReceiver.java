import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;



public class ParReceiver extends TransportLayer{
    public static final int receiverPort = 9999;
    public static final int senderPort = 9998;
    private static final String duplicateMessage = "A duplicate packet has been received";

    public ParReceiver(LossyChannel lc) {
	super(lc);
    }

    public void run() {
	byte nextExpectedPacket = 0;
	Packet arrivedPacket = new Packet();
	Packet sentPacket = new Packet();

	System.out.println("Receiving message: ");

	while(true) {
	    int theEvent = waitForEvent();
	    if(Event_packet_arrival == theEvent) {
			arrivedPacket = receiveFromLossyChannel();

			if (arrivedPacket.isValid()) {   
					if (arrivedPacket.seq == nextExpectedPacket) { 
						this.sendMessage(arrivedPacket);
						nextExpectedPacket = increment(nextExpectedPacket);
					}
					else {   
						System.out.println(duplicateMessage);
						writeFile(duplicateMessage);
					}
				}
				sentPacket.ack = arrivedPacket.seq;
				sendToLossyChannel(sentPacket);
			}
		}
    }
    
    
    void sendMessage(Packet packet) {
		byte[] payload_data = new byte[packet.length];
		for(int i=0; i<payload_data.length; i++)
			payload_data[i] = packet.payload[i];
		String Received = new String(payload_data);
		System.out.println("Received "+packet.length+" bytes: " + Received);
		writeFile("Received "+packet.length+" bytes: "+ Received);

    }
	
	private void writeFile(String receivedPackage){
		String pathToFile = System.getProperty("user.dir")+"/outputText.txt";
		try(BufferedWriter theWriter = new BufferedWriter(new FileWriter(pathToFile,true))){
			theWriter.write(receivedPackage+'\n');
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}  
	}

    public static void main(String args[]) throws Exception { 
		LossyChannel lossyC = new LossyChannel(receiverPort, senderPort);
		ParReceiver theReceiver = new ParReceiver(lossyC);
		lossyC.setTransportLayer(theReceiver);
		theReceiver.run();
    } 
}  
