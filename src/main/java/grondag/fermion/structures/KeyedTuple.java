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

/**
 * Base class for tuple objects with an object key. Key is used for equality
 * tests.
 */
public abstract class KeyedTuple<T> {
    public final T key;

    public KeyedTuple(T key) {
        this.key = key;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object other) {
        if (other instanceof KeyedTuple) {
            return this.key.equals(((KeyedTuple) other).key);
        } else {
            return false;
        }
    }
}
