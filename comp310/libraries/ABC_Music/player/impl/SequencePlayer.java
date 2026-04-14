package provided.abcMusic.player.impl;

import javax.sound.midi.*;

import provided.logger.LogLevel;
import provided.abcMusic.player.ISequencePlayerStatus;

/**
 * A basic sequence player to create and play MIDI music sequences.
 */
public class SequencePlayer extends ASequencePlayer {



	/**
	 * MIDI sequencer object
	 */
	private Sequencer _sequencer;

	/**
	 *  MIDI synthesizer object
	 */
	private Synthesizer _synthesizer;

	/**
	 * Create a new SequencePlayer to create and play music.
	 * 
	 * @param  ticksPerQuarterNote      - tick definition
	 * @param  instrument               - MIDI instrument
	 */
	public SequencePlayer(int ticksPerQuarterNote, int instrument) {
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

		_sequencer = null;
		_synthesizer = null;

		try {
			_sequencer = MidiSystem.getSequencer();
			_synthesizer = MidiSystem.getSynthesizer();

			return initTrack();
		} catch (MidiUnavailableException e) {
			logger.log(LogLevel.ERROR, "MIDI Unavailable, SequencePlayer not initialized.");
			return false;
		} 
	}



	/**
	 * Play the created Sequence.
	 * @param statusCmd The finished() method of this command is called when the track finishes being played, i.e. a normal termination.  There is no notification if play is forcibly stopped.
	 */
	@Override
	public void play(final ISequencePlayerStatus statusCmd) {
		try {
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
						logger.log(LogLevel.INFO, "Finished Playing");
						stop();
						statusCmd.finished();
					}
				}
			});

			// And start playing now.
			_sequencer.start();
		} catch (MidiUnavailableException e) {
			logger.log(LogLevel.ERROR, "Unable to open MIDI synthesizer.");
			// e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			logger.log(LogLevel.CRITICAL, "Unable to play sequence.");
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop playing music and close resources.
	 */
	@Override
	public void stop() {
		logger.log(LogLevel.INFO, "Stopping play.");
		_sequencer.close();
		_synthesizer.close();
	}

}
