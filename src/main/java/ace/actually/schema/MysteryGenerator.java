package ace.actually.schema;

import ace.actually.MMDollhouse;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.MutableText;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameMode;

import java.util.*;

public class MysteryGenerator {
    public static final String[] MOBS = {"Pillager","Vindicator","Evoker","Illusioner","Witch","Shady Trader"};
    public static final Item[] WEAPONS = {Items.IRON_AXE,Items.IRON_SWORD,Items.TRIDENT,Items.BOW,Items.CROSSBOW,Items.POTION,Items.BOOK,Items.BOWL,Items.WHITE_CANDLE};
    public static final String[] ROOMS = {"bathroom","boilerroom","danceroom","kitchen","library","pooltable","storage"};
    public static final int DANGEROUS = 6;

    public static void generateMystery(long seed, ServerPlayerEntity spe)
    {
        Random random = new Random(seed);
        NbtCompound mystery = new NbtCompound();

        String mob = MOBS[random.nextInt(MOBS.length)];
        Item weapon = WEAPONS[random.nextInt(DANGEROUS)];
        List<String> rooms = Arrays.stream(ROOMS).filter(a-> random.nextInt(4)>0).toList();
        String room = rooms.get(random.nextInt(rooms.size()));

        ServerWorld houses = spe.getServer().getWorld(MMDollhouse.HOUSES);
        BlockPos p = new BlockPos(random.nextInt(100000),random.nextInt(200), random.nextInt(100000));
        spe.changeGameMode(GameMode.ADVENTURE);
        spe.teleport(houses,p.getX()+3,p.getY()+2,p.getZ()+3,PositionFlag.ROT,0,0,true);

        spe.getServer().getStructureTemplateManager().getTemplateOrBlank(Identifier.of("mmdollhouse","main_room"))
                .place(spe.getWorld(),p,BlockPos.ORIGIN,new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE),spe.getRandom(),3);
        List<Vec3i> ROOM_POS = new ArrayList<>(List.of(new Vec3i(-9,0,0),new Vec3i(0,0,-9),new Vec3i(-9,0,9), new Vec3i(18,0,0), new Vec3i(18,0,9),new Vec3i(0,0,18),new Vec3i(9,0,18)));
        for(String iroom: rooms)
        {
            Vec3i mov = ROOM_POS.remove(random.nextInt(ROOM_POS.size()));
            spe.getServer().getStructureTemplateManager().getTemplateOrBlank(Identifier.of("mmdollhouse",iroom))
                    .place(spe.getWorld(),p.add(mov),BlockPos.ORIGIN,new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE),spe.getRandom(),3);
        }


        List<String> activities = new ArrayList<>();
        activities.add(mob+">"+room+"#"+weapon.getTranslationKey());
        for (int i = 0; i < 10; i++) {
            String v = MOBS[random.nextInt(MOBS.length)]+">"+rooms.get(random.nextInt(rooms.size()))+"#"+WEAPONS[random.nextInt(WEAPONS.length)].getTranslationKey();
            if(!v.contains(mob) && !v.contains(room) && !v.contains(weapon.getTranslationKey()))
            {
                activities.add(v);
            }

        }
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            passwords.add(rooms.get(i)+": "+random.nextInt(1000,10000));
        }



        NbtCompound stockItems = new NbtCompound();
        for(Item item: WEAPONS)
        {
            if(item==weapon)
            {
                //if the item is the weapon, then there is 1 less in stock then there should be
                stockItems.putInt(item.getTranslationKey(),random.nextInt(1,4)-1);
            }
            else
            {
                stockItems.putInt(item.getTranslationKey(),random.nextInt(1,4));
            }

        }

        List<String> mobCopy = new ArrayList<>(List.of(MOBS));
        mobCopy.remove(mob);
        NbtList pairings = new NbtList();
        while (mobCopy.size()>1)
        {
            String mob1 = mobCopy.get(0);
            String mob2 = mobCopy.get(random.nextInt(mobCopy.size()));
            if(!mob1.equals(mob2))
            {
                pairings.add(NbtString.of(mob1+"@"+mob2));
                mobCopy.remove(mob1);
                mobCopy.remove(mob2);
            }

        }


        List<String> notes = new ArrayList<>();
        for (int i = 0; i < rooms.size()+5; i++) {
            switch (random.nextInt(4))
            {
                case 0 -> notes.add(random.nextInt(rooms.size())+"£"+passwords.get(random.nextInt(passwords.size())));
                case 1 -> notes.add(random.nextInt(rooms.size())+"£"+activities.get(random.nextInt(activities.size())));
                case 2 ->
                {
                    Item ritem = WEAPONS[random.nextInt(WEAPONS.length)];
                    notes.add(random.nextInt(rooms.size())+"£"+ritem.getTranslationKey()+"&"+stockItems.getInt(ritem.getTranslationKey()).get());
                }
                case 3 -> notes.add(random.nextInt(rooms.size())+"£"+pairings.getString(random.nextInt(pairings.size())).get());
            }
        }
        mystery.put("pairings",pairings);
        mystery.putIntArray("exit",new int[]{spe.getBlockX(),spe.getBlockY(),spe.getBlockZ()});
        mystery.putString("room",room);
        mystery.putLong("seed",seed);
        mystery.put("stockItems",stockItems);
        mystery.putString("mob",mob);
        mystery.putString("weapon",weapon.getTranslationKey());
        NbtList nbtRooms = new NbtList();
        rooms.forEach(a->nbtRooms.add(NbtString.of(a)));
        mystery.put("rooms",nbtRooms);
        NbtList nbtActivities = new NbtList();
        activities.forEach(a->nbtActivities.add(NbtString.of(a)));
        mystery.put("activities",nbtActivities);
        NbtList nbtPasswords = new NbtList();
        passwords.forEach(a->nbtPasswords.add(NbtString.of(a)));
        mystery.put("passwords",nbtPasswords);
        NbtList nbtNotes = new NbtList();
        notes.forEach(a->nbtNotes.add(NbtString.of(a)));
        mystery.put("notes",nbtNotes);

        NbtCompound data = spe.getServer().getDataCommandStorage().get(MMDollhouse.DATA);
        NbtCompound mysteries = data.getCompoundOrEmpty("mysteries");
        mysteries.put(""+seed,mystery);


        data.put("mysteries",mysteries);
        NbtCompound compound = data.getCompoundOrEmpty("players");
        compound.putString(spe.getUuidAsString(),""+seed);
        data.put("players",compound);

        spe.getServer().getDataCommandStorage().set(MMDollhouse.DATA,data);



        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        RawFilteredPair<String> title = new RawFilteredPair<>("Initial Questions", Optional.empty());
        String author = spe.getNameForScoreboard();
        List<RawFilteredPair<Text>> pages = new ArrayList<>();
        for(String npc: MOBS)
        {
            Text text = Text.of("");
            switch (random.nextInt(4))
            {
                case 0 ->
                {
                    //if the npc is the antagonist then it shouldn't reveal the correct activity
                    if(npc.equals(mob))
                    {
                        if(activities.size()>1)
                        {
                            text = Text.of(npc+"\n").copy().append(formatText(activities.get(random.nextInt(1,activities.size()))));
                        }
                        else
                        {
                            text = Text.of(npc+"\n").copy().append(formatText(passwords.get(random.nextInt(passwords.size()))));
                        }
                    }
                    else
                    {
                        text = Text.of(npc+"\n").copy().append(formatText(activities.get(random.nextInt(activities.size()))));
                    }
                }
                case 1 ->
                {
                    text = Text.of(npc+"\n").copy().append(formatText(passwords.get(random.nextInt(passwords.size()))));
                }
                case 2 ->
                {
                    Item ritem = WEAPONS[random.nextInt(WEAPONS.length)];
                    if(weapon==ritem)
                    {
                        text = Text.of(npc+"\n").copy().append(formatText(ritem.getTranslationKey()+"&"+(stockItems.getInt(ritem.getTranslationKey()).get()+1)));
                    }
                    else
                    {
                        text = Text.of(npc+"\n").copy().append(formatText(ritem.getTranslationKey()+"&"+stockItems.getInt(ritem.getTranslationKey()).get()));
                    }

                }
                case 3 ->
                {
                    text = Text.of(npc+"\n").copy().append(formatText(pairings.getString(random.nextInt(pairings.size())).get()));
                }
            }
            pages.add(new RawFilteredPair<>(text,Optional.empty()));
        }
        WrittenBookContentComponent component = new WrittenBookContentComponent(title,author,0,pages,true);
        book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT,component);
        spe.giveItemStack(book);
    }

    public static Text formatText(String text)
    {
        MutableText formatted = Text.empty();
        if(text.contains("£"))
        {
            String[] split = text.split("£");
            formatted.append("You found a note: ");
            text = split[1];
        }
        if(text.contains("&"))
        {
            String[] split = text.split("&");
            formatted.append(split[1]).append(" ").append(Text.translatable(split[0])).append("(s) are in stock");
        }
        if(text.contains(": "))
        {
            String[] split = text.split(": ");
            formatted.append("The password for the safe in ").append(split[0]).append(" is ").append(split[1]);
        }
        if(text.contains("@"))
        {
            String[] split = text.split("@");
            formatted.append("Both ").append(split[0]).append(" and ").append(split[1]).append(" say they where in the same room together at the time");
        }
        if(text.contains("#"))
        {
            String[] split = text.split(">");
            String m  = split[0];
            split = split[1].split("#");
            formatted.append("Saw the ").append(m).append(" enter ").append(split[0]).append(" with a ").append(Text.translatable(split[1]));

        }

        return formatted;
    }
}
