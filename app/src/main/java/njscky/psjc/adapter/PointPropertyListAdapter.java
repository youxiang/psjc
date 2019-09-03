package njscky.psjc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import njscky.psjc.R;
import njscky.psjc.model.OptionalProperty;
import njscky.psjc.model.Property;

public class PointPropertyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NORMAL = 0;
    private static final int OPTIONAL = 1;

    private List<Property> properties;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case OPTIONAL:
                View optionalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_property_optional, parent, false);
                return new OptionalVH(optionalView);
            case NORMAL:
            default:
                View commonView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_property, parent, false);
                return new CommonVH(commonView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Property item = getItem(position);

        if (holder instanceof OptionalVH && item instanceof OptionalProperty) {
            ((OptionalVH) holder).bind((OptionalProperty) item);
        } else if (holder instanceof CommonVH) {
            ((CommonVH) holder).bind(item);
        }
    }

    private Property getItem(int position) {
        if (properties == null || position < 0 || position >= properties.size()) {
            return null;
        }
        return properties.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        Property property = getItem(position);
        if (property instanceof OptionalProperty) {
            return OPTIONAL;
        }
        return NORMAL;
    }

    @Override
    public int getItemCount() {
        return properties == null ? 0 : properties.size();
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
        notifyDataSetChanged();
    }

    class OptionalVH extends RecyclerView.ViewHolder {

        TextView name;
        AppCompatSpinner value;

        public OptionalVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_prop_name);
            value = itemView.findViewById(R.id.sp_prop_value);
        }

        public void bind(@NonNull OptionalProperty item) {
            name.setText(item.name);
            value.setSelection(item.getSelection());
            value.setAdapter(new OptionalPropertyValueListAdapter(item.options));
        }
    }

    class CommonVH extends RecyclerView.ViewHolder {

        TextView name;
        EditText value;

        public CommonVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_prop_name);
            value = itemView.findViewById(R.id.et_prop_value);
        }

        public void bind(@NonNull Property item) {
            name.setText(item.name);
            value.setText(item.value);
            value.setEnabled(item.enable);
        }
    }
}
