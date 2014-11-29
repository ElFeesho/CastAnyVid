package fd.com.castanyvid;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by chris on 28/11/14.
 */
public class SeekbarPlaybackControlView implements CastPlaybackControlPresenter.CastPlaybackControlView {
    private final SeekBar seekBar;
    private final TextView currentTime;
    private final TextView duration;
    private final View play;
    private final View pause;
    private final ProgressBar bufferIndicator;

    private Listener listener;

    public SeekbarPlaybackControlView(SeekBar seekBar, TextView currentTime, TextView duration, View play, View pause, ProgressBar bufferIndicator) {
        this.seekBar = seekBar;
        this.currentTime = currentTime;
        this.duration = duration;
        this.play = play;
        this.pause = pause;
        this.bufferIndicator = bufferIndicator;

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.play();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.pause();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setCurrentPlaybackPosition(new Timestamp(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                listener.startedScrubbing();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.scrubTo(new Timestamp(seekBar.getProgress()));
            }
        });
    }

    @Override
    public void allowUse() {
        seekBar.setEnabled(true);
        playing();
    }

    @Override
    public void buffering() {
        bufferIndicator.setVisibility(View.VISIBLE);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
    }

    @Override
    public void disallowUse() {
        seekBar.setEnabled(false);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
        bufferIndicator.setVisibility(View.GONE);
    }

    @Override
    public void playing() {
        bufferIndicator.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
    }

    @Override
    public void paused() {
        bufferIndicator.setVisibility(View.GONE);
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);
    }

    @Override
    public void reset() {
        seekBar.setProgress(0);
        currentTime.setText("0:00");
        duration.setText("0:00");
        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);
        bufferIndicator.setVisibility(View.GONE);
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration.setText(duration.toString());
        seekBar.setMax((int) duration.milliseconds);
    }

    @Override
    public void setCurrentPlaybackPosition(Timestamp timestamp) {
        currentTime.setText(timestamp.toString());
        seekBar.setProgress((int) timestamp.milliseconds);
    }
}
