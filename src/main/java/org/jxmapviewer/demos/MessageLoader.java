package org.jxmapviewer.demos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import io.github.homebeaver.aismodel.AisStreamMessage;

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
	private String testdata;
	private URL url;
    private final AisMapViewer amv;
//    private final List<AisStreamMessage> candidates = new ArrayList<AisStreamMessage>();
//	private Map<Integer, List<AisStreamMessage>> map;
    private long millis = 10; // -1;
	private int cnt = 0;

    public MessageLoader(String fileUrl, AisMapViewer amv) {
    	super();
        this.testdata = fileUrl;
        this.amv = amv;
//		this.map = new HashMap<Integer, List<AisStreamMessage>>();
		this.cnt = 0; // zählt auch die null-Nachrichten
    }
    public MessageLoader(URL url, AisMapViewer amv) {
    	super();
        this.url = url;
        this.amv = amv;
//		this.map = new HashMap<Integer, List<AisStreamMessage>>();
		this.cnt = 0; // zählt auch die null-Nachrichten
    }

    public void setSleep(long millis) {
    	this.millis = millis;
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
				if (millis>0) try {
					Thread.sleep( millis ); // XXX slow down
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				publish(s);
			}
		}

		if (testdata!=null) try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(testdata)));
			return AisStreamMessage.liesUrl(in, new SwingMeldungenCallback());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
			
		if (url!=null) try {
			InputStream input = url.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			return AisStreamMessage.liesUrl(in, new SwingMeldungenCallback());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		/**
		 * Aufruf des eigentlichen Jobs. Zwischenergebnisse werden per Callback returniert. 
		 * Das finale Return-Ergebnis kann in der "done()"-Methode per "get()" abgefragt werden.
		 */
//		return AisStreamMessage.liesUrl(GITHUB_URL, new SwingMeldungenCallback());
//		return AisStreamMessage.liesUrl(testdata, new SwingMeldungenCallback());
//		Exeption java.net.MalformedURLException: no protocol: src/test/java/aisstream.txt
		return false; // TODO
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
//			System.out.println("got no of vessels " + map.size() + ".");
//			System.out.println("vessels with track (more then 1 waypoints):");
//			map.forEach( (k,v) -> {
//				if(v.size()>1) {
//					StringBuilder sb = new StringBuilder();
//					for (int i=0; i<v.size();i++) {
//						sb.append(v.get(i).getAisMessageType());
//						sb.append(',');
//					}
//					System.out.println("\tMMSI="+k +":"+v.size() 
//					+ " "+v.get(0).getMetaData().getShipName()
//					+ " "+sb.toString());
//				}
//			});
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
				LOG.fine("chunk is null");
			} else {
//				int key = msg.getMetaData().getMMSI();
////				LOG.info(">>>>>>>>>>>>>>>"+msg.getAisMessageType() + " MMSI="+key);
//				if (!map.containsKey(key)) {
//					List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
//					map.put(key, waypoints); // empty List waypoints 
//				}
//				map.get(key).add(msg);
				amv.addMessage(msg);
			}
			cnt++;
			//tableRows.setText("" + cnt);
		});
	}

}
