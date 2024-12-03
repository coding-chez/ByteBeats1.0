import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

// class used to describe a song
public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filePath){
        this.filePath = filePath;
        try {
            mp3File = new Mp3File(filePath);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            songLength = convertToSongLengthFormat();  // Will correctly set the song length

            // use the jaudiotagger library to create an audiofile obj to read mp3 file's information
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            // read through the meta data of the audio file
            Tag tag =  audioFile.getTag();
            if(tag != null) {
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);
            }
            if (songTitle == null || songTitle.isEmpty()) {
                songTitle = new File(filePath).getName().replaceFirst("[.][^.]+$", ""); // Use filename as title
            }
            if (songArtist == null || songArtist.isEmpty()) {
                songArtist = "Unknown Artist";
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Set reasonable default values when an exception occurs
            songTitle = new File(filePath).getName().replaceFirst("[.][^.]+$", ""); // Use filename as title
            songArtist = "Unknown Artist";

            // Set a default song length as '00:00' in case of failure to read duration
            songLength = "00:00";
            frameRatePerMilliseconds = 0;
        }
    }


    private String convertToSongLengthFormat(){
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        return formattedTime;
    }

    // getters
    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongLength() {
        return songLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public Mp3File getMp3File(){return mp3File;}
    public double getFrameRatePerMilliseconds(){return frameRatePerMilliseconds;}

    public long getDurationInMilli() {
        if (mp3File != null) {
            return mp3File.getLengthInMilliseconds();
        }
        return 0; // Return 0 if the mp3File is null or there's an issue
    }

}
















