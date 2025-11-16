package hyperplasia.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.*;

public class NeoItems {
    public static Item
    chitin;

    public static final Seq<Item> neoItems = new Seq<>();


        public static void load() {
        chitin = new Item("chitin", Color.valueOf("#52453f")) {{
            hardness = 6;
            cost = 1.5f;
            healthScaling = 0.8f;
            shownPlanets.add(Planets.erekir);
        }};

        neoItems.addAll(
        chitin
        );
    }
}
