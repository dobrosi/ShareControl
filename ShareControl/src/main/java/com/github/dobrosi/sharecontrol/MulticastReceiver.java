package com.github.dobrosi.sharecontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastReceiver extends Thread {
	Logger logger = Logger.getAnonymousLogger();
	protected MulticastSocket socket = null;
	protected byte[] buf = new byte[256];

	public void run() {
		try {
			socket = new MulticastSocket(4446);
			InetAddress group = InetAddress.getByName("230.0.0.0");
			socket.joinGroup(group);
			while (true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				logger.log(Level.INFO, String.format("Broadcast received: %s : %s", received, packet.getAddress()));
				if ("end".equals(received)) {
					break;
				}
			}
			socket.leaveGroup(group);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}