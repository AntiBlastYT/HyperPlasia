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

    // ---- build visuals (all purely cosmetic) ----
    /** Dark tone the silhouette starts as at the beginning of a build cycle. */
    public Color shadowColor = Pal.neoplasmOutline;
    /** Mid tone of the silhouette / outline while it is being "printed". */
    public Color silhouetteColor = Pal.neoplasm2;
    /** Hot tone the silhouette, beat rings, sparks and spores heat up to. */
    public Color glowColor = Pal.neoplasm1;
    /** White-hot tone used for the scan line, bloom core and spawn flash. */
    public Color hotColor = Pal.lighterOrange;
    /** Largest side of the silhouette, in world units, when the unit is (almost) done. The pit is ~23 units wide. */
    public float silhouetteSize = 30f;
    /** Extra squash/stretch of the cocoon at the end of a build. 0 disables the wobble ramp. */
    public float wobbleAmount = 0.05f;
    /** Progress (0-1) where the silhouette starts heating from dark to glowing. */
    public float heatStart = 0.5f;
    /** Progress (0-1) where the bloom glow starts to fade in. */
    public float bloomStart = 0.72f;
    /** Progress (0-1) where sparks start converging on the pit. */
    public float chargeStart = 0.7f;
    /** Screen shake when a unit pops out. 0 disables. */
    public float spawnShake = 2.5f;

    private static final TextureRegion slice = new TextureRegion();

    /** Smooth 0-1-0 bump centered on c with half-width w. */
    private static float bump(float x, float c, float w){
        float d = 1f - Math.min(1f, Math.abs(x - c) / w);
        return d * d * (3f - 2f * d);
    }

    /** "lub-dub" heartbeat, phase in 0-1, result in 0-1. */
    private static float heartbeat(float p){
        return Math.max(bump(p, 0.07f, 0.09f), 0.6f * bump(p, 0.27f, 0.09f));
    }

    public NeoSpawner(String name){
        super(name);

        solid = true;
        update = true;
        hasItems = true;
        itemCapacity = 200;
        ambientSound = Sounds.respawning;
        emitLight = true;
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


        /** The unit's icon, used as the silhouette. Null if nothing usable is loaded. */
        protected @Nullable TextureRegion silhouetteRegion(){
            TextureRegion r = unitType.fullIcon;
            if(r == null || !r.found()) r = unitType.region;
            return r != null && r.found() ? r : null;
        }

        /**
         * Draws the vertical part [from, to] (0 = bottom, 1 = top) of a region as a flat-coloured silhouette.
         * w/h are the size of the WHOLE region, so slices line up with the full sprite.
         */
        protected void drawSil(TextureRegion r, float cx, float cy, float w, float h, float from, float to, Color c, float a){
            if(to <= from || a <= 0.003f) return;

            slice.set(r);
            slice.v2 = Mathf.lerp(r.v2, r.v, from);
            slice.v = Mathf.lerp(r.v2, r.v, to);

            Draw.mixcol(c, 1f);
            Draw.color(Color.white, a);
            Draw.rect(slice, cx, cy - h / 2f + h * (from + to) / 2f, w, h * (to - from));
            Draw.mixcol();
            Draw.color();
        }

        @Override
        public void draw(){
            float bz = Draw.z();
            Draw.rect(region, x, y);

            float prog = Mathf.clamp(buildProgress, 0f, 1f);
            float act = Mathf.clamp(warmup);
            //heat: dark -> glowing, bloom: glowing -> white hot (only the last stretch)
            float heat = Interp.smoother.apply(Mathf.curve(prog, heatStart, 1f));
            float bloom = Interp.pow2In.apply(Mathf.curve(prog, bloomStart, 1f));
            float beat = heartbeat(beatPhase) * (0.3f + 0.7f * heat) * act;
            float t = totalProgress;

            //wobble: gets stronger with progress, driven by build time so it freezes when the block stalls
            float wob = (0.012f + wobbleAmount * heat * heat) * act;
            float wave = Mathf.sin(t * 0.071f) * 0.6f + Mathf.sin(t * 0.113f + 1.7f) * 0.4f;
            //tiny high-frequency tremble right before it bursts
            float tremble = Mathf.curve(prog, 0.88f, 1f) * act;
            float ox = Mathf.sin(t * 0.05f) * 0.8f * heat * act + Mathf.sin(t * 1.9f) * 0.45f * tremble;
            float oy = Mathf.cos(t * 0.043f) * 0.6f * heat * act + Mathf.cos(t * 2.3f) * 0.45f * tremble;

            //---- soft glow in the pit, mostly visible while the cocoon is still small ----
            float pitA = (0.08f + 0.3f * heat + beat * 0.15f) * act;
            if(pitA > 0.004f){
                Draw.z(bz + 0.001f);
                Draw.blend(Blending.additive);
                Tmp.c1.set(silhouetteColor).a(pitA);
                Tmp.c2.set(silhouetteColor).a(0f);
                Fill.light(x, y, 20, 12f + 5f * beat, Tmp.c1, Tmp.c2);
                Draw.blend();
            }

            //---- cocoon: jelly wobble + heartbeat pop + heating up ----
            float ballScale = Math.max(0f, Mathf.log2(1f + 9f * prog) / Mathf.log2(10f));
            if(ballRegion.found() && ballScale > 0.001f){
                float pop = beat * 0.05f;
                float bw = ballRegion.width * Draw.scl * ballScale;
                float bh = ballRegion.height * Draw.scl * ballScale;

                Draw.z(bz + 0.002f);
                Draw.mixcol(hotColor, Mathf.clamp(heat * heat * 0.5f + beat * 0.15f));
                Draw.rect(ballRegion, x + ox, y + oy, bw * (1f + wob * wave + pop), bh * (1f - wob * wave * 0.8f + pop));
                Draw.mixcol();
            }

            //the cocoon bursting: a ghost of it swells and fades right after the unit spawns
            if(flash > 0.01f && ballRegion.found()){
                float grow = 1f + Interp.pow3Out.apply(1f - flash) * 0.3f;
                Draw.z(bz + 0.0025f);
                Draw.mixcol(hotColor, 0.85f);
                Draw.color(Color.white, flash * flash);
                Draw.rect(ballRegion, x, y, ballRegion.width * Draw.scl * grow, ballRegion.height * Draw.scl * grow);
                Draw.mixcol();
                Draw.color();
            }

            //---- silhouette of the unit being built ----
            TextureRegion sil = silhouetteRegion();
            float sw = 0f, sh = 0f, fullW = 0f, fullH = 0f, sx = x, sy = y, fade = 0f, vis = 0f;
            if(sil != null){
                //fit by the sprite's largest side so padded icons don't overflow the pit
                float fit = silhouetteSize / (Math.max(sil.width, sil.height) * Draw.scl);
                fullW = sil.width * Draw.scl * fit;
                fullH = sil.height * Draw.scl * fit;

                float grow = Mathf.lerp(0.72f, 1f, Interp.pow2Out.apply(prog));
                sw = fullW * grow * (1f + wob * 0.5f * wave + beat * 0.03f);
                sh = fullH * grow * (1f - wob * 0.4f * wave + beat * 0.03f);
                sx = x + ox * 0.6f;
                sy = y + oy * 0.6f;
                fade = Mathf.curve(prog, 0f, 0.12f);
                vis = 0.4f + 0.6f * act; //dims when stalled / unpowered

                //outline (blueprint edge): four diagonal offsets of the silhouette
                Draw.z(bz + 0.003f);
                Tmp.c1.set(silhouetteColor).lerp(glowColor, heat);
                for(int i = 0; i < 4; i++){
                    drawSil(sil, sx + Angles.trnsx(i * 90f + 45f, 0.75f), sy + Angles.trnsy(i * 90f + 45f, 0.75f),
                        sw, sh, 0f, 1f, Tmp.c1, 0.55f * fade * vis);
                }

                //ghost of the part that is not built yet
                Draw.z(bz + 0.004f);
                drawSil(sil, sx, sy, sw, sh, 0f, 1f, silhouetteColor, 0.3f * fade * vis);

                //the built part: fills bottom-up, dark -> crimson -> glowing orange
                Draw.z(bz + 0.005f);
                Tmp.c2.set(shadowColor).lerp(silhouetteColor, Mathf.clamp(prog * 1.8f)).lerp(glowColor, heat * 0.9f);
                drawSil(sil, sx, sy, sw, sh, 0f, prog, Tmp.c2, 0.9f * fade * vis);

                //scan line at the fill edge
                Draw.z(bz + 0.006f);
                drawSil(sil, sx, sy, sw, sh, Math.max(0f, prog - 0.05f), prog, hotColor, 0.95f * fade * vis);
            }

            //---- flesh overlay, breathing with the heartbeat ----
            if(topRegion.found()){
                float bs = 1f + beat * 0.018f + wob * 0.25f * wave;
                Draw.z(bz + 0.008f);
                Draw.rect(topRegion, x, y, topRegion.width * Draw.scl * bs, topRegion.height * Draw.scl * bs);
            }

            //---- bloom layer: additive glow that Mindustry's bloom pass picks up ----
            float rp = beatPhase / 0.6f;
            boolean ring = heat > 0.25f && rp < 1f && act > 0.1f;
            if(bloom > 0.003f || flash > 0.003f || ring){
                Draw.z(Layer.bullet - 0.01f);
                Draw.blend(Blending.additive);

                //heartbeat ring rippling out of the pit
                if(ring){
                    Draw.color(glowColor, (1f - rp) * heat * 0.55f * act);
                    Lines.stroke(0.4f + 1.4f * (1f - rp));
                    Lines.circle(x, y, Mathf.lerp(8f, 21f, Interp.pow2Out.apply(rp)));
                }

                if(bloom > 0.003f){
                    //halo, swelling towards the end
                    Tmp.c1.set(hotColor).a((bloom * 0.45f + beat * heat * 0.15f) * act);
                    Tmp.c2.set(glowColor).a(0f);
                    Fill.light(x + ox, y + oy, 24, 13f + 9f * bloom + 3f * beat, Tmp.c1, Tmp.c2);

                    if(sil != null){
                        //white-hot silhouette + brighter scan line
                        drawSil(sil, sx, sy, sw, sh, 0f, 1f, hotColor, (bloom * 0.5f + beat * heat * 0.15f) * act);
                        drawSil(sil, sx, sy, sw, sh, Math.max(0f, prog - 0.05f), prog, Color.white, 0.5f * fade * act);
                    }
                }

                //spawn pop: flash, expanding glow, shockwave
                if(flash > 0.003f){
                    float e = 1f - flash;
                    if(sil != null){
                        float k = 1f + e * 0.2f;
                        drawSil(sil, x, y, fullW * k, fullH * k, 0f, 1f, Color.white, flash * flash);
                    }
                    Tmp.c1.set(hotColor).a(flash * 0.7f);
                    Tmp.c2.set(glowColor).a(0f);
                    Fill.light(x, y, 28, Mathf.lerp(14f, 36f, Interp.pow2Out.apply(e)), Tmp.c1, Tmp.c2);

                    Draw.color(hotColor, flash);
                    Lines.stroke(0.3f + 3.5f * flash);
                    Lines.circle(x, y, Mathf.lerp(8f, 38f, Interp.pow3Out.apply(e)));
                }

                Draw.blend();
            }

            Draw.mixcol();
            Draw.color();
            Draw.z(bz);
        }

        @Override
        public void drawLight(){
            float prog = Mathf.clamp(buildProgress, 0f, 1f);
            float heat = Interp.smoother.apply(Mathf.curve(prog, heatStart, 1f));
            float beat = heartbeat(beatPhase) * heat;
            float radius = size * tilesize * (1.1f + 0.5f * heat + 0.3f * flash + 0.1f * beat);
            Drawf.light(x, y, radius, glowColor, Mathf.clamp((0.18f + 0.45f * heat) * warmup + flash * 0.6f));
        }

        //needs to be "unboxed" after reading, since units are read after buildings.
        public int readUnitId = -1;
        public float buildProgress, totalProgress;
        public float warmup, readyness;
        /** visual only: 1 right after a unit pops out, decays to 0. */
        public float flash;
        /** visual only: heartbeat phase, 0-1. */
        public float beatPhase;
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

            boolean canBuild = Units.canCreate(team, unitType);

            //warmup now also drops to 0 while the unit cap is hit, so the visuals calm down instead of "building" nothing
            warmup = Mathf.approachDelta(warmup, canBuild ? efficiency : 0f, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);

            //---- visual-only state ----
            flash = Mathf.approachDelta(flash, 0f, 1f / 45f);
            float vp = Mathf.clamp(buildProgress);
            //the heartbeat speeds up as the unit nears completion
            beatPhase = (beatPhase + Time.delta * Mathf.lerp(1f / 130f, 1f / 30f, vp * vp) * warmup) % 1f;

            if(!headless && warmup > 0.2f){
                //spores drifting up out of the pit, more of them the further along we are
                if(Mathf.chance(Time.delta * 0.05f * warmup * (0.3f + vp))){
                    NeoFx.spawnerSpore.at(x + Mathf.range(8f), y + Mathf.range(8f), glowColor);
                }
                //sparks sucked into the pit right before the spawn
                float charge = Mathf.curve(vp, chargeStart, 1f);
                if(charge > 0f && Mathf.chance(Time.delta * 0.4f * charge * warmup)){
                    float a = Mathf.random(360f);
                    NeoFx.spawnerCharge.at(x + Angles.trnsx(a, 19f), y + Angles.trnsy(a, 19f), a + 180f, glowColor);
                }
            }

            if(canBuild){
                buildProgress += edelta() / unitBuildTime;
                totalProgress += edelta();


                if(buildProgress >= 1f){
                    if(!net.client()){
                        unit = unitType.create(team);

                        if(unit instanceof BuildingTetherc bt){
                            bt.building(this);
                        }

                        unit.set(x, y);
                        unit.rotation = 90f;
                        unit.add();

                        if(spawnEffect != null){
                            spawnEffect.at(x, y, spawnColor != null ? spawnColor : Color.white);
                        }

                        Call.unitTetherBlockSpawned(tile, unit.id);
                    }

                    // 🔥 IMPORTANT: reset build cycle so it keeps spawning
                    buildProgress = 0f;
                }
            }
        }

        public void spawned(int id){
            Fx.spawn.at(x, y);
            buildProgress = 0f;
            flash = 1f;
            if(spawnShake > 0f && !headless) Effect.shake(spawnShake, spawnShake * 3f, this);
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
            return true;
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