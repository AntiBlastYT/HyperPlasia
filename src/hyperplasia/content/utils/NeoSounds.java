package hyperplasia.content.utils;

import arc.audio.*;
import arc.util.*;
import mindustry.gen.*;

/**
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
        //spawner sounds
        thump = find("explosionDull", "dullExplosion", "blockExplode1"),
        squelch = find("shootSap", "shootArtillerySap", "plantBreak"),
        chargeUp = find("acceleratorCharge", "chargeLancer", "chargeVela"),
        chargeFinal = find("chargeLancer", "chargeVela", "acceleratorCharge"),
        unitPop = find("unitCreateBig", "unitCreate");

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