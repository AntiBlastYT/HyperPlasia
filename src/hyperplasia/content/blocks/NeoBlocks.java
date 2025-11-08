package hyperplasia.content.blocks;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.DrawPart.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class NeoBlocks {
    
        public static Block neoplasmSynthesizer;

    public static void load(){

            neoplasmSynthesizer = new ConsumeGenerator("neoplasm-synthesizer"){{
            requirements(Category.crafting, with(Items.oxide, 40, Items.silicon, 60, Items.graphite, 80));
            size = 3;
            liquidCapacity = 80f;
            outputLiquid = new LiquidStack(Liquids.neoplasm, 20f / 60f);
            explodeOnFull = true;

            consumeLiquid(Liquids.water, 15f / 60f);
            consumeItem(Items.oxide);

            itemDuration = 60f * 3f;
            itemCapacity = 10;

            explosionRadius = 7;
            explosionDamage = 500;
            explodeEffect = new MultiEffect(Fx.bigShockwave, new WrapEffect(Fx.titanSmoke, Liquids.neoplasm.color), Fx.neoplasmSplat);
            
            explodeSound = Sounds.largeExplosion;

            powerProduction = 12f;

            ambientSound = Sounds.bioLoop;
            ambientSoundVolume = 0.1f;

            explosionPuddles = 30;
            explosionPuddleRange = tilesize * 4f;
            explosionPuddleLiquid = Liquids.neoplasm;
            explosionPuddleAmount = 70f;
            explosionMinWarmup = 1f;

            consumeEffect = new RadialEffect(Fx.neoplasiaSmoke, 3, 30f, 30f / 4f);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.neoplasm),
            new DrawCells(){{
                color = Color.valueOf("c33e2b");
                particleColorFrom = Color.valueOf("e8803f");
                particleColorTo = Color.valueOf("8c1225");
                particles = 30;
                range = 4f;
            }});
                drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("", 0.6f * 9f){{
                blurThresh = 0.01f;
            }});
        

        }};


}
}

