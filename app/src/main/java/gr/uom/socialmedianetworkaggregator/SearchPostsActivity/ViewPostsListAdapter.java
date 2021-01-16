package gr.uom.socialmedianetworkaggregator.SearchPostsActivity;

import android.content.Context;
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

import gr.uom.socialmedianetworkaggregator.R;

public class ViewPostsListAdapter extends ArrayAdapter<PostsListEntry> {

    private List<PostsListEntry> dataset;
    private final LayoutInflater inflater;
    private final int layoutResource;


    public ViewPostsListAdapter(@NonNull Context context, int resource, List<PostsListEntry> objects) {
        super(context, resource, objects);

        dataset = objects;
        inflater = LayoutInflater.from(context);
        layoutResource = resource;
    }

    public PostsListEntry getPostsListEntry(int position){
        if(position < dataset.size() ){
            return dataset.get(position);
        }
        return new PostsListEntry(null,null,null);
    }

    public void setNewsEntries(@NonNull List<PostsListEntry> newsEntries) {
        dataset = newsEntries;
        notifyDataSetChanged();
    }

    static class PostsViewHolder {

        public TextView usernameTextView;
        public TextView descriptionTextView;
        public ImageView imageView;
        public ImageView instaMediaView;

        public PostsViewHolder(View itemView) {
            usernameTextView = itemView.findViewById(R.id.userNameText);
            descriptionTextView = itemView.findViewById(R.id.descriptionText);
            imageView = itemView.findViewById(R.id.socialMediaIcon);
            instaMediaView=itemView.findViewById(R.id.instaMediaImage);
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

        PostsListEntry postsListEntry = dataset.get(position);
        holder.usernameTextView.setText(postsListEntry.getUsername());
        holder.descriptionTextView.setText(postsListEntry.getDescription());
        holder.imageView.setImageResource(postsListEntry.getSocialMediaIcon());
        if(postsListEntry.isInstaPost()) {
            holder.instaMediaView.setVisibility(View.VISIBLE);
            Picasso.get().load(postsListEntry.getInstaMediaUrl()).into(holder.instaMediaView);
        }
        return convertView;
    }

    @Override
    public int getCount() {

        return dataset.size();
    }

}
