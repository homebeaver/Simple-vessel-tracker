package io.github.homebeaver.aismodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.homebeaver.aismodel.AisStreamMessage.AisStreamCallback;

public class AisMessageTest {

	private static final Logger LOG = Logger.getLogger(AisMessageTest.class.getName());

	static AisStreamMessageTest asmt;

	static class AisStreamMessageTest implements AisStreamCallback<AisStreamMessage> {

		int lines = 0; // == messages
		int msgNull = 0; // == UnknownMessage
		Map<Integer, AisStreamMessage> msgByMessageID = new HashMap<>();
		List<AisStreamMessage> listOfMsg = new Vector<>();
		Map<AisMessageTypes, List<AisStreamMessage>> msgByType = new HashMap<>();

		@Override
		public void outMessage(AisStreamMessage msg) {
			lines++;
			if (msg == null) {
				LOG.info("line " + lines + " is null");
				msgNull++;
			} else {
				assert listOfMsg.add(msg);
				Integer mid = msg.message.getMessageID();
				LOG.info("MessageID=" + mid + ", messageType:" + msg.messageType);
				AisStreamMessage old = msgByMessageID.put(mid, msg);
				if (old != null) {
					// msg überschreibt old
				}
			}
		}

	}

	@BeforeClass
	public static void staticSetup() {
		asmt = new AisStreamMessageTest();
		LOG.fine("AisStreamMessageTest:" + asmt);
		try {
			// in liesUrl wird outMessage gerufen
//			AisStreamMessage.liesUrl(new FileInputStream("src/test/resources/data/global.txt"), asmt);
			AisStreamMessage.liesUrl(new FileInputStream("src/test/java/aisstream2.txt"), asmt);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (AisStreamMessage m : asmt.listOfMsg) {
			asmt.msgByType.computeIfAbsent(m.getAisMessageType(), k -> new ArrayList<>()).add(m);
		}
		asmt.listOfMsg.clear();
		asmt.msgByType.forEach( (type, v) -> {
			System.out.println("#"+v.size() + "\t "+type);
		});
		System.out.println("staticSetup fertig, types#="+asmt.msgByType.size());
/* Dover:
#  54	 BinaryAcknowledge
# 201	 BaseStationReport
# 617	 ShipStaticData
#   3	 ExtendedClassBPositionReport
# 884	 StandardClassBPositionReport
#3438	 PositionReport
# 106	 AidsToNavigationReport
# 666	 AddressedBinaryMessage
# 833	 DataLinkManagementMessage
# 433	 StaticDataReport
#   4	 Interrogation
staticSetup fertig, types#=11

Global:
# 3054	 AidsToNavigationReport
# 7916	 DataLinkManagementMessage
#  351	 BinaryAcknowledge
#11717	 ShipStaticData
# 1243	 StandardSearchAndRescueAircraftReport
#   16	 AddressedSafetyMessage
#  546	 AssignedModeCommand
#  241	 ChannelManagement
#14416	 StandardClassBPositionReport
#    4	 SafetyBroadcastMessage
#  285	 GnssBroadcastBinaryMessage
#61149	 PositionReport
#  430	 Interrogation
#    7	 CoordinatedUTCInquiry
# 2836	 BaseStationReport
# 7169	 StaticDataReport
#   32	 ExtendedClassBPositionReport
# 1224	 AddressedBinaryMessage
staticSetup fertig, types#=18
 */
	}

	@Before
	public void setup() {
		LOG.fine("setup fertig");
	}

	@Test
	public void testCounter() {
		LOG.info("I expect 31 messages, one per line...");
		Assert.assertEquals(31, asmt.lines);
		Assert.assertEquals(2, asmt.msgNull);
		Assert.assertEquals(22, asmt.msgByMessageID.size());
		Assert.assertEquals(18, asmt.msgByType.size());

		Assert.assertTrue(asmt.msgByMessageID.containsKey(1)); // PositionReport 3x
		Assert.assertTrue(asmt.msgByMessageID.containsKey(2));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(3));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(4)); // BaseStationReport
		Assert.assertTrue(asmt.msgByMessageID.containsKey(5));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(6));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(7)); // BinaryAcknowledge
		// 8 BINARYBROADCASTMESSAGE - noch keine gefunden
		Assert.assertTrue(asmt.msgByMessageID.containsKey(9)); // StandardSearchAndRescueAircraftReport
		Assert.assertTrue(asmt.msgByMessageID.containsKey(10));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(11)); // BaseStationReport
		Assert.assertTrue(asmt.msgByMessageID.containsKey(12));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(13)); // BinaryAcknowledge
		Assert.assertTrue(asmt.msgByMessageID.containsKey(14));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(15));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(16));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(17));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(18));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(19));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(20));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(21));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(22));
		// 23 GROUPASSIGNMENTCOMMAND - noch keine gefunden
		Assert.assertTrue(asmt.msgByMessageID.containsKey(24)); // StaticDataReport
		// welche MessageID fehlen
		Assert.assertFalse(asmt.msgByMessageID.containsKey(8));
		Assert.assertFalse(asmt.msgByMessageID.containsKey(23));
		Assert.assertFalse(asmt.msgByMessageID.containsKey(25));
		Assert.assertFalse(asmt.msgByMessageID.containsKey(26));
		Assert.assertFalse(asmt.msgByMessageID.containsKey(27));
	}

	@Test
	public void testUserId() {
		LOG.info("The user ID should be the MMSI...");
		asmt.msgByMessageID.forEach((k, msg) -> testUserId(msg) );
		asmt.msgByType.forEach((k, msgList) -> {
			msgList.forEach( msg -> testUserId(msg) );
		});
	}
	private void testUserId(AisStreamMessage msg) {
		Integer mmsi = msg.metaData.getMMSI();
		Integer userId =  msg.message.getUserID();
		Assert.assertEquals(mmsi, userId);
	}
	
	@Test
	public void testTtimeUtc() {
		LOG.info("extract MetaData.timeUtc to LocalDateTime...");
		asmt.msgByMessageID.forEach((k, msg) -> testTtimeUtc(msg) );
		asmt.msgByType.forEach((k, msgList) -> {
			msgList.forEach( msg -> testTtimeUtc(msg) );
		});
	}
	private void testTtimeUtc(AisStreamMessage msg) {
		String timeUtc = msg.metaData.getTimeUtc();
		LocalDateTime dt = null;
		try {
			dt = MetaData.convertStringToLocalDateTime(timeUtc);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String hms = String.format("%02d:%02d:%02d", dt.getHour(), dt.getMinute(), dt.getSecond());
		LOG.fine(msg.metaData.getTimeUtc() + " == "+dt.toLocalDate()+" "+dt.toLocalTime()+" "+hms);
		Assert.assertTrue(timeUtc.startsWith(""+dt.toLocalDate()+" "+hms));
		// Die nanos sind mit oder ohne führende Nullen, Beispiele
		// 2025-10-30 20:13:32.089506495 +0000 UTC
		// 2025-10-30 19:42:57.13756116 +0000 UTC
	}

	@Test
	public void testPositionReports() {
		LOG.info("Longitude+Latitude equals to MetaData and COG...");
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=1 && k!=2 && k!=3 && k!=18 && k!=19) return;
			// 1,2,3 = PositionReport
			// 18 = Standard position report for Class B shipborne mobile equipment
			// 19 = Extended Class B equipment position report
			if (k==19) {
				testExtendedClassBPositionReport(msg);
			} else if (k==18) {
				testStandardClassBPositionReport(msg);
			} else {
				testPositionReport(msg);
			}
		});
		asmt.msgByType.forEach((k, msgList) -> {
			if (k==AisMessageTypes.EXTENDEDCLASSBPOSITIONREPORT) {
				msgList.forEach( msg -> testExtendedClassBPositionReport(msg) );
			}
			else if (k==AisMessageTypes.STANDARDCLASSBPOSITIONREPORT) {
				msgList.forEach( msg -> testStandardClassBPositionReport(msg) );
			}
			else if (k==AisMessageTypes.POSITIONREPORT) {
				msgList.forEach( msg -> testPositionReport(msg) );
			}
		});
	}
	private void testExtendedClassBPositionReport(AisStreamMessage msg) {
		Double lo = msg.metaData.getLongitude();
		Double la = msg.metaData.getLatitude();
		AisMessage amsg = msg.message;
		ExtendedClassBPositionReport pr = (ExtendedClassBPositionReport)amsg;
		Assert.assertEquals(lo, pr.getLongitude());
		Assert.assertEquals(la, pr.getLatitude());
		Assert.assertTrue(pr.getCog()>=0 && pr.getCog()<=3600);
	}
	private void testStandardClassBPositionReport(AisStreamMessage msg) {
		Double lo = msg.metaData.getLongitude();
		Double la = msg.metaData.getLatitude();
		AisMessage amsg = msg.message;
		StandardClassBPositionReport pr = (StandardClassBPositionReport)amsg;
		Assert.assertEquals(lo, pr.getLongitude());
		Assert.assertEquals(la, pr.getLatitude());
		Assert.assertTrue(pr.getCog()>=0 && pr.getCog()<=3600);
	}
	private void testPositionReport(AisStreamMessage msg) {
		Double lo = msg.metaData.getLongitude();
		Double la = msg.metaData.getLatitude();
		AisMessage amsg = msg.message;
		PositionReport pr = (PositionReport)amsg;
		Assert.assertEquals(lo, pr.getLongitude());
		Assert.assertEquals(la, pr.getLatitude());
		Assert.assertTrue(pr.getCog()>=0 && pr.getCog()<=3600);
	}

	@Test
	public void testBaseStationReport() {
		LOG.info("Longitude+Latitude equals to MetaData and RepeatIndicator for mobile stations...");
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=4 && k!=11) return;
			// 4 = UTC and position report from base station
			// 11= UTC and position report from mobile station
			Assert.assertEquals(AisMessageTypes.BASESTATIONREPORT, msg.messageType);
			testBaseStationReport(msg);
		});
		asmt.msgByType.forEach((k, msgList) -> {
			if (k==AisMessageTypes.BASESTATIONREPORT) {
				msgList.forEach( msg -> testBaseStationReport(msg) );
			}
		});
	}
	private void testBaseStationReport(AisStreamMessage msg) {
		// base station is not a ship: TODO diese Annahme ist nicht korrekt
//		Assert.assertEquals("", msg.metaData.getShipName());
//		if(!msg.metaData.getShipName().isEmpty()) {
//			System.out.println("##"+msg.metaData + msg.message); // ShipName trotzdem vorhanden
//		}
		// Longitude+Latitude equals to MetaData
		Double lo = msg.metaData.getLongitude();
		Double la = msg.metaData.getLatitude();
		AisMessage amsg = msg.message;
		BaseStationReport bsr = (BaseStationReport)amsg;
		Assert.assertEquals(lo, bsr.getLongitude());
		Assert.assertEquals(la, bsr.getLatitude());
		if (amsg.getMessageID()==11) {
			// When mobile station (11) is transmitting a message, 
			// it should always set the repeat indicator to default = 0.
			Assert.assertEquals(Integer.valueOf(0), amsg.getRepeatIndicator());
		}
		String timeUtc = msg.metaData.getTimeUtc();
		LocalDateTime dt = null;
		try {
			dt = MetaData.convertStringToLocalDateTime(timeUtc);
		} catch (ParseException e) {
			e.printStackTrace();
		}
/*
habe tatsächlich eine station gefunden, die ein völlig falsches Datum sendet:
INFORMATION: 11:18:54 2025-11-08 11:18:54.044748621 class BaseStationReport {
    messageID: 4
    repeatIndicator: 0
    userID: 3669769
    valid: true
    utcYear: 2006
    utcMonth: 3
    utcDay: 25
    utcHour: 11
    utcMinute: 18
    utcSecond: 51
    positionAccuracy: true
    longitude: -79.65936166666667
    latitude: 42.236641666666664
    fixType: 15
    longRangeEnable: false
    spare: null
    raim: true
    communicationState: 114722
}
 */
//		if (!bsr.getUtcYear().equals(dt.getYear())) {
//			String hms = String.format("%02d:%02d:%02d", dt.getHour(), dt.getMinute(), dt.getSecond());
//			LOG.info(hms + " "+dt.toLocalDate()+" "+dt.toLocalTime() + " "+bsr);
//			Assert.assertEquals(Integer.valueOf(dt.getYear()), bsr.getUtcYear());
//			Assert.assertEquals(Integer.valueOf(dt.getMonthValue()), bsr.getUtcMonth());
//			Assert.assertEquals(Integer.valueOf(dt.getDayOfMonth()), bsr.getUtcDay());
//			Assert.assertEquals(Integer.valueOf(dt.getHour()), bsr.getUtcHour());
//			Assert.assertEquals(Integer.valueOf(dt.getMinute()), bsr.getUtcMinute());
//			if (amsg.getMessageID()==4) {
//				// nur base station, bei mobile können sekunden abweichen TODO warum?
//				Assert.assertEquals(Integer.valueOf(dt.getSecond()), bsr.getUtcSecond());
//			}
//		}
	}

	@Test
	public void testAidsToNavigationReport() {
		LOG.info("Longitude+Latitude equals to MetaData and AtoN AIS station is not a ship...");
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=21) return;
			Assert.assertEquals(AisMessageTypes.AIDSTONAVIGATIONREPORT, msg.messageType);
			testAidsToNavigationReport(msg);
		});
		asmt.msgByType.forEach((k, msgList) -> {
			if (k==AisMessageTypes.AIDSTONAVIGATIONREPORT) {
				msgList.forEach( msg -> testAidsToNavigationReport(msg) );
			}
		});
	}
	private void testAidsToNavigationReport(AisStreamMessage msg) {
		// AtoN AIS station is not a ship: TODO diese Annahme ist nicht korrekt
//		Assert.assertEquals("", msg.metaData.getShipName()); // ShipName vorhanden
		// Longitude+Latitude equals to MetaData
		Double lo = msg.metaData.getLongitude();
		Double la = msg.metaData.getLatitude();
		AisMessage amsg = msg.message;
		AidsToNavigationReport atN = (AidsToNavigationReport)amsg;
		Assert.assertEquals(lo, atN.getLongitude());
		Assert.assertEquals(la, atN.getLatitude());
//		Assert.assertNotEquals("", atN.getName());
		// der atN.getName() kann leer sein und nicht mit ShipName übereinstimmen XXX warum?
//		if(!msg.metaData.getShipName().equals(atN.getName())) {
//			System.out.println("##"+msg.metaData.toStringFull() + msg.message);
//		}
	}

	@Test
	public void testBinaryAcknowledge() {
		LOG.info("valid Destinations have DestinationID...");
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=7 && k!=13) return;
			// 7 = acknowledgement of up to four Message 6 (ADDRESSEDBINARYMESSAGE) messages
			// 13= acknowledgement of up to four Message 12 (ADDRESSEDSAFETYMESSAGE) messages
			Assert.assertEquals(AisMessageTypes.BINARYACKNOWLEDGE, msg.messageType);
			testBinaryAcknowledge(msg);
		});
		asmt.msgByType.forEach((k, msgList) -> {
			if (k==AisMessageTypes.BINARYACKNOWLEDGE) {
				msgList.forEach( msg -> testBinaryAcknowledge(msg) );
			}
		});
	}
	private void testBinaryAcknowledge(AisStreamMessage msg) {
		AisMessage amsg = msg.message;
		BinaryAcknowledge ack = (BinaryAcknowledge)amsg;
		if(ack.getDestinations().get0().getValid()) {
			Assert.assertFalse(ack.getDestinations().get0().getDestinationID()==0);
		} else {
			Assert.assertTrue(ack.getDestinations().get0().getDestinationID()==0);
		}
		if(ack.getDestinations().get1().getValid()) {
			Assert.assertFalse(ack.getDestinations().get1().getDestinationID()==0);
		} else {
			Assert.assertTrue(ack.getDestinations().get1().getDestinationID()==0);
		}
		if(ack.getDestinations().get2().getValid()) {
			Assert.assertFalse(ack.getDestinations().get2().getDestinationID()==0);
		} else {
			Assert.assertTrue(ack.getDestinations().get2().getDestinationID()==0);
		}
		if(ack.getDestinations().get3().getValid()) {
			Assert.assertFalse(ack.getDestinations().get3().getDestinationID()==0);
		} else {
			Assert.assertTrue(ack.getDestinations().get3().getDestinationID()==0);
		}
	}

	/*
	 * typischerweise sendet ein Schiff zuerst STATICDATAREPORT mit ReportA, also mit Namen;
	 * innerhalb einer Minute sollte ReportB folgen
	 * "... Message 24B should be transmitted within 1 min following Message 24A."
	 * (das wird hier aber nicht geprüft)
	 * MetaData.ShipName ist nur bei 24A gesetzt, nicht bei 24B
	 */
	@Test
	public void testStaticDataReport() {
		LOG.info("Part A ShipName equals to MetaData and Part B ShipType>0...");
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=24) return;
			Assert.assertEquals(AisMessageTypes.STATICDATAREPORT, msg.messageType);
			testStaticDataReport(msg);
		});
		asmt.msgByType.forEach((k, msgList) -> {
			if (k==AisMessageTypes.STATICDATAREPORT) {
				msgList.forEach( msg -> testStaticDataReport(msg) );
			}
		});
	}
	private void testStaticDataReport(AisStreamMessage msg) {
		AisMessage amsg = msg.message;
		StaticDataReport sdr = (StaticDataReport)amsg;
		if(sdr.getPartNumber()) {
			Assert.assertFalse(sdr.getReportA().getValid());
			Assert.assertTrue(sdr.getReportB().getValid()); // ReportB existiert
			//System.out.println("#"+sdr);
			//Assert.assertFalse(sdr.getReportB().getShipType()==0); // 0 = not available or no ship = default
		} else {
			Assert.assertTrue(sdr.getReportA().getValid());  // ReportA existiert
			// TODO in ReportA strip einbauen
			Assert.assertEquals(sdr.getReportA().getName().strip(), msg.getMetaData().getShipName());
			Assert.assertFalse(sdr.getReportB().getValid());
			Assert.assertTrue(sdr.getReportB().getShipType()==0);
		}
	}

}
