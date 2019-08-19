package njscky.psjc.adapter;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import njscky.psjc.R;
import njscky.psjc.model.LayerInfo;

public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.VH> {

    private ArrayList<LayerInfo> layers;

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layer, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    private LayerInfo getItem(int position) {
        if (layers == null) {
            return null;
        }
        if (position < 0 || position > layers.size()) {
            return null;
        }
        return layers.get(position);
    }

    @Override
    public int getItemCount() {
        return layers == null ? 0 : layers.size();
    }

    public void setLayers(ArrayList<LayerInfo> layers) {
        this.layers = layers;
        notifyDataSetChanged();
    }

    public void selectAll(boolean select) {
        for (LayerInfo layerInfo : layers) {
            layerInfo.visible = select;
        }
        notifyDataSetChanged();
    }

    public ArrayList<LayerInfo> getLayersInfo() {
        return layers;
    }

    public class VH extends RecyclerView.ViewHolder {

        CheckBox checkBox;


        public VH(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView;
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                int position = getAdapterPosition();
                LayerInfo layerInfo = getItem(position);
                if (layerInfo != null && layerInfo.visible != isChecked) {
                    layerInfo.visible = isChecked;
                    notifyItemChanged(position);
                }
            });
        }

        public void bind(LayerInfo layerInfo) {
            checkBox.setChecked(layerInfo.visible);
            checkBox.setText(layerInfo.name);
        }
    }
}
