/*******************************************************************************
 * Copyright 2019 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package grondag.fermion.intstream;

public abstract class AbstractIntStreamCollection<V extends AbstractStreamCursor<V>> {
    protected IIntStream stream;

    protected final V readCursor;
    protected final V writeCursor;
    protected final V internal;

    /**
     * Ints needed for one entry - including keys, hash values, etc.
     */
    protected final int stride;

    /**
     * Current entry count.
     */
    protected int size = 0;

    @SuppressWarnings("null")
    protected AbstractIntStreamCollection(Class<V> cursorType) {
        V writer = null;
        V reader = null;
        V internal = null;

        try {
            writer = cursorType.newInstance();
            reader = cursorType.newInstance();
            internal = cursorType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        writer.owner = this;
        reader.owner = this;
        internal.owner = this;
        this.writeCursor = writer;
        this.readCursor = reader;
        this.internal = internal;
        this.stride = writer.stride();
    }

    public final V reader() {
        return this.readCursor;
    }

    /**
     * Use to input key/value pair to be added to the map.
     */
    public final V writer() {
        return this.writeCursor;
    }

    public abstract void release();

    final void handleClaim() {
        assert stream == null;
        stream = IntStreams.claim();
    }

    final void handleRelease() {
        this.stream.release();
        this.stream = null;
    }

    public final int size() {
        return size;
    }

    public final boolean isEmpty() {
        return size == 0;
    }

    /**
     * Used by cursors to limit iteration.
     */
    abstract int maxIndex();

}
