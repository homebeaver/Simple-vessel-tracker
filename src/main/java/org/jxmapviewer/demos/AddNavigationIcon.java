package org.jxmapviewer.demos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jdesktop.swingx.demos.svg.FeatheRnavigation_grey;
import org.jdesktop.swingx.icon.SizingConstants;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import dk.dma.enav.model.geometry.Position;

public class AddNavigationIcon extends MapClickListener {

	private static final Logger LOG = Logger.getLogger(AddNavigationIcon.class.getName());
	private List painters; // TODO type
    private final JXMapViewer viewer; // TODO in super (MapClickListener) fehlt getter für viewer 

	public AddNavigationIcon(JXMapViewer viewer, List painters) {
		super(viewer);
		this.painters = painters;
		this.viewer = viewer;
	}

	@Override
	public void mapClicked(GeoPosition location) {
		Position pos = Position.create(location.getLatitude(), location.getLongitude());
		LOG.info("GeoPosition:"+location + " "+pos);
		
		// rendert ein navigation icon an der gewählten Position
		WaypointPainter<Waypoint> shipLocationPainter = new WaypointPainter<Waypoint>() {
			public Set<Waypoint> getWaypoints() {
				Set<Waypoint> set = new HashSet<Waypoint>();
				set.add(new DefaultWaypoint(location));
				return set;
			}
		};
		shipLocationPainter.setRenderer(new DefaultWaypointRenderer(4*SizingConstants.M/SizingConstants.M, SizingConstants.M
        		, FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M)));
		painters.add(shipLocationPainter);
		CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
        overlayPainter.setCacheable(false);
        overlayPainter.setPainters(painters);
        viewer.setOverlayPainter(overlayPainter);
	}
}
