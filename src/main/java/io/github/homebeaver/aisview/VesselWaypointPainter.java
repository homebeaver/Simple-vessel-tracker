package io.github.homebeaver.aisview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.MetaData;

// der Painter kennt seine Instanzen nicht, ==> List painters
public class VesselWaypointPainter extends WaypointPainter<Waypoint> {

//	AisStreamMessage msg;
	List<AisStreamMessage> msgList;
	
    public VesselWaypointPainter(AisStreamMessage msg) {
    	this(null, msg);
    }
    public VesselWaypointPainter(List<AisStreamMessage> list, AisStreamMessage msg) {
    	super();
    	this.msgList = list==null ? new Vector<AisStreamMessage>() : list;
    	msgList.add(msg);
    }

//	private Set<Waypoint> getWaypoints(MetaData md) {
//		Set<Waypoint> set = new HashSet<Waypoint>();
//		set.add(new DefaultWaypoint(new GeoPosition(md.getLatitude(), md.getLongitude())));
//		setWaypoints(set);
//		return set;
//	}
//	private Set<Waypoint> getWaypoints(PositionReport pr) {
//		Set<Waypoint> set = new HashSet<Waypoint>();
//		set.add(new DefaultWaypoint(new GeoPosition(pr.getLatitude(), pr.getLongitude())));
//		setWaypoints(set);
//		return set;
//	}
	public Set<Waypoint> getWaypoints() {
		Set<Waypoint> set = new HashSet<Waypoint>();
		msgList.forEach( m -> {
			MetaData md = m.getMetaData();
			set.add(new DefaultWaypoint(new GeoPosition(md.getLatitude(), md.getLongitude())));
		});
		setWaypoints(set);
		return set;
	}

}
