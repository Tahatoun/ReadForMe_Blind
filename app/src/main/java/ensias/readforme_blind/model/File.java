package ensias.readforme_blind.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

public class File implements Serializable {
    int id, views, artist_id;
    String name;
    String urlFile;
    String image;
    String description;
    private String postContent;
    Blind postedBy;
    String duration;
    Blind blind;
    ArrayList<Like> likes;
    ArrayList<Track> tracks;
    ArrayList<Comment> comments;

    public File(int id, String name, String urlFile) {
        this.id = id;
        this.name = name;
        this.urlFile = urlFile;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Blind getBlind() {
        return blind;
    }

    public void setBlind(Blind blind) {
        this.blind = blind;
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

    public Blind getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(Blind postedBy) {
        this.postedBy = postedBy;
    }

    public File(){
        id= -1;
    }
    public static File mapJson(JSONObject object) throws JSONException, ParseException {
        File file = new File();
        file.id = object.getInt("id");
        file.artist_id = object.getInt("artist_id");
        file.views  = object.getInt("views");
        file.name  = object.getString("name");
        file.urlFile  = object.getString("urlFile");
        file.image  = object.getString("image");
        file.description  = object.getString("description");
        file.postContent  = object.getString("description");
        // includes

        if(object.has("blind")){
            JSONObject accountObject = object.getJSONObject("blind");
            file.blind = Blind.mapJson(accountObject);
            file.postedBy = Blind.mapJson(accountObject);
        }
        if(object.has("likes")){
            JSONArray messageNotificationsArray = object.getJSONArray("likes");
            for (int i=0;i<messageNotificationsArray.length();i++){
                JSONObject messageNotificationObject = (JSONObject) messageNotificationsArray.get(i);
                Like like = Like.mapJson(messageNotificationObject);
                file.likes.add(like);
            }
        }
        if(object.has("comments")){
            JSONArray messageNotificationsArray = object.getJSONArray("comments");
            for (int i=0;i<messageNotificationsArray.length();i++){
                JSONObject messageNotificationObject = (JSONObject) messageNotificationsArray.get(i);
                Comment comment = Comment.mapJson(messageNotificationObject);
                file.comments.add(comment);
            }
        }

        if(object.has("tracks")){
            JSONArray tracksArray = object.getJSONArray("tracks");
            for (int i=0;i<tracksArray.length();i++){
                JSONObject trackObject = (JSONObject) tracksArray.get(i);
                Track track = Track.mapJson(trackObject);
                file.tracks.add(track);
            }
        }
        return file;
    }
    public File(int id, int views, int artist_id, String name, String urlFile, String image, String description) {
        this.id = id;
        this.views = views;
        this.artist_id = artist_id;
        this.name = name;
        this.urlFile = urlFile;
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

    public String getUrlfile() {
        return urlFile;
    }

    public void setUrlfile(String urlFile) {
        this.urlFile = urlFile;
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
        if(this.getId() == ((File)obj).getId())
            return true;
        return false;
    }
}
