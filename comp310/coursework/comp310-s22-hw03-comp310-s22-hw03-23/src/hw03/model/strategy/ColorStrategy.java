package hw03.model.strategy;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author Christina
 * Color strategy chooses a random color to switch between
 */
public class ColorStrategy implements IUpdateStrategy {

	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		Random random = new Random();
		float hue = random.nextFloat();
		// Saturation between 0.1 and 0.3
		float saturation = (random.nextInt(2000) + 1000) / 10000f;
		float luminance = 0.9f;
		Color color = Color.getHSBColor(hue, saturation, luminance);
		ball.setColor(color);
	}
	
}
