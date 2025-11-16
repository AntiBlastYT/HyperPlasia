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
