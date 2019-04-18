package ensias.readforme_blind.listAdapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ensias.readforme_blind.R;
import ensias.readforme_blind.dao.ImageTransformation;
import ensias.readforme_blind.dao.Util;
import ensias.readforme_blind.model.Blind;
import ensias.readforme_blind.model.Track;

public class TrackAdapter extends ArrayAdapter<Track> {
    private ArrayList<Track> suggestions;
    private String imagePath;
    private List<Track> posts;
    private List<Track> tracks;
    private Activity context;

    public TrackAdapter(Activity context, int resource, List<Track> items) {
        super(context, resource, items);
        posts=items;
        tracks=new ArrayList<>();
        tracks.addAll(items);
        this.context = context;
        try {
            imagePath = Util.getImagesHost(getContext())+ Util.getProperty("Post",this.getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View v = convertView;
        final LayoutInflater vi = LayoutInflater.from(getContext());
        final Track post= getItem(position);

        if (v == null) {
            v = vi.inflate(R.layout.track_item, null);

        }

        TextView name = (TextView) v.findViewById(R.id.textViewTrackTitle);
        TextView type = (TextView) v.findViewById(R.id.textViewTrackType);

        type.setText(position+1+"");
        name.setText(post.getName());




        v.setContentDescription(post.getName());

        return v;
    }




}
