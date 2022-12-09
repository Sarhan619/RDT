import java.io.*; 
import java.util.List;
import java.util.ArrayList;

  
public class ParSender extends TransportLayer{ 
    public static final int receiverPort = 9999;
    public static final int senderPort = 9998;

    public ParSender(LossyChannel lc) {
		super(lc);
    }

    public void run() {
		String pathToFile = System.getProperty("user.dir")+"/inputText.txt"; 
		List<String> readInput = readFile(pathToFile); 
		sendTheData(readInput); 
	}

	private void sendTheData(List<String> readInput){
		byte nextExpectedPacket = 0;
		Packet arrivedPacket = new Packet();
			byte[] payload_data = null;
		if(readInput.isEmpty()){     
			payload_data = this.getSentPacket();
		}else{
			payload_data = this.getReceivedPacket(readInput);
		}

		if (null == payload_data) {
			return;
		}
		System.out.println("Receiving the Packet: ");
		while(true) {
			arrivedPacket.payload = payload_data;
			arrivedPacket.length = payload_data.length;
			arrivedPacket.seq = nextExpectedPacket;
			sendToLossyChannel(arrivedPacket);
			wakeup_m = false;  
			startTimer();   
			int theEvent = waitForEvent();
			if(Event_packet_arrival == theEvent) { 
				arrivedPacket = receiveFromLossyChannel();
				if(!arrivedPacket.isValid()){ 
					continue;
				}
				if(arrivedPacket.ack == nextExpectedPacket){
					stopTimer();          
					if(!readInput.isEmpty()){
						payload_data = this.getReceivedPacket(readInput); 
					}else{
						payload_data = this.getSentPacket();
					}
					if (payload_data == null) {
						return;
					}
					nextExpectedPacket = increment(nextExpectedPacket);
				}
				else {
					System.out.println("We have received a duplicate packet");
				}
			}
		}
	}

  private List<String> readFile(String pathToFile){
		List<String> readInput = new ArrayList<>();
		try(BufferedReader receivedFile = new BufferedReader(new FileReader(pathToFile))){
			String readSentence = receivedFile.readLine();
		
			LossyChannel.lossyRateController = Integer.valueOf(readSentence); 
			readSentence = receivedFile.readLine();
			while (readSentence!=null){
				readInput.add(readSentence);
				readSentence = receivedFile.readLine();
			}
		} catch (FileNotFoundException e) {
			System.err.println("File unavailable:- "+e);
		} catch (IOException e) {
			System.err.println("Error while reading file:- "+e);
		} 
		return readInput;
	}

	byte[] getReceivedPacket(List<String> inputReader){
		if(!inputReader.isEmpty()){
			String sentence =	inputReader.remove(0);
			System.out.println("Sending: "+sentence);
			return  sentence.getBytes();
		}else{
			System.out.println("The message has been successfully sent");
			System.out.println("Started the user input process");
			return null;
		}
	}
   
	
    byte[] getSentPacket() {
		System.out.println("Input message to send: ");
		try {
				BufferedReader fromSender = 
						new BufferedReader(new InputStreamReader(System.in)); 
				String sentence = fromSender.readLine(); 
				if(null == sentence)
					System.exit(1);
				System.out.println("Sending packet: "+sentence);
	    
				return sentence.getBytes();         
		} catch(Exception e) {
				System.out.println("Input/Output error: "+e);
				return null;
		}
    }

    public static void main(String args[]) throws Exception { 
		LossyChannel lc = new LossyChannel(senderPort, receiverPort);
		ParSender sender = new ParSender(lc);
		lc.setTransportLayer(sender);
		sender.run();
    } 
} 
