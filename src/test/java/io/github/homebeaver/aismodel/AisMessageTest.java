package io.github.homebeaver.aismodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    	Map<Integer, AisStreamMessage> msgByMessageID = new HashMap<Integer, AisStreamMessage>();
		@Override
		public void outMessage(AisStreamMessage msg) {
			lines++;
			if(msg==null) {
				LOG.info("line "+lines + " is null");
				msgNull++;
			} else {
				Integer mid = msg.message.getMessageID();
				LOG.info("MessageID="+mid+", messageType:"+msg.messageType);
				AisStreamMessage old = msgByMessageID.put(mid, msg);
				if(old!=null) {
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
			AisStreamMessage.liesUrl(new FileInputStream("src/test/java/aisstream2.txt"), asmt);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("staticSetup fertig");
	}

	@Before
	public void setup() {
		LOG.info("setup fertig");
	}

	@Test
	public void testCounter() {
		LOG.info("I expect 27 messages, one per line:");
		Assert.assertEquals(27, asmt.lines);
		Assert.assertEquals(2, asmt.msgNull);
		Assert.assertEquals(21, asmt.msgByMessageID.size());

		Assert.assertTrue(asmt.msgByMessageID.containsKey(1)); // PositionReport 3x
		Assert.assertTrue(asmt.msgByMessageID.containsKey(2));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(3));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(4)); // BaseStationReport
		Assert.assertTrue(asmt.msgByMessageID.containsKey(5));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(6));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(7));
		// 8 BINARYBROADCASTMESSAGE - noch keine gefunden
		Assert.assertTrue(asmt.msgByMessageID.containsKey(9)); // StandardSearchAndRescueAircraftReport
		Assert.assertTrue(asmt.msgByMessageID.containsKey(10));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(11)); // BaseStationReport
		Assert.assertTrue(asmt.msgByMessageID.containsKey(12));
		Assert.assertTrue(asmt.msgByMessageID.containsKey(13));
		// welche MessageID fehlen
		Assert.assertFalse(asmt.msgByMessageID.containsKey(8));
		// 14 SAFETYBROADCASTMESSAGE - noch keine gefunden
		Assert.assertFalse(asmt.msgByMessageID.containsKey(14));
		// 22 GROUPASSIGNMENTCOMMAND - noch keine gefunden
		Assert.assertFalse(asmt.msgByMessageID.containsKey(23));
	}

	@Test
	public void testUserId() {
		// The user ID should be the MMSI
		asmt.msgByMessageID.forEach((k, msg) -> {
			Integer mmsi = msg.metaData.getMMSI();
			Integer userId =  msg.message.getUserID();
			Assert.assertEquals(mmsi, userId);
		});
	}
	
	@Test
	public void testTtimeUtc() {
		asmt.msgByMessageID.forEach((k, msg) -> {
			String timeUtc =msg.metaData.getTimeUtc();
			LocalDateTime dt = null;
			try {
				dt = MetaData.convertStringToDate(timeUtc);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String hms = String.format("%02d:%02d:%02d", dt.getHour(), dt.getMinute(), dt.getSecond());
			LOG.fine(msg.metaData.getTimeUtc() + " == "+dt.toLocalDate()+" "+dt.toLocalTime()+" "+hms);
			Assert.assertTrue(timeUtc.startsWith(""+dt.toLocalDate()+" "+hms));
			// Die nanos sind mit oder ohne führende Nullen, Beispiele
			// 2025-10-30 20:13:32.089506495 +0000 UTC
			// 2025-10-30 19:42:57.13756116 +0000 UTC
		});
	}

	@Test
	public void testBaseStationReport() {
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=4 && k!=11) return;
			// 4 = UTC and position report from base station
			// 11= UTC and position report from mobile station
			Assert.assertEquals(AisMessageTypes.BASESTATIONREPORT, msg.messageType);
			// station is not a ship:
			Assert.assertEquals("", msg.metaData.getShipName());
			AisMessage amsg = msg.message;
			if (k==11) {
				// When mobile station is transmitting a message, 
				// it should always set the repeat indicator to default = 0.
				Assert.assertEquals(Integer.valueOf(0), amsg.getRepeatIndicator());
			}
			Assert.assertTrue(amsg instanceof BaseStationReport);
			BaseStationReport bsr = (BaseStationReport)amsg;
			Double lo = msg.metaData.getLongitude();
			Double la = msg.metaData.getLatitude();
			Assert.assertEquals(lo, bsr.getLongitude());
			Assert.assertEquals(la, bsr.getLatitude());
		});
	}

	@Test
	public void testBinaryAcknowledge() {
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=7 && k!=13) return;
			// 7 = acknowledgement of up to four Message 6 (ADDRESSEDBINARYMESSAGE) messages
			// 13= acknowledgement of up to four Message 12 (ADDRESSEDSAFETYMESSAGE) messages
			Assert.assertEquals(AisMessageTypes.BINARYACKNOWLEDGE, msg.messageType);
			AisMessage amsg = msg.message;
			Assert.assertTrue(amsg instanceof BinaryAcknowledge);
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
		});
	}

	@Test
	public void testStaticDataReport() {
		asmt.msgByMessageID.forEach((k, msg) -> {
			if(k!=24) return;
			Assert.assertEquals(AisMessageTypes.STATICDATAREPORT, msg.messageType);
			AisMessage amsg = msg.message;
			Assert.assertTrue(amsg instanceof StaticDataReport);
			StaticDataReport sdr = (StaticDataReport)amsg;
			if(sdr.getPartNumber()) {
				Assert.assertFalse(sdr.getReportA().getValid());
				Assert.assertTrue(sdr.getReportB().getValid()); // ReportB existiert
				Assert.assertFalse(sdr.getReportB().getShipType()==0);
			} else {
				Assert.assertTrue(sdr.getReportA().getValid());
				Assert.assertFalse(sdr.getReportB().getValid());
				Assert.assertTrue(sdr.getReportB().getShipType()==0);
			}
		});
	}

}
