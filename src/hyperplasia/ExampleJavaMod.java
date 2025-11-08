package hyperplasia;

import arc.*;
import arc.util.*;
import hyperplasia.content.*;
import hyperplasia.content.units.NeoUnitTypes;
import hyperplasia.content.blocks.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;;

public class ExampleJavaMod extends Mod{

    public ExampleJavaMod(){
        Log.info("Loaded ExampleJavaMod constructor.");

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
        });
    }

    @Override
    public void loadContent(){
        Log.info("Loading some example content.");
        NeoUnitTypes.load();
        NeoBlocks.load();
    }

}
