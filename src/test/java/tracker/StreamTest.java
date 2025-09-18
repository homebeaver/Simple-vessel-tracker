package tracker;

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

import javax.swing.SwingWorker;

import org.jdesktop.swingx.demos.table.OscarCandidate;
import org.jdesktop.swingx.demos.table.OscarDataParser;
import org.jdesktop.swingx.demos.table.OscarTableModel;

import io.aisstream.app.MessageHandler;
import io.github.homebeaver.aismodel.AisMessageTypes;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.MessageReader;
import io.github.homebeaver.aismodel.AisStreamMessage.MeldungenCallback;

public class StreamTest implements MeldungenCallback<AisStreamMessage> { // rename to AisStreamMessageTest
	
	public static void main(String[] args) throws URISyntaxException {
		URL url = StreamTest.class.getClassLoader().getResource("data/aisstream.txt");
		System.out.println("starting with " + url);
		// Resource-URL aus main
//		AisStreamMessage.liesUrl(url, new AisStreamMessage.ConsoleCallback());
		StreamTest st = new StreamTest();
		AisStreamMessage.liesUrl(url, st);
		st.report();
		
//		int cnt = 0;
////	    MessageHandler mh = new MessageHandler();
//	    MessageReader mr = new MessageReader();
//		try {
//			File file = new File(url.toURI());
////    		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//			BufferedReader reader = new BufferedReader(new FileReader(file));
////    		FileInputStream fis = new FileInputStream(file);
//			while (reader.ready()) {
//				String line = reader.readLine();
//				System.out.println(line);
//				//mh.processMessage(line);
//				//AisStreamMessage.fromJson(line);
////				AisStreamMessage asm =
//				mr.readMessage(line);
//				cnt++;
//			}
//			reader.close();
//		} catch (IOException | URISyntaxException e) {
//			System.out.println("Exeption " + e);
//		}
//		System.out.println("got " + cnt + " lines.");
//		System.out.println("got no of vessels " + mr.getMap().size() + ".");
//		System.out.println("vessels with track (more then 1 waypoints):");
//		mr.getMap().forEach( (k,v) -> {
//			if(v.size()>1) {
//				StringBuilder sb = new StringBuilder();
//				for (int i=0; i<v.size();i++) {
//					sb.append(v.get(i).getAisMessageType());
//					sb.append(',');
//				}
//				System.out.println("\tMMSI="+k +":"+v.size() 
//				+ " "+v.get(0).getMetaData().getShipName()
//				+ " "+sb.toString());
//			}
//		});
	}

	int lines = 0;
	int msgNull = 0;
	
	
	@Override
	public void ausgabeMeldung(AisStreamMessage msg) {
		lines++;
		if(msg==null) {
			msgNull++;
		} else {
			addMessage(msg);
		}
	}

	private Map<Integer, Integer> mmsiCounter = new HashMap<Integer, Integer>();
	private Map<AisMessageTypes, Integer> msgTypeCounter = new HashMap<AisMessageTypes, Integer>();

	public void addMessage(AisStreamMessage msg) {
		int key = msg.getMetaData().getMMSI();
		if (mmsiCounter.containsKey(key)) {
			int cnt = mmsiCounter.get(key);
			cnt++;
			mmsiCounter.replace(key, cnt);
		} else {
			mmsiCounter.put(key, 1);
		}
		
		AisMessageTypes msgType = msg.getAisMessageType();
		if (msgTypeCounter.containsKey(msgType)) {
			int cnt = msgTypeCounter.get(msgType);
			cnt++;
			msgTypeCounter.replace(msgType, cnt);
		} else {
			msgTypeCounter.put(msgType, 1);
		}
	}

	public void report() {
		System.out.println("got " + lines + " lines.");
		System.out.println("got " + msgNull + " null Messages.");
		msgTypeCounter.forEach((k, v) -> {
			System.out.println("\t" + k + ":" + v);
		});
		System.out.println("got no of vessels " + mmsiCounter.size() + ".");
		System.out.println("vessels with track (more then 1 waypoints):");
		mmsiCounter.forEach((k, v) -> {
			if (v > 1) {
				System.out.println("\tMMSI=" + k + ":" + v);
			}
		});
	}

}
