package io.github.homebeaver.aismodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/*
 * 	A list of messages per vessel (or base station) with MMSI as vessel key.
 */
@SuppressWarnings("serial")
public class MmsiMessageList extends HashMap<Integer, List<AisStreamMessage>> 
	implements Map<Integer, List<AisStreamMessage>> {

	public MmsiMessageList() {
		super();
	}
	
	public MmsiMessageList(int mmsi) {
		super();
		List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
//		new ArrayList<AisStreamMessage>();
		super.put(mmsi, waypoints); // empty List waypoints 
	}

	public MmsiMessageList(AisStreamMessage msg) {
		super(msg.getMetaData().getMMSI());
		this.get(msg.getMetaData().getMMSI()).add(msg);
	}

	private List<AisStreamMessage> newList(AisStreamMessage msg) {
		List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
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
		System.out.println("mmsi nicht als schiff erkannt "+mmsi + " / "+msg.getAisMessageType());
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

	public String getName(int mmsi) {
		return getName(get(mmsi));
	}
	public Integer getType(int mmsi) {
		return getType(get(mmsi));
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
				// TODO in ReportA strip einbauen
				return ssd.getName().strip();
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportA().getValid()) {  // ReportA existiert
					// TODO in ReportA strip einbauen
					return sdr.getReportA().getName().strip();
				}
			}
		}
		return null;
	}
	static Integer getType(List<AisStreamMessage> list) {
		if (list==null) return null; // XXX oder exception
		for (AisStreamMessage m : list) {
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
