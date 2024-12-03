import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer extends PlaybackListener {
    // this will be used to update isPaused more synchronously
    private static final Object playSignal = new Object();

    // need reference so that we can update the gui in this class
    private MusicPlayerGUI musicPlayerGUI;

    // we will need a way to store our song's details, so we will be creating a song class
    private Song currentSong;
    public Song getCurrentSong(){
        return currentSong;
    }

    private ArrayList<Song> playlist;

    // we will need to keep track the index we are in the playlist
    private int currentPlaylistIndex;

    // use JLayer library to create an AdvancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    // pause boolean flag used to indicate whether the player has been paused
    private boolean isPaused;

    // boolean flag used to tell when the song has finished
    private boolean songFinished;

    private boolean pressedNext, pressedPrev;

    // stores in teh last frame when the playback is finished (used for pausing and resuming)
    private int currentFrame;
    public void setCurrentFrame(int frame){
        currentFrame = frame;
    }

    // track how many milliseconds has passed since playing the song (used for updating the slider)
    private int currentTimeInMilli;
    public void setCurrentTimeInMilli(int timeInMilli){
        currentTimeInMilli = timeInMilli;
    }
    private List<SongTimeListener> songTimeListeners = new ArrayList<>();


    // constructor
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI){
        this.musicPlayerGUI = musicPlayerGUI;
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

            // Initialize the timer display with the song's duration
            long songDurationInMilli = currentSong.getDurationInMilli();
            String formattedDuration = formatTime(songDurationInMilli);

            // Update GUI on the Event Dispatch Thread
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



    public void loadPlaylist(File playlistFile){
        playlist = new ArrayList<>();

        // store the paths from the text file into the playlist array list
        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // reach each line from the text file and store the text into the songPath variable
            String songPath;
            while((songPath = bufferedReader.readLine()) != null){
                // create song object based on song path
                Song song = new Song(songPath);

                // add to playlist array list
                playlist.add(song);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(playlist.size() > 0){
            // reset playback slider
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilli = 0;

            // update current song to the first song in the playlist
            currentSong = playlist.get(0);

            // start from the beginning frame
            currentFrame = 0;

            // update gui
            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

            // start song
            playCurrentSong();
        }
    }

    public void pauseSong(){
        if(advancedPlayer != null){
            // update isPaused flag
            isPaused = true;

            // then we want to stop the player
            stopSong();
        }
    }

    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void nextSong(){
        // no need to go to the next song if there is no playlist
        if(playlist == null) return;

        // check to see if we have reached the end of the playlist, if so then don't do anything
        if(currentPlaylistIndex + 1 > playlist.size() - 1) return;

        pressedNext = true;

        // stop the song if possible
        if(!songFinished)
            stopSong();

        // increase current playlist index
        currentPlaylistIndex++;

        // update current song
        currentSong = playlist.get(currentPlaylistIndex);

        // reset frame
        currentFrame = 0;

        // reset current time in milli
        currentTimeInMilli = 0;

        // update gui
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        // play the song
        playCurrentSong();
    }

    public void prevSong(){
        // no need to go to the next song if there is no playlist
        if(playlist == null) return;

        // check to see if we can go to the previous song
        if(currentPlaylistIndex - 1 < 0) return;

        pressedPrev = true;

        // stop the song if possible
        if(!songFinished)
            stopSong();

        // decrease current playlist index
        currentPlaylistIndex--;

        // update current song
        currentSong = playlist.get(currentPlaylistIndex);

        // reset frame
        currentFrame = 0;

        // reset current time in milli
        currentTimeInMilli = 0;

        // update gui
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        // play the song
        playCurrentSong();
    }

    public void playCurrentSong(){
        if(currentSong == null) return;

        try{
            // read mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // create a new advanced player
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            // start music
            startMusicThread();

            // start playback slider thread
            startPlaybackSliderThread();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // create a thread that will handle playing the music
    private void startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(isPaused){
                        synchronized(playSignal){
                            // update flag
                            isPaused = false;

                            // notify the other thread to continue (makes sure that isPaused is updated to false properly)
                            playSignal.notify();
                        }

                        // resume music from last frame
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }else{
                        // play music from the beginning
                        advancedPlayer.play();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // create a thread that will handle updating the slider
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
            long startTime = System.currentTimeMillis();

            while (!isPaused && !songFinished && !pressedNext && !pressedPrev) {
                try {
                    // Calculate elapsed time based on actual system time
                    long currentTime = System.currentTimeMillis();
                    currentTimeInMilli = (int)(currentTime - startTime);

                    if (currentTimeInMilli >= songLengthInMilli) {
                        break;
                    }

                    // Calculate the current frame position
                    int calculatedFrame = (int)((double) currentTimeInMilli * currentSong.getFrameRatePerMilliseconds());

                    // Update GUI on EDT
                    SwingUtilities.invokeLater(() -> {
                        // Update slider
                        musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);

                        // Update timer display
                        String currentTimeStr = formatTime(currentTimeInMilli);
                        String totalTimeStr = formatTime(songLengthInMilli);
                        musicPlayerGUI.setTimerLabel(currentTimeStr + " / " + totalTimeStr);

                        // Notify listeners
                        notifySongTimeListeners(currentTimeInMilli);
                    });

                    // Use a smaller sleep interval for smoother updates
                    Thread.sleep(50); // Update every 50ms

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Final update when song finishes
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
        // this method gets called in the beginning of the song
        System.out.println("Playback Started");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        // this method gets called when the song finishes or if the player gets closed
        System.out.println("Playback Finished");
        if(isPaused){
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }else{
            // if the user pressed next or prev we don't need to execute the rest of the code
            if(pressedNext || pressedPrev) return;

            // when the song ends
            songFinished = true;

            if(playlist == null){
                // update gui
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            }else{
                // last song in the playlist
                if(currentPlaylistIndex == playlist.size() - 1){
                    // update gui
                    musicPlayerGUI.enablePlayButtonDisablePauseButton();
                }else{
                    // go to the next song in the playlist
                    nextSong();
                }
            }
        }
        musicPlayerGUI.setPlaybackSliderValue(currentTimeInMilli);
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

    // Call this method periodically (e.g., in a timer or during song playback loop)
    public void updateSongTime(int currentTimeInMilli) {
        notifySongTimeListeners(currentTimeInMilli);
    }


}



















