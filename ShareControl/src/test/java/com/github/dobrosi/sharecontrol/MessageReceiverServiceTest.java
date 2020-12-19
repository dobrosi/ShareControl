package com.github.dobrosi.sharecontrol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.dobrosi.sharecontrol.command.MouseWheelCommand;
import com.github.dobrosi.sharecontrol.command.MousePointerCommand;

public class MessageReceiverServiceTest {
	MessageReceiverService s;

	DatagramSocket socket;

	@BeforeEach
	public void setUp() throws IOException {
		s = new MessageReceiverService();
		socket = mock(DatagramSocket.class);
	}

	@Test
	public void testMouseWheelCommand() throws IOException {
		extracted("{w:-20}");
		MouseWheelCommand c = (MouseWheelCommand) s.receiveMessage(socket);
		assertArrayEquals(new Object[] { "-20" }, c.getArgs());
	}

	@Test
	public void testPointerCommand() throws IOException {
		extracted("{m:100,100}");
		MousePointerCommand c = (MousePointerCommand) s.receiveMessage(socket);
		assertEquals("m", c.getCommand());
	}

	private void extracted(String data) throws IOException {
		Mockito.doAnswer(invocation -> {
			((DatagramPacket) invocation.getArguments()[0]).setData(data.getBytes());
			return null;

		}).when(socket).receive(Mockito.any());
	}

}
