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

public class Float3Int1MapCursor extends AbstractStreamCursor<Float3Int1MapCursor> {
    private static final int VALUE_INDICATOR = -1;

    public float x;
    public float y;
    public float z;

    public int i;

    @Override
    int stride() {
        return 5;
    }

    @Override
    void read() {
        IIntStream stream = owner.stream;
        int a = address;
        boolean hasValue = stream.get(a++) == VALUE_INDICATOR;
        if (hasValue) {
            x = stream.getFloat(a++);
            y = stream.getFloat(a++);
            z = stream.getFloat(a++);
            i = stream.get(a);
        } else {
            x = Float.NaN;
            y = Float.NaN;
            z = Float.NaN;
            i = 0;
        }
    }

    @Override
    void write() {
        IIntStream stream = owner.stream;
        int a = address;
        stream.set(a++, VALUE_INDICATOR);
        stream.setFloat(a++, x);
        stream.setFloat(a++, y);
        stream.setFloat(a++, z);
        stream.set(a, i);
    }

    @Override
    protected int keyHash() {
        return (Float.floatToRawIntBits(y) + Float.floatToRawIntBits(z) * 31) * 31 + Float.floatToRawIntBits(x);
    }

    @Override
    protected boolean hasValue() {
        return owner.stream.get(address) == VALUE_INDICATOR;
    }

    @Override
    protected void delete() {
        owner.stream.set(address, 0);
    }

    @Override
    protected boolean doesKeyMatch(Float3Int1MapCursor other) {
        return x == other.x && y == other.y && z == other.z;
    }
}
