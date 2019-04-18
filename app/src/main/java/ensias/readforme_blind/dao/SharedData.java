package ensias.readforme_blind.dao;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.Serializable;

import ensias.readforme_blind.R;
import ensias.readforme_blind.model.Blind;

public class SharedData {
    public static void saveBlindInSharedPref(String Blind,Context context){
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.blindKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(context.getString(R.string.blindKey), Blind);
        editor.commit();
    }
    public static void removeBlindInSharedPref(Context context){
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.blindKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(context.getResources().getString(R.string.blindKey));
        editor.commit();
    }
    public static Blind getBlindFromSharedPref(Context context){
        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.blindKey), Context.MODE_PRIVATE);
        String key = context.getResources().getString(R.string.blindKey);
        if(pref.contains(key)){
            Blind blind = new Blind();
            blind = new Gson().fromJson(pref.getString(context.getResources().getString(R.string.blindKey),"undefined"), Blind.class);
            return  new Gson().fromJson(pref.getString(context.getResources().getString(R.string.blindKey),"undefined"), Blind.class);
        }
        return null;
    }

    public static Serializable getBlindFromExtra(Activity activity){
        if(activity.getIntent().hasExtra("Blind")){
            if(activity.getIntent().getExtras().getSerializable("Blind") != null){
                return activity.getIntent().getExtras().getSerializable("Blind");
            }else return  null;
        }
        return null;
    }
   

    public static Blind getBlind(Activity activity){
        Blind blind = new Blind();
        blind = (Blind) SharedData.getBlindFromExtra(activity);
        if(blind == null)
            blind = getBlindFromSharedPref(activity);
        return blind;
    }
}
