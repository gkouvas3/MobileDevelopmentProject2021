package gr.uom.socialmedianetworkaggregator;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPostsListAdapter extends ArrayAdapter<TwitterPostEntry> {

    private List<TwitterPostEntry> dataset;
    private final LayoutInflater inflater;
    private final int layoutResource;


    public ViewPostsListAdapter(@NonNull Context context, int resource, List<TwitterPostEntry> objects) {
        super(context, resource, objects);

        dataset = objects;
        inflater = LayoutInflater.from(context);
        layoutResource = resource;
    }

    public TwitterPostEntry getPostsListEntry(int position){
        if(position < dataset.size() ){
            return dataset.get(position);
        }
        return new TwitterPostEntry(null,null,null);
    }

    public void setNewsEntries(@NonNull List<TwitterPostEntry> newsEntries) {
        dataset = newsEntries;
        notifyDataSetChanged();
    }

    static class PostsViewHolder {

        public TextView usernameTextView;
        public TextView descriptionTextView;
        public ImageView imageView;

        public PostsViewHolder(View itemView) {
            usernameTextView = itemView.findViewById(R.id.userNameText);
            descriptionTextView = itemView.findViewById(R.id.descriptionText);
            imageView = itemView.findViewById(R.id.socialMediaIcon);
        }
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PostsViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new PostsViewHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (PostsViewHolder)convertView.getTag();
        }

        TwitterPostEntry postsListEntry = dataset.get(position);
        holder.usernameTextView.setText(postsListEntry.getUsername());
        //na thimithw na to kanw description
        holder.descriptionTextView.setText(postsListEntry.getUrl());
        holder.imageView.setImageResource(postsListEntry.getSocialMediaIcon());
        //Picasso.get().load(postsListEntry.getUrlToImage()).into(holder.imageView);

        return convertView;
    }

    @Override
    public int getCount() {

        return dataset.size();
    }

}
