import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {

    //colors of the frame and user interface
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    // allow us to use file explorer in our app
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;
    private JLabel timerLabel;

    public MusicPlayerGUI(){
        super("Byte Beats");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/assets"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        // Initialize and position the timer label properly
        timerLabel = new JLabel(" ");
        timerLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        timerLabel.setForeground(TEXT_COLOR);
        timerLabel.setBounds(getWidth()/2 - 100/2, 340, 100, 20);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timerLabel);

        addGuiComponents();
    }


    private void addGuiComponents(){
        // add toolbar
        addToolbar();

        // load record image
        JLabel songImage = new JLabel(loadImage("src/assets/bytebeats.png"));
        int imageSize = 250; // Original size of the image
        int xPosition = (getWidth() - imageSize) / 2; // Center the image horizontally
        songImage.setBounds(xPosition, 30, imageSize, imageSize); // Position the image with spacing above
        add(songImage);


        musicPlayer.addSongTimeListener(currentTime -> {
            updateTimerLabel(currentTime);
            updatePlaybackSliderPosition(currentTime);
        });

        // song title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        // song artist
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 20));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        // playback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // when the user is holding the tick we want to the pause the song
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // when the user drops the tick
                JSlider source = (JSlider) e.getSource();

                // get the frame value from where the user wants to playback to
                int frame = source.getValue();

                // update the current frame in the music player to this frame
                musicPlayer.setCurrentFrame(frame);

                // update current time in milli as well
                musicPlayer.setCurrentTimeInMilli((int) (frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));

                // resume the song
                musicPlayer.playCurrentSong();

                // toggle on pause button and toggle off play button
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        // playback buttons (i.e. previous, play, next)
        addPlaybackBtns();


    }

    private void updateTimerLabel(int currentTimeInMilli) {
        if (musicPlayer.getCurrentSong() != null) {
            long totalDurationInMilli = musicPlayer.getCurrentSong().getDurationInMilli();
            int currentMinutes = currentTimeInMilli / 60000;
            int currentSeconds = (currentTimeInMilli % 60000) / 1000;
            int totalMinutes = (int)(totalDurationInMilli / 60000);
            int totalSeconds = (int)((totalDurationInMilli % 60000) / 1000);

            String timeString = String.format("%02d:%02d / %02d:%02d",
                    currentMinutes, currentSeconds, totalMinutes, totalSeconds);

            SwingUtilities.invokeLater(() -> timerLabel.setText(timeString));
        }
    }

    private void updatePlaybackSliderPosition(int currentTimeInMilli) {
        if (musicPlayer.getCurrentSong() != null) {
            int frame = (int)(currentTimeInMilli * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds());
            SwingUtilities.invokeLater(() -> setPlaybackSliderValue(frame));
        }
    }

    private void addToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);

        // prevent toolbar from being moved
        toolBar.setFloatable(false);

        // add drop down menu
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // now we will add a song menu where we will place the loading song option
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        // add the "load song" item in the songMenu
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(e -> {
            // Show the file chooser to select an MP3 file
            int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();

            // If the user presses the "open" button and selects a valid file
            if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                // Create a Song object based on the selected file path
                Song song = new Song(selectedFile.getPath());

//
                //System.out.println();
                System.out.println("Song Duration: " + song.getSongLength());


                // Load the song into the music player
                musicPlayer.loadSong(song);

                // Update the song title and artist in the GUI
                updateSongTitleAndArtist(song);

                // Update the playback slider (this assumes it's implemented)
                updatePlaybackSlider(song);

                // Toggle on pause button and off play button
                enablePauseButtonDisablePlayButton();
            }
        });
        songMenu.add(loadSong);


        // now we will add the playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        // then add the items to the playlist menu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(e -> {
            // load music playlist dialog
            new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true);
        });
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
            jFileChooser.setCurrentDirectory(new File("src/assets"));

            int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
            File selectedFile = jFileChooser.getSelectedFile();

            if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                // stop the music
                musicPlayer.stopSong();

                // load playlist
                musicPlayer.loadPlaylist(selectedFile);
            }
        });
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }

    private void addPlaybackBtns(){
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 430, getWidth() - 10, 90);
        playbackBtns.setBackground(null);

        // previous button
        JButton prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(e -> {
            // go to the previous song
            musicPlayer.prevSong();
        });
        playbackBtns.add(prevButton);

        // play button
        JButton playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(e -> {
            // toggle off play button and toggle on pause button
            enablePauseButtonDisablePlayButton();

            // play or resume song
            musicPlayer.playCurrentSong();
        });
        playbackBtns.add(playButton);

        // pause button
        JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle off pause button and toggle on play button
                enablePlayButtonDisablePauseButton();

                // pause the song
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);

        // next button
        JButton nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(e -> {
            // go to the next song
            musicPlayer.nextSong();
        });
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    // this will be used to update our slider from the music player class
    public void setPlaybackSliderValue(int frame){
        playbackSlider.setValue(frame);
    }

    public void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    public void updatePlaybackSlider(Song song){
        // update max count for slider
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        // create the song length label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

//         beginning will be 00:00
        JLabel labelBeginning = new JLabel();
//        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
//        labelBeginning.setForeground(TEXT_COLOR);

        // end will vary depending on the song
        JLabel labelEnd =  new JLabel(song.getSongLength());
//        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
//        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    public void enablePauseButtonDisablePlayButton(){
        // retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);

        // turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton(){
        // retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);

        // turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath){
        try{
            // read the image file from the given path
            BufferedImage image = ImageIO.read(new File(imagePath));

            // returns an image icon so that our component can render the image
            return new ImageIcon(image);
        }catch(Exception e){
            e.printStackTrace();
        }

        // could not find resource
        return null;
    }

    public void setTimerLabel(String time) {
        if (timerLabel != null) {
            SwingUtilities.invokeLater(() -> timerLabel.setText(time));
        }
    }

}
