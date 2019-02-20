package net.runelite.client.plugins.inventorysetups.ui;

import net.runelite.client.game.AsyncBufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;

public class InventorySetupSlot extends JPanel
{

	private final JLabel imageLabel;

	public InventorySetupSlot(Color color)
	{
		imageLabel = new JLabel();
		imageLabel.setVerticalAlignment(SwingConstants.CENTER);

		setBackground(color);
		add(imageLabel);
	}

	public void setImageLabel(String toolTip, AsyncBufferedImage itemImage)
	{
		if (itemImage == null || toolTip == null)
		{
			imageLabel.setToolTipText("");
			imageLabel.setIcon(null);
			imageLabel.revalidate();
			return;
		}

		imageLabel.setToolTipText(toolTip);
		itemImage.addTo(imageLabel);

		validate();
		repaint();

	}



}
