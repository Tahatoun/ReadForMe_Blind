package ensias.readforme_blind.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

public class Post  implements Serializable {
    int id,artist_id,track_id;
    Volunteer volunteer;
    Track track;
    String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
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

    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Post)obj).getId();
    }
    public static Post mapJson(JSONObject object) throws JSONException, ParseException {
        Post Post = new Post();
        Post.id = object.getInt("id");
        Post.artist_id = object.getInt("artist_id");
        Post.track_id  = object.getInt("track_id");
        // includes
        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            Post.volunteer = Volunteer.mapJson(accountObject);
        }
        if(object.has("track")){
            JSONObject accountObject = object.getJSONObject("blind");
            Post.track = Track.mapJson(accountObject);
        }
        return Post;
    }

}
