package net.nikr.dds;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PixelFormat {
    private static final int ALPHAPIXELS = 0x01;
    private static final int ALPHA = 0x02;
    private static final int FOURCC = 0x04;
    private static final int RGB = 0x40;
    private static final int YUV = 0x200;
    private static final int LUMINANCE = 0x20000;
    private static final int PALETTEINDEXED8 = 0x800;
    private static final int QWVU = 0x100000;
    private static final int NORMAL = 0x200000;

    private int size;
    private int flags;
    private int fourCC;
    private int rgbBitCount;
    private long rMask;
    private long gMask;
    private long bMask;
    private long aMask;
    private int rBits;
    private int gBits;
    private int bBits;
    private int aBits;
    private int rShift;
    private int gShift;
    private int bShift;
    private int aShift;

    public PixelFormat(int size, int flags, int fourCC, int rgbBitCount, long rMask, long gMask, long bMask, long aMask, int rBits, int gBits, int bBits, int aBits, int rShift, int gShift, int bShift, int aShift) {
        this.size = size;
        this.flags = flags;
        this.fourCC = fourCC;
        this.rgbBitCount = rgbBitCount;
        this.rMask = rMask;
        this.gMask = gMask;
        this.bMask = bMask;
        this.aMask = aMask;
        this.rBits = rBits;
        this.gBits = gBits;
        this.bBits = bBits;
        this.aBits = aBits;
        this.rShift = rShift;
        this.gShift = gShift;
        this.bShift = bShift;
        this.aShift = aShift;
    }

    public void printValues(int nSpace) {
        String sSpace = "";
        for (int i = 0; i < nSpace; i++) {
            sSpace = sSpace + "    ";
        }
        System.out.println(sSpace + "PixelFormat: ");

        System.out.println(sSpace + "    size: " + size);
        System.out.print(sSpace + "    flags: " + flags);
        extactedMethod(flags, ALPHAPIXELS, ALPHA, FOURCC, RGB, YUV, LUMINANCE);

    }

    static void extactedMethod(long flags, int alphapixels, int alpha, int fourcc, int rgb, int yuv, int luminance) {
        if ((flags & alphapixels) != 0) System.out.print(" (ALPHAPIXELS)");
        if ((flags & alpha) != 0) System.out.print(" (ALPHA)");
        if ((flags & fourcc) != 0) System.out.print(" (FOURCC)");
        if ((flags & rgb) != 0) System.out.print(" (RGB)");
        if ((flags & yuv) != 0) System.out.print(" (YUV)");
        if ((flags & luminance) != 0) System.out.print(" (LUMINANCE)");
    }
}
