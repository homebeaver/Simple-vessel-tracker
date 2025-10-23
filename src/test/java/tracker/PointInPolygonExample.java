package tracker;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.GeometryFactory;

// aus https://codingtechroom.com/question/-determine-point-inside-polygon-java-jts-awt-geotools
public class PointInPolygonExample {
    public static void main(String[] args) {
        // Create a GeometryFactory for generating points and polygons
        GeometryFactory geometryFactory = new GeometryFactory();

        // Define polygon points
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(5, 0),
                new Coordinate(5, 5),
                new Coordinate(0, 5),
                new Coordinate(0, 0) // Closing the polygon
        };
        Polygon polygon = geometryFactory.createPolygon(coordinates);

        // Define a point to check
        Point point = geometryFactory.createPoint(new Coordinate(3, 3));

        // Check if the point is inside the polygon
        boolean isInside = polygon.contains(point);
        System.out.println("Point is inside polygon: " + isInside);
    }
}