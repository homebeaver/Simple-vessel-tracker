package io.github.homebeaver.aismodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

/*
 * 	A list of messages per vessel (or base station) with MMSI as vessel key.
 */
@SuppressWarnings("serial")
public class MmsiMessageList extends HashMap<Integer, List<AisStreamMessage>> 
	implements Map<Integer, List<AisStreamMessage>> {

	private static final Logger LOG = Logger.getLogger(MmsiMessageList.class.getName());

	public MmsiMessageList() {
		super();
	}
	
	public MmsiMessageList(int mmsi) {
		super();
		List<AisStreamMessage> waypoints = //new Vector<AisStreamMessage>();
		new ArrayList<AisStreamMessage>();
		super.put(mmsi, waypoints); // empty List waypoints 
	}

	public MmsiMessageList(AisStreamMessage msg) {
		super(msg.getMetaData().getMMSI());
		this.get(msg.getMetaData().getMMSI()).add(msg);
	}

	private List<AisStreamMessage> newList(AisStreamMessage msg) {
		List<AisStreamMessage> waypoints = //new Vector<AisStreamMessage>();
		new ArrayList<AisStreamMessage>();
		waypoints.add(msg);
		return waypoints;
	}

	/**
	 * Adds AisStreamMessage to list if msg identified as a ship
	 * @param msg
	 * @return true if list collection changed as a result of the call
	 */
	public boolean addShip(AisStreamMessage msg) {
		int mmsi = msg.getMetaData().getMMSI();
		if (mmsi==0) return false;
		List<AisStreamMessage> list = get(mmsi);
		if (isShip(msg)) {
			if (list==null) {
				put(mmsi, newList(msg));
				return true;
			} else {
				return list.add(msg);
			}
		} else if (isShip(mmsi)) {
			return list.add(msg);
		}
//		System.out.println("mmsi nicht als schiff erkannt "+mmsi + " / "+msg.getAisMessageType());
		LOG.fine("Not a ship MMSI="+mmsi + " / "+msg.getAisMessageType());
		return false;
	}
	
	public boolean isClassAShip(int mmsi) {
		return isClassAShip(get(mmsi));
	}
	
	public boolean isClassBShip(int mmsi) {
		return isClassBShip(get(mmsi));
	}

	public boolean isShip(int mmsi) {
		return isClassAShip(get(mmsi)) || isClassBShip(get(mmsi));
	}

	/*
	 * der Schiffsname wird manuell ins AIS-Geräte eingetragen und kann daher leer sein
	 */
	public String getName(int mmsi) {
		return getName(get(mmsi));
	}

	public Integer getType(int mmsi) {
		return getType(get(mmsi));
	}

	public Integer getShipLength(int mmsi) {
		return getShipLength(get(mmsi));
	}

	public boolean isBaseStation(int mmsi) {
		return isBaseStation(get(mmsi));
	}

	static String getName(List<AisStreamMessage> list) {
		if (list==null) return null; // XXX oder exception
		for (AisStreamMessage m : list) {
//			if (isClassAShip(m)) ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				if (ssd.getName().isEmpty()) {
					LOG.warning("ShipName is empty ("+list.indexOf(m)+"/"+list.size()+"): "+m.getMetaData().toStringFull()
						+ "\n"+ssd); // expected not empty!
				} else {
					return ssd.getName();
				}
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportA().getValid()) {  // ReportA existiert
					return sdr.getReportA().getName();
				}
			}
		}
		return null;
	}

	/*
	 * der Schifftyp wird rückwärts bestimmt, die neueste (letzte) Eingabe gilt
	 */
	static Integer getType(List<AisStreamMessage> list) {
		if (list==null) return null; // XXX oder exception
//		for (AisStreamMessage m : list) // iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
//			if (isClassAShip(m)) ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				return ssd.getType();
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportB().getValid()) {  // ReportB existiert
					return sdr.getReportB().getShipType();
				}
			}
		}
		return null;
	}

	static Integer getShipLength(List<AisStreamMessage> list) {
		if (list==null) return null; // XXX oder exception
//		for (AisStreamMessage m : list) {
// iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// ClassAShip ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				return ssd.getDimension().getLength();
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportB().getValid()) {  // ReportB existiert
					return sdr.getReportB().getDimension().getLength();
				}
			}
		}
		return null;
	}

	static boolean isClassAShip(List<AisStreamMessage> list) {
		if (list==null) return false; // XXX oder exception
		for (AisStreamMessage m : list) {
			if (isClassAShip(m)) return true;
		}
		return false;
	}

	static boolean isClassAShip(AisStreamMessage m) {
		if (m.getAisMessageType() == AisMessageTypes.POSITIONREPORT 
		 || m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) return true;
		return false;
	}

	private boolean isClassBShip(List<AisStreamMessage> list) {
		if (list==null) return false; // XXX oder exception
		for (AisStreamMessage m : list) {
			if (isClassBShip(m)) return true;
		}
		return false;
	}

	static boolean isClassBShip(AisStreamMessage m) {
		if (m.getAisMessageType() == AisMessageTypes.STANDARDCLASSBPOSITIONREPORT 
		 || m.getAisMessageType() == AisMessageTypes.EXTENDEDCLASSBPOSITIONREPORT
		 || m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) return true;
		return false;
	}

	static boolean isShip(AisStreamMessage m) {
		return isClassAShip(m) || isClassBShip(m);
	}

	private boolean isBaseStation(List<AisStreamMessage> list) {
		if (list==null) return false; // XXX oder exception
		for (AisStreamMessage m : list) {
			if (m.getAisMessageType() == AisMessageTypes.BASESTATIONREPORT) return true;
		}
		return false;
	}
}
