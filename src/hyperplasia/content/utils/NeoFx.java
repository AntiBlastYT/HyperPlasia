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

    }).layer(Layer.bullet - 1f),

    /** Small spore that drifts up out of a unit spawner pit. */
    spawnerSpore = new Effect(55f, e -> {
        float wiggle = Mathf.sin(e.id * 1.3f + e.time * 0.2f) * 1.6f * e.finpow();
        Draw.color(e.color, e.data instanceof Color tip ? tip : Pal.lighterOrange, e.fin() * 0.6f);
        Draw.alpha(e.fslope() * 0.9f);
        Fill.circle(e.x + wiggle, e.y + e.finpow() * 10f, 0.5f + e.fslope() * 1.1f);
        Draw.reset();
    }),

    /** Streak that races inward to the centre of a spawner. Spawn it on the rim with rotation pointing inwards. */
    spawnerCharge = new Effect(22f, 40f, e -> {
        float dist = e.finpow() * 15f;
        float len = 1.5f + e.fslope() * 5f;
        Draw.color(e.data instanceof Color tip ? tip : Color.white, e.color, e.fin());
        Lines.stroke(0.5f + e.fslope() * 1.2f);
        Lines.lineAngle(e.x + Angles.trnsx(e.rotation, dist), e.y + Angles.trnsy(e.rotation, dist), e.rotation, len);
        Draw.reset();
    });

    /**
     * Particle burst for the chitin fabricator when it produces something.
     * Color = main color of the sparks, data (optional Color) = hot core color.
     */
    public static final Effect craftBurst = new Effect(46f, 90f, e -> {
        Color core = e.data instanceof Color c ? c : Pal.lighterOrange;

        //shock ring
        Draw.color(core, e.color, e.fin());
        Lines.stroke(1.8f * e.fout());
        Lines.circle(e.x, e.y, 3f + 22f * e.finpow());

        //fast sparks
        Draw.color(core, e.color, e.fin());
        Angles.randLenVectors(e.id, 12, 4f + 30f * e.finpow(), (x, y) -> {
            Lines.stroke(0.4f + 1.3f * e.fout());
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + 5f * e.fout());
        });

        //embers drifting up and fading
        Draw.color(e.color, Pal.neoplasmOutline, e.fin());
        Angles.randLenVectors(e.id + 7, 8, 3f + 16f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x * 0.8f, e.y + y * 0.8f + 6f * e.finpow(), 0.6f + 1.8f * e.fout());
        });

        //dark smoke puff
        Draw.color(Pal.neoplasmOutline, 0.35f * e.fout());
        Angles.randLenVectors(e.id + 13, 4, 2f + 14f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 2f + 4f * e.fin());
        });

        Draw.color();
    });

    // ---------------- gout turret ----------------

    /** Muzzle spurt: spray of neoplasm along the shot direction, streaks, a glowing flash and a bit of smoke. */
    public static final Effect goutShoot = new Effect(34f, 140f, e -> {
        //bright flash at the muzzle
        Draw.color(Pal.neoplasm1, e.color, e.fin());
        Draw.alpha(0.85f * e.fout());
        Fill.circle(e.x, e.y, 2f + 7f * e.fout());
        Drawf.light(e.x, e.y, 34f * e.fout(), Pal.neoplasm1, 0.6f * e.fout());

        //forward spray of blobs
        Draw.color(Pal.neoplasm1, e.color, e.fin());
        Angles.randLenVectors(e.id, 10, 6f + 38f * e.finpow(), e.rotation, 26f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.8f + 3.4f * e.fout());
        });

        //stretched streaks flying out
        Angles.randLenVectors(e.id + 3, 6, 8f + 46f * e.finpow(), e.rotation, 14f, (x, y) -> {
            Lines.stroke(0.6f + 2.2f * e.fout());
            Lines.lineAngle(e.x + x, e.y + y, e.rotation, 2f + 9f * e.fout());
        });

        //dark smoke curling off
        Draw.color(Pal.neoplasmOutline, 0.4f * e.fout());
        Angles.randLenVectors(e.id + 5, 5, 4f + 18f * e.finpow(), e.rotation, 40f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 2f + 4f * e.fin());
        });

        Draw.color();
    });

    /** Little drip shed by gout bullets while they fly. Use as trailEffect. */
    public static final Effect goutDrip = new Effect(26f, e -> {
        Draw.color(Pal.neoplasm1, Pal.neoplasm2, e.fin());
        Fill.circle(e.x, e.y, 0.f + 1.2f * e.fout());
        Draw.color(Pal.neoplasmOutline, 0.35f * e.fout());
    });

    /** Gooey blotch left on the ground where a gout shell lands, plus droplets flung up and out. */
    public static final Effect goutSplat = new Effect(85f, 160f, e -> {
        float spread = Interp.pow3Out.apply(Mathf.clamp(e.fin() * 6f));
        float fade = Interp.pow2Out.apply(e.fout());

        //the blotch settles quickly, then slowly fades
        Draw.color(Pal.neoplasm2, Pal.neoplasmOutline, e.fin());
        Draw.alpha(0.55f * fade);
        Angles.randLenVectors(e.id, 8, 4f + 20f * spread, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 2.5f + 5f * spread * fade);
        });
        Draw.alpha(0.7f * fade);
        Fill.circle(e.x, e.y, 6f * spread * fade);

        //droplets flying up and landing
        Draw.color(Pal.neoplasm1, Pal.neoplasm2, e.fin());
        Angles.randLenVectors(e.id + 2, 9, 8f + 36f * e.finpow(), (x, y) -> {
            float arc = Mathf.sin(e.fin() * Mathf.PI) * 9f;
            Fill.circle(e.x + x, e.y + y + arc, 0.4f + 2.2f * e.fout(Interp.pow2Out));
        });

        Draw.color();
    }).layer(Layer.debris);

    /** Small splash for the little frag bullets. */
    public static final Effect goutFragHit = new Effect(22f, 60f, e -> {
        Draw.color(Pal.neoplasm1, Pal.neoplasm2, e.fin());
        Angles.randLenVectors(e.id, 5, 2f + 14f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.4f + 1.8f * e.fout());
        });
        Lines.stroke(1.2f * e.fout());
        Lines.circle(e.x, e.y, 2f + 7f * e.finpow());
        Draw.color();
    });
}