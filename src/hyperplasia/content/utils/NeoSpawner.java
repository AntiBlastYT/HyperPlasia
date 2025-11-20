package hyperplasia.content.utils;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.entities.*;
import mindustry.world.consumers.ConsumeItems;

import static mindustry.Vars.*;

public class NeoSpawner extends Block{
    
    public TextureRegion topRegion, ballRegion;
    public float sclOffset = 0f;
    public Effect spawnEffect;
    public Color spawnColor;
    public UnitType unitType;
    public float unitBuildTime = 60f * 8f;

    public float polyStroke = 1.8f, polyRadius = 8f;
    public int polySides = 6;
    public float polyRotateSpeed = 1f;
    public Color polyColor = Pal.accent;

    public NeoSpawner(String name){
        super(name);

        solid = true;
        update = true;
        hasItems = true;
        itemCapacity = 200;
        ambientSound = Sounds.respawning;
    }

    @Override
    public boolean outputsItems(){
        return false;
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

    @Override
    public void setBars(){
        super.setBars();

        addBar("units", (UnitTransportSourceBuild e) ->
            new Bar(
            () ->
            Core.bundle.format("bar.unitcap",
                Fonts.getUnicodeStr(unitType.name),
                e.team.data().countType(unitType),
                unitType.useUnitCap ? Units.getStringCap(e.team) : "∞"
            ),
            () -> Pal.power,
            () -> unitType.useUnitCap ? (float)e.team.data().countType(unitType) / Units.getCap(e.team) : 5f
        ));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return super.canPlaceOn(tile, team, rotation) && Units.canCreate(team, unitType);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(!Units.canCreate(Vars.player.team(), unitType)){
            drawPlaceText(Core.bundle.get("bar.cargounitcap"), x, y, valid);
        }
    }

    public static void unitTetherBlockSpawned(Tile tile, int id){
        if(tile == null || !(tile.build instanceof UnitTetherBlock build)) return;
        build.spawned(id);
    }

    public class UnitTransportSourceBuild extends Building implements UnitTetherBlock{
        public float progress, speedScl;

        public float fraction(){
            return progress / unitBuildTime;
        }


        public void draw() {
    Draw.rect(region, x, y);

float frac = Mathf.clamp(buildProgress, 0f, 1f);

// logarithmic growth 0→1
float scale = Math.max(0f, Mathf.log2(1f + 9f * frac) / Mathf.log2(10f));

if(ballRegion.found()){
    Draw.rect(
        ballRegion,
        x, y,
        ballRegion.width  * Draw.scl * scale,
        ballRegion.height * Draw.scl * scale
    );
}


    if(topRegion.found()) Draw.rect(topRegion, x, y);
        }

        //needs to be "unboxed" after reading, since units are read after buildings.
        public int readUnitId = -1;
        public float buildProgress, totalProgress;
        public float warmup, readyness;
        public @Nullable Unit unit;

        @Override
        public void updateTile(){
            //unit was lost/destroyed
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
            }

            if(readUnitId != -1){
                unit = Groups.unit.getByID(readUnitId);
                if(unit != null || !net.client()){
                    readUnitId = -1;
                }
            }

            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);

            if(Units.canCreate(team, unitType)){
                buildProgress += edelta() / unitBuildTime;
                totalProgress += edelta();

                // ❌ removed continuous spawnEffect (the spam)

                if(buildProgress >= 1f){
                    if(!net.client()){
                        unit = unitType.create(team);

                        if(unit instanceof BuildingTetherc bt){
                            bt.building(this);
                        }

                        unit.set(x, y);
                        unit.rotation = 90f;
                        unit.add();

                        // ✅ now only triggers ONCE
                        if(spawnEffect != null){
                            spawnEffect.at(x, y, spawnColor != null ? spawnColor : Color.white);
                        }

                        Call.unitTetherBlockSpawned(tile, unit.id);
                    }
                }
            }
        }

        public void spawned(int id){
            Fx.spawn.at(x, y);
            buildProgress = 0f;
            if(net.client()){
                readUnitId = id;
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.total() < itemCapacity;
        }

        @Override
        public boolean shouldConsume(){
            return unit == null;
        }



        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public float progress(){
            return buildProgress;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(unit == null ? -1 : unit.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            readUnitId = read.i();
        }
    }
}