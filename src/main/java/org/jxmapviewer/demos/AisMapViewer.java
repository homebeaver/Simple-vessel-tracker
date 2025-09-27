package org.jxmapviewer.demos;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.jdesktop.swingx.demos.svg.FeatheRcircle_blue;
import org.jdesktop.swingx.demos.svg.FeatheRnavigation_grey;
import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.SizingConstants;
//import org.jdesktop.swingx.painter.CompoundPainter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import dk.dma.ais.message.NavigationalStatus;
import dk.dma.ais.message.ShipTypeCargo;
import dk.dma.ais.message.ShipTypeColor;
import io.github.homebeaver.aismodel.AisMessage;
import io.github.homebeaver.aismodel.AisMessageTypes;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.PositionReport;
import io.github.homebeaver.aismodel.ShipStaticData;
import io.github.homebeaver.aismodel.StandardClassBPositionReport;

public class AisMapViewer extends JXMapViewer {

	private static final long serialVersionUID = 8162164255394960216L;
	private static final Logger LOG = Logger.getLogger(AisMapViewer.class.getName());

/*
// this works for up to 10 elements:
Map<String, String> test1 = Map.of(
    "a", "b",
    "c", "d"
);
 */
	private static Map<ShipTypeColor, Color> typeToColor = Map.of(
		ShipTypeColor.BLUE, Color.BLUE,
		ShipTypeColor.GREY, Color.LIGHT_GRAY,
		ShipTypeColor.GREEN, Color.GREEN,
		ShipTypeColor.ORANGE, Color.ORANGE,
		ShipTypeColor.PURPLE, Color.MAGENTA,
		ShipTypeColor.RED, Color.RED,
		ShipTypeColor.TURQUOISE, Color.CYAN,
		ShipTypeColor.YELLOW, Color.YELLOW
	);

	// message map per vessel with MMSI as key
	private Map<Integer, List<AisStreamMessage>> map;
	CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
	
	// painters ==> Map<Integer, WaypointPainter>
	// List<WaypointPainter<Waypoint>>> braucht man für die Spur
	// In dieser Version ohne Spur
	/*
	 * pro Schiff (MMSI) den letzen painter merken, damit kann man in overlayPainter
	 * den letzten painter löschen:
	 *   overlayPainter.removePainter(painters.get(key));
	 * bevor man für die neue Position einen neuen erstellt:
	 * 	 overlayPainter.addPainter(shipLocationPainter);
	 */
	private Map<Integer, WaypointPainter<Waypoint>> painters;
//	private Map<Integer, List<WaypointPainter<Waypoint>>> painters;
	
	public AisMapViewer() {
    	super();
		this.map = new HashMap<Integer, List<AisStreamMessage>>();
		painters = new HashMap<>();
		
//		super.setOverlayPainter(overlayPainter); // XXX das funktioniert nicht
	}
	
    public void addMessage(AisStreamMessage msg) {
		int key = msg.getMetaData().getMMSI();
//		if(key!=219024675 && key!=220434000 && key!=219035097 && key!=219007393) return;
// nur grosse Potte:		
//		if(key!=525121076 && key!=230706000 && key!=538010235 && key!=629009622 && key!=247389200) return;
//		LOG.info(msg.getMetaData().getShipName() + " >>> "+msg.getAisMessageType() + " MMSI="+key);
		if (map.containsKey(key)) {
			// mindestens zweite Nachricht
			LOG.info("mindestens zweite Nachricht für "+key + " "+msg.getAisMessageType());
			// ShipData : TODO letzte Position holen, wenn vorhanden Kurs holen, icon löschen und neu anlegen
			// PositionReport TODO map.get(key) 
			AisMessage amsg = msg.getAisMessage();
			Double cog = null; // Kurs
			Integer type = null; // Schiffstyp
			Integer shipLenght = null;
			Integer navStatus = null;
			List<AisStreamMessage> list = map.get(key);
			Iterator<AisStreamMessage> i = list.iterator();
			int n=1; // n sagt wievielte Nachticht ist es => mindestens die Zweite
			while (i.hasNext()) {
				AisStreamMessage next = i.next();
				n++;
				AisMessage nextamsg = next.getAisMessage();
				if (nextamsg instanceof PositionReport pr) {
					cog = pr.getCog();
				} else if (nextamsg instanceof StandardClassBPositionReport scbpr) {
					cog = scbpr.getCog();
				} else if (nextamsg instanceof ShipStaticData ssd) {
					type = ssd.getType();
					shipLenght = ssd.getDimension().getLength();
				}
			}
			if (amsg instanceof PositionReport pr) {
				cog = pr.getCog();
				navStatus = pr.getNavigationalStatus();
			} else if (amsg instanceof StandardClassBPositionReport scbpr) {
				cog = scbpr.getCog();
			} else if (amsg instanceof ShipStaticData ssd) {
				type = ssd.getType();
				shipLenght = ssd.getDimension().getLength();
			}
			LOG.info("-------------->"+key+": "
				+ " NavigationalStatus="+(navStatus==null?"?":NavigationalStatus.get(navStatus))
				+ " cog="+cog + " type="+type + " #="+n);
//			list.forEach( m -> { ... NICHT so

			RadianceIcon icon;
			// cog == null ==> nur SHIPSTATICDATAs
			if(cog==null) {
				icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
			} else {
				int zoom = this.getZoom(); // value between 1 and 15
				// ab zoom 6 kleinste icons XS==18
				//int f = 432/48 = 9 Berechnung für zoom 5:
				int iconsize = shipLenght==null ? 0 : shipLenght/9; // XXX
				if(iconsize<18) iconsize = 18;
				icon = FeatheRnavigation_grey.of(iconsize, iconsize);
				icon.setRotation(cog); // Kurs
			}
			if(type!=null) {
				ShipTypeCargo stc = new ShipTypeCargo(type);
				ShipTypeColor c = ShipTypeColor.getColor(stc.getShipType());
				icon.setColorFilter(color -> typeToColor.get(c)); // ShipTypeColor => java Color
			}
			
			WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
			shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
			
			// den zuletzt erstellten Painter löschen in overlayPainter und aus painters
			overlayPainter.removePainter(painters.get(key));
			overlayPainter.addPainter(shipLocationPainter);
			painters.replace(key, shipLocationPainter); // den letzten painter wegwerfen/überschreiben
		} else {
			List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
			map.put(key, waypoints); // empty List waypoints 
			// msg ist erste Nachricht für Schiff key, wir haben keinen Kurs
			// also neues o-Schiff mit Color
			display1Vessel(msg);
		}
		map.get(key).add(msg);
		super.setOverlayPainter(overlayPainter); // setOverlayPainter im ctor reicht nicht
    }

	private void display1Vessel(AisStreamMessage msg) {
/*

vessels with track (more then 1 waypoints):
	MMSI=265820920:2 SEA4FUN ShipStaticData,PositionReport,
	MMSI=219007393:2 VENUS (GUARD VESSEL) PositionReport,PositionReport,
	MMSI=219024675:3 DANPILOT JULIET PositionReport,PositionReport,ShipStaticData,
	MMSI=219369000:2 LEA ELIZABETH PositionReport,PositionReport,
	MMSI=219000368:2 MERCANDIA IV PositionReport,PositionReport,
	MMSI=220476000:2 FRIGGA PositionReport,ShipStaticData,
	MMSI=219005904:2 AURELIA ShipStaticData,PositionReport,
	MMSI=219024336:2 JEPPE PositionReport,PositionReport,
	MMSI=305773000:2 VOHBURG PositionReport,PositionReport,

 */
		int key = msg.getMetaData().getMMSI();
		assert null==painters.get(key); // expected null
		if (msg.getAisMessageType() == AisMessageTypes.POSITIONREPORT) {
			// POSITIONREPORT
			// Pos + Kurs aus Msg ; > ohne Farbe
			AisMessage amsg = msg.getAisMessage();
			RadianceIcon icon = null;
			if (amsg instanceof PositionReport pr) {
				NavigationalStatus navStatus = NavigationalStatus.get(pr.getNavigationalStatus());
				//if(navStatus==5) { //= moored (festgemacht)
				if(navStatus==NavigationalStatus.UNDER_WAY_USING_ENGINE) { // XXX navStatus nicht in StandardClassBPositionReport
					//icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M); kleiner:
					icon = FeatheRnavigation_grey.of(18, 18);
					icon.setRotation(pr.getCog()); // Kurs
				} else {
					icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
				}
				LOG.info("erste Nachricht ist POSITIONREPORT NavigationalStatus="+navStatus + ", cog="+pr.getCog());
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				painters.put(key, shipLocationPainter);
				overlayPainter.addPainter(shipLocationPainter);
			}
		} else if (msg.getAisMessageType() == AisMessageTypes.STANDARDCLASSBPOSITIONREPORT) {
			// STANDARDCLASSBPOSITIONREPORT
			// Pos + Kurs aus Msg ; > ohne Farbe
			AisMessage amsg = msg.getAisMessage();
			RadianceIcon icon = FeatheRnavigation_grey.of(18, 18);
			if (amsg instanceof StandardClassBPositionReport scbpr) {
				icon.setRotation(scbpr.getCog()); // Kurs
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				painters.put(key, shipLocationPainter);
				overlayPainter.addPainter(shipLocationPainter);
			}
		} else {
			// SHIPSTATICDATA
			// Pos aus Meta, kein Kurs, o mit Farbe
			AisMessage amsg = msg.getAisMessage();
			if (amsg instanceof ShipStaticData ssd) {
				int shipLenght = ssd.getDimension().getLength();
				ShipTypeCargo stype = new ShipTypeCargo(ssd.getType());
				ShipTypeColor c = ShipTypeColor.getColor(stype.getShipType());
				LOG.info("erste Nachricht ist SHIPSTATICDATA ShipType="+stype + ", shipLenght="+shipLenght);
				int iconsize = SizingConstants.XS;
				RadianceIcon icon = FeatheRcircle_blue.of(iconsize, iconsize);
				icon.setColorFilter(color -> typeToColor.get(c)); // ShipTypeColor => java Color
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				painters.put(key, shipLocationPainter);
				overlayPainter.addPainter(shipLocationPainter);
			}
		}
	}

}
