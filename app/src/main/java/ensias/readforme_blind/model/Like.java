package ensias.readforme_blind.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

public class Like  implements Serializable {
    int id;
    int artist_id, track_id;
    Volunteer volunteer;
    Track track;


    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Like)obj).getId();
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

    public int getTrack_id() {
        return track_id;
    }

    public void setTrack_id(int track_id) {
        this.track_id = track_id;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public static Like mapJson(JSONObject object) throws JSONException, ParseException {
        Like Like = new Like();
        Like.id = object.getInt("id");
        Like.artist_id = object.getInt("artist_id");
        Like.track_id  = object.getInt("track_id");
        // includes
        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            Like.volunteer = Volunteer.mapJson(accountObject);
        }
        if(object.has("track")){
            JSONObject accountObject = object.getJSONObject("blind");
            Like.track = Track.mapJson(accountObject);
        }
        return Like;
    }
}
