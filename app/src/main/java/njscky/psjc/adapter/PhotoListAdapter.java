package njscky.psjc.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import njscky.psjc.R;
import njscky.psjc.model.PhotoInfo;
import njscky.psjc.util.GlideApp;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.VH> {

    private static final int PHOTO_LIMIT = 9;
    private static final String TAG = PhotoListAdapter.class.getSimpleName();

    private List<PhotoInfo> photoInfoList;

    private OnItemClickListener listener;

    private AlertDialog dialog;

    public PhotoListAdapter() {
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void addPhotoInfo(PhotoInfo photoInfo) throws IllegalArgumentException {
        if (photoInfo == null) {
            return;
        }
        if (photoInfoList == null) {
            photoInfoList = new ArrayList<>();
        }

        if (photoInfoList.size() >= PHOTO_LIMIT) {
            throw new IllegalArgumentException("超过最大图片数 " + PHOTO_LIMIT);
        }

        photoInfoList.add(photoInfo);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_photo, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (position == getItemCount() - 1 && showAddBtn()) {
            holder.bindAddBtn();
        } else {
            PhotoInfo photoInfo = getPhotoInfo(position);
            holder.bindPhotoInfo(photoInfo);
        }
    }

    private boolean showAddBtn() {
        return getPhotoInfoListSize() < PHOTO_LIMIT;
    }

    private PhotoInfo getPhotoInfo(int pos) {
        if (photoInfoList == null || pos < 0 || pos >= getPhotoInfoListSize()) {
            return null;
        }
        return photoInfoList.get(pos);
    }

    @Override
    public int getItemCount() {
        int photoInfoListSize = getPhotoInfoListSize();
        return photoInfoListSize < PHOTO_LIMIT ? photoInfoListSize + 1 : photoInfoListSize;
    }

    public int getPhotoInfoListSize() {
        return photoInfoList == null ? 0 : photoInfoList.size();
    }

    public void replacePhotoInfo(int candidateReplacedPhotoPosition, PhotoInfo photoInfo) {
        if (candidateReplacedPhotoPosition >= 0 && candidateReplacedPhotoPosition < getPhotoInfoListSize()) {
            PhotoInfo item = photoInfoList.get(candidateReplacedPhotoPosition);
            item.name = photoInfo.name;
            item.path = photoInfo.path;
            notifyItemChanged(candidateReplacedPhotoPosition);
        }

    }

    public List<PhotoInfo> getPhotoInfoList() {
        return photoInfoList;
    }

    public interface OnItemClickListener {
        void onAddPhoto();

        void onPhotoClick(int position);

    }

    public class VH extends RecyclerView.ViewHolder {
        ImageView photoView;

        public VH(@NonNull View itemView) {
            super(itemView);
            photoView = (ImageView) itemView;
            photoView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                boolean showAddBtn = showAddBtn();
                if (pos == getItemCount() - 1 && showAddBtn) {
                    if (listener != null) {
                        listener.onAddPhoto();
                    }
                } else {
                    if (listener != null) {
                        listener.onPhotoClick(pos);
                    }
                }
            });

            photoView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                boolean showAddBtn = showAddBtn();
                if (pos == getItemCount() - 1 && showAddBtn) {
                    Log.w(TAG, "CommonVH: do nothing" );
                } else {
                    showRemoveItemDialog(v.getContext(),pos);
                }
                return true;
            });
        }

        public void bindAddBtn() {
            photoView.setImageResource(R.drawable.icon_addpic_unfocused);
        }

        public void bindPhotoInfo(PhotoInfo photoInfo) {
            GlideApp.with(photoView).load(photoInfo.path).into(photoView);
        }
    }

    private void showRemoveItemDialog(Context context, int position) {
        if (photoInfoList == null || position < 0 || position >= getPhotoInfoListSize()) {
            Log.w(TAG, "showRemoveItemDialog: error ");
            return;
        }

        PhotoInfo photoInfo = photoInfoList.get(position);

        if (dialog == null) {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_title)
                    .create();
        }

        dialog.setMessage(context.getString(R.string.delete_photo, photoInfo.name));
        dialog.setButton(BUTTON_POSITIVE, context.getString(R.string.confirm), (dialog, which) -> {
            Log.i(TAG, "showRemoveItemDialog: " + position);
            removeItem(position);
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void removeItem(int position) {
        if (photoInfoList == null || position < 0 || position >= getPhotoInfoListSize()) {
            Log.w(TAG, "removeItem: removeItem error ");
            return;
        }
        photoInfoList.remove(position);
        notifyItemRemoved(position);
    }
}
