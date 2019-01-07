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

package grondag.fermion.intstream;

import java.util.concurrent.ArrayBlockingQueue;

public class Float3Int1Map extends AbstractIntStreamMap<Float3Int1MapCursor> {
    private static final ArrayBlockingQueue<Float3Int1Map> POOL = new ArrayBlockingQueue<>(16);

    private Float3Int1Map() {
        super(Float3Int1MapCursor.class);
    }

    public static Float3Int1Map claim() {
        Float3Int1Map result = POOL.poll();
        if (result == null)
            result = new Float3Int1Map();
        result.handleClaim();
        return result;
    }

    private static void release(Float3Int1Map freeMap) {
        freeMap.handleRelease();
        POOL.offer(freeMap);
    }

    @Override
    public void release() {
        release(this);
    }

    @Override
    int maxIndex() {
        return capacity;
    }
}
