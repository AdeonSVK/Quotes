package sk.lukas.racko.quotes.activities.Main;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import sk.lukas.racko.quotes.models.Quote;
import sk.lukas.racko.quotes.R;

public class ListAdapter extends ArrayAdapter<Quote> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Quote> quotes) {
        super(context, resource, quotes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_row, null);
        }

        Quote q = getItem(position);

        if (q != null) {

            TextView tv_quote = v.findViewById(R.id.l_quote);
            TextView tv_author = v.findViewById(R.id.l_author);

            tv_quote.setText(q.getQuote());
            tv_author.setText(q.getFirstName() + " " + q.getLastName());
            int color = q.getGender().equals("Male") ? Color.BLUE : Color.parseColor("#cc66ff");
            tv_author.setTextColor(color);
        }

        return v;
    }

}