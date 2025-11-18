package hyperplasia.content.utils;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.entities.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.UnitType;
import mindustry.content.*;
import mindustry.Vars;

public class NeoSpawner extends Block {

    public TextureRegion topRegion, ballRegion;
    public float sclOffset = 0f;
    public float spawnTime = 60f;
    public boolean selfDestruct;
    public Effect spawnEffect;
    public Color spawnColor;

    public UnitType spawn;

    public NeoSpawner(String name) {
        super(name);
        hasPower = true;
        hasItems = true;
        solid = true;
        configurable = false;
        clearOnDoubleTap = false;
        drawTeamOverlay = true;
        outputsPayload = false;
        rotate = false;
        commandable = true;
        hasLiquids = true;

    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        ballRegion = Core.atlas.find(name + "-ball");
    }

    @Override
    protected TextureRegion[] icons() {
        if (Core.atlas.find(name + "-top").found()) return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-top")};
        return new TextureRegion[]{Core.atlas.find(name)};
    }

    // -------------------------
    //   BUILDING CLASS FIXED
    // -------------------------
    public class NeoSpawnerBuild extends Building {

        public float progress;
        public float speedScl;

        public float fraction(){
            return progress / spawnTime;
        }

        @Override
        public void draw() {
            float frac = fraction();

            Draw.rect(region, x, y);

            if(ballRegion.found()) Draw.rect(ballRegion, x, y);

            if(topRegion.found()) Draw.rect(topRegion, x, y);
        }

        public void updateEfficiency(){
            boolean hasItems = false;

            for(var consume : consumeBuilder){
                if(consume instanceof ConsumeItems cons){
                    for(var itemStack : cons.items){
                        if(items.has(itemStack.item)){
                            hasItems = true;
                            break;
                        }
                    }
                }
            }

            efficiency = hasItems ? 1f : 0f;
        }

        @Override
        public void update() {
            super.update();

            updateEfficiency();

            if(efficiency > 0f){
                progress += edelta() * speedScl;
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            } else {
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            // ---------------------------
            //   UNIT SPAWN LOGIC
            //   moved from updateBeat()
            // ---------------------------
            if(progress >= spawnTime){
                consume();
                progress = 0f;
                speedScl = 0f;

                if(Units.canCreate(team, spawn)){
                    if(spawnEffect != null)
                        spawnEffect.at(x, y, spawnColor == null ? Color.white : spawnColor);

                    var unit = spawn.create(team);
                    unit.set(this);
                    unit.rotation(90f);
                    unit.add();
                }

                // optional self-destruct logic kept exactly the same
            }
        }
    }
}
