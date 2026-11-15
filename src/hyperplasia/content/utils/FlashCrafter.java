package hyperplasia.content.utils;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;

import static mindustry.Vars.*;

/**
 * A crafter that flashes a heat image and throws sparks and embers the instant it produces something.
 * Pair it with {@link DrawCraftFlash} in the block's drawer.
 */
public class FlashCrafter extends GenericCrafter{
    /** How long the flash takes to fade out, in ticks. */
    public float flashTime = 28f;
    /** Particle burst played on every craft. Gets {@link #burstColor} as its color and {@link #burstCoreColor} as data. */
    public Effect burstEffect = NeoFx.craftBurst;
    public Color burstColor = Color.valueOf("ff6a2b");
    public Color burstCoreColor = Color.valueOf("ffe2a8");
    /** How many bursts are thrown per craft, spread around the block. */
    public int bursts = 3;

    public FlashCrafter(String name){
        super(name);
        buildType = FlashCrafterBuild::new;
    }

    public class FlashCrafterBuild extends GenericCrafterBuild{
        /** Time.time of the last craft. */
        public float lastCraft = -99999f;

        /** 1 right when something was produced, fading to 0. */
        public float flash(){
            return Mathf.clamp(1f - (Time.time - lastCraft) / flashTime);
        }

        @Override
        public void craft(){
            super.craft();

            lastCraft = Time.time;

            if(!headless && wasVisible && burstEffect != null){
                float spread = size * tilesize * 0.3f;
            }
        }
    }

    /** Draws "<block>-heat" (additively) on top of the block while it flashes. Only works for {@link FlashCrafter}. */
    public static class DrawCraftFlash extends DrawBlock{
        public String suffix = "-heat";
        /** Tint of the image. White = the image as it is. */
        public Color color = Color.white;
        /** Casts light on the ground while flashing. */
        public Color lightColor = Color.valueOf("ff8a3d");
        public float lightRadius = 3.2f;
        /** The flash fades with this power: above 1 = stays bright briefly then drops, below 1 = lingers. */
        public float curve = 1.6f;
        /** Extra size at the start of the flash, as a fraction. */
        public float pop = 0.04f;

        public TextureRegion region;

        @Override
        public void load(Block block){
            region = Core.atlas.find(block.name + suffix);
        }

        protected float flash(Building build){
            return build instanceof FlashCrafter.FlashCrafterBuild b ? Mathf.pow(b.flash(), curve) : 0f;
        }

        @Override
        public void draw(Building build){
            float f = flash(build);
            if(f <= 0.004f || !region.found()) return;

            Draw.z(Layer.blockAdditive);
            Draw.blend(Blending.additive);
            Draw.color(color, f);

            float s = 1f + pop * f;
            Draw.rect(region, build.x, build.y, region.width * region.scl() * s, region.height * region.scl() * s);

            Draw.blend();
            Draw.color();
            Draw.z(Layer.block);
        }

        @Override
        public void drawLight(Building build){
            float f = flash(build);
            if(f > 0.004f){
                Drawf.light(build.x, build.y, build.block.size * tilesize * lightRadius, lightColor, 0.8f * f);
            }
        }
    }
}