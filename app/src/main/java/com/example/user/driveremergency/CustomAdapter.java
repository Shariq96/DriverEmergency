package com.example.user.driveremergency;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.PersonViewHolder> {

    List<historyClass> persons;

    CustomAdapter(List<historyClass> persons) {
        this.persons = persons;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.drivername.setText(persons.get(position).DriverName);
        holder.dest.setText(persons.get(position).Destination);
        holder.time.setText(persons.get(position).time);
        holder.source.setText(persons.get(position).Source);
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView source;
        TextView dest;
        TextView time;
        TextView drivername;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            source = (TextView) itemView.findViewById(R.id.source1);
            dest = (TextView) itemView.findViewById(R.id.Dest1);
            time = (TextView) itemView.findViewById(R.id.time);
            drivername = (TextView) itemView.findViewById(R.id.Drivername);
        }
    }

}
