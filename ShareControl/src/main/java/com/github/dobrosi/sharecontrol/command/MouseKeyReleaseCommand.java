package com.github.dobrosi.sharecontrol.command;

public class MouseKeyReleaseCommand extends Command implements MouseButtonCommand {
	@Override
	public void command() {
		getRobot().mouseRelease(convertMouseButton(trimAndParseInt((String) args[0])));
	}
}