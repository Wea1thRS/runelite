/*
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *	list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the documentation
 *	and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.inferno;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.inferno.displaymodes.InfernoPrayerOverlayMode;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

@Singleton
public class InfernoInfoBoxOverlay extends Overlay
{
	private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);
	private final Client client;
	private final InfernoPlugin plugin;
	private final SpriteManager spriteManager;
	private final PanelComponent imagePanelComponent = new PanelComponent();

	@Inject
	private InfernoInfoBoxOverlay(final Client client, final InfernoPlugin plugin, final SpriteManager spriteManager)
	{
		setPosition(OverlayPosition.BOTTOM_RIGHT);
		setPriority(OverlayPriority.HIGH);
		this.client = client;
		this.plugin = plugin;
		this.spriteManager = spriteManager;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isShowPrayerHelp() || (plugin.getPrayerOverlayMode() != InfernoPrayerOverlayMode.BOTTOM_RIGHT
			&& plugin.getPrayerOverlayMode() != InfernoPrayerOverlayMode.BOTH))
		{
			return null;
		}

		InfernoNPC closestToAttack = null;

		for (InfernoNPC infernoNPC : plugin.getInfernoNpcs().values())
		{
			if (infernoNPC.getTicksTillNextAttack() <= 0 || infernoNPC.getNextAttack() == InfernoNPC.Attack.UNKNOWN)
			{
				continue;
			}

			// Determine which NPC is about to attack next
			if ((closestToAttack == null && infernoNPC.getTicksTillNextAttack() > 0 && infernoNPC.getType().getPriority() < 99)
				|| (closestToAttack != null && infernoNPC.getTicksTillNextAttack() < closestToAttack.getTicksTillNextAttack()
				&& infernoNPC.getType().getPriority() < 99)
				|| (closestToAttack != null && infernoNPC.getTicksTillNextAttack() == closestToAttack.getTicksTillNextAttack()
				&& infernoNPC.getType().getPriority() < closestToAttack.getType().getPriority()))
			{
				if (infernoNPC.getNextAttack() != InfernoNPC.Attack.UNKNOWN)
				{
					closestToAttack = infernoNPC;
				}
			}
		}

		imagePanelComponent.getChildren().clear();

		if (closestToAttack != null)
		{
			final BufferedImage prayerImage = getPrayerImage(closestToAttack.getNextAttack());

			imagePanelComponent.getChildren().add(new ImageComponent(prayerImage));
			imagePanelComponent.setBackgroundColor(client.isPrayerActive(closestToAttack.getNextAttack().getPrayer())
				? ComponentConstants.STANDARD_BACKGROUND_COLOR
				: NOT_ACTIVATED_BACKGROUND_COLOR);
		}
		else
		{
			imagePanelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
		}

		return imagePanelComponent.render(graphics);
	}

	private BufferedImage getPrayerImage(InfernoNPC.Attack attack)
	{
		int prayerSpriteID;

		switch (attack)
		{
			case MELEE:
				prayerSpriteID = SpriteID.PRAYER_PROTECT_FROM_MELEE;
				break;
			case RANGED:
				prayerSpriteID = SpriteID.PRAYER_PROTECT_FROM_MISSILES;
				break;
			case MAGIC:
				prayerSpriteID = SpriteID.PRAYER_PROTECT_FROM_MAGIC;
				break;
			default:
				prayerSpriteID = SpriteID.PRAYER_PROTECT_FROM_MAGIC;
				break;
		}

		return spriteManager.getSprite(prayerSpriteID, 0);
	}
}