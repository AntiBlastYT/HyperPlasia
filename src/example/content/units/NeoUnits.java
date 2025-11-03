package example.content.units;

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

    public static NeoplasmUnitType spidern;

    public static void load(){

        spidern = new NeoplasmUnitType("spidern"){{
            constructor = LegsUnit::create;
            speed = 0.9f;
            drag = 0.11f;
            hitSize = 11f;
            rotateSpeed = 3f;
            health = 150;
            armor = 1f;
            legStraightness = 0.3f;
            stepShake = 0f;
            drawCell = false;

            legCount = 4;
            legLength = 10f;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -7.5f;
            legBaseOffset = 2f;
            legMaxLength = 2.1f;
            legMinLength = 1.4f;
            legLengthScl = 1.1f;
            legForwardScl = 1.3f;
            legSpeed = 0.2f;
            rippleScale = 0.2f;

            legMoveSpace = 1.2f;
            allowLegStep = true;
            legPhysicsLayer = false;

                weapons.add(new Weapon("spidern-weapon"){{
                shootSound = Sounds.missile;
                mirror = false;
                showStatSprite = false;
                x = 0f;
                y = 5f;
                shootY = 4f;
                reload = 63f;
                cooldownTime = 42f;
                heatColor = Pal.turretHeat;

                bullet = new ArtilleryBulletType(3f, 40){{
                    shootEffect = new MultiEffect(Fx.shootSmallColor, new Effect(9, e -> {
                        color(Color.white, e.color, e.fin());
                        stroke(0.7f + e.fout());
                        Lines.square(e.x, e.y, e.fin() * 5f, e.rotation + 45f);

                        Drawf.light(e.x, e.y, 23f, e.color, e.fout() * 0.7f);
                    }));

                    collidesTiles = true;
                    backColor = hitColor = Pal.techBlue;
                    frontColor = Color.white;

                    knockback = 0.8f;
                    lifetime = 46f;
                    width = height = 9f;
                    splashDamageRadius = 19f;
                    splashDamage = 30f;

                    trailLength = 27;
                    trailWidth = 2.5f;
                    trailEffect = Fx.none;
                    trailColor = backColor;

                    trailInterp = Interp.slope;

                    shrinkX = 0.6f;
                    shrinkY = 0.2f;

                    hitEffect = despawnEffect = new MultiEffect(Fx.hitSquaresColor, new WaveEffect(){{
                        colorFrom = colorTo = Pal.techBlue;
                        sizeTo = splashDamageRadius + 2f;
                        lifetime = 9f;
                        strokeFrom = 2f;
                    }});
                }};
            }});
        }};
}
}