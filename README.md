## Design Pattern Applied in ByteBeats: MVC 


In **ByteBeats**, we applied the **Model-View-Controller (MVC)** design pattern to ensure clear separation of concerns, improve maintainability, and provide a seamless user experience. Although the MVC design pattern was not explicitly discussed in the class, we discovered it through resources like **GeeksforGeeks**, which became our main reference. We also realized that MVC is not just a design pattern, but is widely considered a **software architectural design**. Its principles guided us in organizing our code, making the application easier to maintain and extend. Here's how we utilized MVC in our music player application:



<img width="591" alt="Screen Shot 2024-12-08 at 1 20 45 AM" src="https://github.com/user-attachments/assets/3265cb2a-0f7f-4c8c-961d-236edd1ea77b">


### **Model**
The **Model** represents the core data and logic of the application. In ByteBeats, the Model consists of the `Song` and `MusicPlaylist` classes, which handle song data (title, artist, duration) and playlist management. These classes manage the core functionalities like loading, saving, and organizing playlists, as well as extracting and storing metadata for each song.

- **`Song` Class**: Stores and processes metadata like song title, artist, and duration.
- **`MusicPlaylist` Class**: Manages playlist operations, such as adding/removing songs, saving/loading playlists, and maintaining playlist details.

### **View**
The **View** is responsible for presenting the data to the user and handling the graphical user interface (GUI). In ByteBeats, the `MusicPlayerGUI` class is the View. It uses **Java Swing** to create a user-friendly and interactive interface. The View displays the songs in the playlist, provides controls (like play, pause, and add song), and updates in real-time when changes occur.

- **`MusicPlayerGUI` Class**: Implements the GUI where users can interact with the music player—viewing songs, managing playlists, and controlling playback.

Additionally, the **OpenScreen.java** class plays a key role in the initial presentation of the ByteBeats program. It is responsible for creating the first screen the user sees when launching the application.

- **`OpenScreen.java`**: 
This class serves as the introduction screen, providing the user with a welcoming interface. It helps set the tone for the application and navigates to the main `MusicPlayerGUI` after a brief delay or interaction. This screen is part of the **View** layer, providing a smooth entry point for the user experience before they begin interacting with the main functionalities of ByteBeats.

<img width="392" alt="Screen Shot 2024-12-08 at 1 24 42 AM" src="https://github.com/user-attachments/assets/258692f4-127d-4f25-b6a3-c182c0b19492">

### **Controller**
The **Controller** acts as the mediator between the Model and the View. It processes user input, updates the Model accordingly, and refreshes the View to reflect changes. In ByteBeats, the Controller handles actions such as adding songs, saving playlists, and controlling playback. When a user interacts with the GUI (for example, clicking the play button), the Controller processes this input and updates the Model, which in turn updates the View.

- **Controller Logic**: Manages user interactions, including adding/removing songs from the playlist, saving/loading playlists, and controlling playback actions (play, pause, stop).

### **Why We Chose MVC?**
By using the MVC design pattern in ByteBeats, we were able to:
- **Separate Concerns**: The Model, View, and Controller handle specific aspects of the application, making the code easier to maintain and understand.
- **Enhance Maintainability**: Independent components allow us to make updates or changes to one part of the program without affecting others.
- **Provide a Better User Experience**: Clear separation between logic and interface ensures that user interactions are smooth and intuitive.

Using MVC has made ByteBeats easier to manage and more user-friendly, while also following good software design principles.

### **LABAN GROUP 1!**

ser tres lng ser :>
