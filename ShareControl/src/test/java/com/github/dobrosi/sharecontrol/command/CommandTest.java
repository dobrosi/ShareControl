package com.github.dobrosi.sharecontrol.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class CommandTest {
	@Test
	public void testCreate() {
		ICommand c = Command.create(MouseWheelCommand.class, -5);
		assertSame(MouseWheelCommand.class, c.getClass());
		assertArrayEquals(new Object[] { -5 }, c.getArgs());
	}
}
