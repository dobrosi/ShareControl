package com.github.dobrosi.sharecontrol.command;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import java.util.stream.Stream;

public interface ICommand {
	final CommandParameter messengerParameter = new CommandParameter();

	public abstract void execute();

	public abstract String getCommand();

	public abstract Object[] getArgs();

	default String createMessage() {
		return format(messengerParameter.pattern, messengerParameter.commandBegin, getCommand(),
				messengerParameter.commandDelimiter,
				join(messengerParameter.delimiter,
						Stream.of(getArgs()).map(o -> o == null ? null : o.toString()).collect(toList())),
				messengerParameter.commandEnd);
	}

	default int trimAndParseInt(String string) {
		return parseInt(string.trim());
	}
}