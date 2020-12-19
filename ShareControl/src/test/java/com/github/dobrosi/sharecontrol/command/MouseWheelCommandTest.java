package com.github.dobrosi.sharecontrol.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MouseWheelCommandTest {

	@Test
	void testGetCommand() {
		assertEquals("w", new MouseWheelCommand().getCommand());
	}

	@Test
	void testCommand() {
		assertEquals("{w:-5}", Command.create(MouseWheelCommand.class, new Object[] { -5 }).createMessage());
	}

}
