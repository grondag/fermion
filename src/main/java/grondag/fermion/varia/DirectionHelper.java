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

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

public class DirectionHelper {
    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    public static Direction fromOrdinal(int ordinal) {
        return ALL_DIRECTIONS[ordinal];
    }
    
    public static Axis longestAxis(float normalX, float normalY, float normalZ) {
        Axis result = Axis.Y;
        float longest = Math.abs(normalY);

        float a = Math.abs(normalX);
        if(a > longest)
        {
            result = Axis.X;
            longest = a;
        }

        return Math.abs(normalZ) > longest
                ? Axis.Z : result;
    }
}
