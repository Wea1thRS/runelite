/*
 * Copyright (c) 2019, Frosty Fridge <https://github.com/frostyfridge>
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
package net.runelite.client.plugins.blackjack;

import com.google.inject.Provides;
import net.runelite.api.*;
import lombok.Getter;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name = "Blackjack Helper",
        description = "Dynamically change default menu entry while blackjacking to make knock-out or pickpocket the default option",
        tags = {"thieving", "npcs"}
)
public class BlackjackPlugin extends Plugin {


    @Inject
    private Client client;

    @Inject
    private BlackjackConfig config;

    @Getter
    private Map<Integer, NPC> blackjackTargets;

    @Provides
    BlackjackConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(BlackjackConfig.class);
    }

    @Override
    public void startUp()
    {
        blackjackTargets = new HashMap<>();
    }

    @Override
    public void shutDown()
    {
        blackjackTargets = null;
    }

    private boolean isNpcBlackjackTarget(int npcId)
    {
        return npcId == NpcID.VILLAGER ||
                npcId == NpcID.VILLAGER_3553 ||
                npcId == NpcID.VILLAGER_3554 ||
                npcId == NpcID.VILLAGER_3555 ||
                npcId == NpcID.VILLAGER_3556 ||
                npcId == NpcID.VILLAGER_3557 ||
                npcId == NpcID.VILLAGER_3558 ||
                npcId == NpcID.VILLAGER_3559 ||
                npcId == NpcID.VILLAGER_3560 ||
                npcId == NpcID.MENAPHITE_THUG ||
                npcId == NpcID.MENAPHITE_THUG_3550 ||
                npcId == NpcID.BANDIT_734 ||
                npcId == NpcID.BANDIT_735 ||
                npcId == NpcID.BANDIT_736 ||
                npcId == NpcID.BANDIT_737;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        NPC npc = event.getNpc();
        if (isNpcBlackjackTarget(npc.getId()))
        {
            blackjackTargets.put(npc.getIndex(), npc);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        NPC npc = event.getNpc();
        if (isNpcBlackjackTarget(npc.getId()))
        {
            blackjackTargets.remove(npc.getIndex());
        }
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        if (config.menuSwapActive() && blackjackTargets.containsKey(event.getIdentifier())) {
            final int eventId = event.getIdentifier();
            NPC npc = blackjackTargets.get(eventId);

            final String option = Text.removeTags(event.getOption()).toLowerCase();
            final String target = Text.removeTags(event.getTarget()).toLowerCase();
            if (npc.getAnimation() == AnimationID.BLACKJACK_KO) {
                swap("pickpocket", option, target, true);
            } else {
                swap("knock-out", option, target, true);
            }
        }
    }

    //swap() and searchIndex() are copied from MenuEntrySwapperPlugin.
    private void swap(String optionA, String optionB, String target, boolean strict)
    {
        MenuEntry[] entries = client.getMenuEntries();

        int idxA = searchIndex(entries, optionA, target, strict);
        int idxB = searchIndex(entries, optionB, target, strict);

        if (idxA >= 0 && idxB >= 0)
        {
            MenuEntry entry = entries[idxA];
            entries[idxA] = entries[idxB];
            entries[idxB] = entry;

            client.setMenuEntries(entries);
        }
    }

    private int searchIndex(MenuEntry[] entries, String option, String target, boolean strict)
    {
        for (int i = entries.length - 1; i >= 0; i--)
        {
            MenuEntry entry = entries[i];
            String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
            String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

            if (strict)
            {
                if (entryOption.equals(option) && entryTarget.equals(target))
                {
                    return i;
                }
            }
            else
            {
                if (entryOption.contains(option.toLowerCase()) && entryTarget.equals(target))
                {
                    return i;
                }
            }
        }

        return -1;
    }
}
