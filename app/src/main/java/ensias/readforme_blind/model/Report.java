package ensias.readforme_blind.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report implements Serializable {
    int id, artist_id, track_id;
    String description;
    Date date;
    Volunteer volunteer;
    Track track;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public static Report mapJson(JSONObject object) throws JSONException, ParseException {
        Report Report = new Report();
        Report.id = object.getInt("id");
        Report.artist_id = object.getInt("artist_id");
        Report.track_id  = object.getInt("track_id");
        Report.description  = object.getString("description");
        Report.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(object.getJSONObject("date").getString("date"));
        // includes
        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            Report.volunteer = Volunteer.mapJson(accountObject);
        }
        if(object.has("track")){
            JSONObject accountObject = object.getJSONObject("blind");
            Report.track = Track.mapJson(accountObject);
        }
        return Report;
    }


    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Report)obj).getId();
    }
}
