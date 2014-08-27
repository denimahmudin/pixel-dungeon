package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Utils;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

/**
 * Created by debenhame on 26/08/2014.
 */
public class HornOfPlenty extends Artifact {

    //TODO: test for bugs, tune numbers, add sprite switching.

    {
        name = "Horn of Plenty";
        image = ItemSpriteSheet.ARTIFACT_HORN;
        level = 0;
        charge = 0;
        chargeCap = 10;
    }

    private static final float TIME_TO_EAT	= 3f;

    private float energy = 36f;

    public static final String AC_EAT = "EAT";
    public static final String AC_STORE = "STORE";

    protected String inventoryTitle = "Select a piece of food";
    protected WndBag.Mode mode = WndBag.Mode.FOOD;

    private static final String TXT_STATUS	= "%d/%d";

    @Override
    public ArrayList<String> actions( Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped( hero ) && charge > 0)
            actions.add(AC_EAT);
        if (isEquipped( hero ) && level < 150)
            actions.add(AC_STORE);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {
        super.execute(hero, action);

        if (action.equals(AC_EAT)){
            ((Hunger)hero.buff( Hunger.class )).satisfy( energy*charge );

            //if you get at least 100 food energy from the horn
            if (charge >= 3){
                switch (hero.heroClass) {
                    case WARRIOR:
                        if (hero.HP < hero.HT) {
                            hero.HP = Math.min( hero.HP + 5, hero.HT );
                            hero.sprite.emitter().burst( Speck.factory(Speck.HEALING), 1 );
                        }
                        break;
                    case MAGE:
                        hero.belongings.charge( false );
                        ScrollOfRecharging.charge(hero);
                        break;
                    case ROGUE:
                    case HUNTRESS:
                        break;
                }

                Statistics.foodEaten++;
            }
            charge = 0;

            hero.sprite.operate( hero.pos );
            hero.busy();
            SpellSprite.show(hero, SpellSprite.FOOD);
            Sample.INSTANCE.play( Assets.SND_EAT );

            hero.spend( TIME_TO_EAT );

            Badges.validateFoodEaten();

            image = ItemSpriteSheet.ARTIFACT_HORN;

        } else if (action.equals(AC_STORE)){

            GameScene.selectItem(itemSelector, mode, inventoryTitle);
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new hornRecharge();
    }

    @Override
    public String desc() {
        //TODO: add description
        return "";
    }

    @Override
    public String status() {
        return Utils.format(TXT_STATUS, charge, chargeCap);
    }

    @Override
    public String toString() {
        return super.toString() + " (" + status() +  ")" ;
    }

    public class hornRecharge extends ArtifactBuff{

        @Override
        public boolean act() {
            if (charge < chargeCap) {

                partialCharge += (1/(200-level));

                if (partialCharge >= 1) {
                    charge++;
                    //TODO: change sprite based on fullness.
                    partialCharge -= 1;
                    if (charge == chargeCap){
                        GLog.p("Your horn is full of food.");
                        partialCharge = 0;
                    }
                }
            } else
                partialCharge = 0;


            spend( TICK );

            return true;
        }

    }

    protected static WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( Item item ) {
            if (item != null && item instanceof Food) {
                if (item instanceof Blandfruit && ((Blandfruit) item).potionAttrib == null){
                    GLog.w("the horn rejects your unprepared blandfruit.");
                } else {
                    curItem.level += ((Food)item).hornValue;
                    if (curItem.level >= 150){
                        curItem.level = 150;
                        GLog.p("your horn has consumed all the food it can!");
                    } else
                        GLog.p("the horn consumes your food offering and grows in strength!");
                    item.detach(Dungeon.hero.belongings.backpack);
                }

            }
        }
    };

}