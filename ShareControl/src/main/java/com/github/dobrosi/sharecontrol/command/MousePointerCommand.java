package com.github.dobrosi.sharecontrol.command;

import java.awt.MouseInfo;
import java.awt.Point;

public class MousePointerCommand extends Command {
	@Override
	public void command() {
		Point l = MouseInfo.getPointerInfo().getLocation();
		getRobot().mouseMove(l.x + trimAndParseInt((String) getArgs()[0]),
				l.y + trimAndParseInt((String) getArgs()[1]));
	}
}