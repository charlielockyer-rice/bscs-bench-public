package hw06.controller;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.util.function.Supplier;

import javax.swing.JComponent;

import hw06.model.BallModel;
import hw06.model.IViewCtrlAdapter;
import hw06.model.BallVisitors.IBallAlgo;
import hw06.view.BallGUI;
import hw06.view.IModelCtrlAdapter;
import hw06.view.IModelUpdateAdapter;

/**
 * The controller class to instantiate the Ball view as well as Ball model.
 * @author Cole Rabson and Son Nguyen
 */
public class BallControl {

	/**
	 * The BallModel that we use to communicate to the view.
	 */
	private BallModel model;
	/**
	 * The GUI that runs the our model.
	 */
	private BallGUI<IBallAlgo<Void, Void>> view;

	/**
	 * The constructor for the controller that instantiate the model and the view.
	 */
	public BallControl() {
		//Instantiating the model and implement the adapter

		this.model = new BallModel(new IViewCtrlAdapter() {
				@Override
				public Container getCanvas() {
					return view.getCanvas();
				}
	
				@Override
				public void addConfigComponent(String label, Supplier<JComponent> compFac) {
					view.addComponentFac(label,compFac);
				} 
			
			}, () -> view.update());

		//Instantiating the view and implement the adapters
		this.view = new BallGUI<IBallAlgo<Void, Void>>(new IModelCtrlAdapter<IBallAlgo<Void, Void>>() {
			/**
			 * Returns an IBallAlgo that can instantiate the strategy specified
			 * by classname. Returns null if classname is null. The toString() of
			 * the returned factory is the classname.
			* @param classname  Shortened name of desired strategy 
			* @return A factory to make that strategy
			*/
			@Override
			public IBallAlgo<Void, Void> addUpdateStrategy(String classname) {
				return model.makeUpdateStrategyAlgo(classname);
			}

			/**
			 * Returns an IBallAlgo that can instantiate the strategy specified
			 * by classname. Returns null if classname is null. The toString() of
			 * the returned factory is the classname.
			* @param classname  Shortened name of desired strategy 
			* @return A factory to make that strategy
			*/
			@Override
			public IBallAlgo<Void, Void> addPaintStrategy(String classname) {
				return model.makePaintStrategyAlgo(classname);
			}

			@Override
			/**
			 * Add a ball to the system with a strategy as given by the given IBallAlgo
			 * @param selectedItem The fully qualified name of the desired strategy.
			 */
			public void makeBall(IBallAlgo<Void, Void> selectedItem,  String classname) {
				if (selectedItem != null)
					model.makeBall(selectedItem, classname);
			}

			@Override
			/**
			 * Returns an IBallAlgo that can instantiate a MultiStrategy with the
			 * two strategies made by the two given IStrategyFac objects. Returns
			 * null if either supplied factory is null. The toString() of the
			 * returned factory is the toString()'s of the two given factories,
			 * concatenated with "-".             * 
			 * @param selectedItem1 An IStrategyFac for a strategy
			 * @param selectedItem2 An IStrategyFac for a strategy
			 * @return An IStrategyFac for the composition of the two strategies
			 */
			public IBallAlgo<Void, Void> combineStrategies(IBallAlgo<Void, Void> selectedItem1, IBallAlgo<Void, Void> selectedItem2) {
				return model.combineStrategyAlgos(selectedItem1, selectedItem2);
			}

			/**
			 * Removes all existing balls from the view.
			 */
			@Override
			public void clearBalls() {
				model.clearBalls();
			}

			/**
			 * Makes a new empty switcher ball.
			 */
			public void makeSwitcherBall() {
				model.makeBall(model.getSwitcherInstallAlgo(), "Default");
			}

			/** 
			 * Switch all switcher balls to a new strategy. 
			 */
			@Override
			public void switchStrategy(IBallAlgo<Void, Void> selectedItem) {
				model.switchSwitcherStrategy(selectedItem);
			}

			@Override
			public IBallAlgo<Void, Void> addInteractStrategy(String classname) {
				return model.makeInteractStrategyAlgo(classname);
			}

			@Override
			public IBallAlgo<Void, Void> addConfigAlgo(String text) {
				return model.makeConfigAlgo(text);
			}

		}, new IModelUpdateAdapter() {
			/**
			 * Paint the balls onto the view. 
			 */
			public void paint(Graphics g) {
				model.update((context, disp) -> {
					context.updateState(disp);
					context.paint(g);
					context.bounce();
					context.move();
				});
			}
		});
	}

	/**
	 * Starts the Controller once the view and model has been instantiated
	 */
	public void start() {
		this.view.start();
		this.model.start();
	}

	/**
	 * Launch the whole application (View + Model)
	 * @param args Any input arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				(new BallControl()).start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
