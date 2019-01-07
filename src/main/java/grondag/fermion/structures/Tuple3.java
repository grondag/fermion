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

public class Tuple3<T> extends Tuple2<T> {
    protected T v2;

    Tuple3(T v0, T v1, T v2) {
        super(v0, v1);
        this.v2 = v2;
    }

    Tuple3() {
        super();
    }

    @Override
    public T get(int index) {
        switch (index) {
        case 0:
            return v0;
        case 1:
            return v1;
        case 2:
            return v2;
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
        default:
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int size() {
        return 3;
    }
}
