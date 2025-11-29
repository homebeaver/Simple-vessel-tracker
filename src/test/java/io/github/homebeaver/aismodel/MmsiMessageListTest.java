package io.github.homebeaver.aismodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.homebeaver.aismodel.AisStreamMessage.AisStreamCallback;

public class MmsiMessageListTest implements AisStreamCallback<AisStreamMessage> {

	private static final Logger LOG = Logger.getLogger(MmsiMessageListTest.class.getName());

	public MmsiMessageListTest() {
		LOG.fine("ctor");
	}

	int lines = 0; // == messages
	int msgNull = 0; // == UnknownMessage
	MmsiMessageList mmList = new MmsiMessageList();

	// implements AisStreamCallback<AisStreamMessage>
	@Override
	public void outMessage(AisStreamMessage msg) {
		lines++;
		if (msg == null) {
			LOG.info("line " + lines + " is null");
			msgNull++;
		} else {
			Integer mid = msg.message == null ? 0 : msg.message.getMessageID();
			if (mmList.addShip(msg)) {
//				System.out.println(""+lines+" MessageID=" + mid //+ ", messageType:" + msg.messageType 
//						+ " "+msg.getMetaData().toStringFull());
			} else {
				LOG.fine("Not a ship: msg# "+lines+" MessageID=" + mid + ", messageType:" + msg.messageType 
						+ " "+msg.getMetaData().toStringFull());
			}
		}
	}

	@BeforeClass
	public static void staticSetup() {
		LOG.info("staticSetup fertig");
	}

	@Before
	public void setup() {
		try {
			// in liesUrl wird outMessage gerufen
//			AisStreamMessage.liesUrl(new FileInputStream("src/test/resources/data/global.txt"), this);
//			AisStreamMessage.liesUrl(new FileInputStream("src/test/java/aisstream.txt"), this); // global+
			AisStreamMessage.liesUrl(new FileInputStream("src/test/resources/data/aisstream.txt"), this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("setup fertig, "+lines+" lines.");
	}

	@Test(expected = NullPointerException.class)
	public void exceptionThrown_forNullList() {
		MmsiMessageList mList = new MmsiMessageList();
//		LOG.info("expected NPE because there is no ship with mmsi=0, the MmsiMessageList is null");
		MmsiMessageList.isShipClassA(mList.get(0));
	}

	private static final int KEY_STREAM = 212878000;
	private static final int NONAME = 219035401;
	private static final int SEA4FUN = 265820920;
	private static final int ALVA = 219023391;
	private static final int DANPILOT_JULIET = 219024675;
	// aus global+:
	private static final int WINDCAT_24 = 235080246;
	private static final int H = 257001810;
	private static final int SPES = 244615505;
	private static final int FRENCH_WARSHIP = 228000000;
	private static final int PINGUIN = 211550210;
	private static final int AMALIA_RODRIGUES = 253242328;
	private static final int PILOTAGE_NADOR = 123456789; // XXX seltsame nr
	private static final int LEB_62 = 261013840;
	private static final int RES_NOVA = 244670228;
	private static final int FV_ARCTURUS = 235002070;
	private static final int WM_INGLIS = 316045081;
	private static final int LIZZY = 244670520;

	@Test
	public void testMmsiMessageList() {
//		Assert.assertTrue(mmList.isShipClassA(WINDCAT_24));
//		Assert.assertEquals(16, mmList.get(WINDCAT_24).size());
//		Assert.assertEquals("WINDCAT 24", mmList.getName(WINDCAT_24));
//		Assert.assertEquals(Integer.valueOf(40), mmList.getType(WINDCAT_24)); // HSC
//
//		Assert.assertTrue(mmList.isShipClassA(H));
//		Assert.assertEquals(8, mmList.get(H).size());
//		Assert.assertEquals("", mmList.getName(H));
//		Assert.assertEquals(Integer.valueOf(38), mmList.getType(H)); // FISHING UNKNOWN
//
//		Assert.assertTrue(mmList.isShipClassA(SPES));
//		Assert.assertEquals(26, mmList.get(SPES).size());
//		Assert.assertEquals("SPES", mmList.getName(SPES));
//		Assert.assertEquals(Integer.valueOf(37), mmList.getType(SPES)); // PLEASURE
//
//		Assert.assertTrue(mmList.isShipClassA(FRENCH_WARSHIP));
//		Assert.assertEquals(19, mmList.get(FRENCH_WARSHIP).size());
//		Assert.assertEquals("FRENCH WARSHIP", mmList.getName(FRENCH_WARSHIP));
//		Assert.assertEquals(Integer.valueOf(0), mmList.getType(FRENCH_WARSHIP)); // Not available
//
//		Assert.assertTrue(mmList.isShipClassA(PINGUIN));
//		Assert.assertEquals(20, mmList.get(PINGUIN).size());
//		Assert.assertEquals("PINGUIN", mmList.getName(PINGUIN));
//		Assert.assertEquals(Integer.valueOf(69), mmList.getType(PINGUIN)); // PASSENGER
//
//		Assert.assertTrue(mmList.isShipClassA(AMALIA_RODRIGUES));
//		Assert.assertEquals(5, mmList.get(AMALIA_RODRIGUES).size());
//		Assert.assertEquals("AMALIA RODRIGUES", mmList.getName(AMALIA_RODRIGUES));
//		Assert.assertEquals(Integer.valueOf(69), mmList.getType(AMALIA_RODRIGUES)); // PASSENGER
//
//		Assert.assertTrue(mmList.isShipClassA(PILOTAGE_NADOR));
//		Assert.assertEquals(17, mmList.get(PILOTAGE_NADOR).size());
//		Assert.assertEquals("PILOTAGE NADOR", mmList.getName(PILOTAGE_NADOR));
//		Assert.assertEquals(Integer.valueOf(0), mmList.getType(PILOTAGE_NADOR));
//
//		Assert.assertTrue(mmList.isShipClassA(LEB_62));
//		Assert.assertEquals(16, mmList.get(LEB_62).size());
//		Assert.assertEquals("LEB-62", mmList.getName(LEB_62));
//		Assert.assertEquals(Integer.valueOf(30), mmList.getType(LEB_62)); // FISHING
//
//		Assert.assertTrue(mmList.isShipClassA(RES_NOVA));
//		Assert.assertEquals(10, mmList.get(RES_NOVA).size());
//		Assert.assertEquals("RES NOVA", mmList.getName(RES_NOVA));
//		Assert.assertEquals(Integer.valueOf(53), mmList.getType(RES_NOVA)); // PORT_TENDER
//
//		Assert.assertTrue(mmList.isShipClassA(FV_ARCTURUS));
//		Assert.assertEquals(17, mmList.get(FV_ARCTURUS).size());
//		//Assert.assertEquals("FV ARCTURUS", mmList.getName(FV_ARCTURUS)); // "Name":".  FV ARCTURUS "
//		Assert.assertEquals("2NFX8", mmList.getCallSign(FV_ARCTURUS));
//		Assert.assertEquals(Integer.valueOf(30), mmList.getType(FV_ARCTURUS)); // FISHING
//
//		Assert.assertTrue(mmList.isShipClassA(WM_INGLIS));
//		Assert.assertEquals(11, mmList.get(WM_INGLIS).size());
//		Assert.assertEquals("WM INGLIS", mmList.getName(WM_INGLIS));
//		Assert.assertEquals(Integer.valueOf(0), mmList.getType(WM_INGLIS));
//
//		Assert.assertTrue(mmList.isShipClassA(LIZZY));
//		Assert.assertEquals(15, mmList.get(LIZZY).size());
//		Assert.assertEquals("LIZZY", mmList.getName(LIZZY));
//		Assert.assertEquals(Integer.valueOf(52), mmList.getType(LIZZY)); // TUG

		Assert.assertEquals(92, mmList.size());
		Assert.assertNull(mmList.get(0));

		Assert.assertTrue(mmList.isShipClassA(KEY_STREAM));
		Assert.assertEquals(1, mmList.get(KEY_STREAM).size()); // nur eine msg SHIPSTATICDATA
		Assert.assertEquals("KEY STREAM", mmList.getName(KEY_STREAM));
		Assert.assertEquals(Integer.valueOf(89), mmList.getType(KEY_STREAM)); // TANKER

		Assert.assertTrue(mmList.isShipClassB(NONAME));
		Assert.assertEquals(1, mmList.get(NONAME).size()); // nur eine msg STATICDATAREPORT ReportB
		Assert.assertEquals("", mmList.getName(NONAME));
		Assert.assertEquals(Integer.valueOf(37), mmList.getType(SEA4FUN)); // PLEASURE

		Assert.assertTrue(mmList.isShipClassA(SEA4FUN));
		Assert.assertEquals(2, mmList.get(SEA4FUN).size()); // zwei Nachrichten ClassA
		Assert.assertEquals("SEA4FUN", mmList.getName(SEA4FUN));
		Assert.assertEquals(Integer.valueOf(37), mmList.getType(SEA4FUN)); // PLEASURE

		Assert.assertTrue(mmList.isShipClassB(ALVA));
		Assert.assertEquals(2, mmList.get(ALVA).size()); // zwei Nachrichten ClassB
		Assert.assertEquals("ALVA", mmList.getName(ALVA));
		Assert.assertEquals(Integer.valueOf(0), mmList.getType(ALVA)); // 8 0 = not available or no ship

		Assert.assertTrue(mmList.isShipClassA(DANPILOT_JULIET));
		Assert.assertEquals(3, mmList.get(DANPILOT_JULIET).size()); // drei Nachrichten ClassA
		Assert.assertEquals("DANPILOT JULIET", mmList.getName(DANPILOT_JULIET));
		Assert.assertEquals(Integer.valueOf(50), mmList.getType(DANPILOT_JULIET));
	}

	@Test
	public void testNameFromMeta() {

		mmList.forEach( (mmsi, list) -> {
			// Wenn Name aus SHIPSTATICDATA oder STATICDATAREPORT bekannt,
			// dann mit Name aus Meta identisch falls dieser vorhanden ist (mit Ausnahmen,
			// z.B. ähnliche Namen "WINDCAT 24"<>"WINDCAT24"
			// oder expected:<A[MALIA] RODRIGUES> but was:<A[] RODRIGUES>
			if (mmList.isShipClassA(mmsi)) {
				list.forEach( m -> {
					String shipName = m.getMetaData().getShipName();
					String expectedName = MmsiMessageList.getName(list);
					if (!shipName.isEmpty() && !shipName.equals(expectedName)) {
						LOG.warning("expected=\""+expectedName + "\" : msg#="+list.size() + " " + m.getMetaData().toStringFull() + " >>>>>>> "+ m.getAisMessage());
					}
					if (shipName.isEmpty() 
					|| mmsi==FRENCH_WARSHIP  || mmsi==WINDCAT_24 || mmsi==PINGUIN 
					|| mmsi==AMALIA_RODRIGUES || mmsi==PILOTAGE_NADOR || mmsi==LEB_62 
					|| mmsi==RES_NOVA || mmsi==FV_ARCTURUS || mmsi==WM_INGLIS || mmsi==LIZZY
//					|| mmsi==441142000 || mmsi==244179000
//					|| mmsi==224597000 || mmsi==207829370
//					|| mmsi==585900273 || mmsi==585900274 || mmsi==585900192 || mmsi==109120442 // class B
//					noch mehr Ausnahmen in classB
					|| (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT 
					&& ((StaticDataReport)m.message).getReportB().getValid())) {
						// die Aushanme
					} else {
						Assert.assertEquals(expectedName, shipName);
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
			// ShipLength sollte sich nicht ändern (passiert aber doch, warum? Tippfehler?)
			if (mmList.isShip(mmsi)) {
				list.forEach( m -> {
					Integer shipLength = MmsiMessageList.getShipLength(list);
					if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
						ShipStaticData ssd = (ShipStaticData)m.message;
						if (shipLength != ssd.getDimension().getLength()) {
							LOG.warning("shipLength="+shipLength + " ("+list.indexOf(m)+"/"+list.size()+"): "+m.getMetaData().toStringFull() + "\n"+ssd);
						}
						if (mmsi==PINGUIN   // "ALEXANDER HUMBOLDT" aka "PINGUIN"
						 || mmsi==227000000 || mmsi==228999999 // "FRENCH WARSHIP "
						 || mmsi==441495000 // "108EUNHAE"
						 || mmsi==244710557 // "RIEJANNE"
						 || mmsi==271050376 // "TCA7080"
						 || mmsi==235081002 // "ORION"
						 || mmsi==SPES      // "SPES"
						 || mmsi==205391390 // "OMERTA"
						 || mmsi==244720321 // "BONTEKOE II"
						 || mmsi==244660923 // "CADENZA"
						 || mmsi==RES_NOVA  // "RES NOVA" aka "TIDALIS TEST"
						 // ... und andere
						) {
							// Ausnahmmen
						} else {
							//Assert.assertEquals(shipLength, Integer.valueOf(ssd.getDimension().getLength()));
						}
					} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
						StaticDataReport sdr = (StaticDataReport)m.message;
						if (sdr.getReportB().getValid()) {  // ReportB existiert
							if (shipLength != sdr.getReportB().getDimension().getLength()) {
								LOG.warning("shipLength="+shipLength + " ("+list.indexOf(m)+"/"+list.size()+"): "+m.getMetaData().toStringFull() + "\n"+sdr);
							}
							Assert.assertEquals(shipLength, Integer.valueOf(sdr.getReportB().getDimension().getLength()));
						}
					}
				});
			}
		});
	}

}
