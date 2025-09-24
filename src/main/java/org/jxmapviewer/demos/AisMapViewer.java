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

	private Map<Integer, List<AisStreamMessage>> map;
//	OverlayPainter<JXMapViewer> overlayPainter;
	
//	private CompoundPainter<JXMapViewer> painters; 
	// TODO einen Painter aus der Liste entfernen, dazu LIST ==> Map<Integer, Painter>
	// List<WaypointPainter<Waypoint>>> braucht man für die Spur
	// In dieser Version ohne Spur
	private Map<Integer, WaypointPainter<Waypoint>> painters;
//	private Map<Integer, List<WaypointPainter<Waypoint>>> painters;
	
	public AisMapViewer() {
    	super();
		this.map = new HashMap<Integer, List<AisStreamMessage>>();
		painters = new HashMap<>();
		
//		overlayPainter = (OverlayPainter<JXMapViewer>) new OverlayPainter<JXMapViewer>();
//		overlayPainter.setCacheable(false);
////		overlayPainter.setPainters(painters);
//		super.setOverlayPainter(overlayPainter);
	}
	
//	public void setCompoundPainter(CompoundPainter<JXMapViewer> cp) {
//		painters = cp;
//	}
    
    public void addMessage(AisStreamMessage msg) {
		int key = msg.getMetaData().getMMSI();
		if(key!=311001729) return;
//		LOG.info(">>>>>>>>>>>>>>>"+msg.getAisMessageType() + " MMSI="+key);
		if (map.containsKey(key)) {
			// mindestens zweite Nachricht
			LOG.info("mindestens zweite Nachricht für "+key + " "+msg.getAisMessageType());
			// ShipData : TODO letzte Position holen, wenn vorhanden Kurs holen, icon löschen und neu anlegen
			// PositionReport TODO map.get(key) 
			AisMessage amsg = msg.getAisMessage();
			Double cog = null; // Kurs
			Integer type = null; // Schiffstyp
			Integer shipLenght = null;
			List<AisStreamMessage> list = map.get(key);
			Iterator<AisStreamMessage> i = list.iterator();
			int n=0;
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
//				LOG.info(">>="+n+" "+key+":"+next.getAisMessageType() + "cog="+cog + "type="+type);
			}
			if (amsg instanceof PositionReport pr) {
				cog = pr.getCog();
			} else if (amsg instanceof StandardClassBPositionReport scbpr) {
				cog = scbpr.getCog();
			} else if (amsg instanceof ShipStaticData ssd) {
				type = ssd.getType();
				shipLenght = ssd.getDimension().getLength();
//			LOG.info("==============>"+key+": "+ " LAST cog="+cog + " type="+type + " #="+n);
			}
			LOG.info("-------------->"+key+": "+ " cog="+cog + " type="+type + " #="+n);
//			list.forEach( m -> { ... NICHT so

			RadianceIcon icon;
			// cog == null ==> nur SHIPSTATICDATAs
			if(cog==null) {
				icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
			} else {
				int zoom = this.getZoom(); // value between 1 and 15
				// ab zoom 6 kleineste icons XS==18
				//int f = 432/48 = 9 Berechnung für zoom 5:
				int iconsize = shipLenght==null ? 0 : shipLenght/6; // XXX
				if(iconsize<18) iconsize = 18;
				icon = FeatheRnavigation_grey.of(iconsize, iconsize);
				icon.setRotation(cog); // Kurs
			}
			if(type!=null) {
				ShipTypeCargo stc = new ShipTypeCargo(type);
				ShipTypeColor c = ShipTypeColor.getColor(stc.getShipType());
				icon.setColorFilter(color -> typeToColor.get(c)); // ShipTypeColor => java Color
			}
			// TODO den zuletzt erstellten Painter löschen aus painters
			// NEIN : nur letze Positien (ohne Spur - das kommt später)
			WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(list, msg);
			shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
			//painters.remove(key); besser:
			painters.replace(key, shipLocationPainter);
			CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
			overlayPainter.setCacheable(false);
			overlayPainter.setPainters(List.copyOf(painters.values()));
			super.setOverlayPainter(overlayPainter);
		} else {
			List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
			map.put(key, waypoints); // empty List waypoints 
			// msg ist erste Nachricht für Schiff key, wir haben keinen Kurs
			// also neues o-Schiff mit Color
			display1Vessel(msg);
		}
		map.get(key).add(msg);
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
// test: if(key==265820920 || key==-219007393 || key==219024675 || key==-219369000 || key==-219000368 || key==220476000 || key==219005904 || key==-219024336 || key==-305773000)
//if(key==311001729) // XXX test
		if (msg.getAisMessageType() == AisMessageTypes.POSITIONREPORT) {
			// POSITIONREPORT
			// Pos + Kurs aus Msg ; > ohne Farbe
			AisMessage amsg = msg.getAisMessage();
			RadianceIcon icon = null;
			if (amsg instanceof PositionReport pr) {
				int navStatus = pr.getNavigationalStatus();
				//if(navStatus==5) { //= moored (festgemacht)
				if(navStatus==0) { // Under way XXX navStatus nicht in StandardClassBPositionReport
					//icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M); kleiner:
					icon = FeatheRnavigation_grey.of(18, 18);
					icon.setRotation(pr.getCog()); // Kurs
				} else {
					icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
				}
				LOG.info("erste Nachricht ist POSITIONREPORT navStatus="+navStatus + " cog="+pr.getCog());
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				painters.put(key, shipLocationPainter);
				CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
				overlayPainter.setCacheable(false);
				overlayPainter.setPainters(List.copyOf(painters.values()));
				super.setOverlayPainter(overlayPainter);
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
				CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
				overlayPainter.setCacheable(false);
				overlayPainter.setPainters(List.copyOf(painters.values()));
				super.setOverlayPainter(overlayPainter);
			}
		} else {
			// SHIPSTATICDATA
			// Pos aus Meta, kein Kurs, o mit Farbe
			AisMessage amsg = msg.getAisMessage();
			if (amsg instanceof ShipStaticData ssd) {
				int shipLenght = ssd.getDimension().getLength();
				ShipTypeCargo stype = new ShipTypeCargo(ssd.getType());
				ShipTypeColor c = ShipTypeColor.getColor(stype.getShipType());
				LOG.info("erste Nachricht ist SHIPSTATICDATA ShipType="+stype + " shipLenght="+shipLenght);
				int zoom = this.getZoom(); // value between 1 and 15
				// ab zoom 6 kleineste icons XS==10
				//int f = 432/48
				int iconsize = shipLenght/9;
				if(iconsize<SizingConstants.XS) iconsize = SizingConstants.XS;
				// XXX das gilt nur für FeatheRnavigation_grey
				RadianceIcon icon = FeatheRcircle_blue.of(iconsize, iconsize);
				icon.setColorFilter(color -> typeToColor.get(c)); // ShipTypeColor => java Color
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				painters.put(key, shipLocationPainter);
				CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
				overlayPainter.setCacheable(false);
				overlayPainter.setPainters(List.copyOf(painters.values()));
				super.setOverlayPainter(overlayPainter);
			}
		}
	}

}
