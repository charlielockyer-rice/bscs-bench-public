package provided.abcMusic.player;

import provided.abcMusic.musicFW.Note;

/**
 * Abstract sequence player to create and play MIDI music sequences.
 * @author swong
 *
 */
public interface ISequencePlayer {
	/**
	 * End of track marker
	 */
	public static final int END_OF_TRACK = 47;

	/**
	 * Initialize the sequence player as per the supplied ticksPerQuarterNote and instrument
	 * this SequencePlayer was instantiated with.   This method is called by the constructor to 
	 * initialize the player upon instantiation and can be called again to reinitialize the 
	 * SequencePlayer.
	 * 
	 * @param  ticksPerQuarterNote      - tick definition
	 * @param  instrument               - MIDI instrument
	 * @return                         - true if properly initialized, false otherwise
	 */
	boolean init(int ticksPerQuarterNote, int instrument);

	/**
	 * Add a note to the MIDI sequence with a default velocity of 64 for middle volume
	 * 
	 * @param note     - the note to schedule in the sequence
	 * @param start    - the tick at which this note should start playing
	 * @return         - the tick at which this note stops playing
	 */
	int addNote(Note note, int start);

	/**
	 * Add a note to the MIDI sequence.
	 * 
	 * @param note     - the note to schedule in the sequence
	 * @param start    - the tick at which this note should start playing
	 * @param velocity - the volume (0-127)
	 * @return         - the tick at which this note stops playing
	 */
	int addNote(Note note, int start, int velocity);

	/**
	 * Play the created sequence.
	 * @param statusCmd The finished() method of this command is called when the track finishes being played, i.e. a normal termination.  There is no notification if play is forcibly stopped.
	 */
	void play(ISequencePlayerStatus statusCmd);

	/**
	 * Stop playing music and close resources.
	 */
	void stop();

	/**
	 * Accessor to get the current ticks per default note
	 * @return the _ticksPerDefaultNote
	 */
	int getTicksPerDefaultNote();

	/**
	 * Accessor to set the current ticks per default note
	 * @param ticksPerDefaultNote the _ticksPerDefaultNote to set
	 */
	void setTicksPerDefaultNote(int ticksPerDefaultNote);

	/**
	 * Accessor for the current tempo
	 * @return the tempo (in beats per minute)
	 */
	int getTempo();

	/**
	 * Set the tempo - can't change the tempo in the middle of a sequence,
	 * the last tempo set will be used for the whole sequence.
	 * 
	 * @param bpm - beats per minute
	 */
	void setTempo(int bpm);

	/**
	 * Accessor for the current number of ticks per quarter note.
	 * @return the number of ticks per quarter note
	 */
	int getTicksPerQuarterNote();

}