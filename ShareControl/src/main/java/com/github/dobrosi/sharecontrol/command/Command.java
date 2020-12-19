package com.github.dobrosi.sharecontrol.command;

import static java.util.regex.Pattern.quote;

import java.awt.AWTException;
import java.awt.Robot;
import java.lang.reflect.InvocationTargetException;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public abstract class Command implements ICommand {
	protected Object[] args;

	protected static BiMap<String, Class<?>> commands = new ImmutableBiMap.Builder<String, Class<?>>()
			.put("w", MouseWheelCommand.class).put("m", MousePointerCommand.class).put("p", MouseKeyPressedCommand.class)
			.put("r", MouseKeyReleaseCommand.class).put("P", KeyboardKeyPressedCommand.class)
			.put("R", KeyboardKeyReleaseCommand.class).build();

	@Override
	public Object[] getArgs() {
		return args;
	}

	@Override
	public String getCommand() {
		return commands.inverse().get(this.getClass());
	}

	Robot getRobot() {
		try {
			return new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends ICommand> ICommand create(Class<T> c, Object... args) {
		try {
			return ((Command) c.getDeclaredConstructor().newInstance()).withArgs(args);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private ICommand withArgs(Object[] args) {
		this.args = args;
		return this;
	}

	public static ICommand create(String message) {
		return create(
				message.split(quote(messengerParameter.commandEnd))[0].split(quote(messengerParameter.commandBegin))[1]
						.split(quote(messengerParameter.commandDelimiter)));

	}

	private static ICommand create(String[] message) {
		try {
			return ((Command) commands.get(message[0]).getDeclaredConstructor().newInstance())
					.withArgs(message[1].split(messengerParameter.delimiter));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
}