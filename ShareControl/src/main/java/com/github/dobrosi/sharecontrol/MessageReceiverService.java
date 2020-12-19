package com.github.dobrosi.sharecontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.dobrosi.sharecontrol.command.Command;
import com.github.dobrosi.sharecontrol.command.ICommand;

public class MessageReceiverService {
	private Logger logger = Logger.getAnonymousLogger();

	public ICommand receiveMessage(DatagramSocket socket) throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		return receiveMessage(packet);
	}

	public ICommand receiveMessage(DatagramPacket packet) {
		String received = new String(packet.getData(), 0, packet.getLength());
		logger.log(Level.INFO, "Received: " + received);

		return Command.create(received);
	}
}