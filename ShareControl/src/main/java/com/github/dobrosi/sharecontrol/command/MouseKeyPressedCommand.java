package com.github.dobrosi.sharecontrol.command;

public class MouseKeyPressedCommand extends Command implements MouseButtonCommand {
	@Override
	public void execute() {
		getRobot().mousePress(convertMouseButton(trimAndParseInt((String) args[0])));
	}
}