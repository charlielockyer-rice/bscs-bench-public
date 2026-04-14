package hw02.controller;

import java.awt.Graphics;

import hw02.ball.BallModel;
import hw02.ball.IModel2ViewAdapter;
import hw02.view.IView2ModelAdapter;
import hw02.view.ViewGUI;
import provided.utils.displayModel.IDimension;

/**
 * Following the MVC design patter, a controller which initializes and connects the model of moving
 * balls and a view to display the model
 * @author rlmse
 */
public class BallController {

	/**
	 * The model of moving balls
	 */
	private BallModel model;

	/**
	 * The view on which the model is displayed
	 */
	private ViewGUI frame;

	/**
	 * Constructs the model and view
	 */
	public BallController() {

		this.frame = new ViewGUI(new IView2ModelAdapter() {

			@Override
			public void makeBall(String ballType) {
				model.makeBall(ballType);
			}

			@Override
			public void clearBalls() {
				model.clearBalls();
			}

			@Override
			public void paintBalls(Graphics g) {
				BallController.this.model.update(g);
			}

		});

		this.model = new BallModel(new IModel2ViewAdapter() {

			@Override
			public void update() {
				frame.update();
			}

			@Override
			public IDimension getDimension() {
				return new IDimension() {

					@Override
					public int getWidth() {
						return frame.getBallWorld().getWidth();
					}

					@Override
					public int getHeight() {
						return frame.getBallWorld().getHeight();
					}

				};
			}

		});
	}

	/**
	 * Starts the model and view
	 */
	public void start() {
		this.frame.start();
		this.model.start();
	}

	/**
	 * Entry point for the BallWorld system. Initializes its controller
	 * @param args - not used
	 */
	public static void main(String[] args) {
		BallController controller = new BallController();
		controller.start();
	}

}
