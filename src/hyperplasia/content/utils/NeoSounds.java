package hyperplasia.content.utils;

import arc.audio.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;

/**
 * Sounds used by the mod.
 * <p>
 * Custom sounds: drop an {@code .ogg} (or {@code .mp3}) with the matching name into {@code assets/sounds/}
 * (e.g. {@code assets/sounds/thump.ogg} for {@link #thump}) and it is used automatically. Without a file, a
 * vanilla sound is used as a stand-in, so the mod works out of the box.
 * <p>
 * Version-tolerant sound lookup.
 * <p>
 * Mindustry renamed most of its {@link Sounds} fields between v150 and v160 (e.g. {@code smelter} became
 * {@code loopSmelter}), so referencing them directly makes the mod crash with a NoSuchFieldError on the
 * "other" version. Each constant here tries the legacy name first, then the newer one(s), and falls back to
 * {@link Sounds#none} so a missing sound can never crash content loading.
 */
public class NeoSounds{

    public static final Sound
        shootAlt = find("shootAlt", "shootAtrax"),
        shootAltLong = find("shootAltLong", "shootAtrax"),
        artillery = find("artillery", "shootArtillery"),
        largeExplosion = find("largeExplosion", "explosion"),
        neoplasmExplosion = find("largeExplosion", "explosionReactorNeoplasm", "explosion"),
        missileTrail = find("missileTrail", "loopMissileTrail"),
        bioLoop = find("bioLoop", "loopBio"),
        smelter = find("smelter", "loopSmelter"),
        respawning = find("respawning", "loopUnitBuilding", "loopBuild"),

        //---- NeoSpawner build sounds. Files in assets/sounds/ win over the vanilla stand-ins listed here. ----
        /** heartbeat thump (a low dull hit) */
        thump = custom("thump", "drillImpact", "explosionDull"),
        /** wet squelch that goes with the drifting spores */
        squelch = custom("squelch", "plasmadrop", "stepMud", "plantBreak"),
        /** charge-up when the sparks start getting sucked in */
        chargeUp = custom("chargeUp", "lasercharge", "chargeLancer"),
        /** sharper charge-up right before the unit spawns */
        chargeFinal = custom("chargeFinal", "lasercharge2", "chargeVela", "chargeCorvus"),
        /** the unit is done */
        unitPop = custom("unitPop", "respawn", "unitCreate"),

        //---- Cancer ----
        /** loops while the Cancer's beam is out */
        beamLoop = custom("beamLoop", "beam", "beamMeltdown"),
        /** played once when the Cancer's beam starts */
        beamStart = custom("beamStart", "laserbig", "laserblast", "shootLaser");

    /**
     * Sets a public field by name if this game version has it, and does nothing otherwise. For sound fields that only
     * exist in newer versions, so the mod can still be compiled against an older one.
     */
    public static void setIfPresent(Object target, String field, Object value){
        try{
            target.getClass().getField(field).set(target, value);
        }catch(NoSuchFieldException | IllegalAccessException | IllegalArgumentException ignored){
            //not in this game version
        }
    }

    /**
     * Loads {@code assets/sounds/<file>.ogg|.mp3} from this mod if it exists, otherwise falls back to the first
     * vanilla sound in {@code fallbacks} that exists in this game version.
     */
    public static Sound custom(String file, String... fallbacks){
        try{
            if(Vars.tree != null){
                for(String ext : new String[]{".ogg", ".mp3"}){
                    if(Vars.tree.get("sounds/" + file + ext, true).exists()){
                        return Vars.tree.loadSound(file);
                    }
                }
            }
        }catch(Throwable t){
            Log.warn("[hyperplasia] could not load custom sound @: @", file, t);
        }
        return find(fallbacks);
    }

    /** Returns the first {@link Sounds} field that exists under one of the given names, or {@link Sounds#none}. */
    public static Sound find(String... names){
        for(String name : names){
            try{
                //same value a direct "Sounds.<name>" reference would give
                return (Sound)Sounds.class.getField(name).get(null);
            }catch(NoSuchFieldException | IllegalAccessException | ClassCastException ignored){
                //field does not exist in this game version, try the next name
            }
        }
        Log.warn("[hyperplasia] none of the sounds @ exist in this game version, using silence.", String.join(", ", names));
        return Sounds.none;
    }
}