package com.eemf.sirgoingfar.bakingapp.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.activities.FragmentHostActivity;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.NOTIFICATION_SERVICE;

public class StepFragment extends BaseFragment {

    private static final String TAG = StepFragment.class.getSimpleName();
    private static final String CHANNEL_ID = "notif_channel_id";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String STEP_NUMBER = "step_number";
    private static final String MEAL_INDEX = "meal_index";

    @BindView(R.id.exo_player)
    PlayerView mPlayerView;

    @BindView(R.id.tv_video_empty_state)
    TextView videoEmptyState;

    @Nullable
    @BindView(R.id.tv_header_text)
    TextView headerText;

    @Nullable
    @BindView(R.id.step_description_image)
    ImageView stepDescImage;

    @Nullable
    @BindView(R.id.tv_image_empty_state)
    TextView imageEmptyState;

    @Nullable
    @BindView(R.id.tv_step_instruction)
    TextView stepInstruction;

    private int mealIndex;
    private int stepNumber;
    private RecipeData.Step currentStep;
    private SimpleExoPlayer mExoPlayer;

    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;
    private NotificationManager mNotifManager;
//    private ComponentListener listener;
    private int currentWindow;
    private long playbackPosition;
    private boolean playWhenReady = true;


    public static StepFragment newInstance(int mealIndex, int stepNumber) {
        Bundle args = new Bundle();
        StepFragment fragment = new StepFragment();
        fragment.mealIndex = mealIndex;
        fragment.stepNumber = stepNumber;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MEAL_INDEX))
                mealIndex = savedInstanceState.getInt(MEAL_INDEX);

            if (savedInstanceState.containsKey(STEP_NUMBER))
                stepNumber = savedInstanceState.getInt(STEP_NUMBER);
        }

        currentStep = DataUtil.getStepAt(fragmentActivity, mealIndex, (stepNumber - 1));
        setupView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Util.SDK_INT > 23 && isVideoAvailable())
            initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mExoPlayer == null) && isVideoAvailable())
            initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23)
            releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23)
            releasePlayer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MEAL_INDEX, mealIndex);
        outState.putInt(STEP_NUMBER, stepNumber);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
            playWhenReady = mExoPlayer.getPlayWhenReady();
//            mExoPlayer.removeListener(listener);
            mExoPlayer.setVideoListener(null);
            mExoPlayer.setVideoDebugListener(null);
            mExoPlayer.setAudioDebugListener(null);
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void setupView() {

        //initialize Components
//        listener = new ComponentListener();
        mNotifManager = (NotificationManager) fragmentActivity.getSystemService(NOTIFICATION_SERVICE);

        //set header text
        if (headerText != null)
            headerText.setText(fragmentActivity.getString(R.string.step, String.valueOf(stepNumber)));

        //setup video player
        if (mPlayerView != null && videoEmptyState != null) {
            if (TextUtils.isEmpty(currentStep.getVideoURL())) {
                mPlayerView.setVisibility(View.INVISIBLE);
                videoEmptyState.setVisibility(View.VISIBLE);
            } else {
                initializeMediaSession();
                initializePlayer();
            }
        }

        //set step image
        if (stepDescImage != null && imageEmptyState != null) {
            if (TextUtils.isEmpty(currentStep.getThumbnailUrl())) {
                stepDescImage.setVisibility(View.INVISIBLE);
                imageEmptyState.setVisibility(View.VISIBLE);
            } else {
                Glide.with(fragmentActivity).load(currentStep.getThumbnailUrl()).into(stepDescImage);
            }
        }

        //set step instruction
        if (stepInstruction != null && !TextUtils.isEmpty(currentStep.getDescription()))
            stepInstruction.setText(currentStep.getDescription());
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(fragmentActivity, TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);

        mPlaybackStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                );
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
        mMediaSession.setCallback(new QuizMediaSessionCallback());
        mMediaSession.setActive(true);
    }

    private void initializePlayer() {

        if (mExoPlayer == null) {
            TrackSelection.Factory adaptiveSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(fragmentActivity),
                    new DefaultTrackSelector(adaptiveSelectionFactory),
                    new DefaultLoadControl()
            );

            mPlayerView.setPlayer(mExoPlayer);
            /*mExoPlayer.addListener(listener);
            mExoPlayer.setVideoDebugListener(listener);
            mExoPlayer.setAudioDebugListener(listener);*/
            mExoPlayer.setPlayWhenReady(playWhenReady);
            mExoPlayer.seekTo(currentWindow, playbackPosition);
        }

        MediaSource mediaSource = buildMediaSource(Uri.parse(currentStep.getVideoURL()));
        mExoPlayer.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(@NonNull Uri uri) {

        String userAgent = Util.getUserAgent(fragmentActivity, "BakingApp");

        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(fragmentActivity, userAgent);

        return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private boolean isVideoAvailable() {
        return !TextUtils.isEmpty(currentStep.getVideoURL());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotifManager.cancelAll();
        mMediaSession.setActive(false);
    }

    private void showNotification(PlaybackStateCompat state) {

        String play_pause;
        int icon;

        if (state.getState() == PlaybackState.STATE_PAUSED) {
            play_pause = getString(R.string.play);
            icon = R.drawable.exo_controls_play;
        } else {
            play_pause = getString(R.string.pause);
            icon = R.drawable.exo_controls_pause;
        }

        NotificationCompat.Action playPauseAction = new android.support.v4.app.NotificationCompat.Action(icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(fragmentActivity, PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent(fragmentActivity, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentIntent = PendingIntent.getActivity(fragmentActivity, 0,
                new Intent(fragmentActivity, FragmentHostActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(fragmentActivity, CHANNEL_ID)
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_movie_black)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setContentIntent(contentIntent)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1)
                        .setMediaSession(mMediaSession.getSessionToken()));

        mNotifManager.notify(0, notifBuilder.build());
    }

    public static class StepMediaButtonReceiver extends MediaButtonReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    private class QuizMediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

/*    private class ComponentListener implements ExoPlayer.EventListener,
            VideoRendererEventListener, AudioRendererEventListener {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == ExoPlayer.STATE_READY) {
                if (playWhenReady) {
                    mPlaybackStateBuilder.setState(
                            PlaybackStateCompat.STATE_PLAYING,
                            mExoPlayer.getCurrentPosition(),
                            1f);
                } else {
                    mPlaybackStateBuilder.setState(
                            PlaybackStateCompat.STATE_PAUSED,
                            mExoPlayer.getCurrentPosition(),
                            1f);
                }

                mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
                showNotification(mPlaybackStateBuilder.build());
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }
    }*/
}
