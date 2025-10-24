package io.github.homebeaver.aismodel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.json.JSONException;

import io.github.homebeaver.aismodel.AisStreamMessage.AisStreamCallback;

// XXX Singleton ?
public class MessageReader {

	private static final Logger LOG = Logger.getLogger(MessageReader.class.getName());

	private static final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";
	/** Ausfuehrung als Konsolenprogramm (ohne Swing-GUI). */
	public static void mainX(String[] args) {
		AisStreamMessage.liesUrl(GITHUB_URL, new AisStreamMessage.ConsoleCallback());
	}
	public static void main(String[] args) {
		// diese Testdaten sind nicht in github
		String textdatei = ( args != null && args.length > 0 ) ? args[0] : "src/test/java/aisstream.txt";
		System.out.println("start mit " + textdatei);
		try {
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( textdatei ) ) );
			AisStreamMessage.liesUrl(in, new AisStreamMessage.ConsoleCallback());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void mainXX(String[] args) {
		URL url = MessageReader.class.getClassLoader().getResource("data/aisstream.txt");
		// Resource-URL aus main
		AisStreamMessage.liesUrl(url, new AisStreamMessage.ConsoleCallback());
	}

	/*
	 * Konkrete Implementierung der Callback-Klasse zur Ausgabe von
	 * Zwischenergebnissen auf der Konsole.
	 */
	// zählt die MMSI XXX das wird nicht mehr verwendet TODO überprüfen
	public static class KonsoleMeldungenCallback implements AisStreamMessage.AisStreamCallback<String> {
		@Override
		public void outMessage(String s) {
			System.out.println(s);
		}
	}

	private Map<Integer, List<AisStreamMessage>> map = null;

	public MessageReader() {
		this.map = new HashMap<Integer, List<AisStreamMessage>>();
	}

	// Map <MMSI , track>
	public Map<Integer, List<AisStreamMessage>> getMap() {
		return this.map;
	}

	public AisStreamMessage readMessage(String messageJson) {
		try {
			AisStreamMessage asm = AisStreamMessage.fromJson(messageJson);
			if (asm == null) {
				return null;
			}
			int key = asm.metaData.getMMSI();
			if (key == 0) {
				throw new JSONException("Null key (MMSI==0) in " + asm.metaData);
			}
			if (!map.containsKey(key)) {
				List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
				map.put(key, waypoints); // empty List
			} else {
				// LOG.info(">>>>>>>>>>>>>>>>> "+key + "\n"+map.get(key).get(0).message + "\n" + asm.message);
			}
			map.get(key).add(asm);
			return asm;
		} catch (Exception e) {
			LOG.warning("Error reading message: " + messageJson + " " + e);
			return null;
		}
	}

}
