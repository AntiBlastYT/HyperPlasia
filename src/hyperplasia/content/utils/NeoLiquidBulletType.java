package hyperplasia.content.utils;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class NeoLiquidBulletType extends BulletType {
        public Liquid liquid;
    public float puddleSize = 6f;
    public float orbSize = 3f;
    public float boilTime = 5f;

    public NeoLiquidBulletType(Liquid liquid){
        super(3.5f, 0);
        this.liquid = liquid;

        status = liquid.effect;
        hitColor = liquid.color;
        lightColor = liquid.lightColor;
        lightOpacity = liquid.lightColor.a;

        lifetime = 34f;
        statusDuration = 120f;

        drag = 0.001f;
        knockback = 0.5f;

        hitEffect = Fx.hitLiquid;
        despawnEffect = Fx.none;
        smokeEffect = Fx.none;
        shootEffect = Fx.none;
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(liquid == null) return;

        Tile tile = world.tileWorld(b.x, b.y);
        if(tile == null) return;

        // ❗ ONLY remove neoplasm
        Puddle p = Puddles.get(tile);
        if(p != null && p.liquid == Liquids.neoplasm){
            p.amount = 0f;
            p.liquid = null;

            // optional effect
            Fx.steam.at(b.x, b.y, Pal.neoplasm2);
        }
    }

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);

        if(liquid == null) return;

        Tile tile = world.tileWorld(x, y);
        if(tile == null) return;

        // ❗ Remove neoplasm puddle on hit
        Puddle p = Puddles.get(tile);
        if(p != null && p.liquid == Liquids.neoplasm){
            p.amount = 0f;
            p.liquid = null;
        }
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        if(liquid == null) return;

        // simple water orb
        Draw.color(liquid.color);
        Fill.circle(b.x, b.y, orbSize);
        Draw.reset();
    }
}
    

