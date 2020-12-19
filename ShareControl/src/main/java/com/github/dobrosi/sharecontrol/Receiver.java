package com.github.dobrosi.sharecontrol;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver extends Thread {
	private Logger logger = Logger.getAnonymousLogger();
	private MessageReceiverService messageReceiverService = new MessageReceiverService();

	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(ShareControl.PORT);
			boolean running = true;
			while (running) {
				messageReceiverService.receiveMessage(socket).execute();

			}
			socket.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

}