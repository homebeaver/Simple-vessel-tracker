package io.github.homebeaver.aisview;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import org.jxmapviewer.viewer.GeoPosition;

public class Regions {

	public static final String DEFAULT_REGION = "Øresund";

	private static Regions instance = null; // SINGLETON

	public static Regions getInstance() {
		if (instance == null) {
			instance = new Regions();
		}
		return instance;
	}

	// The preferred size of the panel
	static int PREFERRED_WIDTH = 680;
	static int PREFERRED_HEIGHT = 600;
	public static final Dimension PREFERRED_SIZE = new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);

	class Region {
		String name;
		String boundingBox;
		int zoom;
		String descriptionCenter;
		GeoPosition center; // aka setAddressLocation
		
		public String getBoundingBox() {
			return boundingBox;
		}
		public int getZoom() {
			return zoom;
		}
		public GeoPosition getGeoPosition() {
			return center;
		}
		
		public Region name(String name) {
			this.name = name;
			return this;
		}
		public Region boundingBox(String boundingBox) {
			this.boundingBox = boundingBox;
			return this;
		}
		public Region zoom(int zoom) {
			this.zoom = zoom;
			return this;
		}
		public Region descriptionCenter(String descriptionCenter) {
			this.descriptionCenter = descriptionCenter;
			return this;
		}
		public Region center(GeoPosition center) {
			this.center = center;
			return this;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(descriptionCenter).append(" - ").append(name);
			return sb.toString();
		}
		
	}

	private final Map<String, Region> nameToRegion = new HashMap<>();

	private Regions() {
		Region r = new Region();
		r = r.name("Global").descriptionCenter("World")
			.boundingBox("[[[-180, -90], [180, 90]]]")
			.zoom(18)
			.center(new GeoPosition(5.550, -0.200));
		nameToRegion.put(r.name, r);

		r = new Region();
		r = r.name("English Channel").descriptionCenter("Dover") // Ärmelkanal
			.boundingBox("[[[51.670, -2.500], [48.000, 2.500]]]") // NW, SE
			.zoom(10)
			.center(new GeoPosition(51,8,0, 1,19,0));
		nameToRegion.put(r.name, r);

// TODO Suezkanal , West- und Ostküste der USA , Karibik , Straße von Singapur , Ostsee , Istanbul

		r = new Region();
		r = r.name("Panama Canal").descriptionCenter("Panama") // Panamekanal
			.boundingBox("[[[9.300, -80.000], [8.200, -79.000]]]") // NW, SE
			.zoom(9)
			.center(new GeoPosition(8,58,15, -80,27,5));
		nameToRegion.put(r.name, r);

		r = new Region();
		r = r.name(DEFAULT_REGION).descriptionCenter("København")
			.boundingBox("[[[56.2, 13.5], [55.2, 11.6]]]") // NE, SW
			.zoom(10)
			.center(new GeoPosition(55.70, 12.54));
		nameToRegion.put(r.name, r);
	}
	
	public String getBoundingBox(String name) {
		return nameToRegion.get(name).boundingBox;
	}
	
	public GeoPosition getCenter(String name) {
		return nameToRegion.get(name).center;
	}
	
	public int getZoom(String name) {
		return nameToRegion.get(name).zoom;
	}
	
	public String toString(String name) {
		return nameToRegion.get(name).toString();
	}
	
	public Map<String, Region> getRegions() {
		Map<String, Region> result = new HashMap<>();
		nameToRegion.forEach((k, v) -> {
			result.put(v.toString(), v);
		});
		return result;
	}

}
