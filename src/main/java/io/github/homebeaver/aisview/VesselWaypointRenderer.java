package io.github.homebeaver.aisview;

import javax.swing.Icon;

import org.jxmapviewer.viewer.DefaultWaypointRenderer;

public class VesselWaypointRenderer extends DefaultWaypointRenderer {

	public VesselWaypointRenderer(Icon wpIcon) {
		super(wpIcon == null ? 0 : wpIcon.getIconWidth() / 2  // assume adjustX in the middle of icon
			, wpIcon == null ? 0 : wpIcon.getIconHeight() / 2 // assume adjustY in the middle of icon
			, wpIcon);
	}

}
