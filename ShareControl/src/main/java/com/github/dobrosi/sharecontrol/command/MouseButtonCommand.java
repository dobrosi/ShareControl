package com.github.dobrosi.sharecontrol.command;

import java.awt.event.InputEvent;

public interface MouseButtonCommand extends ICommand {

	default int convertMouseButton(int buttons) {
		switch (buttons) {
		case 1:
			return InputEvent.BUTTON1_DOWN_MASK;
		case 2:
			return InputEvent.BUTTON2_DOWN_MASK;
		case 3:
			return InputEvent.BUTTON3_DOWN_MASK;
		default:
			return InputEvent.BUTTON1_DOWN_MASK;
		}
	}
}