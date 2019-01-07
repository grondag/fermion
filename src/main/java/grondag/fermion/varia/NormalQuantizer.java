package grondag.fermion.varia;

public class NormalQuantizer {
    private static int packComponent(float n) {
        return Math.round(n * 256) + 256;
    }

    private static float unpackComponent(int q) {
        return (q - 256) / 256f;
    }

    public static int pack(float x, float y, float z) {
        final int qx = packComponent(x);
        final int qy = packComponent(y);
        final int qz = packComponent(z);
        return qx | (qy << 10) | (qz << 20);
    }

    public static float unpackX(int q) {
        return unpackComponent(q & 0x3FF);
    }

    public static float unpackY(int q) {
        return unpackComponent((q >> 10) & 0x3FF);
    }

    public static float unpackZ(int q) {
        return unpackComponent((q >> 20) & 0x3FF);
    }
}
