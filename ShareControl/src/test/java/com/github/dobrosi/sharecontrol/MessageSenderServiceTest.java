package com.github.dobrosi.sharecontrol;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.github.dobrosi.sharecontrol.command.KeyboardKeyPressedCommand;
import com.github.dobrosi.sharecontrol.command.KeyboardKeyReleaseCommand;
import com.github.dobrosi.sharecontrol.command.MousePointerCommand;

public class MessageSenderServiceTest {
	MessageSenderService s;

	DatagramSocket socket;

	@BeforeEach
	public void setUp() throws IOException {
		s = new MessageSenderService();
		socket = mock(DatagramSocket.class);
	}

	@Test
	public void test() throws IOException {
		s.addCommand(KeyboardKeyPressedCommand.class, 19);
		s.addCommand(KeyboardKeyReleaseCommand.class, 19);

		s.sendMessages(socket);

		assertIterableEquals(asList("{P:19}", "{R:19}"), getMessages(2));

		reset(socket);

		s.addCommand(MousePointerCommand.class, -5, 15);

		s.sendMessages(socket);

		assertIterableEquals(asList("{p:-5,15}"), getMessages(1));
	}

	private List<String> getMessages(int times) throws IOException {
		ArgumentCaptor<DatagramPacket> datagramPacketCaptor = forClass(DatagramPacket.class);
		verify(socket, times(times)).send(datagramPacketCaptor.capture());
		List<String> actual = datagramPacketCaptor.getAllValues().stream().map(b -> new String(b.getData()))
				.collect(toList());
		return actual;
	}
}
