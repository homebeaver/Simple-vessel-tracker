package io.github.homebeaver.aisview;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import io.aisstream.app.API_Key_Provider;
import io.aisstream.app.App;
import io.github.homebeaver.aismodel.AisStreamKeyProvider;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.AisStreamWebsocketClient;

/* Auf Swing-Komponenten soll nur vom Swing Event Dispatch Thread (EDT) aus zugegriffen werden,
 * um Multithreading-Probleme zu vermeiden.
 * Andererseits sollen zeitaufwaendige Aufgaben nicht im EDT ausgefuehrt werden,
 * damit dieser nicht blockiert wird und das GUI bedienbar bleibt.
 * Der "SwingWorker" loest die Aufgabe, 
 * indem die zeitaufwaendigen Aufgaben in einen Extra-Background-Thread verlagert werden,
 * und ueber einen Kommunikationsmechanismus die Swing-Komponenten Thread-sicher 
 * asynchron im EDT manipuliert werden.
 * 
 * MessageLoader muss als Subclass von SwingWorker doInBackground implementieren.
 */
/**
 * @param <T> the result type returned by this {@code SwingWorker's}
 *        {@code doInBackground} and {@code get} methods
 * @param <V> the type used for carrying out intermediate results by this
 *        {@code SwingWorker's} {@code publish} and {@code process} methods
 */
public class MessageLoader extends SwingWorker<Boolean, AisStreamMessage> {

	private static final Logger LOG = Logger.getLogger(MessageLoader.class.getName());
	private String testdata;
	private String boundingBox;
	private URL url;
	private final AisMapKit amv;
	private final JLabel counter;
	private long millis = -1;
	private int cnt = 0;

	public MessageLoader(String fileUrl, AisMapKit amv, JLabel counter) {
		super();
		this.testdata = fileUrl;
		this.amv = amv;
		this.counter = counter;
		this.cnt = 0; // zählt auch die null-Nachrichten
	}

	public MessageLoader(URL url, AisMapKit amv, JLabel counter, String boundingBox) {
		super();
		this.url = url;
		this.amv = amv;
		this.counter = counter;
		this.boundingBox = boundingBox;
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
		final class SwingCallback implements AisStreamMessage.AisStreamCallback<AisStreamMessage> {
			/**
			 * Die "publish()"-Methode sendet Zwischenergebnis-Objekte an die "process()"-Methode, 
			 * in welcher Swing-Aktionen Thread-sicher asynchron im EDT ausgefuehrt werden.
			 */
			@Override
			public void outMessage(AisStreamMessage s) {
				if (isCancelled()) {
					LOG.info("canceled.");
					return;
				}
				if (millis>0) try {
					Thread.sleep( millis ); // XXX slow down
				} catch (InterruptedException e) { // sleep throws this when thread is canceled
					// TODO Auto-generated catch block 
					e.printStackTrace();
					return;
				}
				publish(s);
			}
		}

		if (testdata!=null) try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(testdata)));
			return AisStreamMessage.liesUrl(in, new SwingCallback());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
			
		if (url!=null) try {
			InputStream input = url.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			return AisStreamMessage.liesUrl(in, new SwingCallback());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		/*
		 * Aufruf des eigentlichen Jobs. Zwischenergebnisse werden per Callback returniert. 
		 * Das finale Return-Ergebnis kann in der "done()"-Methode per "get()" abgefragt werden.
		 */
		SwingCallback smc = new SwingCallback();
		AisStreamKeyProvider keyProvider = AisStreamKeyProvider.getInstance();
		if(keyProvider.getKey()==null) {
			keyProvider.run(); // key aus konsole
		}
		AisStreamWebsocketClient client = new AisStreamWebsocketClient(true, keyProvider, boundingBox) {
			@Override
			public void onMessage(ByteBuffer message) {
				String jsonString = StandardCharsets.UTF_8.decode(message).toString();
				//System.out.println(jsonString);
				if (isCancelled()) {
					LOG.info("canceled.");
					this.close();
					return;
				}
				smc.outMessage(AisStreamMessage.fromJson(jsonString));
			}
		};
		int ms = 600000; // 10min
		Thread.sleep( ms );
		LOG.info("Ende nach "+ms/60000+"min.");
		client.close();
		return true;
	}

	/*
	 * Die "done()"-Methode wird nach Beendigung der "doInBackground()"-Methode aufgerufen. 
	 * Sie laeuft im EDT und kann Swing-Komponenten manipulieren.
	 */
	@Override
	protected void done() {
		Boolean ret = Boolean.FALSE;
		String reason = "";
		try {
			// Abfrage der Ergebnisses der "doInBackground()"-Methode:
			ret = get();
//			System.out.println("got " + cnt + " lines. res=" + ret);
		} catch (CancellationException e) {
			System.out.println(">>>>>>>>>>>>" + e);
			reason = "Cancellation";
		} catch (ExecutionException | InterruptedException e) {
			System.out.println(">>>>>>>>>>>>" + e);
			reason = e.getMessage();
		}
		System.out.println("got " + cnt + " lines. res=" + ret + " " + reason + " "+this.getState());
	}

	/*
	 * Die "process()"-Methode empfaengt die ueber "publish()" uebergebenen Objekte.
	 * Sie laeuft im EDT und kann asynchron Swing-Komponenten manipulieren. 
	 */ 
	/**
	 * Receives data chunks asynchronously on the EDT
	 * 
	 * @param chunks intermediate results to process
	 */
	@Override
	protected void process(List<AisStreamMessage> chunks) {
//		LOG.info("chunks#:" + chunks.size());
		chunks.forEach(msg -> {
			if (msg == null) {
				LOG.fine("chunk is null");
			} else {
				amv.addMessage(msg);
			}
			cnt++;
		});
		LOG.fine("chunks#:" + chunks.size()+"/"+cnt+"/"+amv.getNoOfVessels());
		counter.setText(""+amv.getNoOfVessels());
	}

}
