/*
 * Copyright (c) 2019, Ganom <https://github.com/ganom>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.mesenhanced;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mesEnhanced")
public interface MesEnhancedConfig extends Config
{
	@ConfigItem(
		keyName = "leftClickLog",
		name = "1 Click Lighting",
		description = "This will allow you to left click logs to light them.",
		position = 1
	)
	default boolean leftClickLog()
	{
		return false;
	}

	@ConfigItem(
		keyName = "leftClickEssenceBlock",
		name = "1 Click Essence Blocks",
		description = "This will allow you to left click essence blocks to chisel them.",
		position = 2
	)
	default boolean leftClickEssenceBlock()
	{
		return false;
	}

	@ConfigItem(
		keyName = "quickBones",
		name = "1 Click Bones",
		description = "This will allow you to left click an altar to use your bones on them.",
		position = 3
	)
	default boolean quickBones()
	{
		return false;
	}

	@ConfigItem(
		keyName = "quickBurn",
		name = "1 Click Burn",
		description = "This will burn your food, but with only a single click.",
		position = 4
	)
	default boolean quickBurn()
	{
		return false;
	}

	@ConfigItem(
		keyName = "lavaStuff",
		name = "Auto use earth runes.",
		description = "Lava-ify stuff.",
		position = 5
	)
	default boolean lavaStuff()
	{
		return false;
	}

	@ConfigItem(
		keyName = "npcContactPouch",
		name = "NPC Contact on depleted rune pouches.",
		description = "NPC CONTACT STUFF MKAY.",
		position = 6
	)
	default boolean npcContactPouch()
	{
		return false;
	}
}
