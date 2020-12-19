package com.github.dobrosi.sharecontrol.command;

public class MousePointerCommand extends Command {
	@Override
	public void command() {
		getRobot().mouseMove(trimAndParseInt((String) getArgs()[0]), trimAndParseInt((String) getArgs()[1]));
	}
}