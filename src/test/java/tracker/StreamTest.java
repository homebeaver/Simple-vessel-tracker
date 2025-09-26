package tracker;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.github.homebeaver.aismodel.AisMessage;
import io.github.homebeaver.aismodel.AisMessageTypes;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.AisStreamMessage.MeldungenCallback;
import io.github.homebeaver.aismodel.PositionReport;

public class StreamTest implements MeldungenCallback<AisStreamMessage> { // rename to AisStreamMessageTest
	
	static final String TEST_DATA = "data/aisstream.txt";
	static final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/"+TEST_DATA;

	public static void main(String[] args) throws URISyntaxException {
//		URL url = StreamTest.class.getClassLoader().getResource(TEST_DATA);
		URL url = StreamTest.class.getClassLoader().getResource("aisstream.txt");
		StreamTest st = new StreamTest();
//		AisStreamMessage.liesUrl(url, st);
		AisStreamMessage.liesUrl(GITHUB_URL, st);
		st.report();
		
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

	// mmsiCounter aka msg counter per ship
	// mmsi -> [NoAllMsges, PositionReports, ShipStaticData]
	private Map<Integer, Integer[]> mmsiCounter = new HashMap<Integer, Integer[]>();
	private Map<AisMessageTypes, Integer> msgTypeCounter = new HashMap<AisMessageTypes, Integer>();

	public void addMessage(AisStreamMessage msg) {
		AisMessageTypes msgType = msg.getAisMessageType();
		if (msgType == AisMessageTypes.POSITIONREPORT) {
			AisMessage amsg = msg.getAisMessage();
			if (amsg instanceof PositionReport pr) {
				if(msg.getMetaData().getLatitude().equals(pr.getLatitude()) 
						&& msg.getMetaData().getLongitude().equals(pr.getLongitude())) {
					// expected to be equal
				} else {
					System.out.println("NOT equal " + msg.getMetaData() + " and "+pr);
				}
			}
		}
		int mmsi = msg.getMetaData().getMMSI();
		if (mmsiCounter.containsKey(mmsi)) {
			Integer cnt[] = mmsiCounter.get(mmsi);
			cnt[0]++;
			if (msgType == AisMessageTypes.POSITIONREPORT) cnt[1]++;
			if (msgType == AisMessageTypes.SHIPSTATICDATA) cnt[2]++;
			mmsiCounter.replace(mmsi, cnt);
		} else {
			Integer cnt[] = new Integer[3];
			cnt[0] = 1;
			cnt[1] = (msgType == AisMessageTypes.POSITIONREPORT) ? 1 : 0;
			cnt[2] = (msgType == AisMessageTypes.SHIPSTATICDATA) ? 1 : 0;
			mmsiCounter.put(mmsi, cnt);
		}
		
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
			if (v[0] > 1) {
//				System.out.println("\tMMSI=" + k + ":" + v[0] + " POSITIONREPORTs:"+v[1]+ " SHIPSTATICDATA:"+v[2]);
				System.out.println("\tMMSI=" + k + ":" + " SHIPSTATICDATA:"+v[2] + " POSITIONREPORTs:"+v[1]
						+ (v[0]==v[1]+v[2] ? "" : " other:"+(v[0]-v[1]-v[2]))
						);
			}
		});
	}

}
