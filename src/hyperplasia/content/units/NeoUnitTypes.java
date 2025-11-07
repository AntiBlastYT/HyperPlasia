package hyperplasia.content.units;

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


public class NeoUnitTypes {

    public static UnitType spidern;

    public static void load(){

        spidern = new UnitType("spidern") {{
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

            weapons.addAll(new Weapon("hyperplasia-spidern") {{
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
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = Pal.neoplasm1;
                    backColor = trailColor = lightColor = Pal.neoplasm2;
                    width = height = 15;
                    trailLength = 14;
                }};
            }}, new Weapon("spidern-weapon") {{
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
}
}