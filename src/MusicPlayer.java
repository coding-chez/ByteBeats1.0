import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    private Song currentSong;
    private ArrayList<Song> playlist;
    private int currentPlaylistIndex;
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused;
    private boolean songFinished;
    private boolean pressedNext, pressedPrev;
    private int currentFrame;
    private int currentTimeInMilli;
    private long pausedTime;
    private List<SongTimeListener> songTimeListeners = new ArrayList<>();

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    public void loadSong(Song song) {
        currentSong = song;
        playlist = null;

        if (!songFinished) {
            stopSong();
        }

        if (currentSong != null) {
            currentFrame = 0;
            currentTimeInMilli = 0;
            songFinished = false;
            pausedTime = 0;

            long songDurationInMilli = currentSong.getDurationInMilli();
            String formattedDuration = formatTime(songDurationInMilli);

            SwingUtilities.invokeLater(() -> {
                musicPlayerGUI.setPlaybackSliderValue(0);
                musicPlayerGUI.setTimerLabel("00:00 / " + formattedDuration);
            });

            playCurrentSong();
        }
    }

    private String formatTime(long timeInMilli) {
        long totalSeconds = timeInMilli / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(playlistFile))) {
            String songPath;
            while ((songPath = bufferedReader.readLine()) != null) {
                Song song = new Song(songPath);
                playlist.add(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (playlist.size() > 0) {
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilli = 0;
            currentSong = playlist.get(0);
            currentFrame = 0;
            pausedTime = 0;

            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

            playCurrentSong();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            pausedTime = currentTimeInMilli;
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void nextSong() {
        if (playlist == null || currentPlaylistIndex + 1 > playlist.size() - 1) return;

        pressedNext = true;
        if (!songFinished) stopSong();

        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilli = 0;
        pausedTime = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    public void prevSong() {
        if (playlist == null || currentPlaylistIndex - 1 < 0) return;

        pressedPrev = true;
        if (!songFinished) stopSong();

        currentPlaylistIndex--;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMilli = 0;
        pausedTime = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    public void playCurrentSong() {
        if (currentSong == null) return;

        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();
            startPlaybackSliderThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMusicThread() {
        new Thread(() -> {
            try {
                if (isPaused) {
                    synchronized (playSignal) {
                        isPaused = false;
                        playSignal.notify();
                    }
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    advancedPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void startPlaybackSliderThread() {
        Thread sliderThread = new Thread(() -> {
            if (isPaused) {
                try {
                    synchronized (playSignal) {
                        playSignal.wait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            long songLengthInMilli = currentSong.getMp3File().getLengthInMilliseconds();
            long startTime = System.currentTimeMillis() - pausedTime;

            while (!isPaused && !songFinished && !pressedNext && !pressedPrev) {
                try {
                    long currentTime = System.currentTimeMillis();
                    currentTimeInMilli = (int)(currentTime - startTime);

                    if (currentTimeInMilli >= songLengthInMilli) {
                        break;
                    }

                    int calculatedFrame = (int)((double) currentTimeInMilli * currentSong.getFrameRatePerMilliseconds());

                    SwingUtilities.invokeLater(() -> {
                        musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                        String currentTimeStr = formatTime(currentTimeInMilli);
                        String totalTimeStr = formatTime(songLengthInMilli);
                        musicPlayerGUI.setTimerLabel(currentTimeStr + " / " + totalTimeStr);
                        notifySongTimeListeners(currentTimeInMilli);
                    });

                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!isPaused && !pressedNext && !pressedPrev) {
                SwingUtilities.invokeLater(() -> {
                    String totalTimeStr = formatTime(songLengthInMilli);
                    musicPlayerGUI.setTimerLabel(totalTimeStr + " / " + totalTimeStr);
                    musicPlayerGUI.setPlaybackSliderValue((int)(songLengthInMilli * currentSong.getFrameRatePerMilliseconds()));
                });
            }
        });

        sliderThread.setDaemon(true);
        sliderThread.start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        if (isPaused) {
            currentFrame += (int)((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        } else {
            if (pressedNext || pressedPrev) return;

            songFinished = true;

            if (playlist == null) {
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            } else if (currentPlaylistIndex == playlist.size() - 1) {
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            } else {
                nextSong();
            }
        }
    }

    public interface SongTimeListener {
        void onSongTimeUpdate(int currentTimeInMilli);
    }

    public void addSongTimeListener(SongTimeListener listener) {
        songTimeListeners.add(listener);
    }

    private void notifySongTimeListeners(int currentTimeInMilli) {
        for (SongTimeListener listener : songTimeListeners) {
            listener.onSongTimeUpdate(currentTimeInMilli);
        }
    }
}