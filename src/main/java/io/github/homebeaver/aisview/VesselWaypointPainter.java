package io.github.homebeaver.aisview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.MetaData;

// der Painter kennt seine Instanzen nicht, ==> List painters
public class VesselWaypointPainter extends WaypointPainter<Waypoint> {

	List<AisStreamMessage> msgList;
	
	public VesselWaypointPainter(List<AisStreamMessage> list) {
		super();
		setMsgList(list);
	}

	public void setMsgList(List<AisStreamMessage> list) {
		msgList = list;
	}
	public void setRenderer(VesselWaypointRenderer r) {
		super.setRenderer(r);
	}
	public Waypoint getLastWaypoint() {
		AisStreamMessage msg = msgList.get(msgList.size()-1);
		MetaData md = msg.getMetaData();
		return new DefaultWaypoint(new GeoPosition(md.getLatitude(), md.getLongitude()));
	}
	public Set<Waypoint> getWaypoints() {
		Set<Waypoint> set = new HashSet<Waypoint>();
//		msgList.forEach( m -> {
//			MetaData md = m.getMetaData();
//			set.add(new DefaultWaypoint(new GeoPosition(md.getLatitude(), md.getLongitude())));
//		});
		set.add(getLastWaypoint());
		setWaypoints(set);
		return set;
	}

}
