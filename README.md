<h1 align="center">
🎧⋆⸜ ｡°✩   ByteBeats Music Player   ✩°｡⋆⸜ 🎧 </h1>

<h3 align="center">
Where every byte is a beat...
</h3>
<div align="center">
  
![logo](https://github.com/user-attachments/assets/695cd4d1-6048-4f7f-b241-9e27c09245ed)
</div>

<p align="center">
Welcome to <b>ByteBeats</b>, a simple music player application. This project demonstrates our application of key software design principles and patterns for a well-organized and maintainable codebase.
</p>

<h2 align = "center">
  Design Patterns Applied in ByteBeats: MVC and Factory Method
</h2>

In **ByteBeats**, we primarily applied the **Model-View-Controller (MVC)** design pattern to ensure clear separation of concerns, improve maintainability, and provide a seamless user experience. Although the MVC design pattern was not explicitly discussed in class, we discovered it through resources like **GeeksforGeeks**, which became our main reference. We also realized that MVC is not just a design pattern, but is widely considered a **software architectural design**. Its principles guided us in organizing our code, making the application easier to maintain and extend.

Additionally, to complement the MVC architecture, we utilized the **Factory Method** design pattern, a **creational design pattern**, in parts of the Model layer. The Factory Method pattern is used to encapsulate the creation of objects, ensuring flexibility and scalability when adding new types of songs or playlists in the future. This approach reduces coupling and enhances the extensibility of the codebase. 

### How Factory Method Is Used
The Factory Method is primarily employed in the creation of `Song` and `MusicPlaylist` objects:
- **Song Factory**: A factory method is implemented to create different types of `Song` objects, depending on the metadata provided. For instance, the method can handle scenarios like creating songs with custom properties or default values if certain metadata is missing.
- **Playlist Factory**: Similarly, a factory method generates `MusicPlaylist` objects, streamlining the creation process for new playlists while handling variations in initial setup or configurations.

By combining MVC with the Factory Method pattern, ByteBeats achieves:
- **Seamless Object Creation**: Encapsulated creation logic ensures that new types of songs or playlists can be integrated without altering existing code significantly.
- **Enhanced Scalability**: New features or variations can be added easily by extending factory methods without disrupting the main flow.
- **Reduced Coupling**: Clear delegation of object creation keeps different components of the codebase independent.

---
<div align="center">
<img width="613" alt="Screen Shot 2024-12-09 at 6 58 14 PM" src="https://github.com/user-attachments/assets/fb657db8-3ace-4cf6-9998-4c00b15e698f">
<img width="591" alt="Screen Shot 2024-12-08 at 1 20 45 AM" src="https://github.com/user-attachments/assets/3265cb2a-0f7f-4c8c-961d-236edd1ea77b">
</div>

### **Model**
The **Model** represents the core data and logic of the application. In ByteBeats, the Model consists of the `Song` and `MusicPlaylist` classes, which handle song data (title, artist, duration) and playlist management. These classes manage the core functionalities like loading, saving, and organizing playlists, as well as extracting and storing metadata for each song.

- **`Song` Class**: Stores and processes metadata like song title, artist, and duration.
- **`MusicPlaylist` Class**: Manages playlist operations, such as adding/removing songs, saving/loading playlists, and maintaining playlist details.

---

### **View**
The **View** is responsible for presenting the data to the user and handling the graphical user interface (GUI). In ByteBeats, the `MusicPlayerGUI` class is the View. It uses **Java Swing** to create a user-friendly and interactive interface. The View displays the songs in the playlist, provides controls (like play, pause, and add song), and updates in real-time when changes occur.

- **`MusicPlayerGUI` Class**: Implements the GUI where users can interact with the music player—viewing songs, managing playlists, and controlling playback.

Additionally, the **OpenScreen.java** class plays a key role in the initial presentation of the ByteBeats program. It is responsible for creating the first screen the user sees when launching the application.

- **`OpenScreen.java`**: This class serves as the introduction screen, providing the user with a welcoming interface. It helps set the tone for the application and navigates to the main `MusicPlayerGUI` after a brief delay or interaction. This screen is part of the **View** layer, providing a smooth entry point for the user experience before they begin interacting with the main functionalities of ByteBeats.

<div align="center">
  
<img width="392" alt="Screen Shot 2024-12-08 at 1 24 42 AM" src="https://github.com/user-attachments/assets/258692f4-127d-4f25-b6a3-c182c0b19492">
</div>

---

### **Controller**
The **Controller** acts as the mediator between the Model and the View. It processes user input, updates the Model accordingly, and refreshes the View to reflect changes. In ByteBeats, the Controller handles actions such as adding songs, saving playlists, and controlling playback. When a user interacts with the GUI (for example, clicking the play button), the Controller processes this input and updates the Model, which in turn updates the View.

- **Controller Logic**: Manages user interactions, including adding/removing songs from the playlist, saving/loading playlists, and controlling playback actions (play, pause, stop).

---

### **Why We Chose MVC?**
By using the MVC design pattern in ByteBeats, we were able to:
- **Separate Concerns**: The Model, View, and Controller handle specific aspects of the application, making the code easier to maintain and understand.
- **Enhance Maintainability**: Independent components allow us to make updates or changes to one part of the program without affecting others.
- **Provide a Better User Experience**: Clear separation between logic and interface ensures that user interactions are smooth and intuitive.

---

By combining the principles of MVC and the Factory Method design pattern, ByteBeats showcases a scalable, and user-friendly music player application built on solid software design foundations. 

### **LABAN GROUP 1!**

ser tres lng ser :>
