package ensias.readforme_blind.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

public class Track implements Serializable {
    int id, views, artist_id;
    String name;
    String urlTrack;
    String image;
    String description;
    String postContent;
    Volunteer postedBy;
    String duration;
    Volunteer volunteer;
    ArrayList<Like> likes;
    ArrayList<Comment> comments;

    public Track(int id, String name, String urlTrack) {
        this.id = id;
        this.name = name;
        this.urlTrack = urlTrack;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public ArrayList<Like> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Like> likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getPostContent() {
        return postContent;
    }
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public Volunteer getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(Volunteer postedBy) {
        this.postedBy = postedBy;
    }

    public Track(){
        id= -1;
    }
    public static Track mapJson(JSONObject object) throws JSONException, ParseException {
        Track Track = new Track();
        Track.id = object.getInt("id");
        Track.artist_id = object.getInt("artist_id");
        Track.views  = object.getInt("views");
        Track.name  = object.getString("name");
        Track.urlTrack  = object.getString("urlTrack");
        Track.image  = object.getString("image");
        Track.description  = object.getString("description");
        // includes
        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            Track.volunteer = Volunteer.mapJson(accountObject);
        }
        if(object.has("likes")){
            JSONArray messageNotificationsArray = object.getJSONArray("likes");
            for (int i=0;i<messageNotificationsArray.length();i++){
                JSONObject messageNotificationObject = (JSONObject) messageNotificationsArray.get(i);
                Like like = Like.mapJson(messageNotificationObject);
                Track.likes.add(like);
            }
        }
        if(object.has("comments")){
            JSONArray messageNotificationsArray = object.getJSONArray("comments");
            for (int i=0;i<messageNotificationsArray.length();i++){
                JSONObject messageNotificationObject = (JSONObject) messageNotificationsArray.get(i);
                Comment comment = Comment.mapJson(messageNotificationObject);
                Track.comments.add(comment);
            }
        }
        return Track;
    }
    public Track(int id, int views, int artist_id, String name, String urlTrack, String image, String description) {
        this.id = id;
        this.views = views;
        this.artist_id = artist_id;
        this.name = name;
        this.urlTrack = urlTrack;
        this.image = image;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlTrack() {
        return urlTrack;
    }

    public void setUrlTrack(String urlTrack) {
        this.urlTrack = urlTrack;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getId() == ((Track)obj).getId())
            return true;
        return false;
    }
}
