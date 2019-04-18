package ensias.readforme_blind.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

public class Notification  implements Serializable {
    int id, artist_id, track_id;
    String description;
    boolean seen;
    Volunteer volunteer;
    Track track;
    public Notification(){

    }
    public static Notification mapJson(JSONObject object) throws JSONException, ParseException {
        Notification notification = new Notification();
        notification.id = object.getInt("id");
        notification.artist_id = object.getInt("artist_id");
        notification.track_id  = object.getInt("track_id");
        notification.description  = object.getString("description");
        notification.seen = (object.getInt("seen") != 0 );
        // includes
        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            notification.volunteer = Volunteer.mapJson(accountObject);
        }
        if(object.has("track")){
            JSONObject accountObject = object.getJSONObject("blind");
            notification.track = Track.mapJson(accountObject);
        }
        return notification;
    }

    public Notification(int id, int artist_id, String description, boolean seen) {
        this.id = id;
        this.artist_id = artist_id;
        this.description = description;
        this.seen = seen;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
