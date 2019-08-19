package njscky.psjc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import njscky.psjc.R;

public class DBFileListAdapter extends RecyclerView.Adapter<DBFileListAdapter.VH> {

    private List<File> dbFiles;
    private int selected = -1;

    public DBFileListAdapter() {
    }

    @NonNull
    @Override
    public DBFileListAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_db_file, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DBFileListAdapter.VH holder, int position) {
        holder.bind(getItem(position));
    }

    private File getItem(int position) {
        return dbFiles.get(position);
    }

    @Override
    public int getItemCount() {
        return dbFiles == null ? 0 : dbFiles.size();
    }

    public void setData(List<File> dbFiles) {
        this.dbFiles = dbFiles;
        notifyDataSetChanged();
    }

    private void setSelectedItem(int position) {
        this.selected = position;
        notifyDataSetChanged();
    }

    public File getSelectedItem() {
        if (selected < 0 || selected >= getItemCount()) {
            return null;
        }
        return dbFiles.get(selected);
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView name;

        public VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title);

            itemView.setOnClickListener(v -> {
                setSelectedItem(getAdapterPosition());
            });
        }

        public void bind(File item) {
            name.setText(item.getName());
            itemView.setSelected(selected == getAdapterPosition());
        }
    }
}
