package io.github.runescarpet.loader.impl.runescape.applet;

import io.github.runescarpet.loader.impl.runescape.OSRSGameProvider;

import net.fabricmc.loader.impl.FormattedException;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class AppletFrame extends Frame implements WindowListener {
		private Applet osrsApplet;

		public AppletFrame(String title) {
				super(title);
				this.addWindowListener(this);
		}

		public void launch(ClassLoader loader, OSRSGameProvider provider) {
				// Initialize and configure applet.
				MethodHandle ctor;

				try {
						Class<?> c = loader.loadClass(provider.clientClass);
						ctor = MethodHandles.lookup().findConstructor(c, MethodType.methodType(void.class));
				} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
						throw FormattedException.ofLocalized("exception.osrs.missingCtor", e);
				}

				// Construct applet, configure and run.
				try {
						this.osrsApplet = (Applet) ctor.invoke();
				} catch (Throwable th) {
						throw FormattedException.ofLocalized("exception.osrs.generic", th);
				}

				// Configure panel.
				this.osrsApplet.setStub(new CustomAppletStub(provider));

				// Register applet within frame.
				this.add(this.osrsApplet);
				this.pack();
				this.setLocationRelativeTo(null);
				this.setResizable(true);
				this.validate();

				// Launch!
				this.osrsApplet.init();
				this.osrsApplet.start();
				this.setVisible(true);
		}

		@Override
		public void windowOpened(WindowEvent e) {

		}

		@Override
		public void windowClosing(WindowEvent e) {
				// Destroy the applet.
				if (this.osrsApplet != null) {
						this.osrsApplet.stop();
						this.osrsApplet.destroy();
				}
		}

		@Override
		public void windowClosed(WindowEvent e) {

		}

		@Override
		public void windowIconified(WindowEvent e) {

		}

		@Override
		public void windowDeiconified(WindowEvent e) {

		}

		@Override
		public void windowActivated(WindowEvent e) {

		}

		@Override
		public void windowDeactivated(WindowEvent e) {

		}
}
