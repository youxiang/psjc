package njscky.psjc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esri.core.map.Graphic;

import java.util.List;

public class GraphicListAdpater extends BaseAdapter {

    private List<Graphic> data;

    public GraphicListAdpater(List<Graphic> graphics) {
        this.data = graphics;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Graphic getItem(int position) {
        return data == null ? null : data.get(position);
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
        Graphic graphic = getItem(position);
        String jcjbh = (String) graphic.getAttributeValue("JCJBH");
        vh.textView.setText(jcjbh);
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
