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

package grondag.fermion.structures;

public class Tuple4<T> extends Tuple3<T> {
    protected T v3;

    Tuple4(T v0, T v1, T v2, T v3) {
        super(v0, v1, v2);
    }

    Tuple4() {
        super();
    }

    @Override
    public final T get(int index) {
        switch (index) {
        case 0:
            return v0;
        case 1:
            return v1;
        case 2:
            return v2;
        case 3:
            return v3;
        default:
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void set(int index, T value) {
        switch (index) {
        case 0:
            v0 = value;
            return;
        case 1:
            v1 = value;
            return;
        case 2:
            v2 = value;
            return;
        case 3:
            v3 = value;
            return;
        default:
            throw new IndexOutOfBoundsException();
        }
    }

    public @Override final int size() {
        return 4;
    }
}
