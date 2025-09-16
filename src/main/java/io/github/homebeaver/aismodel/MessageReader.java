package io.github.homebeaver.aismodel;

import java.io.BufferedReader;
import java.io.File;
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

// XXX Singleton ?
public class MessageReader {

	private static final Logger LOG = Logger.getLogger(MessageReader.class.getName());

	private static final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";
	/** Ausfuehrung als Konsolenprogramm (ohne Swing-GUI). */
	public static void mainX(String[] args) {
		AisStreamMessage.liesUrl(GITHUB_URL, new AisStreamMessage.ConsoleCallback());
	}
	public static void mainXX(String[] args) {
		URL url = MessageReader.class.getClassLoader().getResource("data/aisstream.txt");
		// Resource-URL aus main
		AisStreamMessage.liesUrl(url, new AisStreamMessage.ConsoleCallback());
	}
	public static void main(String[] args) {
		String textdatei = ( args != null && args.length > 0 ) ? args[0] : "src/test/java/aisstream.txt";
		System.out.println("starting with " + textdatei);
		try {
			BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( textdatei ) ) );
			AisStreamMessage.liesUrl(in, new AisStreamMessage.ConsoleCallback());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Konkrete Implementierung der Callback-Klasse zur Ausgabe von
	 * Zwischenergebnissen auf der Konsole.
	 */
	public static class KonsoleMeldungenCallback implements AisStreamMessage.MeldungenCallback<String> {
		@Override
		public void ausgabeMeldung(String s) {
			System.out.println(s);
		}
	}

	   public static void mainXXX( String[] args )
	   {
	      String textdatei = ( args != null && args.length > 0 ) ? args[0] : "src/test/java/de/torsten/horn/DateiLeserMitCallback.java";
	      String charEncod = ( args != null && args.length > 1 ) ? args[1] : "ISO-8859-1";
	      liesTextdatei( textdatei, charEncod, new KonsoleMeldungenCallback() );
	   }
	   
	   /** "Arbeitsmethode" mit Callback zum Returnieren von Zwischenergebnissen waehrend der Verarbeitung. */
	   public static Boolean liesTextdatei( String textdatei, String charEncod, AisStreamMessage.MeldungenCallback<String> meldungenCallback )
	   {
	      String ausgabeAbgrenzung = "------------------------------------------------------------------";
	      meldungenCallback.ausgabeMeldung( "\n" + ausgabeAbgrenzung );

	      if( textdatei == null || textdatei.trim().length() == 0 ) {
	         meldungenCallback.ausgabeMeldung( "Fehler: Dateiname ist leer." );
	         return Boolean.FALSE;
	      }
	      if( !(new File( textdatei )).exists() ) {
	         meldungenCallback.ausgabeMeldung( "Fehler: Datei " + textdatei + " existiert nicht." );
	         return Boolean.FALSE;
	      }

	      meldungenCallback.ausgabeMeldung( "Textdateianzeige" );
	      meldungenCallback.ausgabeMeldung( "Textdatei:        " + textdatei );
	      meldungenCallback.ausgabeMeldung( "Zeichenkodierung: " + charEncod );
	      meldungenCallback.ausgabeMeldung( ausgabeAbgrenzung );

	      try( BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( textdatei ), charEncod ) ) ) {
	         String line;
	         while( (line = in.readLine()) != null ) {
	            meldungenCallback.ausgabeMeldung( line );
	            // Zur Simulation berechnungsintensiver Aufgaben:
	            Thread.sleep( 10 );
	         }
	      } catch( Exception ex ) {
	         meldungenCallback.ausgabeMeldung( "Fehler-Exception: " + ex.getMessage() + "\n" + ex.toString() );
	         return Boolean.FALSE;
	      }

	      meldungenCallback.ausgabeMeldung( ausgabeAbgrenzung );
	      meldungenCallback.ausgabeMeldung( "Fertig." );
	      meldungenCallback.ausgabeMeldung( ausgabeAbgrenzung );
	      return Boolean.TRUE;
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
