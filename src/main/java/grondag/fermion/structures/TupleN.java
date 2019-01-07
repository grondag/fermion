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

import java.lang.reflect.Array;

public class TupleN<T> implements ITuple<T> {
    final T[] values;

    /**
     * RETAINS REFERENCE!
     */
    TupleN(T[] values) {
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    TupleN(Class<T> clazz, int order) {
        values = (T[]) Array.newInstance(clazz, order);
    }

    @Override
    public T get(int index) {
        return values[index];
    }

    @Override
    public void set(int index, T value) {
        values[index] = value;
    }

    @Override
    public int size() {
        return values.length;
    }
}
