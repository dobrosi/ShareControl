package com.github.dobrosi.sharecontrol.command;

public class MouseWheelCommand extends Command {
	@Override
	public void execute() {
		getRobot().mouseWheel(trimAndParseInt((String) args[0]));
	}
}