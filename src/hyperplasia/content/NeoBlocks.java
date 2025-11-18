package hyperplasia.content;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import hyperplasia.content.utils.*;
import mindustry.*;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
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
import mindustry.world.blocks.units.UnitFactory.UnitPlan;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class NeoBlocks {
    
        public static Block neoplasmSynthesizer, chitinFabricator,

        gastritisSpawn,
        
        hemophilia, impetigo
        ;

    public static void load(){

            impetigo = new ItemTurret("impetigo"){{
            requirements(Category.turret, with(Items.beryllium, 150, Items.silicon, 200, Items.graphite, 200, Items.tungsten, 50));

            ammo(
            Items.graphite, new BasicBulletType(8f, 41){{
                knockback = 4f;
                width = 25f;
                hitSize = 7f;
                height = 20f;
                shootEffect = Fx.shootBigColor;
                smokeEffect = Fx.shootSmokeSquareSparse;
                ammoMultiplier = 1;
                hitColor = backColor = trailColor = Color.valueOf("ea8878");
                frontColor = Pal.redLight;
                trailWidth = 6f;
                trailLength = 3;
                hitEffect = despawnEffect = Fx.hitSquaresColor;
                buildingDamageMultiplier = 0.2f;
            }},
            Items.oxide, new BasicBulletType(8f, 90){{
                knockback = 3f;
                width = 25f;
                hitSize = 7f;
                height = 20f;
                shootEffect = Fx.shootBigColor;
                smokeEffect = Fx.shootSmokeSquareSparse;
                ammoMultiplier = 2;
                hitColor = backColor = trailColor = Color.valueOf("a0b380");
                frontColor = Color.valueOf("e4ffd6");
                trailWidth = 6f;
                trailLength = 3;
                hitEffect = despawnEffect = Fx.hitSquaresColor;
                buildingDamageMultiplier = 0.2f;
            }},
            Items.silicon, new BasicBulletType(8f, 35){{
                knockback = 3f;
                width = 25f;
                hitSize = 7f;
                height = 20f;
                homingPower = 0.045f;
                shootEffect = Fx.shootBigColor;
                smokeEffect = Fx.shootSmokeSquareSparse;
                ammoMultiplier = 1;
                hitColor = backColor = trailColor = Pal.graphiteAmmoBack;
                frontColor = Pal.graphiteAmmoFront;
                trailWidth = 6f;
                trailLength = 6;
                hitEffect = despawnEffect = Fx.hitSquaresColor;
                buildingDamageMultiplier = 0.2f;
            }}
            );

            shoot = new ShootSpread(15, 4f);

            coolantMultiplier = 15f;

            inaccuracy = 0.2f;
            velocityRnd = 0.17f;
            shake = 1f;
            ammoPerShot = 3;
            maxAmmo = 30;
            consumeAmmoOnce = true;
            targetUnderBlocks = false;

            shootSound = Sounds.shootAltLong;
            shootY = 5f;
            outlineColor = Pal.neoplasmOutline;
            size = 3;
            envEnabled |= Env.space;
            reload = 30f;
            recoil = 2f;
            range = 125;
            shootCone = 40f;
            scaledHealth = 210;
            rotateSpeed = 3f;

            coolant = consume(new ConsumeLiquid(Liquids.water, 15f / 60f));
            limitRange(25f);
        }};

            gastritisSpawn = new NeoSpawner("gastritis-spawner"){{
            requirements(Category.units, with(Items.beryllium, 1));
            consumeItems(with(Items.beryllium, 100));

            size = 5;

            spawn = UnitTypes.renale;
            spawnEffect = Fx.neoplasiaSmoke;
            spawnColor = Liquids.hydrogen.color;
            spawnTime = 120f;
        }};

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
            hasLiquids = true;
            envEnabled |= Env.space | Env.underwater;
            envDisabled = Env.none;
            itemCapacity = 30;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCrucibleFlame(),new DrawRegion("-center"),
            new DrawDefault());
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

