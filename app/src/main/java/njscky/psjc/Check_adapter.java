package njscky.psjc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2015/10/8.
 */
public class Check_adapter extends BaseAdapter implements Filterable {

    private LayoutInflater mLayoutInflater;
    private List<checkclass> checkList;
    private List<checkclass> checkFilterList;
    private AddressFilter checkFilter;
    private Activity activity;

    public Check_adapter(Activity activity, List data){
        checkList = data;
        checkFilterList=data;
        mLayoutInflater = activity.getLayoutInflater();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return checkList.size();
    }

    @Override
    public checkclass getItem(int position) {
        return checkList.get(position);
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
            updateView = mLayoutInflater.inflate(R.layout.check_listitem, null);
            viewHolder = new ViewHolder();

            viewHolder.tvChecktype = (TextView) updateView.findViewById(R.id.checktypeTV);
            viewHolder.tvGxtype = (TextView) updateView.findViewById(R.id.gxtypeTV);
            viewHolder.tvGxdh = (TextView) updateView.findViewById(R.id.gxdhTV);
            viewHolder.tvLjdh = (TextView) updateView.findViewById(R.id.ljdhTV);
            viewHolder.tvCz = (TextView) updateView.findViewById(R.id.czTV);
            viewHolder.tvSfcx = (TextView) updateView.findViewById(R.id.sfcxTV);

            updateView.setTag(viewHolder);

        } else {
            updateView = view;
            viewHolder = (ViewHolder) updateView.getTag();
        }

        final checkclass item = getItem(position);

        viewHolder.tvChecktype.setText(String.valueOf(item.getChecktype()));
        viewHolder.tvGxtype.setText(String.valueOf(item.getGxtype()));
        viewHolder.tvGxdh.setText(String.valueOf(item.getGxdh()));
        viewHolder.tvLjdh.setText(String.valueOf(item.getLjdh()));
        viewHolder.tvCz.setText(String.valueOf(item.getCz()));
        viewHolder.tvSfcx.setText(String.valueOf(item.getSfcx()));

        return updateView;

    }

    @Override
    public Filter getFilter() {
        if (checkFilter == null) {
            checkFilter = new AddressFilter();
        }
        return checkFilter;
    }

    static class ViewHolder{
        TextView tvChecktype;
        TextView tvGxtype;
        TextView tvGxdh;
        TextView tvLjdh;
        TextView tvCz;
        TextView tvSfcx;
    }

    private class AddressFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (!constraint.toString().trim().equals("")) {
                ArrayList<checkclass> filterList = new ArrayList<checkclass>();
                for (int i = 0; i < checkFilterList.size(); i++) {
                    if ( String.valueOf((checkFilterList.get(i).getChecktype()))== constraint.toString().trim()) {
                        checkclass check = checkFilterList.get(i);
                        filterList.add(check);
                    }
                }

                results.count = filterList.size();
                results.values = filterList;

            } else {

                results.count = checkFilterList.size();
                results.values = checkFilterList;

            }
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            checkList = (ArrayList<checkclass>)results.values;
            notifyDataSetChanged();
        }
    }

}
