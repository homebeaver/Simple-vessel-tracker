package io.github.homebeaver.aismodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.homebeaver.aismodel.AisStreamMessage.AisStreamCallback;

public class MmsiMessageListTest implements AisStreamCallback<AisStreamMessage> {

	private static final Logger LOG = Logger.getLogger(MmsiMessageListTest.class.getName());

	int lines = 0; // == messages
	int msgNull = 0; // == UnknownMessage
//	List<AisStreamMessage> listOfMsg = new Vector<>();
	MmsiMessageList mmList = new MmsiMessageList();

	@Override
	public void outMessage(AisStreamMessage msg) {
		lines++;
		if (msg == null) {
			LOG.info("line " + lines + " is null");
			msgNull++;
		} else {
//			assert listOfMsg.add(msg);
			Integer mid = msg.message == null ? 0 : msg.message.getMessageID();
			LOG.fine("MessageID=" + mid + ", messageType:" + msg.messageType);
			mmList.addShip(msg);
		}
	}

	@BeforeClass
	public static void staticSetup() {
		System.out.println("staticSetup fertig");
	}

	@Before
	public void setup() {
//		asmt = new AisStreamMessageTest();
//		LOG.fine("AisStreamMessageTest:" + asmt);
//		try {
//			// in liesUrl wird outMessage gerufen
//			AisStreamMessage.liesUrl(new FileInputStream("src/test/resources/data/aisstream.txt"), asmt);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		LOG.fine("setup fertig");
	}

	@Test
	public void testMmsiMessageList() {
		MmsiMessageList mList = new MmsiMessageList();
//		Assert.assertThrows(NullPointerException.class, mList.isShip(0)); // XXX
		Assert.assertFalse(mList.isShip(0));

		try {
			// in liesUrl wird outMessage gerufen
//			AisStreamMessage.liesUrl(new FileInputStream("src/test/resources/data/global.txt"), this);
//			AisStreamMessage.liesUrl(new FileInputStream("src/test/java/aisstream.txt"), this); // global+
			AisStreamMessage.liesUrl(new FileInputStream("src/test/resources/data/aisstream.txt"), this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Assert.assertTrue(mmList.isClassAShip(212878000));
		Assert.assertEquals(1, mmList.get(212878000).size()); // nur eine SHIPSTATICDATA
		Assert.assertTrue(mmList.isClassBShip(219035401));
		Assert.assertEquals(1, mmList.get(219035401).size()); // nur eine STATICDATAREPORT
		Assert.assertEquals(2, mmList.get(265820920).size()); // zwei Nachrichten ClassA
		Assert.assertEquals(2, mmList.get(219023391).size()); // zwei Nachrichten ClassB
		Assert.assertEquals(3, mmList.get(219024675).size()); // drei Nachrichten ClassA

		mmList.forEach( (mmsi, list) -> {
			// Wenn Name aus SHIPSTATICDATA oder STATICDATAREPORT bekannt,
			// dann mit Name aus Meta identisch falls dieser vorhanden ist (mit Ausnahmen,
			// z.B. ähnliche Namen "WINDCAT 24"<>"WINDCAT24"
			// oder expected:<A[MALIA] RODRIGUES> but was:<A[] RODRIGUES>
			if (mmList.isClassAShip(mmsi) && MmsiMessageList.getName(list)!=null) {
				list.forEach( m -> {
					String shipName = m.getMetaData().getShipName();
					if (!shipName.isEmpty() && !shipName.equals(MmsiMessageList.getName(list))) {
						LOG.warning("\""+MmsiMessageList.getName(list) + "\" : "+m.messageType + " " + m.getMetaData().toStringFull() + " >>>>>>> "+ m.getAisMessage());
					}
					if (shipName.isEmpty() 
					|| mmsi==228000000 || mmsi==123456789 || mmsi==441142000 || mmsi==244179000 || mmsi==235080246
					|| mmsi==211550210 || mmsi==224597000 || mmsi==207829370 || mmsi==316045081 
					|| mmsi==253242328 || mmsi==261013840 || mmsi==244670228 || mmsi==244670520
					|| mmsi==235002070 || mmsi==585900273 || mmsi==585900274 || mmsi==585900192 || mmsi==109120442 // class B
//					noch mehr Ausnahmen in classB
					|| (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT 
					&& ((StaticDataReport)m.message).getReportB().getValid())) {
						// die Aushanme
					} else {
						Assert.assertEquals(MmsiMessageList.getName(list), shipName);
					}
				});
			}
			// ShipType ändert sich nicht (Ausnahmen)
			if (mmList.isShip(mmsi) && MmsiMessageList.getType(list)!=null) {
				list.forEach( m -> {
					Integer shipType = MmsiMessageList.getType(list);
					if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
						ShipStaticData ssd = (ShipStaticData)m.message;
						if (shipType != ssd.getType()) {
							LOG.warning("shipType="+shipType + " : "+m.messageType + " " + m.getMetaData().toStringFull() + " >>>>>>> "+ m.getAisMessage());
						}
						//Assert.assertEquals(shipType, ssd.getType()); // einige Ausnahmen
					} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
						StaticDataReport sdr = (StaticDataReport)m.message;
						if (sdr.getReportB().getValid()) {  // ReportB existiert
							if (shipType != sdr.getReportB().getShipType()) {
								LOG.warning("shipType="+shipType + " : "+m.messageType + " " + m.getMetaData().toStringFull() + " >>>>>>> "+ m.getAisMessage());
							}
							Assert.assertEquals(shipType, sdr.getReportB().getShipType());
						}
					}
				});
			}
		});
	}

}
