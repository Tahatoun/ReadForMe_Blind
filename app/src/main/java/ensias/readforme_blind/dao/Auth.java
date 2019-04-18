package ensias.readforme_blind.dao;


import android.content.SharedPreferences;

import ensias.readforme_blind.model.AccessToken;

public class Auth {
    private SharedPreferences token_prefs;
    private SharedPreferences.Editor editor;

    private static Auth INSTANCE = null;

    private Auth(SharedPreferences token_prefs){
        this.token_prefs = token_prefs;
        this.editor = token_prefs.edit();
    }

    static  synchronized public  Auth getInstance(SharedPreferences token_prefs){
        if(INSTANCE == null){
            INSTANCE = new Auth(token_prefs);
        }
        return INSTANCE;
    }

    public void saveToken(AccessToken token){
        editor.putString("ACCESS_TOKEN", token.getAccessToken()).commit();
        editor.putString("REFRESH_TOKEN", token.getRefreshToken()).commit();
    }

    public void deleteToken(){
        editor.remove("ACCESS_TOKEN").commit();
        editor.remove("REFRESH_TOKEN").commit();
    }

    public AccessToken getToken(){
        AccessToken token = new AccessToken();
        token.setAccessToken(token_prefs.getString("ACCESS_TOKEN", null));
        token.setRefreshToken(token_prefs.getString("REFRESH_TOKEN", null));
        return token;
    }
}
