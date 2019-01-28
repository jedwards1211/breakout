/*
 * Copyright 2012 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package jogamp.opengl.util.awt.text;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.packrect.BackingStoreManager;

import jogamp.opengl.util.awt.text.TextureBackingStore.EventListener;
import jogamp.opengl.util.awt.text.TextureBackingStore.EventType;


/**
 * Handler for allocating and reallocating texture backing stores.
 *
 * <p>
 * When a backing store is no longer big enough a new backing store needs to be created to replace
 * it.  Accordingly, the data from the old backing store should be copied to the new one.
 *
 * <p>
 * Throughout this process decisions may need to be made about getting rid of old entries and
 * handling failures.  {@code TextureBackingStoreManager} handles these issues, although it
 * delegates some actions to observers by firing backing store events.
 */
final class TextureBackingStoreManager implements BackingStoreManager {

    /**
     * Whether or not texture backing store manager should print debugging information.
     */
    private static final boolean DEBUG = false;

    /**
     * Observers of backing store events.
     */
    /*@Nonnull*/
    private final List<EventListener> listeners = new ArrayList<EventListener>();

    /**
     * Style of text.
     */
    /*@Nonnull*/
    private final Font font;

    /**
     * True to render smooth edges.
     */
    private final boolean antialias;

    /**
     * True to use subpixel accuracy.
     */
    private final boolean subpixel;

    /**
     * True for high quality texturing.
     */
    private final boolean mipmap;

    /**
     * True to interpolate samples.
     */
    private boolean smooth = false;

    /**
     * Constructs a {@link TextureBackingStoreManager}.
     *
     * @param font Style of text
     * @param antialias True to render smooth edges
     * @param subpixel True to use subpixel accuracy
     * @param mipmap True for high quality texturing
     * @throws NullPointerException if font is null
     */
    TextureBackingStoreManager(/*@Nonnull*/ final Font font,
                               final boolean antialias,
                               final boolean subpixel,
                               final boolean mipmap) {

        Check.notNull(font, "Font cannot be null");

        this.font = font;
        this.antialias = antialias;
        this.subpixel = subpixel;
        this.mipmap = mipmap;
    }

    /**
     * Flushes all currently drawn text to the graphics card
     */
    @Override
    public void flush() {
        // Print debugging information
        if (DEBUG) {
            System.err.println("*** Flushing backing store ***");
        }

        // Pass event to observers
        fireEvent(EventType.AUTOMATIC_FLUSH);
    }

    /**
     * Adds an object that wants to be notified of events.
     *
     * <p>The observer will be notified when:
     *
     * <ul>
     * <li>a backing store needs to be expanded and
     * <li>an item cannot be added to the backing store.
     * </ul>
     *
     * @param listener Observer of backing store events
     * @throws NullPointerException if listener is null
     */
    void addListener(/*@Nonnull*/ final EventListener listener) {

        Check.notNull(listener, "Listener cannot be null");

        listeners.add(listener);
    }

    /**
     * Creates a new backing store for the packer.
     *
     * @param width Width of new backing store
     * @param height Height of new backing store
     * @return New backing store, not null
     * @throws IllegalArgumentException if width or height is negative
     */
    /*@Nonnull*/
    @Override
    public Object allocateBackingStore(/*@Nonnegative*/ final int width,
                                       /*@Nonnegative*/ final int height) {

        Check.argument(width >= 0, "Width is negative");
        Check.argument(height >= 0, "Height is negative");

        // Print debugging information
        if (DEBUG) {
            System.err.printf("Make back store %d x %d\n", width, height);
        }

        // Make a new backing store
        return new TextureBackingStore(
                width, height,
                font,
                antialias, subpixel,
                smooth, mipmap);
    }

    /**
     * Disposes of a backing store.
     *
     * <p>
     * Happens immediately before a backing store needs to be expanded, since the manager will
     * actually make a new one.
     *
     * @param bs Backing store being deleted
     * @throws NullPointerException if backing store is null
     * @throws ClassCastException if backing store is not a {@code TextureBackingStore}
     */
    @Override
    public void deleteBackingStore(/*@Nonnull*/ final Object bs) {

        Check.notNull(bs, "Backing store cannot be null");

        // Dispose the backing store
        final GL gl = GLContext.getCurrentGL();
        final TextureBackingStore tbs = (TextureBackingStore) bs;
        tbs.dispose(gl);
    }

    /**
     * Sends an event to all listeners.
     *
     * @param type Type of event to send, assumed not null
     */
    private void fireEvent(/*@Nonnull*/ final EventType type) {
        for (final EventListener listener : listeners) {
            assert listener != null : "addListener rejects null";
            listener.onBackingStoreEvent(type);
        }
    }

    /**
     * Returns true if is interpolating samples.
     */
    final boolean getUseSmoothing() {
        return smooth;
    }

    /**
     * Changes whether texture should interpolate samples.
     *
     * @param useSmoothing True if texture should interpolate
     */
    final void setUseSmoothing(final boolean useSmoothing) {
        this.smooth = useSmoothing;
    }
}
