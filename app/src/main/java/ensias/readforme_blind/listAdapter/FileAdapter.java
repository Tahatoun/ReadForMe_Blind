package ensias.readforme_blind.listAdapter;


import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ensias.readforme_blind.R;
import ensias.readforme_blind.dao.ImageTransformation;
import ensias.readforme_blind.dao.Util;
import ensias.readforme_blind.model.Blind;
import ensias.readforme_blind.model.File;
import ensias.readforme_blind.model.Playlist;
import ensias.readforme_blind.model.Volunteer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileAdapter extends ArrayAdapter<File>  {
    private  ArrayList<File> suggestions;
    private String imagePath;
    private List<File> posts;
    private Blind blind;
    private List<File> files;
    private Activity context;

    public FileAdapter(Activity context, int resource, Blind blind, List<File> items) {
        super(context, resource, items);
        posts=items;
        files=new ArrayList<>();
        files.addAll(items);
        this.context = context;
        this.blind = blind;
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
        final File post= getItem(position);

        if (v == null) {
            v = vi.inflate(R.layout.file_item, null);

        }
            v = vi.inflate(R.layout.file_item, null);
            ImageView picture = (ImageView) v.findViewById(R.id.imageViewImage);
            TextView name = (TextView) v.findViewById(R.id.textViewName);
            TextView desc = (TextView) v.findViewById(R.id.textViewType);
            TextView by = (TextView) v.findViewById(R.id.textViewBy);
            final TextView content = (TextView) v.findViewById(R.id.textViewContent);
            //ImageView trackImage = (ImageView) v.findViewById(R.id.imageViewImageTrack);
            final LikeButton like = (LikeButton) v.findViewById(R.id.imageViewLike);

            like.setLiked(false);
            like.setEnabled(true);
            name.setText(post.getBlind().getFirstName()+" "+post.getBlind().getLastName());

            desc.setText(R.string.shared);
            desc.setText(desc.getText() + " " + post.getName());
            if(post.getBlind().getId() != post.getBlind().getId() ){
                by.setText(R.string.by);
                by.setText(by.getText() + " " +  post.getBlind().getFirstName() + " " + post.getBlind().getLastName());
            }

            v.setContentDescription(post.getName());
            content.setText(post.getDescription());
            content.setContentDescription(post.getDescription());
            if(post.getBlind().getImage() != null){
                System.out.println(post.getBlind().getImage() );
                Picasso.with(getContext()).load(post.getBlind().getImage() )
                        .transform(new ImageTransformation())
                        .placeholder(R.mipmap.ic_camera)
                        .error(R.mipmap.ic_camera)
                        .into(picture);
            }else {
                picture.setImageResource(R.mipmap.ic_camera);
            }
            /*if(post.getImage() == null) trackImage.setVisibility(View.GONE);
            else{
                Picasso.with(getContext()).load(post.getImage() )
                        .placeholder(R.mipmap.ic_camera)
                        .error(R.mipmap.ic_camera)
                        .into(trackImage);
            }*/
  /*          if(blind.getFavourites().contains(post)){
                like.setLiked(true);
            }else{
                like.setLiked(false);
            }*/


        return v;
    }



    public void filter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        if (posts.size()>=files.size()){
            files.clear();
            files.addAll(posts);
        }
        posts.clear();
        if (query.length()==0){
            posts.addAll(files);
        }
        else {
            for (File file : files){
                if (file.getName().toLowerCase(Locale.getDefault())
                        .contains(query)){
                    posts.add(file);
                }
            }
        }
        Log.d("array size files",files.size()+"");
        Log.d("array size posts",posts.size()+"");
        notifyDataSetChanged();
    }
}
