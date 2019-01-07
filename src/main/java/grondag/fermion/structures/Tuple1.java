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

public class Tuple1<T> implements ITuple<T> {
    protected T v0;

    Tuple1(T v0) {
        this.v0 = v0;
    }

    Tuple1() {
    }

    @Override
    public T get(int index) {
        assert index == 0;
        return v0;
    }

    @Override
    public void set(int index, T value) {
        assert index == 0;
        v0 = value;
    }

    @Override
    public int size() {
        return 1;
    }

}
