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

public abstract class AbstractStreamCursor<T extends AbstractStreamCursor<T>> {
    AbstractIntStreamCollection<T> owner;

    int index = 0;

    int address = 0;

    /**
     * Ints needed for one entry - including keys, hash and values.
     */
    abstract int stride();

    final int index() {
        return index;
    }

    final int address() {
        return address;
    }

    final void moveToAddress(int address) {
        this.address = address;
        this.index = address / stride();
    }

    final void moveToIndex(int index) {
        this.index = index;
        this.address = index * stride();
    }

    /**
     * Moves cursor to beginning of collection, returning true if collection is
     * non-empty.
     */
    public final boolean origin() {
        if (owner.isEmpty())
            return false;

        final int limit = owner.maxIndex();
        for (int i = 0; i < limit; i++) {
            this.moveToIndexAndRead(i);
            if (this.hasValue())
                return true;
        }

        // should never get here
        assert false : "origin failed on non-empty collection";
        return false;
    }

    /**
     * Moves cursor to next element in collection and return true. Returns false if
     * past the last element in collection. If returns false, cursor may not have a
     * value.
     */
    public boolean next() {
        int i = this.index + 1;
        this.moveToIndexAndRead(i);
        final int limit = owner.maxIndex();
        while (i < limit && !this.hasValue())
            this.moveToIndexAndRead(++i);

        return i < limit && this.hasValue();
    }

    abstract void read();

    abstract void write();

    void moveToAddressAndRead(int address) {
        moveToAddress(address);
        read();
    }

    void moveToAddressAndWrite(int address) {
        moveToAddress(address);
        write();
    }

    void moveToIndexAndRead(int index) {
        moveToIndex(index);
        read();
    }

    void moveToIndexAndWrite(int index) {
        moveToIndex(index);
        write();
    }

    protected abstract int keyHash();

    protected abstract boolean hasValue();

    /**
     * For use by sets and hash maps.
     */
    protected abstract boolean doesKeyMatch(T other);

    /**
     * Writes empty key/value(s) to the stream at current index/address
     */
    protected abstract void delete();
}
