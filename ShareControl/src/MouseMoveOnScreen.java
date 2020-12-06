
import static java.lang.Integer.parseInt;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
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

	Robot robot;
	String lastMsg;
	private DatagramSocket socket;
	private InetAddress address;
	private int port = 1979;
	private long d = System.currentTimeMillis();

	static boolean controller;

	static int released;

	static int pressed;

	static MulticastPublisher multicastPublisher = new MulticastPublisher();
	static MulticastReceiver multicastReceiver = new MulticastReceiver();

	MouseMoveOnScreen(boolean controller, String host) throws AWTException, SocketException {
		this.controller = controller;
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

				if (controller) {
					sendLatestMouseMovement();
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

						try {
							if (points[0].equalsIgnoreCase("M")) {
								Point p = MouseInfo.getPointerInfo().getLocation();
								int x = p.x + parseInt(points[1].trim());
								int y = p.y + parseInt(points[2].trim());
								int pressed = parseInt(points[3].trim());
								int released = parseInt(points[4].trim());
								robot.mouseMove(x, y);
								if (pressed > 0) {
									robot.mousePress(pressed == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON3_MASK);
								}
								if (released > 0) {
									robot.mouseRelease(
											released == 1 ? InputEvent.BUTTON1_MASK : InputEvent.BUTTON3_MASK);
								}
							} else if (points[0].equalsIgnoreCase("K")) {

							}
						} catch (Exception e) {
							// TODO: handle exception
						}
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
		Point point = MouseInfo.getPointerInfo().getLocation();
		String msg = String.format("M,%s,%s,%s,%s,", point.x - 500, point.y - 500, pressed, released);

		if (!msg.equals(lastMsg)) {
			byte[] buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			try {
				socket.send(packet);
				logger.log(Level.INFO, "Sent: " + msg);
			} catch (IOException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
			robot.mouseMove(500, 500);
			pressed = -1;
			released = -1;
			lastMsg = msg;
		}

	}

	public static void main(String[] args) throws Exception {

		new MouseMoveOnScreen(Boolean.parseBoolean(args[0]), args[1]);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("Track Mouse On Screen");
				f.addMouseListener(createMouseListener());
				f.setBounds(400, 400, 200, 200);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				if (controller) {
					BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
					Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0),
							"blank cursor");
					f.getContentPane().setCursor(blankCursor);
					f.setVisible(true);
				} else {
					f.setVisible(false);
				}
			}

			private MouseListener createMouseListener() {
				return new MouseListener() {

					@Override
					public void mousePressed(MouseEvent e) {
						pressed = e.getButton();
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						released = e.getButton();
						try {
							multicastPublisher.multicast("Hello!!!!");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub

					}

					@Override
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub

					}
				};

			}
		};
		SwingUtilities.invokeLater(r);
		if (!controller) {
			multicastReceiver.start();
		}
	}

}