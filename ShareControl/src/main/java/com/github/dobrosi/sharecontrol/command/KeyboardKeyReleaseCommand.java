package com.github.dobrosi.sharecontrol.command;

public class KeyboardKeyReleaseCommand extends Command {

	@Override
	public void execute() {
		getRobot().keyRelease(trimAndParseInt((String) args[0]));
	}
}