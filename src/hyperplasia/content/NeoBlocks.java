package hyperplasia.content;

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
    
        public static Block neoplasmSynthesizer, chitinFabricator;

    public static void load(){

            neoplasmSynthesizer = new ConsumeGenerator("neoplasm-synthesizer"){{
            requirements(Category.crafting, with(Items.oxide, 40, Items.silicon, 60, Items.graphite, 80));
            size = 3;
            liquidCapacity = 40f;
            outputLiquid = new LiquidStack(Liquids.neoplasm, 6f / 60f);
            explodeOnFull = true;

            consumeLiquid(Liquids.water, 15f / 60f);
            consumeItem(Items.oxide);
            generateEffect = Fx.neoplasiaSmoke;

            itemDuration = 60f * 4f;
            itemCapacity = 20;

            explosionRadius = 7;
            explosionDamage = 500;
            explodeEffect = new MultiEffect(Fx.bigShockwave, new WrapEffect(Fx.titanSmoke, Liquids.neoplasm.color), Fx.neoplasmSplat);
            
            explodeSound = Sounds.largeExplosion;

            powerProduction = 12f;

            ambientSound = Sounds.bioLoop;
            ambientSoundVolume = 0.1f;

            explosionPuddles = 90;
            explosionPuddleRange = tilesize * 5f;
            explosionPuddleLiquid = Liquids.neoplasm;
            explosionPuddleAmount = 130f;
            explosionMinWarmup = 1f;

            consumeEffect = new RadialEffect(Fx.neoplasiaSmoke, 4, 90f, 27f / 4f);
            



            drawer = new DrawMulti(new DrawBlurSpin("-rotator", 6f), new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.neoplasm), new DrawDefault());
         
        }};

            chitinFabricator = new GenericCrafter("chitin-fabricator"){{
            requirements(Category.crafting, with(Items.tungsten, 60, Items.silicon, 60, Items.beryllium, 110));
            craftEffect = Fx.none;
            outputItem = new ItemStack(NeoItems.chitin, 2);
            liquidCapacity = 40f;
            craftTime = 60f;
            size = 3;
            hasPower = true;
            hasLiquids = false;
            envEnabled |= Env.space | Env.underwater;
            envDisabled = Env.none;
            itemCapacity = 30;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawArcSmelt(),new DrawRegion("-center"),
            new DrawRegion("-bottom"),
            new DrawLiquidTile(Liquids.arkycite, 3f),
            new DrawCircles(){{
                color = Color.valueOf("feb380").a(0.5f);
                strokeMax = 3.25f;
                radius = 65f / 4f;
                amount = 5;
                timeScl = 200f;
            }}, new DrawDefault());
            fogRadius = 3;
            researchCost = with(Items.beryllium, 150, Items.graphite, 50);
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.12f;

            consumeItems(with(Items.tungsten, 2));
            consumeLiquid(Liquids.neoplasm, 4f / 60f);
            consumePower(5f);
        }};

}
}

