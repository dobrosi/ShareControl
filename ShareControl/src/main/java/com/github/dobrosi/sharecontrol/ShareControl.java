package com.github.dobrosi.sharecontrol;

import java.awt.Robot;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class ShareControl {
	public static Robot ROBOT;

	Logger logger = Logger.getAnonymousLogger();

	public static int PORT = 1979;

	public static void main(String[] args) throws Exception {
		ROBOT = new Robot();
		new ShareControl().start(Boolean.parseBoolean(args[0]), args[1]);
	}

	private void start(boolean controller, String host) throws UnknownHostException {
		if (controller) {
			new Controller().start(host);
		} else {
			new Receiver().start();
		}
	}

	public static void exitApplication() {
		System.exit(-1);
	}
}