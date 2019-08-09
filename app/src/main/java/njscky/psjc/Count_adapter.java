package njscky.psjc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class Count_adapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<countclass> countList;
    private Activity activity;

    public Count_adapter(Activity activity, List data){
        countList = data;
        mLayoutInflater = activity.getLayoutInflater();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return countList.size();
    }

    @Override
    public countclass getItem(int position) {
        return countList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View updateView;
        ViewHolder viewHolder;
        if (view == null) {
            updateView = mLayoutInflater.inflate(R.layout.count_listitem, null);
            viewHolder = new ViewHolder();
            viewHolder.tvGxtype = (TextView) updateView.findViewById(R.id.gxtypeC);
            viewHolder.tvMxd = (TextView) updateView.findViewById(R.id.mxdC);
            viewHolder.tvYbd = (TextView) updateView.findViewById(R.id.ybdC);
            viewHolder.tvZs = (TextView) updateView.findViewById(R.id.zsC);
            updateView.setTag(viewHolder);

        } else {
            updateView = view;
            viewHolder = (ViewHolder) updateView.getTag();
        }

        final countclass item = getItem(position);
        viewHolder.tvGxtype.setText(String.valueOf(item.getGxtype()));
        viewHolder.tvMxd.setText(String.valueOf(item.getMxd()));
        viewHolder.tvYbd.setText(String.valueOf(item.getYbd()));
        viewHolder.tvZs.setText(String.valueOf(item.getZs()));
        return updateView;

    }

    static class ViewHolder{
        TextView tvGxtype;
        TextView tvMxd;
        TextView tvYbd;
        TextView tvZs;
    }
}
