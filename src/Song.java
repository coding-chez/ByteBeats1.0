import com.mpatric.mp3agic.Mp3File;           // Reads and analyzes MP3 files to get details like frame count, duration, etc.
import org.jaudiotagger.tag.FieldKey;        //Helps specify the type of metadata to retrieve (e.g., title, artist).
import org.jaudiotagger.audio.AudioFile;     //used to read and access metadata like song title and artist from audio files.
import org.jaudiotagger.audio.AudioFileIO;
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
        this.filePath = filePath; //Loads the MP3 file from the given path and analyzes it.
        try {
            mp3File = new Mp3File(filePath);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            songLength = convertToSongLengthFormat();  // Will correctly set the song length

            // Use the jaudiotagger library to create an AudioFile object to read mp3 file's information
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            // Read through the metadata of the audio file
                Tag tag = audioFile.getTag();
                if (tag != null) {
                    songTitle = tag.getFirst(FieldKey.TITLE);
                    songArtist = tag.getFirst(FieldKey.ARTIST);
                }

                if (songTitle == null || songTitle.isEmpty()) {
                    String fileName = new File(filePath).getName().replaceFirst("[.][^.]+$", ""); // Remove file extension
                    if (fileName.contains(" - ")) {
                        String[] parts = fileName.split(" - ", 2); // Split at the first occurrence of " - "
                        songTitle = parts[0].trim();  // Title is the part before " - "
                        songArtist = parts[1].trim(); // Artist is the part after " - "
                    } else {
                        songTitle = fileName;        // Use entire file name as title if no " - " is found
                        songArtist = "Unknown Artist"; // Default artist value
                    }
                } else if (songArtist != null && !songArtist.isEmpty()) {
                    // Ensure consistent format if tags are available
                    songTitle = songTitle.trim();
                    songArtist = songArtist.trim();
                }

                // Print to terminal in the format: Song - Artist
                System.out.println("Loaded song: " + songTitle + " - " + songArtist);

            } catch (Exception e) {
                e.printStackTrace();
                // Set reasonable default values when an exception occurs
                String fileName = new File(filePath).getName().replaceFirst("[.][^.]+$", ""); // Remove file extension
                if (fileName.contains(" - ")) {
                    String[] parts = fileName.split(" - ", 2); // Split at the first occurrence of " - "
                    songTitle = parts[0].trim();  // Title is the part before " - "
                    songArtist = parts[1].trim(); // Artist is the part after " - "
                } else {
                    songTitle = fileName;         // Use entire file name as title if no " - " is found
                    songArtist = "Unknown Artist"; // Default artist value
                }

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
