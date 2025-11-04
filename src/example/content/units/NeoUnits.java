package example.content.units;

import arc.Core;
import arc.freetype.FreeType.Size;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.Items;
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


public class NeoUnits {

    public static ErekirUnitType spidern;

    public static void load(){

        spidern = new ErekirUnitType("spidern"){{
            constructor = LegsUnit::create;
            speed = 0.9f;
            drag = 0.11f;
            hitSize = 21f;
            rotateSpeed = 2f;
            health = 150;
            armor = 6f;
            legStraightness = 2f;
            stepShake = 0.1f;

            legCount = 4;
            legLength = 10f;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -7.5f;
            legBaseOffset = 0f;
            legMaxLength = 2.1f;
            legMinLength = 1.4f;
            legLengthScl = 1.1f;
            legForwardScl = 1.3f;
            legSpeed = 0.3f;
            rippleScale = 0.2f;

            hovering = true;
            legMoveSpace = 1.5f;
            allowLegStep = true;
            legPhysicsLayer = false;
            groundLayer = Layer.legUnit;

                weapons.addAll(new Weapon("hyperplasia-spidern-artillery"){{
                y = -7f;
                x = 9f;
                shootY = 7f;
                reload = 45;
                shake = 3f;
                rotateSpeed = 2f;
                shootSound = Sounds.artillery;
                rotate = true;
                shadow = 8f;
                recoil = 3f;
                shoot.shots = 3;
                shoot.shotDelay = 4f;

                bullet = new ArtilleryBulletType(2f, 12){{
                    hitEffect = Fx.sapExplosion;    
                    knockback = 0.8f;
                    lifetime = 70f;
                    width = height = 19f;
                    collidesTiles = true;
                    ammoMultiplier = 4f;
                    splashDamageRadius = 70f;
                    splashDamage = 65f;
                    backColor = Pal.neoplasm2;
                    frontColor = lightningColor = Pal.neoplasm2;
                    lightning = 3;
                    lightningLength = 10;
                    smokeEffect = Fx.shootBigSmoke2;
                    shake = 5f;

                    status = StatusEffects.sapped;
                    statusDuration = 60f * 10;
                }};
            }});
        }};
}
}