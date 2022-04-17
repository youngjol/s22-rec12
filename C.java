import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class C extends Canvas {
	private static final long serialVersionUID = 1480902466428347458L;
	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private static final int RANGE = 30;
	private static final int INTERVAL = 5;

	private double[][] values = new double[WIDTH][HEIGHT];
	private double inertia = 0.8;
	private double decay = 0.9995;
	private double dissipation = 0.92;

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				values[x][y] = 0.0;
				g.setColor(color(values[x][y]));
				g.drawLine(x, y, x, y);
			}
		}

		int lastX = -1;
		int lastY = -1;
		int lastT = -1;
		while (true) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (Math.random() < 1.0/INTERVAL) {
				lastX = (int) (Math.random() * WIDTH);
				lastY = (int) (Math.random() * HEIGHT);
				lastT = 0;
			}
			else if (lastT >= 3) {
				lastX = -100;
			}
			lastT++;
			
			double[][] newValues = computeNewValues(lastX, lastY);
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					g.setColor(color(values[x][y]));
					g.drawLine(x, y, x, y);
				}
			}
			values = newValues;
		}
	}

	private double[][] computeNewValues(int lastX, int lastY) {
		double[][] newValues = new double[WIDTH][HEIGHT];
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				double value = 0.0;
				int count = 0;
				double dist = Math.sqrt(Math.pow(Math.abs(i - lastX), 2) + Math.pow(Math.abs(j - lastY), 2));
				if (dist < RANGE) {
					double newValue = 1.0;
					while (dist-- > 0) newValue *= dissipation;
					newValue = Math.max(newValue, values[i][j]);
					value = inertia * values[i][j] + (1 - inertia) * newValue;
				} else {
					for (int x = i - 5; x <= i + 5; x++) {
						for (int y = j - 5; y <= j + 5; y++) {
							if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
								continue;
							value += values[x][y];
							count++;
						}
					}
					value /= count;
				}
				newValues[i][j] = value * decay;
			}
		}
		return newValues;
	}

	private float fromValue(double value) {
		return (float) (((300 * (1.0 - value) + 300) % 360) / 360.0);
	}

	private Color color(double value) {
		return Color.getHSBColor(fromValue(value), 1.0f, .5f);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();

		frame.setSize(WIDTH, HEIGHT);
		frame.add(new C());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}