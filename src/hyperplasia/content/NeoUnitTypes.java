package hyperplasia.content;

import arc.Core;
import arc.freetype.FreeType.Size;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import hyperplasia.content.utils.NeoFx;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.part.DrawPart.PartProgress;
import mindustry.entities.pattern.*;
import mindustry.entities.units.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;
import mindustry.world.meta.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;


public class NeoUnitTypes {

    public static NeoplasmUnitType spidern, gastritis;

    public static void load(){

        spidern = new NeoplasmUnitType("spidern") {{
            constructor = LegsUnit::create;
            speed = 0.45f;
            hitSize = 16;
            range = 120;
            health = 2800;
            stepShake = 0.1f;
            armor = 5;
            targetAir = true;
            rotateSpeed = 1.4f;
            shadowElevation = 0.25f;
            outlines = true;
            drawCell = false;

            legCount = 4;
            baseLegStraightness = 0.25f;
            legStraightness = 0.1f;
            legLength = 40f;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -.5f;
            legBaseOffset = 6f;
            legMaxLength = 1.05f;
            legMinLength = 0.8f;
            legLengthScl = 0.95f;
            legForwardScl = 0.8f;
            legPairOffset = 2;

            legMoveSpace = 0.9f;    

            weapons.addAll(new Weapon("hyperplasia-spidern-gun") {{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = -4;
                y = -3;
                recoil = 2;
                shootSound = Sounds.shootAltLong;
                shootY = 4;
                reload = 90;
                bullet = new BasicBulletType(8f, 60) {{
                    sprite = "missile-large";
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    lifetime = 15;
                    pierceCap = 3;
                    shootEffect = Fx.shootSmallColor;
                    frontColor = Pal.neoplasm1;
                    backColor = trailColor = lightColor = Pal.neoplasm2;
                    width = height = 15;
                    trailLength = 14;
                }};
            }}, new Weapon("hyperplasia-spidern-weapon") {{
                rotate = true;
                rotateSpeed = 1.1f;
                mirror = false;
                x = 6f;
                y = 6f;
                shootSound = Sounds.shootAlt;
                shootY = 4;
                reload = 160;
                inaccuracy = 7;
                bullet = new BasicBulletType(2.5f, 45) {{
                    sprite = "missile-large";
                    shrinkY = 0.1f;
                    shrinkX = 0.4f;
                    knockback = 5;
                    hitSize = 4;
                    lifetime = 20;
                    shootEffect = Fx.shootSmallColor;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = Pal.neoplasm1;
                    backColor = trailColor = lightColor = Pal.neoplasm2;
                    width = height = 8;
                    trailLength = 7;
                }};
            }});
        }};
gastritis = new NeoplasmUnitType("gastritis") {{
    constructor = LegsUnit::create;
    speed = 0.65f;
    drag = 0.1f;
    hitSize = 22f;
    rotateSpeed = 1f;
    health = 4400;
    armor = 1f;
    legStraightness = 0.6f;
    baseLegStraightness = 0.5f;
    fogRadius = 40f;
    stepShake = 0f;

    legCount = 5;
    legLength = 18f;
    lockLegBase = true;
    legContinuousMove = true;
    legGroupSize = 4;
    legExtension = -3f;
    legBaseOffset = 7f;
    legMaxLength = 1.1f;
    legMinLength = 0.2f;
    legLengthScl = 0.95f;
    legForwardScl = 0.9f;
    alwaysShootWhenMoving = true;

                    abilities.add(new LiquidExplodeAbility() {
                        {
                            this.liquid = Liquids.neoplasm;
                            
                        }
                    });



    legMoveSpace = 1f;
    hovering = true;

    shadowElevation = 0.2f;
    groundLayer = Layer.legUnit - 1f;
    weapons.addAll(new Weapon("hyperplasia-gastritis-weapon") {{
        mirror = false;
        x = -7;
        y = -2;
        showStatSprite = true;
        baseRotation = 5;
        reload = 300f;
        layerOffset = 0.01f;
        heatColor = Color.red;
        cooldownTime = 60f;
        smoothReloadSpeed = 0.15f;
        shootWarmupSpeed = 0.05f;
        minWarmup = 0.9f;
        rotationLimit = 70f;
        rotateSpeed = 0.5f;
        inaccuracy = 3f;
        shootStatus = StatusEffects.slow;
        alwaysShootWhenMoving = true;
        rotate = true;

        parts.add(new RegionPart("-missile") {{
    progress = PartProgress.reload.curve(Interp.pow2In);
    
    y = 2f;

    color = new Color(224, 84, 56, 0f);

    colorTo = Pal.neoplasm2;

    mixColor = new Color(224, 84, 56, 0f);
    mixColorTo = Pal.accent;
    under = true;
    layerOffset = 0.02f;

    moves.add(new PartMove(PartProgress.warmup.inv(), 0f, 2f, 0f));
        }});

        parts.add(new RegionPart("-back") {{
            mirror = false;
            under = true;
            moves.add(new PartMove(PartProgress.reload,0f, -1f, 0f));
            heatColor = Color.red;
            cooldownTime = 60f;
            layerOffset = 0.02f;
        }});

        parts.add(new RegionPart("-right-blade") {{
            mirror = false;
            moveRot = -10f;
            under = true;
            moves.add(new PartMove(PartProgress.reload, 1f, -0.5f, 0f));
            heatColor = Color.red;
            cooldownTime = 60f;
            layerOffset = 0.02f;
        }});

        parts.add(new RegionPart("-left-blade") {{
            mirror = false;
            moveRot = 10f;
            under = true;
            moves.add(new PartMove(PartProgress.reload,-1f, -0.5f, 0f));
            heatColor = Color.red;
            cooldownTime = 60f;
            layerOffset = 0.02f;
        }});



        bullet = new BulletType(0f, 0f) {{
            keepVelocity = false;
            collides = false;
            absorbable = false;
            hittable = false;
            reflectable = false;
            despawnEffect = Fx.none;

            spawnUnit = new MissileUnitType("gastritis-weapon-missile") {{
                shootEffect = Fx.shootSmallSmoke;
                smokeEffect = Fx.shootSmokeMissileColor;
                    speed = 2.6f;
                    maxRange = 3f;
                    lifetime = 30f * 5.5f;
                    outlineColor = Pal.neoplasmOutline;
                    engineColor = trailColor = Pal.redLight;
                    engineLayer = Layer.effect;
                    engineSize = 3.1f;
                    engineOffset = 10f;
                    rotateSpeed = 0.25f;
                    trailLength = 18;
                    trailEffect = Fx.disperseTrail;
                    missileAccelTime = 100f;
                    lowAltitude = true;
                    loopSound = Sounds.missileTrail;
                    loopSoundVolume = 0.6f;
                    deathSound = Sounds.largeExplosion;
                    targetAir = false;
                    targetUnderBlocks = false;

                    fogRadius = 6f;

                    health = 360;

                    puddleAmount = 80f;
                    puddleLiquid = Liquids.neoplasm;
                    puddleAmount = 200f;
                    puddleRange = 5f;
                    fragLifeMin = 0.2f;
                    fragBullets = 5;
                    fragSpread = 40f;
                            abilities.add(new LiquidExplodeAbility(){{
                            liquid = Liquids.neoplasm;
                            puddleRange = tilesize * 15f;
                            puddleAmount = 80f;
                            puddleLiquid = Liquids.neoplasm;
                            puddleAmount = 200f;
                    }});

weapons.add(new Weapon() {{
    shootOnDeath = true;
    mirror = false;
    reload = 1f;
    shootCone = 360f;
    fragSpread = 120f;

    bullet = new BasicBulletType(3f, 16f) {{
        lifetime = 0.5f;
        speed = 0.5f;
        width = 8f;
        height = 8f;

        splashDamage = 20f;
        splashDamageRadius = 28f;

                    fragBullets = 3;
                    fragLifeMin = 0f;
                    fragRandomSpread = 30f;

                    fragBullet = new ArtilleryBulletType(9f, 20){{
                        collidesGround = true;
                        width = 10f;
                        height = 10f;
                        pierce = false;
                        pierceBuilding = false;
                        
                        puddleLiquid = Liquids.neoplasm;
                        puddleAmount = 20f;
                        puddleRange = 4f;
                        puddles = 3;

                        lifetime = 20f;
                        splashDamage = 15f;
                        splashDamageRadius = 10f;
                                            trailLength = 9;
                        trailWidth = 3.1f;
                        frontColor = Pal.neoplasm1;
                        backColor = Pal.neoplasm2;
                        trailColor = Pal.neoplasm2;
                    }};

        frontColor = Pal.neoplasm1;
        backColor = Pal.neoplasm2;
        trailColor = Pal.neoplasm2;
        trailEffect = Fx.shootSmallSmoke;
        trailWidth = 1.3f;
        trailLength = 8;
                        abilities.add(new MoveEffectAbility(){{
                        effect = NeoFx.neoMissileTrail;
                        rotation = 180f;
                        y = -9f;
                        color = Color.grays(0.6f).lerp(Pal.redLight, 0.5f).a(0.4f);
                        interval = 7f;
                    }});
    }};
}});

            }};
        }};
    }});

    weapons.add(new Weapon("hyperplasia-gastritis-side-weapon") {{
        reload = 240f;
        recoil = -1f;
        xRand = 10f;
        yRand = 3f;
        shootY = 0f;
        shootCone = 270f;
        top = false;
        mirror = false;
        velocityRnd = 0.3f;
        baseRotation = 90f;
        x = 7;
        y = 3;

        shoot = new ShootMulti(
            new ShootBarrel() {{
                shots = 3;
                shotDelay = 9f;
                barrels = new float[]{
                    0, 0, 180,
                    0, 0, 190,
                    0, 0, 200
                };
            }},
            new ShootPattern() {{
                shots = 3;
                shotDelay = 9f;
            }}
        );

        bullet = new MissileBulletType(3f, 14) {{
            sprite = "missile-large";
            width = 6f;
            height = 7;
            shrinkY = 0f;
            drag = -0.003f;
            homingRange = 60f;
            keepVelocity = false;
            splashDamageRadius = 25f;
            splashDamage = 15f;
            lifetime = 60f;
            trailColor = Pal.unitBack;
            backColor = Pal.unitBack;
            frontColor = Pal.unitFront;
            hitEffect = Fx.blastExplosion;
            despawnEffect = Fx.blastExplosion;
            weaveScale = 8f;
            weaveMag = 2f;
        }};
    }});
}};

}
}