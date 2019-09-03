package njscky.psjc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OptionalPropertyValueListAdapter extends BaseAdapter {

    private String[] propertyValues;

    public OptionalPropertyValueListAdapter(String[] propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public int getCount() {
        return propertyValues == null ? 0 : propertyValues.length;
    }

    @Override
    public String getItem(int position) {
        if (propertyValues == null || position < 0 || position >= propertyValues.length) {
            return null;
        }
        return propertyValues[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            vh = new ViewHolder();
            vh.textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        String item = getItem(position);
        vh.textView.setText(item);
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
