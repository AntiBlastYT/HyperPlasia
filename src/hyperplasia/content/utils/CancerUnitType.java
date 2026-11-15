package hyperplasia.content.utils;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.abilities.*;
import mindustry.entities.part.*;
import mindustry.entities.part.DrawPart.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;

/**
 * The Cancer: a {@link LivingNeoplasmUnitType} made of a body, a ring of loose parts and a glowing core.
 * <ul>
 *   <li><b>Spreading parts</b> - see {@link #spreadParts}. The parts slide away from the body a little while the weapon is
 *       winding up (target found / mouse1 held), and a lot more while the beam is actually firing.</li>
 *   <li><b>Core</b> - the {@code <name>-effect} sprite is drawn under the body and every part (but above the legs). It spins
 *       all the time, spins faster while the weapon winds up and faster still while the beam is out, and glows harder with it.
 *       Its colour is {@link #effectColor}.</li>
 *   <li><b>Cell</b> - the {@code <name>-cell} sprite is drawn above the parts, not under them.</li>
 * </ul>
 * All of it is visual only and driven by the state of the unit's weapon number {@link #effectWeapon} (the first one by default).
 */
public class CancerUnitType extends LivingNeoplasmUnitType{

    //region settings

    /** Colour of the spinning core. */
    public Color effectColor = Color.valueOf("33FFFF");
    /** Size of the core. 1 = the sprite's native size (the 740px sprite is 185 world units wide). */
    public float effectScale = 0.3f;
    /** Core spin speed in degrees per tick while idle, while the weapon winds up, and while the beam is out. */
    public float effectSpinIdle = 0.35f, effectSpinCharge = 2.2f, effectSpinFire = 6.5f;
    /** Core opacity while idle / while the beam is out. */
    public float effectAlphaIdle = 1f, effectAlphaFire = 1f;
    /** Strength of the soft glow around the core while attacking (it is drawn on the layer the game's bloom picks up). 0 = off. */
    public float effectGlow = 2f;
    /** Index of the weapon whose state drives the core and the parts. Unit parts always follow weapon 0. */
    public int effectWeapon = 0;

    //endregion

    protected TextureRegion effectRegion;

    public CancerUnitType(String name){
        super(name);

        abilities.add(new CoreAbility());
    }

    @Override
    public void init(){
        super.init();

        //the core and the spread parts reach far outside of the hitbox, don't let the unit pop out of view at the screen edge
        clipSize = Math.max(clipSize, hitSize * 3f);
    }

    @Override
    public void load(){
        super.load();

        effectRegion = Core.atlas.find(name + "-effect");
    }

    /**
     * Adds a ring of loose parts that slide away from the body.
     *
     * @param names      sprite suffixes without the dash, e.g. "one" for <code>&lt;unit&gt;-one</code>. Drawn in this order.
     * @param directions for every part the direction it slides in, measured IN THE SPRITE (x right, y DOWN),
     *                   from the body to the part. Doesn't need to be normalized.
     * @param windup     how far (world units) the parts slide while the weapon is winding up
     * @param fire       how much farther they slide while the beam is out
     */
    public void spreadParts(String[] names, float[][] directions, float windup, float fire){
        for(int i = 0; i < names.length; i++){
            float len = Mathf.len(directions[i][0], directions[i][1]);
            //sprite y points down, part y points forward
            final float dx = directions[i][0] / len, dy = -directions[i][1] / len;
            final int index = i;

            parts.add(new RegionPart("-" + names[i]){{
                outline = false;
                mirror = false;
                under = false;
                //above the body, in the order of the list, below the cell
                layerOffset = 0.0005f + index * 0.00005f;

                //weapon is winding up: slide out slowly and smoothly
                moves.add(new PartMove(PartProgress.warmup.curve(Interp.smoother), dx * windup, dy * windup, 0f));
                //beam is out: snap out further, then slowly slide back once it is gone
                moves.add(new PartMove(PartProgress.recoil, dx * fire, dy * fire, 0f));
            }});
        }
    }

    /** Same as vanilla, but a hair above the parts. In the sprites the cell lies over them. */
    @Override
    public void drawCell(Unit unit){
        float z = Draw.z();
        Draw.z(z + 0.002f);
        super.drawCell(unit);
        Draw.z(z);
    }

    /** Keeps the spin and the excitement of one unit's core. Added automatically by {@link CancerUnitType}. */
    public static class CoreAbility extends Ability{
        /** Core rotation in degrees. Local to each client, only used for visuals. */
        public float spin;
        /** 0-1: weapon is winding up. */
        public float charge;
        /** 0-1: beam is out. */
        public float fire;

        public CoreAbility(){
            display = false;
        }

        /** 0 idle, ~0.45 winding up, 1 firing. */
        public float excitement(){
            return Math.max(charge * 0.45f, fire);
        }

        @Override
        public void update(Unit unit){
            if(!(unit.type instanceof CancerUnitType type)) return;

            float c = 0f, f = 0f;
            if(unit.mounts.length > type.effectWeapon){
                WeaponMount mount = unit.mounts[type.effectWeapon];
                c = mount.warmup;
                //a continuous beam keeps recoil at 1 while it is out, and it fades quickly afterwards
                f = mount.bullet != null ? 1f : mount.recoil;
            }

            charge = Mathf.lerpDelta(charge, c, 0.12f);
            fire = Mathf.lerpDelta(fire, f, 0.06f);

            float speed = Mathf.lerp(Mathf.lerp(type.effectSpinIdle, type.effectSpinCharge, charge), type.effectSpinFire, fire);
            spin = (spin + speed * Time.delta) % 360f;
        }

        @Override
        public void draw(Unit unit){
            if(!(unit.type instanceof CancerUnitType type) || type.effectRegion == null || !type.effectRegion.found()) return;

            float e = excitement();
            float alpha = Mathf.lerp(type.effectAlphaIdle, type.effectAlphaFire, e);
            float size = type.effectRegion.width * type.effectRegion.scl() * type.effectScale;

            //same layer maths as UnitType.draw: legs are at z - 0.02, the soft shadow at z - 0.01 and the body at z
            float z = unit.elevation > 0.5f ? type.flyingLayer : type.groundLayer + Mathf.clamp(type.hitSize / 4000f, 0f, 0.01f);

            //---- the core: three scalloped discs turning in opposite directions, so the spin is visible through the gaps ----
            Draw.z(z - 0.005f);
            Draw.blend(Blending.additive);
            for(int i = 0; i < 1; i++){
                float s = size * (1f - i * 0.2f);
                float rot = spin * (i % 2 == 0 ? 1f : -1.3f) * (1f + i * 0.35f) + i * 11f;
                float layerAlpha = i == 0 ? 0.6f : i == 1 ? 0.45f : 0.35f;

                //the inner discs are hotter, and everything heats up with the excitement
                Draw.color(Tmp.c1.set(type.effectColor).lerp(Color.white, i * 0.12f + e * 0.18f), alpha * layerAlpha);
                Draw.rect(type.effectRegion, unit.x, unit.y, s, s, rot);
            }
            Draw.blend();

            //---- soft glow on the bloom layer ----
            if(type.effectGlow > 0f && e > 0.01f){
                Draw.z(Layer.bullet - 0.01f);
                Draw.blend(Blending.additive);
                Tmp.c1.set(type.effectColor).lerp(Color.white, 0.3f).a(0.28f * type.effectGlow * e * e);
                Tmp.c2.set(type.effectColor).a(0f);
                Fill.light(unit.x, unit.y, 32, size * 0.5f, Tmp.c1, Tmp.c2);
                Draw.blend();
            }

            Draw.color();
            Draw.z(z);
        }
    }
}