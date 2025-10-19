package io.github.homebeaver.icon;

import java.awt.*;
import java.awt.geom.*;
import java.util.Stack;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.RadianceIconUIResource;

/**
 * This class has been automatically generated using 
 * <a href="https://jdesktop.wordpress.com/2022/09/25/svg-icons/">Radiance SVG converter</a>.
 */
public class Settings implements RadianceIcon {
    private Shape shape = null;
    private GeneralPath generalPath = null;
    private Paint paint = null;
    private Stroke stroke = null;
    private RadianceIcon.ColorFilter colorFilter = null;
    private Stack<AffineTransform> transformsStack = new Stack<>();

	// EUG https://github.com/homebeaver (rotation + point/axis reflection)
    private int rsfx = 1, rsfy = 1;
    public void setReflection(boolean horizontal, boolean vertical) {
    	this.rsfx = vertical ? -1 : 1;
    	this.rsfy = horizontal ? -1 : 1;
    }    
    public boolean isReflection() {
		return rsfx==-1 || rsfy==-1;
	}
	
    private double theta = 0;
    public void setRotation(double theta) {
    	this.theta = theta;
    }    
    public double getRotation() {
		return theta;
	}
	// EUG -- END

    

	private void _paint0(Graphics2D g,float origAlpha) {
transformsStack.push(g.getTransform());
// 
g.setComposite(AlphaComposite.getInstance(3, 1.0f * origAlpha));
transformsStack.push(g.getTransform());
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -0.0f, -0.0f));
// _0
g.setComposite(AlphaComposite.getInstance(3, 1.0f * origAlpha));
transformsStack.push(g.getTransform());
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_0
paint = (colorFilter != null) ? colorFilter.filter(new Color(0, 0, 0, 255)) : new Color(0, 0, 0, 255);
stroke = new BasicStroke(2.0f,1,1,4.0f,null,0.0f);
shape = new Ellipse2D.Double(9.0, 9.0, 6.0, 6.0);
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
g.setTransform(transformsStack.pop());
g.setComposite(AlphaComposite.getInstance(3, 1.0f * origAlpha));
transformsStack.push(g.getTransform());
g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
// _0_1
paint = (colorFilter != null) ? colorFilter.filter(new Color(0, 0, 0, 255)) : new Color(0, 0, 0, 255);
stroke = new BasicStroke(2.0f,1,1,4.0f,null,0.0f);
if (generalPath == null) {
   generalPath = new GeneralPath();
} else {
   generalPath.reset();
}
generalPath.moveTo(19.4f, 15.0f);
generalPath.curveTo(19.127653f, 15.61709f, 19.258333f, 16.337812f, 19.73f, 16.82f);
generalPath.lineTo(19.789999f, 16.88f);
generalPath.curveTo(20.165552f, 17.255136f, 20.376572f, 17.764181f, 20.376572f, 18.294998f);
generalPath.curveTo(20.376572f, 18.825817f, 20.165552f, 19.334862f, 19.789999f, 19.71f);
generalPath.curveTo(19.414862f, 20.085552f, 18.905817f, 20.296572f, 18.375f, 20.296572f);
generalPath.curveTo(17.844181f, 20.296572f, 17.335136f, 20.085552f, 16.96f, 19.71f);
generalPath.lineTo(16.9f, 19.65f);
generalPath.curveTo(16.417812f, 19.178333f, 15.69709f, 19.047653f, 15.08f, 19.32f);
generalPath.curveTo(14.47553f, 19.579067f, 14.082623f, 20.172358f, 14.08f, 20.83f);
generalPath.lineTo(14.08f, 21.0f);
generalPath.curveTo(14.08f, 22.10457f, 13.184569f, 23.0f, 12.08f, 23.0f);
generalPath.curveTo(10.9754305f, 23.0f, 10.08f, 22.10457f, 10.08f, 21.0f);
generalPath.lineTo(10.08f, 20.91f);
generalPath.curveTo(10.064157f, 20.23267f, 9.635872f, 19.633865f, 9.0f, 19.4f);
generalPath.curveTo(8.38291f, 19.127653f, 7.6621866f, 19.258333f, 7.18f, 19.73f);
generalPath.lineTo(7.12f, 19.789999f);
generalPath.curveTo(6.7448635f, 20.165552f, 6.2358184f, 20.376572f, 5.705f, 20.376572f);
generalPath.curveTo(5.1741815f, 20.376572f, 4.6651363f, 20.165552f, 4.29f, 19.789999f);
generalPath.curveTo(3.9144459f, 19.414862f, 3.7034266f, 18.905817f, 3.7034266f, 18.375f);
generalPath.curveTo(3.7034266f, 17.844181f, 3.9144459f, 17.335136f, 4.29f, 16.96f);
generalPath.lineTo(4.35f, 16.9f);
generalPath.curveTo(4.821666f, 16.417812f, 4.9523463f, 15.69709f, 4.68f, 15.08f);
generalPath.curveTo(4.4209313f, 14.47553f, 3.8276427f, 14.082623f, 3.1699998f, 14.08f);
generalPath.lineTo(3.0f, 14.08f);
generalPath.curveTo(1.8954304f, 14.08f, 1.0f, 13.184569f, 1.0f, 12.08f);
generalPath.curveTo(1.0f, 10.9754305f, 1.8954304f, 10.08f, 3.0f, 10.08f);
generalPath.lineTo(3.09f, 10.08f);
generalPath.curveTo(3.767329f, 10.064157f, 4.3661346f, 9.635872f, 4.6f, 9.0f);
generalPath.curveTo(4.8723464f, 8.38291f, 4.741666f, 7.6621866f, 4.27f, 7.18f);
generalPath.lineTo(4.21f, 7.12f);
generalPath.curveTo(3.834446f, 6.7448635f, 3.6234267f, 6.2358184f, 3.6234267f, 5.705f);
generalPath.curveTo(3.6234267f, 5.1741815f, 3.834446f, 4.6651363f, 4.21f, 4.29f);
generalPath.curveTo(4.5851364f, 3.9144459f, 5.0941815f, 3.7034266f, 5.625f, 3.7034266f);
generalPath.curveTo(6.1558185f, 3.7034266f, 6.6648636f, 3.9144459f, 7.04f, 4.29f);
generalPath.lineTo(7.1f, 4.35f);
generalPath.curveTo(7.5821867f, 4.821666f, 8.30291f, 4.9523463f, 8.92f, 4.68f);
generalPath.lineTo(9.0f, 4.68f);
generalPath.curveTo(9.60447f, 4.4209313f, 9.997377f, 3.8276427f, 10.0f, 3.1699998f);
generalPath.lineTo(10.0f, 3.0f);
generalPath.curveTo(10.0f, 1.8954304f, 10.895431f, 1.0f, 12.0f, 1.0f);
generalPath.curveTo(13.104569f, 1.0f, 14.0f, 1.8954304f, 14.0f, 3.0f);
generalPath.lineTo(14.0f, 3.09f);
generalPath.curveTo(14.002623f, 3.7476428f, 14.39553f, 4.3409314f, 15.0f, 4.6f);
generalPath.curveTo(15.61709f, 4.8723464f, 16.337812f, 4.741666f, 16.82f, 4.27f);
generalPath.lineTo(16.88f, 4.21f);
generalPath.curveTo(17.255136f, 3.834446f, 17.764181f, 3.6234267f, 18.294998f, 3.6234267f);
generalPath.curveTo(18.825817f, 3.6234267f, 19.334862f, 3.834446f, 19.71f, 4.21f);
generalPath.curveTo(20.085552f, 4.5851364f, 20.296572f, 5.0941815f, 20.296572f, 5.625f);
generalPath.curveTo(20.296572f, 6.1558185f, 20.085552f, 6.6648636f, 19.71f, 7.04f);
generalPath.lineTo(19.65f, 7.1f);
generalPath.curveTo(19.178333f, 7.5821867f, 19.047653f, 8.30291f, 19.32f, 8.92f);
generalPath.lineTo(19.32f, 9.0f);
generalPath.curveTo(19.579067f, 9.60447f, 20.172358f, 9.997377f, 20.83f, 10.0f);
generalPath.lineTo(21.0f, 10.0f);
generalPath.curveTo(22.10457f, 10.0f, 23.0f, 10.895431f, 23.0f, 12.0f);
generalPath.curveTo(23.0f, 13.104569f, 22.10457f, 14.0f, 21.0f, 14.0f);
generalPath.lineTo(20.91f, 14.0f);
generalPath.curveTo(20.252357f, 14.002623f, 19.659067f, 14.39553f, 19.4f, 15.0f);
generalPath.closePath();
shape = generalPath;
g.setPaint(paint);
g.setStroke(stroke);
g.draw(shape);
g.setTransform(transformsStack.pop());
g.setTransform(transformsStack.pop());
g.setTransform(transformsStack.pop());

}



	private void innerPaint(Graphics2D g) {
        float origAlpha = 1.0f;
        Composite origComposite = g.getComposite();
        if (origComposite instanceof AlphaComposite) {
            AlphaComposite origAlphaComposite = 
                (AlphaComposite)origComposite;
            if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                origAlpha = origAlphaComposite.getAlpha();
            }
        }
        
	    _paint0(g, origAlpha);


	    shape = null;
	    generalPath = null;
	    paint = null;
	    stroke = null;
        transformsStack.clear();
	}

    /**
     * Returns the X of the bounding box of the original SVG image.
     * 
     * @return The X of the bounding box of the original SVG image.
     */
    public static double getOrigX() {
        return 0.0;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static double getOrigY() {
        return 0.0;
    }

	/**
	 * Returns the width of the bounding box of the original SVG image.
	 * 
	 * @return The width of the bounding box of the original SVG image.
	 */
	public static double getOrigWidth() {
		return 24.0;
	}

	/**
	 * Returns the height of the bounding box of the original SVG image.
	 * 
	 * @return The height of the bounding box of the original SVG image.
	 */
	public static double getOrigHeight() {
		return 24.0;
	}

	/** The current width of this icon. */
	private int width;

    /** The current height of this icon. */
	private int height;

	/**
	 * Creates a new transcoded SVG image. This is marked as private to indicate that app
	 * code should be using the {@link #of(int, int)} method to obtain a pre-configured instance.
	 */
	private Settings() {
        this.width = (int) getOrigWidth();
        this.height = (int) getOrigHeight();
	}

    @Override
	public int getIconHeight() {
		return height;
	}

    @Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public synchronized void setDimension(Dimension newDimension) {
		this.width = newDimension.width;
		this.height = newDimension.height;
	}

    @Override
    public boolean supportsColorFilter() {
        return true;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
    }

    @Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if(getRotation()!=0) {
            g2d.rotate(getRotation(), x+width/2, y+height/2);
        }
        if(isReflection()) {
        	g2d.translate(x+width/2, y+height/2);
        	g2d.scale(this.rsfx, this.rsfy);
        	g2d.translate(-x-width/2, -y-height/2);
        }
		g2d.translate(x, y);

        double coef1 = (double) this.width / getOrigWidth();
        double coef2 = (double) this.height / getOrigHeight();
        double coef = Math.min(coef1, coef2);
        g2d.clipRect(0, 0, this.width, this.height);
        g2d.scale(coef, coef);
        g2d.translate(-getOrigX(), -getOrigY());
        if (coef1 != coef2) {
            if (coef1 < coef2) {
               int extraDy = (int) ((getOrigWidth() - getOrigHeight()) / 2.0);
               g2d.translate(0, extraDy);
            } else {
               int extraDx = (int) ((getOrigHeight() - getOrigWidth()) / 2.0);
               g2d.translate(extraDx, 0);
            }
        }
        Graphics2D g2ForInner = (Graphics2D) g2d.create();
        innerPaint(g2ForInner);
        g2ForInner.dispose();
        g2d.dispose();
	}
    
    /**
     * Returns a new instance of this icon with specified dimensions.
     *
     * @param width Required width of the icon
     * @param height Required height of the icon
     * @return A new instance of this icon with specified dimensions.
     */
    public static RadianceIcon of(int width, int height) {
       Settings base = new Settings();
       base.width = width;
       base.height = height;
       return base;
    }

    /**
     * Returns a new {@link UIResource} instance of this icon with specified dimensions.
     *
     * @param width Required width of the icon
     * @param height Required height of the icon
     * @return A new {@link UIResource} instance of this icon with specified dimensions.
     */
    public static RadianceIconUIResource uiResourceOf(int width, int height) {
       Settings base = new Settings();
       base.width = width;
       base.height = height;
       return new RadianceIconUIResource(base);
    }

    /**
     * Returns a factory that returns instances of this icon on demand.
     *
     * @return Factory that returns instances of this icon on demand.
     */
    public static Factory factory() {
        return Settings::new;
    }
}

