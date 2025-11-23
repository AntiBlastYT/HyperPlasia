package hyperplasia.content.utils;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.units.UnitAssembler.*;

import static arc.graphics.g2d.Draw.rect;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;


public class NeoFx {

    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();

    public static final Effect

    none = new Effect(0, 0f, e -> {}),

    smokeColor = new Effect(15, 300f, b -> {
        float intensity = 2.8f;
        float baseLifetime = 15f + intensity * 6f;
        b.lifetime = 30f + intensity * 40f;

        color(b.color);
        alpha(0.5f);
        for(int i = 0; i < 4; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.4f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.9f * intensity), 14f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 2.35f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 0.5f, b.color, 0.4f);
                });
            });
        }

        b.scaled(baseLifetime, e -> {
            Draw.color();
            e.scaled(5 + intensity * 2f, i -> {
                stroke((3.1f + intensity/5f) * i.fout());
                Lines.circle(e.x, e.y, (3f + i.fin() * 8f) * intensity);
                Drawf.light(e.x, e.y, i.fin() * 8f * 1f * intensity, Color.white, 0.9f * e.fout());
            });

            color(Pal.lighterOrange, b.color, e.fin());
            stroke((2f * e.fout()));

            Draw.z(Layer.effect + 0.001f);
            randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 18f * intensity, (x, y, in, out) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
            });
        });
    }),

        gouteffect = new Effect(15, 200f, b -> {
        float intensity = 2f;
        float baseLifetime = 15f + intensity * 6f;
        b.lifetime = 20f + intensity * 30f;

        color(b.color);
        alpha(0.5f);
        for(int i = 0; i < 1; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(0.4f, 0.8f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.3f * intensity), 10f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 2f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 0.5f, Pal.neoplasm1, 0.4f);
                });
            });
        }

        b.scaled(baseLifetime, e -> {
            Draw.color();
            e.scaled(2 + intensity * 2f, i -> {
                stroke((2.1f + intensity/5f) * i.fout());
                Lines.circle(e.x, e.y, (3f + i.fin() * 5f) * intensity);
                Drawf.light(e.x, e.y, i.fin() * 5f * 1f * intensity, Pal.neoplasm1, 0.9f * e.fout());
            });

            color(Pal.lighterOrange, b.color, e.fin());
            stroke((2f * e.fout()));

            Draw.z(Layer.effect + 0.001f);
            randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(5 * intensity), 13f * intensity, (x, y, in, out) -> {
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Pal.neoplasm1, 0.4f);
            });
        });
    }),

        neoMissileTrail = new Effect(180f, 50f, b  -> {
        float intensity = 2f;

        color(Pal.neoplasm2, 0.7f);
        
        for(int i = 0; i < 3; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(1.2f, 1.4f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(1.1f * intensity), 7f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.8f, 1f);
                    float rad = fout * ((2f + intensity) * 0.6f);

                    Fill.circle(e.x + x, e.y + y, rad);
                });
            });
        }

                color(Pal.neoplasm1, 0.7f);
        
        for(int i = 0; i < 2; i++){
            rand.setSeed(b.id*2 + i);
            float lenScl = rand.random(1f, 1.2f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(0.5f * intensity), 4f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.6f, 0.8f);
                    float rad = fout * ((2f + intensity) * 0.6f);

                    Fill.circle(e.x + x, e.y + y, rad);
                });
            });
        }

    }).layer(Layer.bullet - 1f);
}
