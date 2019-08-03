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

package grondag.fermion.varia;

import net.minecraft.nbt.CompoundTag;

/**
 * Slightly more flexible version of INBTSerializable that allows for writing to
 * an existing tag instead of always creating a new one.
 */
public interface ReadWriteNBT {
    void writeTag(CompoundTag tag);

    void readTag(CompoundTag tag);

    default CompoundTag toTag() {
        CompoundTag result = new CompoundTag();
        this.readTag(result);
        return result;
    }
}
