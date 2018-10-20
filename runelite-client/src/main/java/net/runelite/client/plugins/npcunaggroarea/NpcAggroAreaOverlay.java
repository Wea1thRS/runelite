/*
 * Copyright (c) 2018, Woox <https://github.com/wooxsolo>
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
package net.runelite.client.plugins.npcunaggroarea;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.geometry.Geometry;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class NpcAggroAreaOverlay extends Overlay
{
	private final static int MAX_LOCAL_DRAW_LENGTH = 20 * Perspective.LOCAL_TILE_SIZE;

	@Inject
	private Client client;

	@Inject
	private NpcAggroAreaConfig config;

	@Inject
	private NpcAggroAreaPlugin plugin;

	private PanelComponent panelComponent;

	@Inject
	public NpcAggroAreaOverlay()
	{
		panelComponent = new PanelComponent();
		panelComponent.setPreferredSize(new Dimension(150, 0));

		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.LOW);
	}

	private void renderPath(Graphics2D graphics, GeneralPath path, Color color)
	{
		LocalPoint playerLp = client.getLocalPlayer().getLocalLocation();
		Rectangle viewArea = new Rectangle(
			playerLp.getX() - MAX_LOCAL_DRAW_LENGTH,
			playerLp.getY() - MAX_LOCAL_DRAW_LENGTH,
			MAX_LOCAL_DRAW_LENGTH * 2,
			MAX_LOCAL_DRAW_LENGTH * 2);

		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(1));

		path = Geometry.clipPath(path, viewArea);
		path = Geometry.filterPath(path, (p1, p2) ->
			Perspective.localToCanvas(client, new LocalPoint((int)p1[0], (int)p1[1]), client.getPlane()) != null &&
			Perspective.localToCanvas(client, new LocalPoint((int)p2[0], (int)p2[1]), client.getPlane()) != null);
		path = Geometry.transformPath(path, coords ->
		{
			Point point = Perspective.localToCanvas(client, new LocalPoint((int)coords[0], (int)coords[1]), client.getPlane());
			coords[0] = point.getX();
			coords[1] = point.getY();
		});

		graphics.draw(path);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isActive())
		{
			return null;
		}

		if (plugin.getSafeCenters()[1] == null)
		{
			setPosition(OverlayPosition.TOP_LEFT);
			panelComponent.getChildren().clear();
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Unaggressive NPC timers will start working when you teleport far away or enter a dungeon.")
				.build());
			return panelComponent.render(graphics);
		}

		setPosition(OverlayPosition.DYNAMIC);

		GeneralPath lines = plugin.getLinesToDisplay()[client.getPlane()];
		if (lines == null)
		{
			return null;
		}

		Color outlineColor = config.aggroAreaColor();
		Instant endTime = plugin.getEndTime();
		if (endTime != null && Instant.now().compareTo(endTime) < 0)
		{
			outlineColor = new Color(
				outlineColor.getRed(),
				outlineColor.getGreen(),
				outlineColor.getBlue(),
				100);
		}

		renderPath(graphics, lines, outlineColor);

		return null;
	}
}
