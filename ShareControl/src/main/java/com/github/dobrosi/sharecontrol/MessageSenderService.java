package com.github.dobrosi.sharecontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.dobrosi.sharecontrol.command.Command;
import com.github.dobrosi.sharecontrol.command.ICommand;

public class MessageSenderService {
	private Logger logger = Logger.getAnonymousLogger();

	private Stack<ICommand> commands = new Stack<>();

	public void sendMessages(DatagramSocket socket) {
		commands.forEach(e -> sendMessage(socket, e));
		commands.clear();
	}

	private void sendMessage(DatagramSocket socket, ICommand e) {
		sendMessage(socket, e.createMessage().getBytes());
	}

	private void sendMessage(DatagramSocket socket, byte[] message) {
		try {
			socket.send(new DatagramPacket(message, message.length));
		} catch (IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

	public <T extends ICommand> void addCommand(Class<T> class1, Object... args) {
		commands.push(Command.create(class1, args));
	}
}