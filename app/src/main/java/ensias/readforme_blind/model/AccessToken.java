package ensias.readforme_blind.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;


public class AccessToken {

    String token_type;

    int expires_in;

    String access_token;

    String refresh_token;


    public static AccessToken mapJson(JSONObject object) throws JSONException, ParseException {
        AccessToken accessToken = new AccessToken();
        accessToken.token_type = object.getString("token_type");
        accessToken.expires_in = object.getInt("expires_in");
        accessToken.access_token = object.getString("access_token");
        accessToken.refresh_token = object.getString("refresh_token");
        return accessToken;
    }


    public String getTokenType() {
        return token_type;
    }

    public int getExpiresIn() {
        return expires_in;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setTokenType(String tokenType) {
        this.token_type = tokenType;
    }

    public void setExpiresIn(int expires_in) {
        this.expires_in = expires_in;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public void setRefreshToken(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
