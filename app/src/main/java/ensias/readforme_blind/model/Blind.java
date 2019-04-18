package ensias.readforme_blind.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;


public class Blind implements Serializable {
    private String firstName;
    private String lastName;
    private String image;
    private String email;
    private String password;
    int id;
    int gender;
    String birthdate;
    private ArrayList<File> files;
    ArrayList<Notification> notifications;

    public Blind(Blind body) {
        this();
        this.firstName = body.firstName;
        this.lastName = body.lastName;
        this.image = body.image;
        this.id = body.id;
        this.gender = body.gender;
        this.birthdate = body.birthdate;
    }


    public Blind(int id, String firstName, String lastName, String email, String password, String image, int gender, String birthdate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.id = id;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public Blind(String email, String password) {

        this.email = email;
        this.password = password;

    }

    public Blind() {
        firstName = " ";
        lastName = " ";
        files = new ArrayList<>();
        notifications = new ArrayList<>();

    }

    public static Blind mapJson(JSONObject object) throws JSONException, ParseException {
        Blind blind = new Blind();
        blind.id = object.getInt("id");
        blind.firstName = object.getString("firstName");
        blind.lastName = object.getString("lastName");
        blind.image = object.getString("image");
        blind.birthdate = object.getString("birthdate");

        if (object.has("files")) {
            JSONArray notificationsArray = object.getJSONArray("files");
            for (int i = 0; i < notificationsArray.length(); i++) {
                JSONObject notificationObject = (JSONObject) notificationsArray.get(i);
                File file = File.mapJson(notificationObject);
                blind.files.add(file);
            }
        }
        if (object.has("notifications")) {
            JSONArray messageNotificationsArray = object.getJSONArray("notifications");
            for (int i = 0; i < messageNotificationsArray.length(); i++) {
                JSONObject messageNotificationObject = (JSONObject) messageNotificationsArray.get(i);
                Notification notification = Notification.mapJson(messageNotificationObject);
                blind.notifications.add(notification);
            }
        }


        return blind;
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

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }


    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((Blind) obj).getId();
    }

    @Override
    public String toString() {
        return "{'id':" + getId() +
                ",'firstName':'" + getFirstName() + "'" +
                ",'lastName':'" + getLastName() + "'" +
                ",'gender':" + getGender() +
                ",'birthdate':'" + getBirthdate() + "'" +
                ",'image':'" + getImage() + "'" +
                "}";
    }
}
