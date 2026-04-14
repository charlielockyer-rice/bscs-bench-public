/**
 * 
 */
package provided.abcMusic.player.impl;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.abcMusic.musicFW.Note;
import provided.abcMusic.player.ISequencePlayer;

/**
 * Common aspects of all sequence player implementations.
 * @author swong
 *
 */
public abstract class ASequencePlayer implements ISequencePlayer {
	
	/**
	 * Logger for this and subclasses to use
	 */
	protected ILogger logger = ILoggerControl.getSharedLogger();
	
	/**
	 * The offset of the ticks from zero to reduce the "repeated first note" problem.
	 */
	private static final int TICK_OFFSET = 15;  
	
	/**
	 * Half note steps from C in an octave
	 */
	private static final int[] _offsets = { // add these amounts to the base value A  B  C  D  E  F  G
		9, 11, 0, 2, 4, 5, 7 
	};
	
	/**
	 * Track within music sequence
	 */
	private Track _track;

	/**
	 * Tempo (beats per minute)
	 */
	private int _bpm;

	/**
	 * Default note duration
	 */
	private int _ticksPerDefaultNote;

	/**
	 * Definition of a tick
	 */
	private int _ticksPerQuarterNote;

	/**
	 * MIDI Instrument
	 */
	private int _instrument;
	
	/**
	 * Music sequence
	 */
	private Sequence _sequence;
	
	/**
	 * Create a new abstract SequencePlayer to create and play music.
	 * 
	 * @param  ticksPerQuarterNote      - tick definition
	 * @param  instrument               - MIDI instrument
	 */
	public ASequencePlayer(int ticksPerQuarterNote, int instrument) {
		init(ticksPerQuarterNote, instrument);
	}

	/**
	 * Base initialization that sets the internal ticks per quarter note
	 * and instrument.   Also zeroes out the beaths per minute,
	 * sets the internal ticks per default note to the ticks per quarter note
	 * and sets the internal MIDI track to null.
	 * @param  ticksPerQuarterNote      - tick definition
	 * @param  instrument               - MIDI instrument
	 */
	protected void initBase(int ticksPerQuarterNote, int instrument) {
		_ticksPerQuarterNote = ticksPerQuarterNote;
		_instrument = instrument;
		_bpm = 0;
		_ticksPerDefaultNote = _ticksPerQuarterNote;
		_track = null;
	}
	
	/**
	 * Initial the internal Sequence and Track.
	 * @return True if successful, false otherwise.
	 */
	protected boolean initTrack() {
		try {
			_sequence = new Sequence(Sequence.PPQ, _ticksPerQuarterNote);
			_track = _sequence.createTrack();

			ShortMessage sm = new ShortMessage();
			sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, _instrument, 0);
			_track.add(new MidiEvent(sm, 0));
			return true;
		} catch (InvalidMidiDataException e) {
			logger.log(LogLevel.CRITICAL, "Invalid MIDI data.");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Add a note to the MIDI sequence with a default velocity of 64 for middle volume
	 * 
	 * @param note     - the note to schedule in the sequence
	 * @param startTick    - the tick at which this note should start playing
	 * @return         - the tick at which this note stops playing
	 */
	@Override
	public int addNote(Note note, int startTick) {
		return addNote(note, startTick, 64);
	}

	/**
	 * Accessor to get the current ticks per default note
	 * @return the _ticksPerDefaultNote
	 */
	@Override
	public int getTicksPerDefaultNote() {
		return _ticksPerDefaultNote;
	}

	/**
	 * Accessor to set the current ticks per default note
	 * @param ticksPerDefaultNote the _ticksPerDefaultNote to set
	 */
	@Override
	public void setTicksPerDefaultNote(int ticksPerDefaultNote) {
		_ticksPerDefaultNote = ticksPerDefaultNote;
	}

	/**
	 * Accessor for the current tempo
	 * @return the tempo (in beats per minute)
	 */
	@Override
	public int getTempo() {
		return _bpm;
	}

	/**
	 * Set the tempo - can't change the tempo in the middle of a sequence,
	 * the last tempo set will be used for the whole sequence.
	 * 
	 * @param bpm - beats per minute
	 */
	@Override
	public void setTempo(int bpm) {
		_bpm = bpm;
	}

	/**
	 * Accessor for the current number of ticks per quarter note.
	 * @return the number of ticks per quarter note
	 */
	@Override
	public int getTicksPerQuarterNote() {
		return _ticksPerQuarterNote;
	}
	
	/**
	 * Internal accessor for the Sequence object
	 * @return The internal Sequence object
	 */
	protected Sequence getSequence() {
		return _sequence;
	}

	/**
	 * Add a note to the MIDI sequence.
	 * 
	 * @param note     - the note to schedule in the sequence
	 * @param startTick    - the tick at which this note should start playing
	 * @param velocity - the volume (0-127)
	 * @return         - the tick at which this note stops playing
	 */
	@Override
	public int addNote(Note note, int startTick, int velocity) {
		int start = startTick + TICK_OFFSET;
		if (_track == null) {
			return -1;
		}
	
		int duration = (int) Math.round(note.getDuration()
				* _ticksPerDefaultNote);
		if ('Z' == note.getName()) {
			// Rest - just return end tick count
			return startTick + duration;
		}
	
		int key = 60; // start at middle C
		key += note.getOctave() * 12;
		key += _offsets[note.getName() - 'A'];
		key += note.getAccidental();
	
		ShortMessage on;
		ShortMessage off;
		try {
			on = new ShortMessage();
			on.setMessage(ShortMessage.NOTE_ON, 0, key, velocity);
			off = new ShortMessage();
			off.setMessage(ShortMessage.NOTE_OFF, 0, key, velocity);
		} catch (InvalidMidiDataException e) {
			logger.log(LogLevel.CRITICAL, "Invalid MIDI Data, note not added (" + note
					+ ", " + start + ").");
			// e.printStackTrace();
			return startTick;
		}
	
		_track.add(new MidiEvent(on, start));
		_track.add(new MidiEvent(off, start + duration));
		return startTick + duration; 
	}
}
