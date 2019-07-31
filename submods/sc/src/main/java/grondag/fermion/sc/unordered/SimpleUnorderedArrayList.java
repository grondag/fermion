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

package grondag.fermion.sc.unordered;

import java.util.function.Consumer;

/**
 * Lightweight, non-concurrent collection-like class for managing small
 * unordered lists. Uses = for comparison.
 * 
 * @author grondag
 *
 */
public class SimpleUnorderedArrayList<T> extends AbstractUnorderedArrayList<T> implements Consumer<T> {
    public SimpleUnorderedArrayList() {
        super();
    }

    public SimpleUnorderedArrayList(int startingCapacity) {
        super(startingCapacity);
    }

    /**
     * Returns true if was added (not already present)
     */
    public boolean addIfNotPresent(T newItem) {
        for (int i = this.size - 1; i >= 0; i--) {
            if (items[i] == newItem)
                return false;
        }
        this.add(newItem);
        return true;
    }

    public void removeIfPresent(T target) {
        super.remove(target);
    }

    @Override
    public void accept(T t) {
        this.add(t);
    }

}
