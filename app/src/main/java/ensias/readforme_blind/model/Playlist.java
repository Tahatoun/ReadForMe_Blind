package ensias.readforme_blind.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

public class Playlist  implements Serializable {
    int id;
    int artist_id;
    int views;
    int numberOfTracks;
    String name;
    Volunteer volunteer;

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }


    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Playlist)obj).getId();
    }
    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    ArrayList<Track> tracks;

    public Playlist(){
        tracks = new ArrayList<>();
    }
    public static Playlist mapJson(JSONObject object) throws JSONException, ParseException {
        Playlist Playlist = new Playlist();
        Playlist.id = object.getInt("id");
        Playlist.artist_id = object.getInt("artist_id");
        Playlist.views  = object.getInt("views");
        Playlist.name  = object.getString("name");
        // includes
        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            Playlist.volunteer = Volunteer.mapJson(accountObject);
        }
        if(object.has("tracks")){
            JSONArray messageNotificationsArray = object.getJSONArray("tracks");
            for (int i=0;i<messageNotificationsArray.length();i++){
                JSONObject messageNotificationObject = (JSONObject) messageNotificationsArray.get(i);
                Track track = Track.mapJson(messageNotificationObject);
                Playlist.tracks.add(track);
            }
        }
        return Playlist;
    }

    public Playlist(int id,  String name, int artist_id, int views) {
        this.id = id;
        this.artist_id = artist_id;
        this.views = views;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfTracks() {
        return numberOfTracks;
    }

    public void setNumberOfTracks(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
    }

}
