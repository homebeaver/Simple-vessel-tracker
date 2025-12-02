package io.github.homebeaver.aisview;

import org.jxmapviewer.viewer.TileFactoryInfo;

/*
 * Overlay tiles https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png
 */
public class OpenSeaMapTileFactoryInfo extends TileFactoryInfo {

	private static final int MAX_ZOOM = 19;

	/**
	 * Default constructor
	 */
	public OpenSeaMapTileFactoryInfo() {
		this("OpenSeaMap", "https://tiles.openseamap.org/seamark");
	}

	/**
	 * @param name    the name of the factory
	 * @param baseURL the base URL to load tiles from
	 */
	public OpenSeaMapTileFactoryInfo(String name, String baseURL) {
		super(name, 0, MAX_ZOOM, MAX_ZOOM, // minimumZoomLevel, maximumZoomLevel, totalMapZoom
				256, true, true, // tile size is 256 and x/y orientation is normal
				baseURL, "z", "x", "y"); // 5/15/10.png
	}

	@Override
	public String getTileUrl(int x, int y, int zoom) {
		int invZoom = MAX_ZOOM - zoom;
		String url = this.baseURL + "/" + invZoom + "/" + x + "/" + y + ".png";
		return url;
	}

	@Override
	public String getAttribution() {
		return "\u00A9 OpenStreetMap contributors";
	}

	@Override
	public String getLicense() {
		return "Creative Commons Attribution-ShareAlike 2.0";
	}

}
