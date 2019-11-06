/*
 * Copyright (c) 2018-2019, Ethan <https://github.com/Wea1thRS/>
 * Copyright (c) 2018, https://openosrs.com
 * Copyright (c) 2019, Dillon <https://github.com/dillydill123>
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
package net.runelite.client.plugins.inventorysetups.ui;

import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupItem;
import net.runelite.client.plugins.inventorysetups.InventorySetupPlugin;
import net.runelite.client.ui.ColorScheme;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;

@Singleton
abstract class InventorySetupContainerPanel extends JPanel
{

	private final ItemManager itemManager;

	private final InventorySetupPlugin plugin;

	InventorySetupContainerPanel(final ItemManager itemManager, final InventorySetupPlugin plugin, String captionText)
	{
		this.itemManager = itemManager;
		this.plugin = plugin;
		JPanel containerPanel = new JPanel();

		final JPanel containerSlotsPanel = new JPanel();

		// sets up the custom container panel
		setupContainerPanel(containerSlotsPanel);

		// caption
		final JLabel caption = new JLabel(captionText);
		caption.setHorizontalAlignment(JLabel.CENTER);
		caption.setVerticalAlignment(JLabel.CENTER);

		// panel that holds the caption and any other graphics
		final JPanel captionPanel = new JPanel();
		captionPanel.add(caption);

		containerPanel.setLayout(new BorderLayout());
		containerPanel.add(captionPanel, BorderLayout.NORTH);
		containerPanel.add(containerSlotsPanel, BorderLayout.CENTER);

		add(containerPanel);
	}

	void setContainerSlot(int index, final InventorySetupSlot containerSlot, final List<InventorySetupItem> items)
	{
		if (index >= items.size() || items.get(index).getId() == -1)
		{
			containerSlot.setImageLabel(null, null);
			return;
		}

		int itemId = items.get(index).getId();
		int quantity = items.get(index).getQuantity();
		final String itemName = items.get(index).getName();
		AsyncBufferedImage itemImg = itemManager.getImage(itemId, quantity, quantity > 1);
		String toolTip = itemName;
		if (quantity > 1)
		{
			toolTip += " (" + quantity + ")";
		}
		containerSlot.setImageLabel(toolTip, itemImg);
	}

	void highlightDifferentSlotColor(final InventorySetup setup, InventorySetupItem savedItem, InventorySetupItem currItem, final InventorySetupSlot containerSlot)
	{
		// important note: do not use item names for comparisons
		// they are all empty to avoid clientThread usage when highlighting

		// first check if stack differences are enabled and compare quantities
		if (setup.isStackDifference() && currItem.getQuantity() != savedItem.getQuantity())
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// obtain the correct item ids using the variation difference if applicable
		int currId = currItem.getId();
		int checkId = savedItem.getId();

		if (!setup.isVariationDifference())
		{
			currId = ItemVariationMapping.map(currId);
			checkId = ItemVariationMapping.map(checkId);
		}

		// if the ids don't match, highlight the container slot
		if (currId != checkId)
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// set the color back to the original, because they match
		containerSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
	}

	protected abstract void setupContainerPanel(final JPanel containerSlotsPanel);

	abstract public void highlightDifferences(final List<InventorySetupItem> currContainer, final InventorySetup inventorySetup);

	abstract public void setSlots(final InventorySetup setup);

	abstract public void resetSlotColors();
}
