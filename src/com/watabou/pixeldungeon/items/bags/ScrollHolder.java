/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.items.bags;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.R;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.scrolls.Scroll;
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet;

public class ScrollHolder extends Bag {

	{
		name = Game.getVar(R.string.ScrollHolder_Name);
		image = ItemSpriteSheet.HOLDER;
		
		size = 12;
	}
	
	@Override
	public boolean grab( Item item ) {
		return item instanceof Scroll;
	}
	
	@Override
	public int price() {
		return 50;
	}
	
	@Override
	public String info() {
		return Game.getVar(R.string.ScrollHolder_Info);
	}
}