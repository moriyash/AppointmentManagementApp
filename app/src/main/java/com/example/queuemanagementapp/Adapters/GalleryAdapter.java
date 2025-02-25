package com.example.queuemanagementapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.queuemanagementapp.Fragments.ImagePopupFragment;
import com.example.queuemanagementapp.R;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final List<Integer> imageList;

    public GalleryAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int imageRes = imageList.get(position);
        holder.imageView.setImageResource(imageRes);

        holder.imageView.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) v.getContext();
            ImagePopupFragment popupFragment = ImagePopupFragment.newInstance(imageRes);
            popupFragment.show(activity.getSupportFragmentManager(), "image_popup");
        });
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
