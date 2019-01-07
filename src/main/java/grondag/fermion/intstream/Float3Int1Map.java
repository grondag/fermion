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
