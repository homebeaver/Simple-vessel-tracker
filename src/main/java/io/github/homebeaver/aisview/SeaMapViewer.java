package io.github.homebeaver.aisview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.logging.Logger;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Tile;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.TileListener;
import org.jxmapviewer.viewer.empty.EmptyTileFactory;

public class SeaMapViewer extends JXMapViewer {

	private static final long serialVersionUID = 8080049277464637881L;
	private static final Logger LOG = Logger.getLogger(SeaMapViewer.class.getName());

	private TileFactory factory;
	private TileFactory factorySea;

	public SeaMapViewer() {
		super();
		factory = new EmptyTileFactory();
		factorySea = new EmptyTileFactory();
	}

	public TileFactory getTileFactory() {
		return factory;
	}

	public TileFactory getTileFactorySea() {
		return factorySea;
	}

	protected void drawMapTiles(final Graphics g, final int zoom, Rectangle viewportBounds) {
//		LOG.info("zoom=" + zoom + " viewportBounds: " + viewportBounds);
//		super.drawMapTiles(g, zoom, viewportBounds);
		int size = getTileFactory().getTileSize(zoom);
		Dimension mapSize = getTileFactory().getMapSize(zoom);

		// calculate the "visible" viewport area in tiles
		int numWide = viewportBounds.width / size + 2;
		int numHigh = viewportBounds.height / size + 2;

		TileFactoryInfo info = getTileFactory().getInfo();

		// number of tiles in x direction
		int tpx = (int) Math.floor(viewportBounds.getX() / info.getTileSize(0));
		// number of tiles in y direction
		int tpy = (int) Math.floor(viewportBounds.getY() / info.getTileSize(0));
//		LOG.info("number of tiles in x direction = "+tpx + " , in y direction = "+tpy);

		// fetch the tiles from the factory and store them in the tiles cache
		// attach the tileLoadListener
		for (int x = 0; x <= numWide; x++) {
			for (int y = 0; y <= numHigh; y++) {
				int itpx = x + tpx;// topLeftTile.getX();
				int itpy = y + tpy;// topLeftTile.getY();
				// only proceed if the specified tile point lies within the area being painted
				if (g.getClipBounds().intersects(
						new Rectangle(itpx * size - viewportBounds.x, itpy * size - viewportBounds.y, size, size))) {
					Tile tileSea = getTileFactorySea().getTile(itpx, itpy, zoom);
					Tile tile = getTileFactory().getTile(itpx, itpy, zoom);
					int ox = ((itpx * getTileFactory().getTileSize(zoom)) - viewportBounds.x);
					int oy = ((itpy * getTileFactory().getTileSize(zoom)) - viewportBounds.y);

					// if the tile is off the map to the north/south, then just don't paint anything
					if (!isTileOnMap(itpx, itpy, mapSize)) {
						if (isOpaque()) {
							g.setColor(getBackground());
							g.fillRect(ox, oy, size, size);
						}
					} else if (tile.isLoaded()) {
						g.drawImage(tile.getImage(), ox, oy, null);
						if (tileSea.isLoaded()) {
							g.drawImage(tileSea.getImage(), ox, oy, null);
						}
					} else {
						Tile superTile = null;

						// Use tile at higher zoom level with 200% magnification and if we are not
						// already at max resolution
						if (zoom < info.getMaximumZoomLevel()) {
							superTile = getTileFactory().getTile(itpx / 2, itpy / 2, zoom + 1);
						}

						if (superTile != null && superTile.isLoaded()) {
							int offX = (itpx % 2) * size / 2;
							int offY = (itpy % 2) * size / 2;
							g.drawImage(superTile.getImage(), ox, oy, ox + size, oy + size, offX, offY, offX + size / 2,
									offY + size / 2, null);
						} else {
							int imageX = (getTileFactory().getTileSize(zoom) - getLoadingImage().getWidth(null)) / 2;
							int imageY = (getTileFactory().getTileSize(zoom) - getLoadingImage().getHeight(null)) / 2;
							g.setColor(Color.GRAY);
							g.fillRect(ox, oy, size, size);
							g.drawImage(getLoadingImage(), ox + imageX, oy + imageY, null);
						}
					}
					if (isDrawTileBorders()) {

						g.setColor(Color.black);
						g.drawRect(ox, oy, size, size);
						g.drawRect(ox + size / 2 - 5, oy + size / 2 - 5, 10, 10);
						g.setColor(Color.white);
						g.drawRect(ox + 1, oy + 1, size, size);

						String text = itpx + ", " + itpy + ", " + getZoom();
						g.setColor(Color.BLACK);
						g.drawString(text, ox + 10, oy + 30);
						g.drawString(text, ox + 10 + 2, oy + 30 + 2);
						g.setColor(Color.WHITE);
						g.drawString(text, ox + 10 + 1, oy + 30 + 1);
					}
				}
			}
		}
	}

	private boolean isTileOnMap(int x, int y, Dimension mapSize) {
		return (y >= 0 && y < mapSize.getHeight()) 
			&& (isInfiniteMapRendering() || x >= 0 && x < mapSize.getWidth());
	}

	public void setTileFactories(TileFactory factory, TileFactory factorySea) {
		if (factorySea != null) {
			setTileFactorySea(factorySea);
		}
		setTileFactory(factory);
	}

	public void setTileFactory(TileFactory factory) {
		if (factory == null) {
			throw new NullPointerException("factory must not be null");
		}
		LOG.config("removeTileListener "+tileLoadListener+" from "+getTileFactory()+" and dispose");
		this.factory.removeTileListener(tileLoadListener);
		this.factory.dispose();

		LOG.config("set "+factory+" , zoom , tileLoadListener and repaint:");
		this.factory = factory;
		this.setZoom(this.factory.getInfo().getDefaultZoomLevel());
		this.factory.addTileListener(tileLoadListener);

		repaint();
	}

	public void setTileFactorySea(TileFactory factory) {
		if (factory == null) {
			throw new NullPointerException("factory must not be null");
		}
//		LOG.info(""+getTileFactorySea()+" dispose");
		factorySea.dispose();

//		LOG.info(" set "+factory);
		this.factorySea = factory;
	}

	private TileListener tileLoadListener = new TileListener() {
		@Override
		public void tileLoaded(Tile tile) {
			if (tile.getZoom() == getZoom()) {
//				LOG.fine("URL:"+tile.getURL()+" z="+tile.getZoom()+" x="+tile.getX()+" y="+tile.getY());
				repaint();
				/*
				 * this optimization doesn't save much and it doesn't work if you wrap around the world 
				 */
//				Rectangle viewportBounds = getViewportBounds(); 
//				TilePoint tilePoint = t.getLocation(); 
//				Point point = new Point(tilePoint.getX() * getTileFactory().getTileSize(), 
//						tilePoint.getY() * getTileFactory().getTileSize()); 
//				Rectangle tileRect = new Rectangle(point, new Dimension(getTileFactory().getTileSize(),
//				  getTileFactory().getTileSize())); 
//				if (viewportBounds.intersects(tileRect)) {
//				  //convert tileRect from world space to viewport space 
//					  repaint(new Rectangle(
//				  tileRect.x - viewportBounds.x, tileRect.y - viewportBounds.y, tileRect.width,
//				  tileRect.height )); 
//				}
			}
		}

	};

}
