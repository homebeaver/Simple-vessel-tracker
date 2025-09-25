package org.jxmapviewer.demos;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Painter;

import org.jdesktop.beans.JavaBean;
import org.jdesktop.swingx.painter.AbstractPainter;

/*
 * ich implementiere hier org.jdesktop.swingx.painter.CompoundPainter nochmal
 * und statt private Painter<?>[] painters ==> List<Painter<T>> wie in jxmapviewer2 implementiert
 */
@JavaBean
public class CompoundPainter<T> extends AbstractPainter<T> {
	
	private static final Logger LOG = Logger.getLogger(CompoundPainter.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override // implements the abstract method AbstractPainter.doPaint
	protected void doPaint(Graphics2D g, T component, int width, int height) {
		for (Painter<?> p : getPainters()) {
			Graphics2D temp = (Graphics2D) g.create();

			try {
				Painter<T> painter = (Painter<T>)p;
				painter.paint(temp, component, width, height);
				if (isClipPreserved()) {
					g.setClip(temp.getClip());
				}
			} finally {
				temp.dispose();
			}
		}
	}

    private static class Handler implements PropertyChangeListener {
        private final WeakReference<CompoundPainter<?>> ref;
        
        public Handler(CompoundPainter<?> painter) {
            ref = new WeakReference<CompoundPainter<?>>(painter);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            CompoundPainter<?> painter = ref.get();
            
            if (painter == null) {
                AbstractPainter<?> src = (AbstractPainter<?>) evt.getSource();
                src.removePropertyChangeListener(this);
            } else {
                String property = evt.getPropertyName();
                
                if ("dirty".equals(property) && evt.getNewValue() == Boolean.FALSE) {
                    return;
                }
                
                painter.setDirty(true);
            }
        }
    }
    
    private Handler handler;
    
	private List<Painter<T>> painters = new ArrayList<Painter<T>>(10);

    private AffineTransform transform;
    private boolean clipPreserved = false;

    private boolean checkForDirtyChildPainters = true;

    /** Creates a new instance of CompoundPainter */
    public CompoundPainter() {
        this((Painter<T>[]) null);
   }
    
    /**
     * Convenience constructor for creating a CompoundPainter for an array
     * of painters. A defensive copy of the given array is made, so that future
     * modification to the array does not result in changes to the CompoundPainter.
     *
     * @param painters array of painters, which will be painted in order
     */
    public CompoundPainter(Painter<?>... painters) {
        handler = new Handler(this);
        
        setPainters(painters);
    }

    /**
     * Convenience constructor for creating a CompoundPainter for a list of painters.
     * @param painters list of painters
     */
    public CompoundPainter(List<? extends Painter<T>> painters) {
        handler = new Handler(this);

        setPainters(painters);
    }

    /**
     * Sets the array of Painters to use. These painters will be executed in
     * order. A null value will be treated as an empty array. To prevent unexpected 
     * behavior all values in provided array are copied to internally held array. 
     * Any changes to the original array will not be reflected.
     *
     * @param painters array of painters, which will be painted in order
     */
    public void setPainters(Painter<?>... painters) {
        Painter<?>[] old = getPaintersAsArray();
        for (Painter<?> p : old) {
            if (p instanceof AbstractPainter) {
                ((AbstractPainter<?>) p).removePropertyChangeListener(handler);
            }
        }
        
        this.painters = new ArrayList<Painter<T>>();
        if (painters != null) {
            System.arraycopy(painters, 0, this.painters, 0, painters.length);
        }
        
        for (Painter<?> p : this.painters) {
            if (p instanceof AbstractPainter) {
                ((AbstractPainter<?>) p).addPropertyChangeListener(handler);
            }
        }
        
        setDirty(true);
        firePropertyChange("painters", old, getPainters());
    }
    /**
     * 
     * @param painters in a list
     */
    public void setPainters(List<? extends Painter<T>> painters) {
        Collection<Painter<T>> old = new ArrayList<Painter<T>>(getPainters());
        
        for (Painter<?> p : old) {
            if (p instanceof AbstractPainter) {
                ((AbstractPainter<?>) p).removePropertyChangeListener(handler);
            }
        }

        this.painters = new ArrayList<Painter<T>>();
        for (Painter<T> p : painters) {
        	this.painters.add(p);
        }

        for (Painter<?> p : this.painters) {
            if (p instanceof AbstractPainter ap) {
                ap.addPropertyChangeListener(handler);
            }
        }

        setDirty(true);
        firePropertyChange("painters", old, getPainters());
    }
    
    /**
     * Gets the array of painters used by this CompoundPainter
     * @return a defensive copy of the painters used by this CompoundPainter.
     *         This will never be null.
     */
    public final Painter<?>[] getPaintersAsArray() {
        Painter<?>[] results = new Painter<?>[painters.size()];
        System.arraycopy(painters.toArray(), 0, results, 0, results.length);
        return results;
    }
    // XXX neu DONE
	public final Collection<Painter<T>> getPainters() {
		return Collections.unmodifiableCollection(painters);
	}
    
    // XXX neu DONE 
    public void addPainter(Painter<T> painter) {
        Collection<Painter<T>> old = new ArrayList<Painter<T>>(getPainters());
        LOG.fine("painters.size="+painters.size() + " addPainter "+painter);
        painters.forEach( p -> {
            LOG.finer("painters.size="+painters.size() + " ... "+p);
        });
        this.painters.add(painter);
        
        if (painter instanceof AbstractPainter ap) {
            ap.addPropertyChangeListener(handler);
        }

        setDirty(true);
        firePropertyChange("painters", old, getPainters());
    }
    // XXX neu DONE 
    public void removePainter(Painter<T> painter) {
        Collection<Painter<T>> old = new ArrayList<Painter<T>>(getPainters());
        
        boolean done = this.painters.remove(painter);
        LOG.fine("removePainter done="+done + " "+painter);
        
        if (painter instanceof AbstractPainter ap) {
            ap.removePropertyChangeListener(handler);
        }

        setDirty(true);
        firePropertyChange("painters", old, getPainters());
    }
    
    /**
     * Indicates if the clip produced by any painter is left set once it finishes painting. 
     * Normally the clip will be reset between each painter. Setting clipPreserved to
     * true can be used to let one painter mask other painters that come after it.
     * @return if the clip should be preserved
     * @see #setClipPreserved(boolean)
     */
    public boolean isClipPreserved() {
        return clipPreserved;
    }
    
    /**
     * Sets if the clip should be preserved.
     * Normally the clip will be reset between each painter. Setting clipPreserved to
     * true can be used to let one painter mask other painters that come after it.
     * 
     * @param shouldRestoreState new value of the clipPreserved property
     * @see #isClipPreserved()
     */
    public void setClipPreserved(boolean shouldRestoreState) {
        boolean oldShouldRestoreState = isClipPreserved();
        this.clipPreserved = shouldRestoreState;
        setDirty(true);
        firePropertyChange("clipPreserved",oldShouldRestoreState,shouldRestoreState);
    }

    /**
     * Gets the current transform applied to all painters in this CompoundPainter. May be null.
     * @return the current AffineTransform
     */
    public AffineTransform getTransform() {
        return transform;
    }

    /**
     * Set a transform to be applied to all painters contained in this CompoundPainter
     * @param transform a new AffineTransform
     */
    public void setTransform(AffineTransform transform) {
        AffineTransform old = getTransform();
        this.transform = transform;
        setDirty(true);
        firePropertyChange("transform",old,transform);
    }
    
    /**
     * <p>Iterates over all child <code>Painter</code>s and gives them a chance
     * to validate themselves. If any of the child painters are dirty, then
     * this <code>CompoundPainter</code> marks itself as dirty.</p>
     *
     * {@inheritDoc}
     */
    @Override
    protected void validate(T object) {
        boolean dirty = false;
        for (Painter<?> p : painters) {
            //if (p instanceof AbstractPainter) {
			// AbstractPainter<T> ap = (AbstractPainter<T>) p;
        	// ap.validate(object); // not visible ??? XXX warum? ist doch protected, dto ap.isDirty()
            if (p instanceof CompoundPainter) {
            	@SuppressWarnings("unchecked")
				CompoundPainter<T> ap = (CompoundPainter<T>)p;
                ap.validate(object);
                if (ap.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }
        clearLocalCacheOnly = true;
        setDirty(dirty); //super will call clear cache
        clearLocalCacheOnly = false;
    }

    //indicates whether the local cache should be cleared only, as opposed to the
    //cache's of all of the children. This is needed to optimize the caching strategy
    //when, during validate, the CompoundPainter is marked as dirty
    private boolean clearLocalCacheOnly = false;

    /**
     * Used by {@link #isDirty()} to check if the child <code>Painter</code>s
     * should be checked for their <code>dirty</code> flag as part of
     * processing.<br>
     * Default value is: <code>true</code><br>
     * This should be set to <code>false</code> if the cacheable state
     * of the child <code>Painter</code>s are different from each other.  
     * This will allow the cacheable == <code>true</code> <code>Painter</code>s to
     * keep their cached image during regular repaints.  
     * In this case, client code should call {@link #clearCache()} manually when the cacheable
     * <code>Painter</code>s should be updated.
     *
     * @return <code>false</code> if the cacheable state of the child <code>Painter</code>s are different from each other
     *
     * @see #isDirty()
     */
    public boolean isCheckingDirtyChildPainters() {
        return checkForDirtyChildPainters;
    }

    /**
     * Set the flag used by {@link #isDirty()} to check if the 
     * child <code>Painter</code>s should be checked for their 
     * <code>dirty</code> flag as part of processing.
     * 
     * @param b checkForDirtyChildPainters
     *
     * @see #isCheckingDirtyChildPainters()
     * @see #isDirty()
     */
    public void setCheckingDirtyChildPainters(boolean b) {
        boolean old = isCheckingDirtyChildPainters();
        this.checkForDirtyChildPainters = b;
        firePropertyChange("checkingDirtyChildPainters",old, isCheckingDirtyChildPainters());
    }

    /**
     * {@inheritDoc}
     * 
     * This <code>CompoundPainter</code> is dirty if it, or (optionally) any of its children,
     *       are dirty. If the super implementation returns <code>true</code>, we return
     *       <code>true</code>. Otherwise, if {@link #isCheckingDirtyChildPainters()} is
     *       <code>true</code>, we iterate over all child <code>Painter</code>s and query them to
     *       see if they are dirty. If so, then <code>true</code> is returned. Otherwise, we return
     *       <code>false</code>.
     * @see #isCheckingDirtyChildPainters()
     */
    @Override
    protected boolean isDirty() {
        boolean dirty = super.isDirty();
        if (dirty) {
            return true;
        } 
		// else
        if (isCheckingDirtyChildPainters()) {
            for (Painter<?> p : painters) {
//                if (p instanceof AbstractPainter) {
//                    AbstractPainter<?> ap = (AbstractPainter<?>) p;
                if (p instanceof CompoundPainter) {
                	CompoundPainter<?> ap = (CompoundPainter<?>) p;
                    if (ap.isDirty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDirty(boolean d) {
        boolean old = super.isDirty();
        boolean ours = isDirty();
        
        super.setDirty(d);
        
        //must perform this check to ensure we do not double notify
        if (d != old && d == ours) {
            firePropertyChange("dirty", old, isDirty());
        }
    }

    /**
     * <p>Clears the cache of this <code>Painter</code>, and all child
     * <code>Painters</code>. This is done to ensure that resources
     * are collected, even if clearCache is called by some framework
     * or other code that doesn't realize this is a CompoundPainter.</p>
     *
     * <p>Call #clearLocalCache if you only want to clear the cache of this
     * <code>CompoundPainter</code>
     *
     * {@inheritDoc}
     */
    @Override
    public void clearCache() {
        if (!clearLocalCacheOnly) {
            for (Painter<?> p : painters) {
                if (p instanceof AbstractPainter) {
                    AbstractPainter<?> ap = (AbstractPainter<?>) p;
                    ap.clearCache();
                }
            }
        }
        super.clearCache();
    }

    /**
     * <p>Clears the cache of this painter only, and not of any of the children.</p>
     */
    public void clearLocalCache() {
        super.clearCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureGraphics(Graphics2D g) {
        //applies the transform
        AffineTransform tx = getTransform();
        if (tx != null) {
            g.setTransform(tx);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldUseCache() {
        return super.shouldUseCache(); // || (painters != null && painters.length > 1);
    }
}
