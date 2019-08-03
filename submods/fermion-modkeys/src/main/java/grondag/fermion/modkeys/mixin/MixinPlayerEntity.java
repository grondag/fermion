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
package grondag.fermion.modkeys.mixin;

import org.spongepowered.asm.mixin.Mixin;

import grondag.fermion.modkeys.impl.ModKeysAccess;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements ModKeysAccess {
    private byte modifierFlags = 0;

    @Override
    public void mk_flags(byte flags) {
        modifierFlags = flags;
    }

    @Override
    public byte mk_flags() {
        return modifierFlags;
    }
}
