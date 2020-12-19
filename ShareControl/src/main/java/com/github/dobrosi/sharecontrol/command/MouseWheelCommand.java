package com.github.dobrosi.sharecontrol.command;

public class MouseWheelCommand extends Command {
	@Override
	public void command() {
		getRobot().mouseWheel(trimAndParseInt((String) args[0]));
	}
}