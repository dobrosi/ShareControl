package com.github.dobrosi.sharecontrol;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver extends Thread {
	private Logger logger = Logger.getAnonymousLogger();
	private MessageReceiverService messageReceiverService = new MessageReceiverService();

	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(ShareControl.PORT);
			boolean running = true;
			byte[] buf = new byte[1024];

			while (running) {
				messageReceiverService.receiveMessage(socket);

			}
			socket.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

}