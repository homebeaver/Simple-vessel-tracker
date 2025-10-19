package io.github.homebeaver.aisview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.SizingConstants;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import dk.dma.enav.model.geometry.Position;
import io.github.homebeaver.icon.Circle;
import io.github.homebeaver.icon.Crosshair;
import io.github.homebeaver.icon.Vessel;

@Deprecated
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
//		Position pos = Position.create(location.getLatitude(), location.getLongitude());
//		LOG.info("GeoPosition:"+location + " "+pos);
        int zoom = viewer.getZoom();
        LOG.info(String.format("Lat/Lon=(%.2f / %.2f) - Zoom: %d", location.getLatitude(), location.getLongitude(), zoom));
		
		// rendert ein icon an der gewählten Position
		WaypointPainter<Waypoint> shipLocationPainter = new WaypointPainter<Waypoint>() {
			public Set<Waypoint> getWaypoints() {
				Set<Waypoint> set = new HashSet<Waypoint>();
				set.add(new DefaultWaypoint(location));
				return set;
			}
		};
//		RadianceIcon icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
//		RadianceIcon icon = FeatheRnavigation_grey.of(SizingConstants.L, SizingConstants.L);
//		icon.setRotation(SizingConstants.NORTH_EAST);
		RadianceIcon icon = Crosshair.of(SizingConstants.L, SizingConstants.L);
		
		shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
		painters.add(shipLocationPainter);
		CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
        overlayPainter.setCacheable(false);
        overlayPainter.setPainters(painters);
        viewer.setOverlayPainter(overlayPainter);
	}
}
