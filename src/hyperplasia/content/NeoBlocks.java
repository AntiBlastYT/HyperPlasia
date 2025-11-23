package hyperplasia.content;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import hyperplasia.content.utils.*;
import mindustry.*;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
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
        
        hemophilia, impetigo, gout
        ;

    public static void load(){
        
            gout = new ItemTurret("gout"){{
            requirements(Category.turret, with(Items.beryllium, 150, Items.silicon, 200, Items.graphite, 200, Items.tungsten, 50));
            ammo(
                NeoItems.synthecite, new DeaccelBullet(3f, 20){{
                    knockback = 0.8f;
                    lifetime = 80f;
                    width = height = 14f;
                    collidesTiles = false;
                    splashDamageRadius = 25f * 2.75f;
                    splashDamage = 90f;

                    backColor = hitColor = trailColor = Pal.neoplasm2;
                    frontColor = Pal.neoplasm1;
                    deaccelAmount = 0.3f;
            
                    fragBullets = 4;       // spread angle
                    fragVelocityMin = 1.3f;
                    fragVelocityMax = 1.8f;
                    hitEffect = despawnEffect = new MultiEffect(
                            NeoFx.gouteffect,
                            new WaveEffect(){{
                                startDelay = 0f;
                                sizeFrom = 3f;
                                sizeTo = 23f;
                                lifetime = 15f;
                                strokeFrom = 5.5f;
                                colorFrom = Pal.neoplasm2;
                                colorTo = Pal.neoplasm2;
                            }}
                        );

                    fragBullet = new DeaccelBullet(1.5f, 24f){{
                    deaccelAmount = 0.025f; 
                    pulseSpeed = 0f;
                    width = 7f;
                    hitSize = 5f;
                    height = 15f;
                    pierceCap = 1;
                    lifetime = 54f;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = Pal.neoplasm2;
                    frontColor = Pal.neoplasm1;
                    trailWidth = 1.5f;
                    trailLength = 11;
                    targetAir = true;
                    hitEffect = despawnEffect = new WaveEffect(){{
                        colorFrom = colorTo = Pal.neoplasm2;
                        frontColor = Pal.neoplasm1;
                        sizeTo = 4f;
                        strokeFrom = 4f;
                        lifetime = 10f;
                    }};
                    buildingDamageMultiplier = 0.3f;
                    homingPower = 0.15f;
                    }};
                    
                }}
            );
            shoot = new ShootMulti(new ShootPattern(), new ShootPattern() {{
                shots = 1;
            }}, new ShootBarrel() {{
                shots = 2;
                shotDelay = 4f;
                firstShotDelay = 2f;
                barrels = new float[]
                        {5.35f, -14f, -25f, -5.35f, -14f, 25f};
            }});
            clipSize = 32f;
            xRand = 3f;
            outlineColor = Pal.darkOutline;
            targetAir = false;
            size = 3;
            shoot.shots = 3;
            inaccuracy = 12f;
            reload = 60f;
            ammoEjectBack = 5f;
            ammoPerShot = 2;
            velocityRnd = 0.2f;
            scaleLifetimeOffset = 1f / 9f;
            recoil = 1.5f;
            shake = 2f;
            range = 150f;
            minRange = 50f;
            coolant = consumeCoolant(0.3f);
                shootEffect = new MultiEffect(Fx.shootTitan, new WaveEffect(){{
                    colorFrom = colorTo = Pal.neoplasm2;
                    sizeTo = 16f;
                    lifetime = 10f;
                    strokeFrom = 4f;
                }});
            scaledHealth = 230;
            shootSound = Sounds.artillery;
            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(
                    
                (new RegionPart("-rightblade"){{
                    progress = PartProgress.warmup;
                    moveRot = -5f;
                    mirror = false;
                    moves.add(new PartMove(PartProgress.recoil, 0f, -2f, -2f));
                    heatColor = Color.red;
                    layerOffset = -0.01f;
                }}),
                (new RegionPart("-leftblade"){{
                    progress = PartProgress.warmup;
                    moveRot = 5f;
                    mirror = false;
                    moves.add(new PartMove(PartProgress.recoil, 0f, -2f, 2f));
                    heatColor = Color.red;
                    layerOffset = -0.01f;
                }}),
                    new RegionPart("-right"){{
                    progress = PartProgress.warmup;
                    moveRot = -2.5f;
                    mirror = false;
                    moves.add(new PartMove(PartProgress.recoil, 0f, -1f, -2f));
                    heatColor = Color.red;
                    layerOffset = -0.01f;
                }},
                (new RegionPart("-left"){{
                    progress = PartProgress.warmup;
                    moveRot = 2.5f;
                    mirror = false;
                    moves.add(new PartMove(PartProgress.recoil, 0f, -1f, 2f));
                    heatColor = Color.red;
                    layerOffset = -0.01f;
                }}));;
            }};
        }};


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
            unitType = NeoUnitTypes.gastritis;
            size = 5;
            configurable = false;
            fogRadius = 5;
            consumePower(1.5f);
            rotate = false;
            unitBuildTime = 60f * 16f;
            itemCapacity = 200;
            spawnEffect = NeoFx.smokeColor;
            spawnColor = Liquids.hydrogen.color;
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
            drawer = new DrawMulti(new DrawRegion("-center"), new DrawLiquidTile(Liquids.neoplasm), new DrawRegion("-bottom"),
            new DrawDefault(), new DrawCrucibleFlame(),
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

