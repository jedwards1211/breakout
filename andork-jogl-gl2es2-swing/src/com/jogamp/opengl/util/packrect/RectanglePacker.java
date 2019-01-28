/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package com.jogamp.opengl.util.packrect;

import java.util.*;

/** Packs rectangles supplied by the user (typically representing
    image regions) into a larger backing store rectangle (typically
    representing a large texture). Supports automatic compaction of
    the space on the backing store, and automatic expansion of the
    backing store, when necessary. */

public class RectanglePacker {

  private static final float DEFAULT_EXPANSION_FACTOR = 0.5f;

  private final BackingStoreManager manager;
  private Object backingStore;
  private LevelSet levels;
  private final float EXPANSION_FACTOR;

  private int maxWidth  = -1;
  private int maxHeight = -1;

  static class RectHComparator implements Comparator<Rect> {
    @Override
    public int compare(final Rect r1, final Rect r2) {
      return r2.h() - r1.h();
    }

    @Override
    public boolean equals(final Object obj) {
      return this == obj;
    }
  }

  public RectanglePacker(final BackingStoreManager manager,
                         final int initialWidth,
                         final int initialHeight) {
    this(manager, initialWidth, initialHeight, DEFAULT_EXPANSION_FACTOR);
  }

  public RectanglePacker(final BackingStoreManager manager,
                         final int initialWidth,
                         final int initialHeight,
                         final float expansionFactor) {
    this.manager = manager;
    levels = new LevelSet(initialWidth, initialHeight);
    EXPANSION_FACTOR = expansionFactor;
  }

  public Object getBackingStore() {
    if (backingStore == null) {
      backingStore = manager.allocateBackingStore(levels.w(), levels.h());
    }

    return backingStore;
  }

  /** Sets up a maximum width and height for the backing store. These
      are optional and if not specified the backing store will grow as
      necessary. Setting up a maximum width and height introduces the
      possibility that additions will fail; these are handled with the
      BackingStoreManager's allocationFailed notification. */
  public void setMaxSize(final int maxWidth, final int maxHeight) {
    this.maxWidth  = maxWidth;
    this.maxHeight = maxHeight;
  }

  /** Decides upon an (x, y) position for the given rectangle (leaving
      its width and height unchanged) and places it on the backing
      store. May provoke re-layout of other Rects already added. If
      the BackingStoreManager does not support compaction, and {@link
      BackingStoreManager#preExpand BackingStoreManager.preExpand}
      does not clear enough space for the incoming rectangle, then
      this method will throw a RuntimeException. */
  public void add(final Rect rect) throws RuntimeException {
    // Allocate backing store if we don't have any yet
    getBackingStore();

    if (!levels.add(rect)) {
        manager.flush();
        int newWidth = Math.max((int) (levels.w() * (1.0f + EXPANSION_FACTOR)), rect.w());
        int newHeight = Math.max((int) (levels.h() * (1.0f + EXPANSION_FACTOR)), rect.h());
        if (newWidth > maxWidth) {
        	if (rect.w() > maxWidth) {
				throw new RuntimeException("Rect is wider than maxWidth");
        	}
        	newWidth = maxWidth;
        }
        if (newHeight > maxHeight) {
        	if (rect.h() > maxHeight) {
				throw new RuntimeException("Rect is taller than maxHeight");
        	}
        	newHeight = maxHeight;
        }
        if (newWidth != levels.w() || newHeight != levels.h()) {
        	levels = new LevelSet(newWidth, newHeight);
        	// reallocate backing store
        	backingStore = null;
        	getBackingStore();
        } else {
        	levels.clear();
        }
        if (!levels.add(rect)) {
        	throw new RuntimeException("Failed to add rect after flushing");
        }
        
    }
  }

  /** Removes the given rectangle from this RectanglePacker. */
  public void remove(final Rect rect) {
    levels.remove(rect);
  }

  /** Visits all Rects contained in this RectanglePacker. */
  public void visit(final RectVisitor visitor) {
    levels.visit(visitor);
  }

  /** Returns the vertical fragmentation ratio of this
      RectanglePacker. This is defined as the ratio of the sum of the
      heights of all completely empty Levels divided by the overall
      used height of the LevelSet. A high vertical fragmentation ratio
      indicates that it may be profitable to perform a compaction. */
  public float verticalFragmentationRatio() {
    return levels.verticalFragmentationRatio();
  }

  /** Clears all Rects contained in this RectanglePacker. */
  public void clear() {
    levels.clear();
  }

  /** Disposes the backing store allocated by the
      BackingStoreManager. This RectanglePacker may no longer be used
      after calling this method. */
  public void dispose() {
    if (backingStore != null)
      manager.deleteBackingStore(backingStore);
    backingStore = null;
    levels = null;
  }
}
