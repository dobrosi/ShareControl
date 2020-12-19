package com.github.dobrosi.sharecontrol;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.github.dobrosi.sharecontrol.command.ICommand;
import com.github.dobrosi.sharecontrol.command.KeyboardKeyPressedCommand;
import com.github.dobrosi.sharecontrol.command.KeyboardKeyReleaseCommand;
import com.github.dobrosi.sharecontrol.command.MouseKeyPressedCommand;
import com.github.dobrosi.sharecontrol.command.MouseKeyReleaseCommand;
import com.github.dobrosi.sharecontrol.command.MouseWheelCommand;

public class Controller {
	private Logger logger = Logger.getAnonymousLogger();

	private MessageSenderService messageSenderService;

	private DatagramSocket socket;

	public void start(String host) throws UnknownHostException {
		startGui();
		startSocket(InetAddress.getByName(host));
		startTimer();
		logger.log(Level.INFO, "Controller started.");
	}

	private void startGui() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("ShareControl");
				f.addMouseListener(createMouseListener());
				f.addKeyListener(createKeyListener());
				f.addMouseWheelListener(createMouseWheelListener());
				f.setBounds(50, 50, 200, 200);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
				Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0),
						"blank cursor");
				f.getContentPane().setCursor(blankCursor);
				f.setVisible(true);
			}

			private MouseListener createMouseListener() {
				return new MouseListener() {
					@Override
					public void mousePressed(MouseEvent e) {
						addMouseKeyPressedCommand(e);
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						addMouseKeyReleasedCommand(e);
					}

					@Override
					public void mouseEntered(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
					}

					@Override
					public void mouseClicked(MouseEvent e) {
					}
				};

			}

			private KeyListener createKeyListener() {
				return new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {
					}

					@Override
					public void keyReleased(KeyEvent e) {
						addKeyboardKeyReleaseCommand(e);
					}

					@Override
					public void keyPressed(KeyEvent e) {
						addKeyboardKeyPressedCommand(e);
					}
				};
			}

			private MouseWheelListener createMouseWheelListener() {
				return e -> addMouseWheelCommand(e);
			}

			private <T extends ICommand> void addEvent(Class<T> class1, Object... args) {
				messageSenderService.addCommand(class1, args);
			}

			private void addMouseKeyPressedCommand(MouseEvent e) {
				addEvent(MouseKeyPressedCommand.class, e.getButton());
			}

			private void addMouseKeyReleasedCommand(MouseEvent e) {
				addEvent(MouseKeyReleaseCommand.class, e.getButton());
			}

			private void addKeyboardKeyPressedCommand(KeyEvent e) {
				addEvent(KeyboardKeyPressedCommand.class, e.getKeyCode());
			}

			private void addKeyboardKeyReleaseCommand(KeyEvent e) {
				addEvent(KeyboardKeyReleaseCommand.class, e.getKeyCode());
			}

			private void addMouseWheelCommand(MouseWheelEvent e) {
				addEvent(MouseWheelCommand.class, e.getWheelRotation());
			}

		};
		SwingUtilities.invokeLater(r);
	}

	private void startSocket(InetAddress address) {
		try {
			messageSenderService = new MessageSenderService(address);
			socket = new DatagramSocket(ShareControl.PORT);
		} catch (SocketException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

	private void startTimer() {
		new Timer(1, e -> messageSenderService.sendMessages(socket)).start();
	}
}
