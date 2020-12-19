package com.github.dobrosi.sharecontrol.command;

public class KeyboardKeyPressedCommand extends Command {
	@Override
	public void command() {
		getRobot().keyPress(trimAndParseInt((String) args[0]));
	}
}