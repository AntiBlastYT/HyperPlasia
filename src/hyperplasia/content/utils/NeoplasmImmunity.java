package hyperplasia.content.utils;

import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;

/**
 * Makes blocks immune to neoplasm "infection".
 * <p>
 * Vanilla neoplasm infects any building that holds water: puddles next to (or on) the building drain its water,
 * spread around it and slowly damage it. All of that is done by the neoplasm liquid itself, once per puddle,
 * so it can't be switched off for single blocks directly.
 * <p>
 * Instead, every neoplasm puddle on or touching an immune block is swapped for an inert twin of the liquid. The twin
 * looks exactly the same but has no spreading and no damage. When the puddle is no longer near an immune block
 * (the block was removed) it turns back into normal neoplasm. Checked every 4 ticks, around immune blocks only.
 * <p>
 * Usage: <code>NeoplasmImmunity.add(someBlock, otherBlock);</code> while loading content.
 */
public class NeoplasmImmunity{
    private static final ObjectSet<Block> immune = new ObjectSet<>();

    private static final Seq<Puddle> converted = new Seq<>();

    private static Liquid inert;
    private static boolean hooked;
    private static float timer;

    /** Makes these blocks immune to neoplasm infection. Nulls are ignored. */
    public static void add(Block... blocks){
        for(Block b : blocks){
            if(b != null) immune.add(b);
        }
        hook();
    }

    /** @return whether this block can not be infected. */
    public static boolean isImmune(Block block){
        return immune.contains(block);
    }

    /** The harmless look-alike that neoplasm puddles turn into next to immune blocks. */
    public static @Nullable Liquid inertNeoplasm(){
        return inert;
    }

    private static void hook(){
        if(hooked) return;
        hooked = true;

        var vanilla = (CellLiquid)Liquids.neoplasm;

        //same as neoplasm, except it never spreads and never damages anything
        inert = new CellLiquid("neoplasm-inert", vanilla.color){{
            heatCapacity = vanilla.heatCapacity;
            temperature = vanilla.temperature;
            viscosity = vanilla.viscosity;
            flammability = vanilla.flammability;
            capPuddles = vanilla.capPuddles;
            incinerable = vanilla.incinerable;
            blockReactive = vanilla.blockReactive;
            moveThroughBlocks = vanilla.moveThroughBlocks;
            canStayOn.addAll(vanilla.canStayOn);
            colorFrom = vanilla.colorFrom;
            colorTo = vanilla.colorTo;
            cells = vanilla.cells;

            spreadTarget = null;
            hidden = true;
        }};

        Events.run(Trigger.update, () -> {
            //the server decides, clients receive the result with the puddle sync
            if(net.client() || !state.isGame() || immune.isEmpty()) return;

            //a few ticks between checks is plenty and keeps it cheap
            timer += Time.delta;
            if(timer < 4f) return;
            timer = 0f;

            //1) neoplasm puddles on or touching an immune block turn inert
            Groups.build.each(b -> immune.contains(b.block), b -> {
                int size = b.block.size;
                int low = b.tile.x - (size - 1) / 2 - 1, lowY = b.tile.y - (size - 1) / 2 - 1;

                for(int x = low; x <= low + size + 1; x++){
                    for(int y = lowY; y <= lowY + size + 1; y++){
                        Puddle p = Puddles.get(world.tile(x, y));
                        if(p != null && p.liquid == Liquids.neoplasm && nearImmune(p.tile)){
                            p.liquid = inert;
                            converted.add(p);
                        }
                    }
                }
            });

            //2) inert puddles that are no longer near an immune block turn back into normal neoplasm
            converted.removeAll(p -> {
                if(!p.isAdded() || p.liquid != inert || p.tile == null) return true;
                if(!nearImmune(p.tile)){
                    p.liquid = Liquids.neoplasm;
                    return true;
                }
                return false;
            });
        });
    }

    /** Same tiles the neoplasm liquid checks when it infects: its own tile and the 4 tiles around it. */
    private static boolean nearImmune(Tile tile){
        if(isImmuneBuild(tile.build)) return true;

        for(Point2 d : Geometry.d4){
            Tile other = tile.nearby(d);
            if(other != null && isImmuneBuild(other.build)) return true;
        }
        return false;
    }

    private static boolean isImmuneBuild(@Nullable Building build){
        return build != null && immune.contains(build.block);
    }
}
