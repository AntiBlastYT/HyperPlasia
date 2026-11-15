package hyperplasia.content.utils;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;

/**
 * A hitscan beam with a twisting double helix around a bright core (like a Necron gauss cannon).
 * <p>
 * Unlike a normal laser, this beam is stopped by walls: it ends at the first solid static wall and at the first
 * solid enemy building (which it still damages). Everything on its way up to that point is hit.
 */
public class HelixLaserBulletType extends BulletType{
    /** Maximum length of the beam, in world units. */
    public float length = 260f;
    /** Hit a bit wider than the beam itself, like a laser does. */
    public boolean largeHit = true;

    //region blocking
    /** Beam stops at static walls (stone walls, cliffs...). */
    public boolean blockedByTerrain = true;
    /** Beam stops at solid enemy buildings (walls, turrets...), after damaging them. */
    public boolean blockedByBuildings = true;
    /** Beam also stops at solid buildings of the shooter's own team. */
    public boolean blockedByAllies = false;
    //endregion

    //region helix look
    /** Radius of the helix, in world units. */
    public float helixRadius = 5.5f;
    /** Twists per world unit, in radians. 0.12 is one full twist every ~52 units. */
    public float helixFrequency = 0.12f;
    /** How fast the helix spins along the beam, in radians per tick. */
    public float helixSpeed = 0.4f;
    /** How many strands wind around the core. */
    public int strands = 2;
    public float strandWidth = 2.8f;
    public float coreWidth = 3.4f;
    /** Distance over which the helix opens up at the start and closes at the end. */
    public float taperLength = 26f;
    /** Colors of the strands, used in turn. */
    public Color[] strandColors = {Color.valueOf("8fd14a"), Pal.neoplasm1};
    public Color coreColor = Color.valueOf("f4ffd6");
    public Color glowColor = Color.valueOf("8fd14a");
    //endregion

    /** Played where the beam ends on a wall. */
    public Effect endEffect = HelixLaserBulletType.helixEnd;

    private static final Vec2 vec = new Vec2();
    private static Tile blocker;

    public HelixLaserBulletType(){
        super(0f, 0f);

        hitEffect = HelixLaserBulletType.helixHit;
        despawnEffect = Fx.none;
        smokeEffect = Fx.none;
        shootEffect = HelixLaserBulletType.helixShoot;
        chargeEffect = HelixLaserBulletType.helixCharge;
        hitColor = Color.valueOf("8fd14a");

        lifetime = 26f;
        hitSize = 4f;
        impact = true;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
        reflectable = false;
        removeAfterPierce = false;
    }

    @Override
    public void init(){
        super.init();

        drawSize = Math.max(drawSize, length * 2f);
    }

    @Override
    protected float calculateRange(){
        return Math.max(length, maxRange);
    }

    @Override
    public float estimateDPS(){
        return super.estimateDPS() * 3f;
    }

    /** How far the beam gets before a wall stops it. */
    protected float findBeamLength(Bullet b){
        vec.trnsExact(b.rotation(), length);
        blocker = null;

        //a shooter standing inside a wall (legged units can) ignores that wall
        Tile start = world.tile(b.tileX(), b.tileY());
        boolean[] inside = {start != null && start.solid()};

        World.raycast(b.tileX(), b.tileY(), World.toTile(b.x + vec.x), World.toTile(b.y + vec.y), (x, y) -> {
            Tile t = world.tile(x, y);
            if(t == null){
                return false;
            }

            if(!t.solid()){
                inside[0] = false;
                return false;
            }
            if(inside[0]) return false;

            boolean stops;
            if(t.build == null){
                stops = blockedByTerrain;
            }else if(t.build.team == b.team){
                stops = blockedByAllies;
            }else{
                stops = blockedByBuildings && t.build.collide(b);
            }

            if(stops){
                blocker = t;
                return true;
            }
            return false;
        });

        if(blocker == null) return length;

        float dst = b.dst(blocker.worldx(), blocker.worldy());
        if(blocker.build == null){
            //end right at the surface of the wall
            return Mathf.clamp(dst - tilesize / 2f, 6f, length);
        }else{
            //reach into the building so it is hit
            return Mathf.clamp(dst + tilesize / 2f, 6f, length);
        }
    }

    @Override
    public void init(Bullet b){
        float resultLength = findBeamLength(b);
        float rot = b.rotation();

        Damage.collideLine(b, b.team, b.x, b.y, rot, resultLength, largeHit, false, pierceCap);
        b.fdata = resultLength;

        if(blocker != null && endEffect != null){
            endEffect.at(b.x + Angles.trnsx(rot, resultLength), b.y + Angles.trnsy(rot, resultLength), rot, hitColor);
        }
    }

    @Override
    public void draw(Bullet b){
        float len = b.fdata;
        float rot = b.rotation();
        float fout = b.fout();
        float grow = Mathf.curve(b.fin(), 0f, 0.12f); //the beam shoots out quickly
        float shown = len * grow;
        if(shown < 1f) return;

        float cos = Mathf.cosDeg(rot), sin = Mathf.sinDeg(rot);
        float ex = b.x + cos * shown, ey = b.y + sin * shown;
        float step = 4f;
        int segs = Math.max(2, Mathf.ceil(shown / step));
        float fade = Interp.pow2Out.apply(fout);

        //soft glow under everything
        Draw.blend(Blending.additive);
        Draw.color(glowColor, 0.22f * fade);
        Lines.stroke((helixRadius * 2f + strandWidth) * (0.6f + 0.4f * fout));
        Lines.line(b.x, b.y, ex, ey, false);
        Draw.blend();

        //pass 0: strands that are behind the core, pass 1: core, pass 2: strands in front of it
        for(int pass = 0; pass < 3; pass++){
            if(pass == 1){
                Draw.color(coreColor, fade);
                Lines.stroke(coreWidth * fout);
                Lines.line(b.x, b.y, ex, ey, false);
                continue;
            }

            for(int s = 0; s < strands; s++){
                float offset = s * Mathf.PI2 / strands;
                Color c = strandColors[s % strandColors.length];
                float px = b.x, py = b.y, pdepth = 0f;
                float prevSide = 0f;

                for(int i = 0; i <= segs; i++){
                    float d = Math.min(i * step, shown);
                    //helix opens up after the muzzle, and closes again toward the end
                    float taper = Mathf.clamp(d / taperLength) * Mathf.clamp((len - d) / (taperLength * 0.6f) + (len - shown < 1f ? 0f : 1f));
                    float phase = d * helixFrequency - b.time * helixSpeed + offset;
                    float sideRaw = Mathf.sin(phase);
                    float depth = Mathf.cos(phase);
                    float side = sideRaw * helixRadius * taper * (0.65f + 0.35f * fout);

                    float x = b.x + cos * d - sin * side;
                    float y = b.y + sin * d + cos * side;

                    if(i > 0){
                        float midDepth = (depth + pdepth) * 0.5f;
                        boolean front = midDepth >= 0f;

                        if(front == (pass == 2)){
                            //strands in front are bright and thick, those behind are darker and thinner
                            float k = front ? 1f : 0f;
                            Tmp.c1.set(c).lerp(Color.black, front ? 0f : 0.45f);
                            Draw.color(Tmp.c1, (front ? 1f : 0.8f) * fade);
                            Lines.stroke(strandWidth * (0.55f + 0.45f * (midDepth * 0.5f + 0.5f)) * (0.4f + 0.6f * fout));
                            Lines.line(px, py, x, y, false);
                        }

                        //bright node where the strand crosses the core
                        if(pass == 2 && s == 0 && taper > 0.2f && prevSide * sideRaw < 0f){
                            Draw.color(coreColor, fade);
                            Fill.circle(x, y, (1.4f + 0.8f * fout) * taper);
                        }
                    }

                    px = x;
                    py = y;
                    pdepth = depth;
                    prevSide = sideRaw;
                }
            }
        }

        //flare at the muzzle and where the beam ends
        Draw.color(glowColor, fade);
        Fill.circle(b.x, b.y, 4.5f * fout + 1f);
        Draw.color(coreColor, fade);
        Fill.circle(b.x, b.y, 2.4f * fout);

        Draw.color(glowColor, fade);
        Lines.stroke(1.6f * fout);
        Lines.circle(ex, ey, 3f + 9f * (1f - fout));
        Draw.color(coreColor, fade);
        Fill.circle(ex, ey, 2.8f * fout);

        Draw.reset();

        Drawf.light(b.x, b.y, ex, ey, 26f * fout, glowColor, 0.7f);
    }

    @Override
    public void drawLight(Bullet b){
        //drawn in draw()
    }

    //region effects

    /** Muzzle flash when the beam is released. */
    public static final Effect helixShoot = new Effect(28f, 140f, e -> {
        Draw.color(Color.valueOf("f4ffd6"), Color.valueOf("8fd14a"), e.fin());
        Fill.circle(e.x, e.y, 3f + 8f * e.fout());

        Lines.stroke(2f * e.fout());
        Lines.circle(e.x, e.y, 4f + 18f * e.finpow());

        Draw.color(Color.valueOf("8fd14a"), Pal.neoplasm1, e.fin());
        Angles.randLenVectors(e.id, 10, 6f + 40f * e.finpow(), e.rotation, 22f, (x, y) -> {
            Lines.stroke(0.6f + 1.8f * e.fout());
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1.5f + 7f * e.fout());
        });

        Drawf.light(e.x, e.y, 46f * e.fout(), Color.valueOf("8fd14a"), 0.7f * e.fout());
        Draw.color();
    });

    /** Energy gathering at the muzzle while the weapon charges. */
    public static final Effect helixCharge = new Effect(42f, 120f, e -> {
        Draw.color(Color.valueOf("8fd14a"), Color.valueOf("f4ffd6"), e.fin());

        //sparks pulled into the muzzle
        Angles.randLenVectors(e.id, 14, 4f + 30f * e.fout(Interp.pow3Out), (x, y) -> {
            Lines.stroke(0.5f + 1.5f * e.fin());
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y) + 180f, 1f + 5f * e.fin());
        });

        //growing orb with a shrinking ring around it
        Fill.circle(e.x, e.y, 1f + 4.5f * e.finpow());
        Lines.stroke(1.4f * e.fin());
        Lines.circle(e.x, e.y, 3f + 20f * e.fout(Interp.pow2Out));

        Drawf.light(e.x, e.y, 14f + 34f * e.fin(), Color.valueOf("8fd14a"), 0.6f * e.fin());
        Draw.color();
    });

    /** Small hit spark, played on everything the beam touches. */
    public static final Effect helixHit = new Effect(22f, 70f, e -> {
        Draw.color(Color.valueOf("f4ffd6"), Color.valueOf("8fd14a"), e.fin());
        Lines.stroke(1.4f * e.fout());
        Lines.circle(e.x, e.y, 2f + 9f * e.finpow());

        Angles.randLenVectors(e.id, 6, 3f + 16f * e.finpow(), (x, y) -> {
            Lines.stroke(0.5f + 1.2f * e.fout());
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + 4f * e.fout());
        });
        Draw.color();
    });

    /** Bigger burst where the beam gets stopped by a wall. */
    public static final Effect helixEnd = new Effect(40f, 130f, e -> {
        Draw.color(Color.valueOf("f4ffd6"), Color.valueOf("8fd14a"), e.fin());
        Fill.circle(e.x, e.y, 2f + 6f * e.fout());

        Lines.stroke(2.4f * e.fout());
        Lines.circle(e.x, e.y, 4f + 26f * e.finpow());

        //sparks thrown back off the wall
        Draw.color(Color.valueOf("8fd14a"), Pal.neoplasm1, e.fin());
        Angles.randLenVectors(e.id, 12, 6f + 34f * e.finpow(), e.rotation + 180f, 70f, (x, y) -> {
            Lines.stroke(0.6f + 1.6f * e.fout());
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1.5f + 6f * e.fout());
        });

        //dust and embers
        Draw.color(Pal.neoplasmOutline, 0.4f * e.fout());
        Angles.randLenVectors(e.id + 4, 5, 3f + 16f * e.finpow(), e.rotation + 180f, 80f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 2f + 3.5f * e.fin());
        });

        Drawf.light(e.x, e.y, 40f * e.fout(), Color.valueOf("8fd14a"), 0.7f * e.fout());
        Draw.color();
    });

    //endregion
}
