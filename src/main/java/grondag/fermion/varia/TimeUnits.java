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

public class TimeUnits {
    public static final int MINUTES_PER_HOUR = 60;
    public static final int SECONDS_PER_HOUR = MINUTES_PER_HOUR * 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;
    public static final int SECONDS_PER_DAY = MINUTES_PER_DAY * 60;

    public static final int TICKS_PER_REAL_SECOND = 20;
    public static final int TICKS_PER_REAL_MINUTE = TICKS_PER_REAL_SECOND * 60;
    public static final int TICKS_PER_REAL_HOUR = TICKS_PER_REAL_MINUTE * 60;
    public static final int TICKS_PER_REAL_DAY = TICKS_PER_REAL_HOUR * 24;

    public static final int TICKS_PER_SIMULATED_DAY = 24000;
    public static final int TICKS_PER_SIMULATED_HOUR = TICKS_PER_SIMULATED_DAY / 24;
    public static final float TICKS_PER_SIMULATED_MINUTE = TICKS_PER_SIMULATED_HOUR / 60f;
    public static final float TICKS_PER_SIMULATED_SECOND = TICKS_PER_SIMULATED_MINUTE / 60f;

    public static final int SIMULATED_SECONDS_PER_REAL_SECOND = SECONDS_PER_DAY * TICKS_PER_REAL_SECOND
            / TICKS_PER_SIMULATED_DAY;
    public static final float SIMULATED_SECONDS_PER_TICK = (float) SECONDS_PER_DAY / TICKS_PER_SIMULATED_DAY;

    /**
     * Equivalent to (ticks * TimeUnits.SIMULATED_SECONDS_PER_TICK) but avoids
     * floating point math
     */
    public static long ticksToSimulatedSeconds(long ticks) {
        return ticks * SECONDS_PER_DAY / TICKS_PER_SIMULATED_DAY;
    }

    /**
     * Equivalent to (ticks * TimeUnits.SIMULATED_SECONDS_PER_TICK) but avoids
     * floating point math
     */
    public static int ticksToSimulatedSeconds(int ticks) {
        return ticks * SECONDS_PER_DAY / TICKS_PER_SIMULATED_DAY;
    }

}
