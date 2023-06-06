package io.github.runescarpet.loader.impl.runescape.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.github.runescarpet.loader.impl.runescape.OSRSGameProvider;

/**
 * Applet stub,
 */
public class CustomAppletStub implements AppletStub, AppletContext {
		private final OSRSGameProvider provider;

		private static final Map<String, String> JAGEX_PROPERTIES = new HashMap<>();

		static {
				JAGEX_PROPERTIES.put("14", "0");
				JAGEX_PROPERTIES.put("7", "0");
				JAGEX_PROPERTIES.put("11", "https://auth.jagex.com/");
				JAGEX_PROPERTIES.put("25", "214");
				JAGEX_PROPERTIES.put("17", "http://www.runescape.com/g=oldscape/slr.ws?order=LPWM");
				JAGEX_PROPERTIES.put("15", "0");
				JAGEX_PROPERTIES.put("20", "https://social.auth.jagex.com/");
				JAGEX_PROPERTIES.put("5", "1");
				JAGEX_PROPERTIES.put("2", "https://payments.jagex.com/");
				JAGEX_PROPERTIES.put("6", "0");
				JAGEX_PROPERTIES.put("12", "505");
				JAGEX_PROPERTIES.put("19", "196515767263-1oo20deqm6edn7ujlihl6rpadk9drhva.apps.googleusercontent.com");
				JAGEX_PROPERTIES.put("9", "ElZAIrq5NpKN6D3mDdihco3oPeYN2KFy2DCquj7JMmECPmLrDP3Bnw");
				JAGEX_PROPERTIES.put("8", "true");
				JAGEX_PROPERTIES.put("10", "5");
				JAGEX_PROPERTIES.put("21", "0");
				JAGEX_PROPERTIES.put("3", "true");
				JAGEX_PROPERTIES.put("16", "false");
				JAGEX_PROPERTIES.put("4", "1");
				JAGEX_PROPERTIES.put("28", "https://account.jagex.com/");
				JAGEX_PROPERTIES.put("18", "");
				JAGEX_PROPERTIES.put("13", ".runescape.com");
		}

		public CustomAppletStub(OSRSGameProvider provider) {
				this.provider = provider;
		}

		@Override
		public boolean isActive() {
				return true;
		}

		@Override
		public URL getDocumentBase() {
				try {
						return new URL("http://oldschool205.runescape.com/");
//						return new URL(this.provider.gameProperties.getProperty("codebase"));
				} catch (MalformedURLException e) {
						throw new RuntimeException("invalid codebase url", e);
				}
		}

		@Override
		public URL getCodeBase() {
				try {
						return new URL("http://oldschool205.runescape.com/");
//						return new URL(this.provider.gameProperties.getProperty("codebase"));
				} catch (MalformedURLException e) {
						throw new RuntimeException("invalid codebase url", e);
				}
		}

		@Override
		public String getParameter(String name) {
				// TODO properly handle properties from the game config (stored by launcher)
				return JAGEX_PROPERTIES.get(name);
//				return this.provider.gameProperties.getProperty(name);
		}

		@Override
		public AppletContext getAppletContext() {
				return this;
		}

		@Override
		public void appletResize(int width, int height) {

		}

		@Override
		public AudioClip getAudioClip(URL url) {
				throw new UnsupportedOperationException();
		}

		@Override
		public Image getImage(URL url) {
				throw new UnsupportedOperationException();
		}

		@Override
		public Applet getApplet(String name) {
				throw new UnsupportedOperationException();
		}

		@Override
		public Enumeration<Applet> getApplets() {
				throw new UnsupportedOperationException();
		}

		@Override
		public void showDocument(URL url) {
				// TODO
		}

		@Override
		public void showDocument(URL url, String target) {
				// TODO
		}

		@Override
		public void showStatus(String status) {
				throw new UnsupportedOperationException();
		}

		@Override
		public void setStream(String key, InputStream stream) {
				throw new UnsupportedOperationException();
		}

		@Override
		public InputStream getStream(String key) {
				throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<String> getStreamKeys() {
				throw new UnsupportedOperationException();
		}
}
