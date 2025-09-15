package org.jxmapviewer.demos;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.MessageReader;
import io.github.homebeaver.aismodel.AisStreamMessage.MeldungenCallback;

/*
 * @param <T> the result type returned by this {@code SwingWorker's}
 *        {@code doInBackground} and {@code get} methods
 * @param <V> the type used for carrying out intermediate results by this
 *        {@code SwingWorker's} {@code publish} and {@code process} methods
 */
//public static final class TextDateiLesenSwingWorker extends SwingWorker<Boolean,String>
/* Auf Swing-Komponenten soll nur vom Swing Event Dispatch Thread (EDT) aus zugegriffen werden,
um Multithreading-Probleme zu vermeiden.
Andererseits sollen zeitaufwaendige Aufgaben nicht im EDT ausgefuehrt werden,
damit dieser nicht blockiert wird und das GUI bedienbar bleibt.
Der "SwingWorker" loest die Aufgabe, indem die zeitaufwaendigen Aufgaben in einen Extra-Background-Thread verlagert werden,
und ueber einen Kommunikationsmechanismus die Swing-Komponenten Thread-sicher asynchron im EDT manipuliert werden. */
public class MessageLoader extends SwingWorker<Boolean, AisStreamMessage> {

	private static final Logger LOG = Logger.getLogger(MessageLoader.class.getName());
	private static final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";
    private final URL url;
    private final MessageReader mr;
    private final List<AisStreamMessage> candidates = new ArrayList<AisStreamMessage>();
	private Map<Integer, List<AisStreamMessage>> map;
	private int cnt = 0;

    public MessageLoader(URL url, MessageReader mr) {
    	super();
        this.url = url;
        this.mr = mr;
		this.map = new HashMap<Integer, List<AisStreamMessage>>();
		this.cnt = 0;
    }

	/*
	 * Die "doInBackground()"-Methode wird in einem eigenen Background-Thread ausgefuehrt. 
	 * Sie darf nicht direkt Swing-Komponenten manipulieren.
	 */
	@Override
	protected Boolean doInBackground() throws Exception {
//   final class SwingMeldungenCallback implements DateiLeserMitCallback.MeldungenCallback<String>
		final class SwingMeldungenCallback implements AisStreamMessage.MeldungenCallback<AisStreamMessage> {
			/**
			 * Die "publish()"-Methode sendet Zwischenergebnis-Objekte an die "process()"-Methode, 
			 * in welcher Swing-Aktionen Thread-sicher asynchron im EDT ausgefuehrt werden.
			 */
			@Override
			public void ausgabeMeldung(AisStreamMessage s) {
				publish(s);
			}
		}

		/**
		 * Aufruf des eigentlichen Jobs. Zwischenergebnisse werden per Callback
		 * returniert. Das finale Return-Ergebnis kann in der "done()"-Methode per
		 * "get()" abgefragt werden.
		 */
//   return DateiLeserMitCallback.liesTextdatei( textdatei, charEncod, new SwingMeldungenCallback() );
		return AisStreamMessage.liesUrl(GITHUB_URL, new SwingMeldungenCallback());
	}
//	@Override
//	protected List<AisStreamMessage> doInBackground() throws Exception {
//		int cnt = 0;
////		try {
//			File file = new File(url.toURI());
//			LOG.info(file.getAbsolutePath());
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			while (reader.ready()) {
//				String line = reader.readLine();
//				LOG.info(line);
////                if (mr.getMap().size() % 3 == 0) {
////                    try { // slow it down so we can see progress :-)
////                        Thread.sleep(100);
////                    } catch (Exception ex) {
////                    }
////                }
//				AisStreamMessage asm = mr.readMessage(line); // kann null sein
//				if(asm!=null) {
//					candidates.add(asm);
//					publish(asm);
//					//setProgress(100 * mr.getMap().size() / 59); //expectedNumber);
//				}
//				cnt++;
//			}
//			reader.close();
////		} catch (IOException | URISyntaxException e) {
////			LOG.warning("Exeption " + e);
////		}
////		try {
////			AisStreamMessage asm = AisStreamMessage.fromJson(messageJson);
////			if (asm == null) {
////				return null;
////			}
////			int key = asm.metaData.getMMSI();
////			if (key == 0) {
////				throw new JSONException("Null key (MMSI==0) in " + asm.metaData);
////			}
////			if (!map.containsKey(key)) {
////				List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
////				map.put(key, waypoints); // empty List
////			} else {
////				// LOG.info(">>>>>>>>>>>>>>>>> "+key + "\n"+map.get(key).get(0).message + "\n" + asm.message);
////			}
////			map.get(key).add(asm);
////			return asm;
////		} catch (Exception e) {
////			LOG.warning("Error reading message: " + messageJson + " " + e);
////			return null;
////		}
//		return candidates;
//		//return mr.getMap();
//	}

    /** Die "done()"-Methode wird nach Beendigung der "doInBackground()"-Methode aufgerufen.
    Sie laeuft im EDT und kann Swing-Komponenten manipulieren. * /
@Override
protected void done()
{
   Boolean ret = Boolean.FALSE;
   try {
      / ** Abfrage der Ergebnisses der "doInBackground()"-Methode: * /
      ret = get();
   } catch( ExecutionException | InterruptedException | CancellationException ex ) { / * ok * / }
   if( textArea != null ) {
      if( ret != null && ret.booleanValue() ) {
         textArea.setForeground( new Color( 0x008800 ) );
      } else {
         textArea.setBackground( new Color( 0xFFFFEE ) );
         textArea.setForeground( Color.RED );
      }
   }
   if( button != null ) { button.setEnabled( true ); }
}
} */
	
	protected void done() {
		Boolean ret = Boolean.FALSE;
		try {
			// Abfrage der Ergebnisses der "doInBackground()"-Methode:
			ret = get();
			System.out.println("got " + cnt + " lines. res="+ret);
			System.out.println("got no of vessels " + map.size() + ".");
			System.out.println("vessels with track (more then 1 waypoints):");
			map.forEach( (k,v) -> {
				if(v.size()>1) {
					StringBuilder sb = new StringBuilder();
					for (int i=0; i<v.size();i++) {
						sb.append(v.get(i).getAisMessageType());
						sb.append(',');
					}
					System.out.println("\tMMSI="+k +":"+v.size() 
					+ " "+v.get(0).getMetaData().getShipName()
					+ " "+sb.toString());
				}
			});
		} catch (ExecutionException | InterruptedException | CancellationException ex) {
		}
	}

    /* Die "process()"-Methode empfaengt die ueber "publish()" uebergebenen Objekte.
    Sie laeuft im EDT und kann asynchron Swing-Komponenten manipulieren. * /
@Override
protected void process( List<String> chunks )
{
   if( chunks != null && textArea != null ) {
      for( String s : chunks ) {
         textArea.append( s + "\n" );
      }
   }
} */
    /**
     * Receives data chunks asynchronously on the EDT
     * 
     * @param chunks intermediate results to process
     */
	@Override
	protected void process(List<AisStreamMessage> chunks) {
		LOG.info("chunks#:"+chunks.size()); // XXX ? die Anzahl stimmt nicht
		chunks.forEach( msg -> {
			if(msg==null) {
				LOG.info("chunk is null");
			} else {
				int key = msg.getMetaData().getMMSI();
//				LOG.info(">>>>>>>>>>>>>>>"+msg.getAisMessageType() + " MMSI="+key);
				if (!map.containsKey(key)) {
					List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
					map.put(key, waypoints); // empty List waypoints 
				}
				map.get(key).add(msg);
			}
			cnt++;
		});
	}

}
