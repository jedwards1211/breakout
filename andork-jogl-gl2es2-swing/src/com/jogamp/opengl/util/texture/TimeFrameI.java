package com.jogamp.opengl.util.texture;

/**
 * Integer time frame in milliseconds, maybe specialized for texture/video, audio, .. animated content.
 * <p>
 * Type and value range has been chosen to suit embedded CPUs
 * and characteristics of audio / video streaming and animations.
 * Milliseconds of type integer with a maximum value of {@link Integer#MAX_VALUE}
 * will allow tracking time up 2,147,483.647 seconds or
 * 24 days 20 hours 31 minutes and 23 seconds.
 * </p>
 * <p>
 * Milliseconds granularity is also more than enough to deal with A-V synchronization,
 * where the threshold usually lies within 22ms.
 * </p>
 * <p>
 * Milliseconds granularity for displaying video frames might seem inaccurate
 * for each single frame, i.e. 60Hz != 16ms, however, accumulated values diminish
 * this error and vertical sync is achieved by build-in V-Sync of the video drivers.
 * </p>
 */
public class TimeFrameI {
    /** Constant marking an invalid PTS, i.e. Integer.MIN_VALUE == 0x80000000 == {@value}. Sync w/ native code. */
    public static final int INVALID_PTS = 0x80000000;

    /** Constant marking the end of the stream PTS, i.e. Integer.MIN_VALUE - 1 == 0x7FFFFFFF == {@value}. Sync w/ native code. */
    public static final int END_OF_STREAM_PTS = 0x7FFFFFFF;

    protected int pts;
    protected int duration;

    public TimeFrameI() {
        pts = INVALID_PTS;
        duration = 0;
    }
    public TimeFrameI(final int pts, final int duration) {
        this.pts = pts;
        this.duration = duration;
    }

    /** Get this frame's presentation timestamp (PTS) in milliseconds. */
    public final int getPTS() { return pts; }
    /** Set this frame's presentation timestamp (PTS) in milliseconds. */
    public final void setPTS(final int pts) { this.pts = pts; }
    /** Get this frame's duration in milliseconds. */
    public final int getDuration() { return duration; }
    /** Set this frame's duration in milliseconds. */
    public final void setDuration(final int duration) { this.duration = duration; }

    @Override
    public String toString() {
        return "TimeFrame[pts " + pts + " ms, l " + duration + " ms]";
    }
}
