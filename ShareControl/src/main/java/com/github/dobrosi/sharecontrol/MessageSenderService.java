package com.github.dobrosi.sharecontrol;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.dobrosi.sharecontrol.command.Command;
import com.github.dobrosi.sharecontrol.command.ICommand;
import com.github.dobrosi.sharecontrol.command.MousePointerCommand;

public class MessageSenderService {
	private Logger logger = Logger.getAnonymousLogger();

	private Stack<ICommand> commands = new Stack<>();

	private InetAddress address;

	private Point previousMousePointerLocation;

	public MessageSenderService(InetAddress address) {
		this.address = address;
	}

	public void sendMessages(DatagramSocket socket) {
		addPointerCommand();
		commands.forEach(e -> sendMessage(socket, e));
		commands.clear();
	}

	private void addPointerCommand() {
		Point l = MouseInfo.getPointerInfo().getLocation();
		if (!l.equals(previousMousePointerLocation)) {
			if (previousMousePointerLocation != null) {
				addCommand(MousePointerCommand.class, l.x - previousMousePointerLocation.x,
						l.y - previousMousePointerLocation.y);
			}
			previousMousePointerLocation = l;
		}
	}

	private void sendMessage(DatagramSocket socket, ICommand e) {
		sendMessage(socket, e.createMessage().getBytes());
	}

	private void sendMessage(DatagramSocket socket, byte[] message) {
		try {
			logger.log(Level.INFO, new String(message));
			socket.send(new DatagramPacket(message, message.length, address, ShareControl.PORT));
		} catch (IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

	public <T extends ICommand> void addCommand(Class<T> class1, Object... args) {
		commands.push(Command.create(class1, args));
	}
}