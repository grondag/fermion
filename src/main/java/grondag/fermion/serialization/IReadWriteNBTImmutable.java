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

package grondag.fermion.serialization;

import net.minecraft.nbt.CompoundTag;

/**
 * NBT read/write interface for classes with immutable values
 */
public interface IReadWriteNBTImmutable<T> {
    /**
     * Should return the instance used to invoke the method if tag is not present or
     * invalid
     */
    T deserializeNBT(CompoundTag tag);

    void serializeNBT(CompoundTag tag);

    default CompoundTag serializeNBT() {
        CompoundTag result = new CompoundTag();
        this.serializeNBT(result);
        return result;
    }
}
