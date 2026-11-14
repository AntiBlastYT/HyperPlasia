package hyperplasia.content.utils;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.type.*;
import mindustry.type.unit.*;

/**
 * A neoplasm unit that is alive:
 * <ul>
 *   <li><b>Pulsing</b> - the body swells rhythmically. The beat gets faster (and a bit stronger) the lower the unit's health is.</li>
 *   <li><b>Clean outlines</b> - parts that touch (legs / leg bases / feet / joints / body) share one outline around their
 *       combined silhouette, instead of each part having its own outline drawn across the others.
 *       Parts listed in {@link #outlinedParts} keep their own normal outline.</li>
 *   <li><b>Neoplasm clouds</b> - a puff of neoplasm is released on every beat, and a big burst on death. Visual only.</li>
 * </ul>
 * Meant for legged units (and mechs / plain units). Treads (tanks) are not handled.
 */
public class LivingNeoplasmUnitType extends NeoplasmUnitType{

    //region settings

    /** Extra body size at the top of a beat, as a fraction (0.06 = +6%). */
    public float pulseAmount = 0.06f;
    /** Beat speed in radians per tick at full health. 0.05 is roughly one beat every two seconds. */
    public float pulseMinSpeed = 0.05f;
    /** Beat speed in radians per tick when health is nearly gone. 0.28 is roughly one beat every 0.4 seconds. */
    public float pulseMaxSpeed = 0.28f;
    /** Above 1 the beat stays calm until health gets low; below 1 it speeds up right away. */
    public float pulseCurve = 1.5f;
    /** How much stronger the swelling gets when health is gone (0.5 = up to 50% stronger). */
    public float pulseDangerBoost = 0.5f;

    /** Release neoplasm clouds (visual only, no damage). */
    public boolean neoplasmClouds = true;
    /** How many puffs are released per beat. */
    public int cloudsPerBeat = 2;
    /** Extra puffs per beat at zero health. */
    public int cloudsPerBeatDanger = 3;
    /** Puffs spawn at a random point within this radius from the unit's center. Negative = 45% of hitSize. */
    public float cloudRadius = -1f;
    /** Puffs released when the unit dies. */
    public int deathClouds = 18;
    /** Effect used for every puff. Gets the cloud color as its color. */
    public Effect cloudEffect = LivingNeoplasmUnitType.neoplasmCloud;
    public Color cloudColor = Pal.neoplasm1;

    /**
     * If true, touching parts get one shared outline around their combined shape.
     * If false, the unit uses the vanilla outline behavior (every part outlined on its own).
     */
    public boolean cleanOutlines = true;
    /**
     * Parts that should keep their own regular outline, even where they touch other parts.
     * Entries are sprite suffixes: "" (body), "-leg", "-leg-base", "-foot", "-joint", "-joint-base".
     * Example: <code>outlinedParts.add("-foot");</code>
     */
    public Seq<String> outlinedParts = new Seq<>();

    //endregion

    protected TextureRegion legOutlineRegion, legBaseOutlineRegion, footOutlineRegion, jointOutlineRegion, baseJointOutlineRegion;

    private static final Vec2 legPos = new Vec2();

    /** Soft, drifting puffs of neoplasm. */
    public static final Effect neoplasmCloud = new Effect(110f, 90f, e -> {
        Draw.color(e.color, Pal.neoplasm2, e.fin());

        float grow = Interp.pow2Out.apply(e.fin());
        float fade = Interp.pow2Out.apply(e.fout());

        Draw.alpha(0.16f * fade);
        Angles.randLenVectors(e.id, 4, 4f + 22f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 4f + 9f * grow);
        });

        Draw.alpha(0.28f * fade);
        Angles.randLenVectors(e.id + 1, 3, 2f + 16f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 2f + 5f * grow);
        });

        Draw.color();
    }).layer(Layer.bullet - 0.01f);

    public LivingNeoplasmUnitType(String name){
        super(name);

        //no healing by absorbing neoplasm puddles
        abilities.removeAll(a -> a instanceof LiquidRegenAbility);

        abilities.add(new PulseAbility());
    }

    //region pulse

    /** @return the current body scale of this unit (1 = normal). */
    public float pulseScale(Unit unit){
        if(pulseAmount <= 0f) return 1f;

        for(Ability a : unit.abilities){
            if(a instanceof PulseAbility p){
                return p.scale(this, unit);
            }
        }
        return 1f;
    }

    @Override
    public void drawBody(Unit unit){
        float s = pulseScale(unit);

        if(s == 1f || unit instanceof UnderwaterMovec){
            super.drawBody(unit);
            return;
        }

        applyColor(unit);
        Draw.rect(region, unit.x, unit.y, region.width * region.scl() * s, region.height * region.scl() * s, unit.rotation - 90f);
        Draw.reset();
    }

    //endregion
    //region outlines

    @Override
    public void load(){
        super.load();

        legOutlineRegion = Core.atlas.find(name + "-leg-outline");
        legBaseOutlineRegion = Core.atlas.find(name + "-leg-base-outline", legOutlineRegion);
        footOutlineRegion = Core.atlas.find(name + "-foot-outline");
        jointOutlineRegion = Core.atlas.find(name + "-joint-outline");
        baseJointOutlineRegion = Core.atlas.find(name + "-joint-base-outline");
    }

    @Override
    public void createIcons(MultiPacker packer){
        if(!cleanOutlines || !outlines){
            super.createIcons(packer);
            return;
        }

        //let the vanilla code do everything except baking an outline into each region,
        //since that baked outline is what ends up drawn across neighbouring parts
        outlines = false;
        try{
            super.createIcons(packer);
        }finally{
            outlines = true;
        }

        //weapons keep the vanilla outline behavior
        for(Weapon weapon : weapons){
            if(!weapon.name.isEmpty() && (minfo.mod == null || weapon.name.startsWith(minfo.mod.name)) && (weapon.top || !packer.isOutlined(weapon.name) || weapon.parts.contains(p -> p.under))){
                makeOutline(PageType.main, packer, weapon.region, !weapon.top || weapon.parts.contains(p -> p.under), outlineColor, outlineRadius);
            }
        }

        ObjectSet<String> done = new ObjectSet<>();
        for(TextureRegion target : Seq.with(region, jointRegion, footRegion, baseJointRegion, legRegion, legBaseRegion)){
            if(!(target instanceof AtlasRegion at) || !target.found() || !done.add(at.name)) continue;

            String suffix = at.name.startsWith(name) ? at.name.substring(name.length()) : at.name;

            if(outlinedParts.contains(suffix)){
                //regular outline, baked into the sprite
                makeOutline(PageType.main, packer, target, false, outlineColor, outlineRadius);
            }else{
                //separate "<sprite>-outline" sprite, drawn underneath every part at runtime.
                //(for the body this is the "<unit>-outline" sprite the game already draws under the body)
                makeOutline(PageType.main, packer, target, true, outlineColor, outlineRadius);
            }
        }
    }

    /** The body outline is drawn below the legs, so legs never get an outline line drawn over them. */
    @Override
    public void drawOutline(Unit unit){
        if(!cleanOutlines){
            super.drawOutline(unit);
            return;
        }

        Draw.reset();

        if(Core.atlas.isFound(outlineRegion)){
            float z = Draw.z();
            float s = pulseScale(unit);

            Draw.z(z - 0.05f);
            applyColor(unit);
            applyOutlineColor(unit);
            Draw.rect(outlineRegion, unit.x, unit.y, outlineRegion.width * outlineRegion.scl() * s, outlineRegion.height * outlineRegion.scl() * s, unit.rotation - 90f);
            Draw.z(z);
            Draw.reset();
        }
    }

    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit){
        if(!cleanOutlines){
            super.drawLegs(unit);
            return;
        }

        float z = Draw.z();

        //pass 1: outlines of every leg piece, below all the fills
        Draw.z(z - 0.01f);
        drawLegPieces(unit, true);

        //pass 2: the actual leg pieces
        Draw.z(z);
        drawLegPieces(unit, false);

        Draw.z(z);
    }

    /** Same as the vanilla leg drawing, but can draw either only the outline sprites or only the normal ones. */
    protected <T extends Unit & Legsc> void drawLegPieces(T unit, boolean outlinePass){
        applyColor(unit);
        if(outlinePass) applyOutlineColor(unit);
        Tmp.c3.set(Draw.getMixColor());

        Leg[] legs = unit.legs();

        float ssize = footRegion.width * footRegion.scl() * 1.5f;
        float rotation = unit.baseRotation();
        float invDrown = 1f - unit.drownTime;

        TextureRegion foot = outlinePass ? footOutlineRegion : footRegion;
        TextureRegion leg1 = outlinePass ? legOutlineRegion : legRegion;
        TextureRegion legBase = outlinePass ? legBaseOutlineRegion : legBaseRegion;
        TextureRegion joint = outlinePass ? jointOutlineRegion : jointRegion;
        TextureRegion baseJoint = outlinePass ? baseJointOutlineRegion : baseJointRegion;

        if(!outlinePass && footRegion.found()){
            for(Leg leg : legs){
                Drawf.shadow(leg.base.x, leg.base.y, ssize, invDrown);
            }
        }

        //legs are drawn front first
        for(int j = legs.length - 1; j >= 0; j--){
            int i = (j % 2 == 0 ? j/2 : legs.length - 1 - j/2);
            Leg leg = legs[i];
            boolean flip = i >= legs.length/2f;
            int flips = Mathf.sign(flip);

            Vec2 position = unit.legOffset(legPos, i).add(unit);

            Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension);

            if(!outlinePass && footRegion.found() && leg.moving && shadowElevation > 0){
                float scl = shadowElevation * invDrown;
                float elev = Mathf.slope(1f - leg.stage) * scl;
                Draw.color(Pal.shadow);
                Draw.rect(footRegion, leg.base.x + shadowTX * elev, leg.base.y + shadowTY * elev, position.angleTo(leg.base));
                Draw.color();
            }

            Draw.mixcol(Tmp.c3, Tmp.c3.a);

            if(foot.found()){
                Draw.rect(foot, leg.base.x, leg.base.y, position.angleTo(leg.base));
            }

            if(legBaseUnder){
                if(legBase.found()){
                    Lines.stroke(legBaseRegion.height * legRegion.scl() * flips);
                    Lines.line(legBase, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);
                }

                if(leg1.found()){
                    Lines.stroke(legRegion.height * legRegion.scl() * flips);
                    Lines.line(leg1, position.x, position.y, leg.joint.x, leg.joint.y, false);
                }
            }else{
                if(leg1.found()){
                    Lines.stroke(legRegion.height * legRegion.scl() * flips);
                    Lines.line(leg1, position.x, position.y, leg.joint.x, leg.joint.y, false);
                }

                if(legBase.found()){
                    Lines.stroke(legBaseRegion.height * legRegion.scl() * flips);
                    Lines.line(legBase, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);
                }
            }

            if(joint.found()){
                Draw.rect(joint, leg.joint.x, leg.joint.y);
            }
        }

        //base joints are drawn after everything else
        if(baseJoint.found()){
            for(int j = legs.length - 1; j >= 0; j--){
                Vec2 position = unit.legOffset(legPos, (j % 2 == 0 ? j/2 : legs.length - 1 - j/2)).add(unit);
                Draw.rect(baseJoint, position.x, position.y, rotation);
            }
        }

        if(!outlinePass && baseRegion.found()){
            Draw.rect(baseRegion, unit.x, unit.y, rotation - 90);
        }

        Draw.reset();
    }

    //endregion

    /** Keeps the heartbeat of one unit. Added automatically by {@link LivingNeoplasmUnitType}. */
    public static class PulseAbility extends Ability{
        /** Position in the current beat, in radians. Local to each client, only used for visuals. */
        public float phase;

        public PulseAbility(){
            display = false;
        }

        /** 0 at the bottom of a beat, 1 at the top. */
        public float beat(){
            float t = 0.5f + 0.5f * Mathf.cos(phase);
            //a slow rest, then a quick swell
            return t * t * t;
        }

        public float danger(LivingNeoplasmUnitType type, Unit unit){
            return Mathf.pow(1f - Mathf.clamp(unit.healthf()), type.pulseCurve);
        }

        public float scale(LivingNeoplasmUnitType type, Unit unit){
            return 1f + type.pulseAmount * (1f + danger(type, unit) * type.pulseDangerBoost) * beat();
        }

        @Override
        public void update(Unit unit){
            if(!(unit.type instanceof LivingNeoplasmUnitType type)) return;

            float danger = danger(type, unit);
            phase += Mathf.lerp(type.pulseMinSpeed, type.pulseMaxSpeed, danger) * Time.delta;

            //phase 0 is the top of the beat
            if(phase >= Mathf.PI2){
                phase %= Mathf.PI2;
                puff(type, unit, type.cloudsPerBeat + Mathf.round(type.cloudsPerBeatDanger * danger));
            }
        }

        @Override
        public void death(Unit unit){
            if(unit.type instanceof LivingNeoplasmUnitType type){
                puff(type, unit, type.deathClouds);
            }
        }

        protected void puff(LivingNeoplasmUnitType type, Unit unit, int amount){
            if(!type.neoplasmClouds || Vars.headless || type.cloudEffect == null) return;
            if(unit.inFogTo(Vars.player.team())) return;

            float radius = type.cloudRadius >= 0f ? type.cloudRadius : type.hitSize * 0.45f;

            for(int i = 0; i < amount; i++){
                Tmp.v1.trns(Mathf.random(360f), Mathf.random(radius));
                type.cloudEffect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, Mathf.random(360f), type.cloudColor);
            }
        }
    }
}
