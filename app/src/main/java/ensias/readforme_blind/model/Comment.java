package ensias.readforme_blind.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment implements Serializable{
    int id, comment_id, track_id;
    String content;
    Date date;
    Volunteer volunteer;
    Track track;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getTrack_id() {
        return track_id;
    }

    public void setTrack_id(int track_id) {
        this.track_id = track_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Comment)obj).getId();
    }


    public static Comment mapJson(JSONObject object) throws JSONException, ParseException {
        Comment comment = new Comment();
        comment.id = object.getInt("id");
        comment.comment_id = object.getInt("comment_id");
        comment.track_id  = object.getInt("track_id");
        comment.content  = object.getString("content");
        comment.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(object.getJSONObject("date").getString("date"));

        // includes
        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            comment.volunteer = Volunteer.mapJson(accountObject);
        }
        if(object.has("track")){
            JSONObject accountObject = object.getJSONObject("blind");
            comment.track = Track.mapJson(accountObject);
        }
        return comment;
    }
}
