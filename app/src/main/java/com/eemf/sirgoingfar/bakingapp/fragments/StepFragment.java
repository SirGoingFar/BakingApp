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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.activities.MealListActivity;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.eemf.sirgoingfar.bakingapp.utils.ArchitectureUtil;
import com.eemf.sirgoingfar.bakingapp.utils.Constants;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.eemf.sirgoingfar.bakingapp.utils.Constants.MEAL_INDEX;
import static com.eemf.sirgoingfar.bakingapp.utils.Constants.STEP_NUMBER;

public class StepFragment extends BaseFragment {

    private static final String TAG = StepFragment.class.getSimpleName();
    private static final String CHANNEL_ID = "notif_channel_id";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

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

    @BindView(R.id.rl_exo_player_layout)
    RelativeLayout playerLayout;

    @BindView(R.id.pb_buffer)
    ProgressBar bufferLoader;

    private int mealIndex;
    private int stepNumber;
    private RecipeData.Step currentStep;
    private SimpleExoPlayer mExoPlayer;

    private static MediaSessionCompat mMediaSession;
    private NotificationManager mNotifManager;
    private ComponentListener listener;
    PlaybackStateCompat.Builder mPlaybackStateBuilder;
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

            if (savedInstanceState.containsKey(Constants.KEY_PLAYBACK_POSITION))
                playbackPosition = savedInstanceState.getLong(Constants.KEY_PLAYBACK_POSITION);
            else
                playbackPosition = 0L;
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
        if ((Util.SDK_INT <= 23) && isVideoAvailable())
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
        outState.putInt(Constants.MEAL_INDEX, mealIndex);
        outState.putInt(Constants.STEP_NUMBER, stepNumber);
        outState.putLong(Constants.KEY_PLAYBACK_POSITION, playbackPosition);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.removeListener(listener);
            mExoPlayer.release();
            mExoPlayer = null;

            mNotifManager.cancelAll();
            mMediaSession.setActive(false);
        }
    }

    private void setupView() {

        //initialize Components
        listener = new ComponentListener();
        mNotifManager = (NotificationManager) fragmentActivity.getSystemService(NOTIFICATION_SERVICE);

        //set header text
        if (headerText != null)
            headerText.setText(fragmentActivity.getString(R.string.step, String.valueOf(stepNumber)));

        //setup video player
        if (mPlayerView != null && videoEmptyState != null) {
            if (TextUtils.isEmpty(currentStep.getVideoURL())) {
                playerLayout.setVisibility(View.INVISIBLE);
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

        //show full screen for Phone
        if (ArchitectureUtil.isPhoneRotated(fragmentActivity) && !ArchitectureUtil.isTablet(fragmentActivity))
            showFullScreen();
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
                                PlaybackStateCompat.ACTION_FAST_FORWARD |
                                PlaybackStateCompat.ACTION_REWIND
                );
        mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
        mMediaSession.setCallback(new StepMediaSessionCallback());
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
        }

        MediaSource mediaSource = buildMediaSource(Uri.parse(currentStep.getVideoURL()));
        mExoPlayer.prepare(mediaSource, true, false);

        mPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.seekTo(currentWindow, playbackPosition);
        mExoPlayer.addListener(listener);
        mExoPlayer.setPlayWhenReady(playWhenReady);
    }

    private MediaSource buildMediaSource(@NonNull Uri uri) {

        String userAgent = Util.getUserAgent(fragmentActivity, "BakingApp");

        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(fragmentActivity, userAgent);

        return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private boolean isVideoAvailable() {
        return !TextUtils.isEmpty(currentStep.getVideoURL());
    }

    private void showFullScreen() {
        fragmentActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
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

        NotificationCompat.Action rewindAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.exo_icon_rewind, getString(R.string.rewind),
                MediaButtonReceiver.buildMediaButtonPendingIntent(fragmentActivity, PlaybackStateCompat.ACTION_REWIND));

        NotificationCompat.Action fastforwardAction = new android.support.v4.app.NotificationCompat.Action(R.drawable.exo_icon_fastforward, getString(R.string.fastforward),
                MediaButtonReceiver.buildMediaButtonPendingIntent(fragmentActivity, PlaybackStateCompat.ACTION_FAST_FORWARD));

        PendingIntent contentIntent = PendingIntent.getActivity(fragmentActivity, 0,
                new Intent(fragmentActivity, MealListActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(fragmentActivity, CHANNEL_ID)
                .setContentText(currentStep.getShortDescription())
                .setSmallIcon(R.drawable.ic_movie_black)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(rewindAction)
                .addAction(playPauseAction)
                .addAction(fastforwardAction)
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

    private class StepMediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onRewind() {
            mExoPlayer.seekTo(mExoPlayer.getContentPosition() - 1);
        }

        @Override
        public void onFastForward() {
            mExoPlayer.seekTo(mExoPlayer.getContentPosition() + 1);
        }
    }

    private class ComponentListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY) {
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

                bufferLoader.setVisibility(View.INVISIBLE);
                mMediaSession.setPlaybackState(mPlaybackStateBuilder.build());
                showNotification(mPlaybackStateBuilder.build());
            } else if (playbackState == Player.STATE_BUFFERING) {
                if (playWhenReady)
                    bufferLoader.setVisibility(View.VISIBLE);
                else
                    bufferLoader.setVisibility(View.INVISIBLE);
            } else {
                bufferLoader.setVisibility(View.INVISIBLE);
                mNotifManager.cancelAll();
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
