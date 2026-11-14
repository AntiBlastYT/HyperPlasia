package hyperplasia.content.utils;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.draw.*;

/**
 * Fills the octagonal chamber in the middle of a block with a liquid, rising and falling with the amount the block stores.
 * <p>
 * Draw it <b>above</b> the bottom sprite and <b>below</b> the main sprite, so the main sprite's opening acts as the mask.
 * The chamber is measured in sprite pixels (the chitin fabricator's opening is 28x28 with 6px cut corners).
 */
public class DrawNeoFill extends DrawBlock{
    public Liquid liquid;

    /** Width/height of the chamber opening, in sprite pixels. */
    public int chamberSize = 28;
    /** Size of the cut corners of the octagonal opening, in sprite pixels. */
    public int cornerCut = 6;

    /** Opacity of the liquid. */
    public float alpha = 0.95f;
    /** How much darker the liquid gets towards the bottom (0 = flat). */
    public float depthDarkness = 0.3f;
    /** Brightness of the 1px line along the surface (0 = none). */
    public float surfaceShine = 0.3f;
    /** Surface ripple height in sprite pixels, and its speed. */
    public float waveAmount = 0.9f, waveSpeed = 0.07f;
    /** If set, overrides the liquid's own color. */
    public @Nullable Color color;

    public DrawNeoFill(Liquid liquid){
        this.liquid = liquid;
    }

    @Override
    public void draw(Building build){
        if(build.liquids == null) return;

        float fill = Mathf.clamp(build.liquids.get(liquid) / build.block.liquidCapacity);
        if(fill <= 0.001f) return;

        int n = chamberSize;
        float px = Draw.scl;
        float x0 = build.x - n * px / 2f;
        float y0 = build.y - n * px / 2f;
        float fillH = fill * n;
        boolean wavy = fill < 0.985f && waveAmount > 0f;

        Color base = color != null ? color : liquid.color;
        Tmp.c1.set(base).lerp(Color.black, depthDarkness);     //bottom
        Tmp.c2.set(base);                                       //middle
        Tmp.c3.set(base).lerp(Color.white, surfaceShine);       //surface line

        for(int c = 0; c < n; c++){
            //octagon: the first/last columns start a bit higher and end a bit lower
            int d = Math.min(c, n - 1 - c);
            float lo = d < cornerCut ? cornerCut - d : 0f;
            float hi = n - lo;

            float h = fillH + (wavy ? Mathf.sin(Time.time * waveSpeed + c * 0.6f) * waveAmount : 0f);
            float top = Mathf.clamp(h, lo, hi);
            if(top - lo < 0.05f) continue;

            float cx = x0 + (c + 0.5f) * px;
            float height = top - lo;

            //lower part: darker, upper part: base color
            float split = lo + height * 0.5f;
            Draw.color(Tmp.c1, alpha);
            Fill.rect(cx, y0 + (lo + split) / 2f * px, px, (split - lo) * px);
            Draw.color(Tmp.c2, alpha);
            Fill.rect(cx, y0 + (split + top) / 2f * px, px, (top - split) * px);

            //bright line on the surface (not when the chamber is completely full)
            if(surfaceShine > 0f && top < hi - 0.01f){
                float bandLo = Math.max(lo, top - 1f);
                Draw.color(Tmp.c3, alpha);
                Fill.rect(cx, y0 + (bandLo + top) / 2f * px, px, (top - bandLo) * px);
            }
        }

        Draw.color();
    }
}
