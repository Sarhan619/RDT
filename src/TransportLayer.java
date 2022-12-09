import java.util.*;

public abstract class TransportLayer {
    public static final int Maximum_sequence = 1;
    public static final int Event_packet_arrival = 0;
    public static final int Event_timeout = 1;
    public static final int Event_message_toSend = 2;
    public static final long Timeout = 1000; 
	public static final int Channel_lossy_rate = 3;


    java.util.Timer timer_m;
    SendTimerTask timerTask_m = new SendTimerTask();

    boolean wakeup_m = false;

    int event_m = -1;

    LossyChannel lossyChannel_m = null;

    public TransportLayer(LossyChannel lc) {
		lossyChannel_m = lc;
    }

    public abstract void run();

    byte increment(byte seq) {
		byte newseq;
		if(seq < Maximum_sequence)
			newseq = 1;
		else
			newseq = 0;
		return newseq; 
    }

    void sendToLossyChannel(Packet p) {
		lossyChannel_m.send(p.toBytes());
    }

    Packet receiveFromLossyChannel() {
		byte[] receivedData = lossyChannel_m.receive();
		Packet packet = new Packet(receivedData);
		return packet;
    }

    void startTimer() {
		try {
			timer_m = new java.util.Timer();
			timer_m.schedule(new SendTimerTask(), Timeout);
		} catch(Exception e) {}
    }

    void stopTimer() {
		try {
			timer_m.cancel();
		} catch(Exception e) {}
    }

    public synchronized int waitForEvent() {
		while(!wakeup_m) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		wakeup_m = false;
		return event_m;
    }

    public synchronized void onPacketArrival() {
		wakeup_m = true;
		event_m = Event_packet_arrival;
		notifyAll();
    }

    public synchronized void onTimeout() {
		wakeup_m = true;
		event_m = Event_timeout;
		notifyAll();
    }

    public class SendTimerTask extends TimerTask {
		public void run() {
			onTimeout();
		}
    }
}
