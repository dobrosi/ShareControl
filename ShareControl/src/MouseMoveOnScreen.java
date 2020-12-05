
import static java.lang.Integer.parseInt;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MouseMoveOnScreen {
	Logger logger = Logger.getAnonymousLogger();
	Point point, lastPoint;
	Robot robot;
	private DatagramSocket socket;
	private InetAddress address;
	private int port = 1979;
	private long d = System.currentTimeMillis();

	MouseMoveOnScreen(boolean controller, String host) throws AWTException, SocketException {
		printIpInfo();
		if (controller) {
			startController(host);
		} else {
			startReceiver();
		}
		robot = new Robot();
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (System.currentTimeMillis() - d > 180000) {
					System.exit(-1);
				}
				point = MouseInfo.getPointerInfo().getLocation();
				if (controller && !point.equals(lastPoint)) {
					sendLatestMouseMovement();
					robot.mouseMove(500, 500);
				}
			}
		};
		Timer timer = new Timer(1, al);
		timer.start();
	}

	private void printIpInfo() throws SocketException {
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		String info = "\n";
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				info += "IP: " + i.getHostAddress() + "\n";
			}
		}
		logger.log(Level.INFO, info);
	}

	private void startReceiver() {
		new Thread() {
			public void run() {
				try {
					DatagramSocket socket = new DatagramSocket(port);
					boolean running = true;
					byte[] buf = new byte[1024];

					while (running) {
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						socket.receive(packet);

						InetAddress address = packet.getAddress();
						int port = packet.getPort();
						packet = new DatagramPacket(buf, buf.length, address, port);
						String received = new String(packet.getData(), 0, packet.getLength());

						logger.log(Level.INFO, "Received: " + received);
						String[] points = received.split(",");

						Point p = MouseInfo.getPointerInfo().getLocation();
						robot.mouseMove(p.x + parseInt(points[0].trim()), p.y + parseInt(points[1].trim()));

						if (received.equals("end")) {
							running = false;
							continue;
						}

						socket.send(packet);
					}
					socket.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			}
		}.start();
		logger.log(Level.INFO, "Receiver started.");
	}

	private void startController(String host) {
		try {
			socket = new DatagramSocket();
			address = InetAddress.getByName(host);
		} catch (SocketException | UnknownHostException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		logger.log(Level.INFO, "Controller started.");
	}

	public void sendLatestMouseMovement() {
		if (lastPoint == null) {
			return;
		}
		String msg = String.format("%s,%s,", point.x - 500, point.y - 500);

		byte[] buf = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
		try {
			socket.send(packet);
			logger.log(Level.INFO, "Sent: " + msg);
		} catch (IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		lastPoint = point;

	}

	public static void main(String[] args) throws Exception {
		new MouseMoveOnScreen(Boolean.parseBoolean(args[0]), args[1]);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("Track Mouse On Screen");
				f.setBounds(100, 100, 100, 100);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				// f.pack();
				f.setLocationByPlatform(true);
				f.setVisible(true);
			}
		};
		SwingUtilities.invokeLater(r);
	}
}