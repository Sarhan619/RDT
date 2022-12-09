public class Packet {
    public static final byte[] PREAMBLE = 
       {'E','E','C','4','8','4','F','A'};
    public static final int Max_paxket_payload = 1024;
    public static final int Max_packet_size = Max_paxket_payload + 6 + PREAMBLE.length; 

	public byte seq; 
    public byte ack; 
    public int length; 
    public byte[] payload = new byte[Max_paxket_payload];

    private boolean m_isValid = true;

    public Packet() {
		seq = -1;
		ack = -1;
		length = 0;
    }

    public Packet(byte[] receivedData) {
    	
		m_isValid = verifyPacket(receivedData);
		int index = PREAMBLE.length;
		if(m_isValid) {
	    
			seq = receivedData[index++];

	  
			ack = receivedData[index++];
	    
	 
			byte[] intArray = {receivedData[index], receivedData[index+1], receivedData[index+2], receivedData[index+3]};
			index += 4;
			length = ByteArrayUtils.readInt(intArray);

	    
			for(int i=0; i<length; i++) {
				payload[i] = receivedData[index+i];
			}
	    
		}
    }

    public byte[] toBytes() {
	
		int totalLen = length + 2 + 4 + PREAMBLE.length;
		byte[] data = new byte[totalLen];

		int i = 0;
		for(i=0; i<PREAMBLE.length; i++)
			data[i] = PREAMBLE[i];

		
		data[i++] = seq; 
		data[i++] = ack; 

		byte[] intArray = new byte[4];
		ByteArrayUtils.writeInt(intArray, length);
		int k = 0;
		data[i++] = intArray[k++];
		data[i++] = intArray[k++];
		data[i++] = intArray[k++];
		data[i++] = intArray[k++];

	

		for(int j=0; j<length; j++,i++)
				data[i] = payload[j];



		return data;
    }

    private boolean verifyPacket(byte[] data) {
		boolean verified = true;
		for(int i=0; i<PREAMBLE.length; i++)
				if(data[i] != PREAMBLE[i]) {
						verified = false;
						break;
				}
		return verified;
    }

    public boolean isValid() {
			return m_isValid;
    }
}
