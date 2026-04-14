package provided.abcMusic.player.impl;

import java.util.ArrayList;

import javax.sound.midi.*;

import provided.logger.LogLevel;
import provided.abcMusic.player.ISequencePlayerStatus;

/**
 * An advanced sequence player to create and play MIDI music sequences.
 * Differs from SequencePlayer in that this class has 
 * a factory method that creates an SequencePlayer2.IPlayable object that can be individually 
 * and asynchronously started and stopped.   This enables more control over the playing process,
 * including managing multiple, simultaneously playing songs. 
 * The play() and stop() methods are overridden to automatically create IPlayables and hold them in 
 * an internal queue. 
 */
public class SequencePlayer2  extends ASequencePlayer{

	/**
	 * Interface  that represents a playable entity.
	 * @author swong
	 *
	 */
	public static interface IPlayable {

		/**
		 * Starts the sequencer associated with this playable entity
		 */
		public void start();

		/**
		 * Stops the sequencer and synthesizer associated with this playable entity
		 */
		public void stop();

		/**
		 * Null object instance that no-ops the start and stop methods.
		 */
		public static final IPlayable NULL = new IPlayable() {

			@Override
			public void start() {
			}

			@Override
			public void stop() {
			}
		};
	}

	/**
	 * End of track marker
	 */
	public static final int END_OF_TRACK = 47;


	/**
	 * Create a new SequencePlayer to create and play music.
	 * 
	 * @param  ticksPerQuarterNote      - tick definition
	 * @param  instrument               - MIDI instrument
	 */
	public SequencePlayer2(int ticksPerQuarterNote, int instrument) {
		super(ticksPerQuarterNote, instrument);
	}

	/**
	 * Initialize the SequencePlayer as per the supplied ticksPerQuarterNote and instrument
	 * this SequencePlayer was instantiated with.   This method is called by the constructor to 
	 * initialize the player upon instantiation and can be called again to reinitialize the 
	 * SequencePlayer.
	 * 
	 * @param  ticksPerQuarterNote      - tick definition
	 * @param  instrument               - MIDI instrument
	 * @return                         - true if properly initialized, false otherwise
	 */
	@Override
	public boolean init(int ticksPerQuarterNote, int instrument) {
		initBase(ticksPerQuarterNote, instrument);
		
		return initTrack();
	}



	/**
	 * Factory method for an IPlayable object that can play the currently programmed music sequence.
	 * @param statusCmd The finished() method of this command is called when the track finishes being played, i.e. a normal termination.  There is no notification if play is forcibly stopped.
	 * @return An IPlayable object associated with the currently programmed music sequence.
	 */
	public IPlayable makePlayable(final ISequencePlayerStatus statusCmd) {
		try {
			final Sequencer _sequencer = MidiSystem.getSequencer(); // MIDI Sequencer object
			final Synthesizer _synthesizer = MidiSystem.getSynthesizer(); // MIDI synthesizer object

			final IPlayable playable = new IPlayable() {

				@Override
				public void start() {
					_sequencer.start();
				}

				@Override
				public void stop() {
					_sequencer.close();
					_synthesizer.close();
				}
			};

			_sequencer.open();
			_synthesizer.open();

			_sequencer.getTransmitter().setReceiver(_synthesizer.getReceiver());

			// Specify the sequence to play, and the tempo to play it at
			_sequencer.setSequence(getSequence());
			_sequencer.setTempoInBPM(getTempo());

			// Let us know when it is done playing
			_sequencer.addMetaEventListener(new MetaEventListener() {
				public void meta(MetaMessage m) {
					// A message of this type is automatically sent
					// when we reach the end of the track
					if (m.getType() == END_OF_TRACK) {
						logger.log(LogLevel.INFO, "SequencePlayer2.play(): End of Track");
						playable.stop();
						statusCmd.finished();
					} else
						logger.log(LogLevel.INFO, "Message type received: " + m.getType());
				}
			});

			return playable;
		} catch (MidiUnavailableException e) {
			logger.log(LogLevel.ERROR, "Unable to open MIDI synthesizer.");
			// e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			logger.log(LogLevel.CRITICAL, "Unable to play sequence.");
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return IPlayable.NULL;
	}

	/**
	 * The queue of of currently playing songs
	 */
	private ArrayList<SequencePlayer2.IPlayable> _playableQueue = new ArrayList<SequencePlayer2.IPlayable>();
	
	/**
	 * Plays the currently loaded song and adds it to the internal queue of IPlayables
	 * @param statusCmd The command to run when the currently song finishes playing.
	 */
	@Override
	public void play(ISequencePlayerStatus statusCmd) {
		logger.log(LogLevel.INFO, "[SequencePlayer2.play()] adding song to queue and playing it...");
		IPlayable[] _playable = new IPlayable[1];
		_playable[0] = this.makePlayable(new ISequencePlayerStatus() {

			@Override
			public void finished() {
				statusCmd.finished();
				_playableQueue.remove(_playable[0]);
			}});	
		_playable[0].start();
		_playableQueue.add(_playable[0]);
	}

	/**
	 * Stop the first playable in the internal queue if there is any.
	 */
	@Override
	public void stop() {
		if(_playableQueue.isEmpty()){
			logger.log(LogLevel.INFO, "[SequencePlayer2.stop()] Nothing stopped. The internal queue is empty.");
		}
		else {
			logger.log(LogLevel.INFO, "[SequencePlayer2.stop()] Stopped the first IPlayable in a queue of "+_playableQueue.size()+".");
			_playableQueue.remove(0).stop();
		}
		
	}

}
