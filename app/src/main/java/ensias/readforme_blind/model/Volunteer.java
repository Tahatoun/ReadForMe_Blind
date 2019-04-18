package ensias.readforme_blind.model;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class Volunteer implements Serializable{
    private String firstName;
    private String lastName;
    private String image;
    private String email;
    private String password;
    int id;
    int gender;
    String birthdate;
    ArrayList<Track> tracks;
    ArrayList<Notification> notifications;
    private List suggestionsFile;
    private List<File> favouritesFile;

    public Volunteer(Volunteer body) {
        this();
        this.firstName = body.firstName;
        this.lastName = body.lastName;
        this.image = body.image;
        this.id = body.id;
        this.gender = body.gender;
        this.birthdate=body.birthdate;
    }


    public Volunteer(int id, String firstName, String lastName,  String email, String password, String image, int gender, String birthdate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.id = id;
        this.email=email;
        this.password=password;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public Volunteer(  String email, String password) {

        this.email=email;
        this.password=password;

    }

    public Volunteer(){
        firstName = " ";
        lastName = " ";
        tracks = new ArrayList<>();
        notifications = new ArrayList<>();

    }
    
    public static Volunteer mapJson(JSONObject object) throws JSONException, ParseException {
        Volunteer volunteer = new Volunteer();
        volunteer.id = object.getInt("id");
        volunteer.firstName = object.getString("firstName");
        volunteer.lastName  = object.getString("lastName");
        volunteer.image  = object.getString("image");
        volunteer.birthdate  = object.getString("birthdate");

        if(object.has("tracks")){
            JSONArray notificationsArray = object.getJSONArray("tracks");
            for (int i=0;i<notificationsArray.length();i++){
                JSONObject notificationObject = (JSONObject) notificationsArray.get(i);
                Track track = Track.mapJson(notificationObject);
                volunteer.tracks.add(track);
            }
        }
        if(object.has("notifications")){
            JSONArray messageNotificationsArray = object.getJSONArray("notifications");
            for (int i=0;i<messageNotificationsArray.length();i++){
                JSONObject messageNotificationObject = (JSONObject) messageNotificationsArray.get(i);
                Notification notification = Notification.mapJson(messageNotificationObject);
                volunteer.notifications.add(notification);
            }
        }



        return volunteer;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }


    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Volunteer)obj).getId();
    }

    @Override
    public String toString() {
        return "{'id':"+getId()+
                ",'firstName':'"+getFirstName()+"'"+
                ",'lastName':'"+getLastName()+"'"+
                ",'gender':" +getGender()+
                ",'birthdate':'" +getBirthdate()+"'"+
                ",'image':'" +getImage()+"'"+
                "}";
    }

    public List getSuggestions() {

        if(suggestionsFile==null) suggestionsFile=new ArrayList();
        return suggestionsFile;
    }

    public List<File> getFavourites() {
        return favouritesFile;
    }
}
