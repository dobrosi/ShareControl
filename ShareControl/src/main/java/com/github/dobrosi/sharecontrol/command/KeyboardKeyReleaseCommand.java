package com.github.dobrosi.sharecontrol.command;

public class KeyboardKeyReleaseCommand extends Command {

	@Override
	public void command() {
		getRobot().keyRelease(trimAndParseInt((String) args[0]));
	}
}