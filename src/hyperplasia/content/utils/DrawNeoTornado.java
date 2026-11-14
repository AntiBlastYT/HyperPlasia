package hyperplasia.content.utils;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.draw.*;

/**
 * A red, swirling vortex that spins up out of the middle of a block while it works.
 * <p>
 * It is built from stacked layers of spinning arcs: small and hot at the bottom, wide and dark at the top, each layer
 * turning at a different speed, which reads as a funnel when seen from above. It is drawn additively on top of the block,
 * so it glows and is picked up by the bloom pass. Everything scales with the block's warmup, so it spins up and winds down.
 */
public class DrawNeoTornado extends DrawBlock{
    /** Main color of the funnel. */
    public Color color = Color.valueOf("ff3b2f");
    /** Hot color at the bottom / core. */
    public Color coreColor = Color.valueOf("ffb27a");
    /** Dark color the top of the funnel fades towards. */
    public Color darkColor = Color.valueOf("8a1014");

    /** Number of stacked layers. */
    public int layers = 7;
    /** Spinning arcs per layer. */
    public int arms = 5;
    /** Length of each arc, as a fraction of a full circle. */
    public float armLength = 0.12f;
    /** How high (world units) the top of the funnel sits above the bottom. */
    public float height = 7f;
    /** Funnel radius (world units) at the bottom and at the top. */
    public float baseRadius = 1.2f, topRadius = 4.8f;
    /** Spin speed in degrees per tick of block progress. The bottom spins faster than the top. */
    public float spinSpeed = 7f;
    /** Line thickness at the bottom and top. */
    public float baseStroke = 0.5f, topStroke = 1.1f;
    /** Overall opacity. */
    public float alpha = 0.85f;
    /** Flung-out specks per layer (0 = none). */
    public int specks = 2;
    /** Radius of the soft glow in the center. */
    public float glowRadius = 9f;
    /** Light the block casts while the vortex is going. 0 disables. */
    public float lightRadius = 45f;

    public DrawNeoTornado(){}

    @Override
    public void draw(Building build){
        float warm = build.warmup();
        if(warm < 0.01f) return;

        float prog = build.totalProgress();
        float z = Draw.z();

        Draw.z(Layer.blockAdditive);
        Draw.blend(Blending.additive);

        //soft glow in the middle
        Tmp.c1.set(coreColor).a(0.35f * warm * alpha);
        Tmp.c2.set(color).a(0f);
        Fill.light(build.x, build.y, 24, glowRadius * (0.6f + 0.4f * warm), Tmp.c1, Tmp.c2);

        for(int i = 0; i < layers; i++){
            float t = layers <= 1 ? 0f : i / (layers - 1f);

            float cy = build.y + height * t * warm;
            float radius = Mathf.lerp(baseRadius, topRadius, Mathf.pow(t, 1.3f)) * warm;
            float spin = prog * spinSpeed * (1.5f - t) + i * 37f;

            //hot and bright at the bottom, dark and faint at the top
            Tmp.c1.set(coreColor).lerp(color, Mathf.clamp(t * 2f)).lerp(darkColor, Mathf.clamp(t * 2f - 1f));
            float a = alpha * warm * (0.75f - 0.45f * t);

            Draw.color(Tmp.c1, a);
            Lines.stroke(Mathf.lerp(baseStroke, topStroke, t));
            for(int k = 0; k < arms; k++){
                Lines.arc(build.x, cy, radius, armLength, spin + k * 360f / arms);
            }

            //specks flung around the funnel
            for(int k = 0; k < specks; k++){
                float ang = -spin * 1.3f + k * 360f / Math.max(1, specks) + i * 53f;
                float rr = radius * (1.1f + 0.15f * Mathf.sin(prog * 0.1f + i + k));
                Draw.color(Tmp.c1, a * 0.9f);
                Fill.circle(build.x + Angles.trnsx(ang, rr), cy + Angles.trnsy(ang, rr), 0.3f + 0.35f * t);
            }
        }

        Draw.blend();
        Draw.color();
        Draw.z(z);
    }

    @Override
    public void drawLight(Building build){
        if(lightRadius <= 0f) return;

        float warm = build.warmup();
        if(warm < 0.01f) return;

        Drawf.light(build.x, build.y, lightRadius * warm, color, 0.5f * warm);
    }
}
