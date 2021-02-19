package com.ngsown.knowyoursky.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ngsown.knowyoursky.R;
import com.ngsown.knowyoursky.domain.forecast.HourlyForecast;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {
    private List<HourlyForecast> hourlyForecastList;
    private Context context;

    public HourlyAdapter(List<HourlyForecast> hourlyForecastList, Context context) {
        this.hourlyForecastList = hourlyForecastList;
        this.context = context;
    }

    public void setHourlyForecastList(List<HourlyForecast> hourlyForecastList) {
        this.hourlyForecastList = hourlyForecastList;
    }

    @NonNull
    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_list_item, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyForecast hourlyForecast = hourlyForecastList.get(position);
        holder.imgWeatherIcon.setImageDrawable(ContextCompat.getDrawable(context, hourlyForecast.getIconId()));
        holder.txtHourlyTime.setText(hourlyForecast.getDateTime());
        holder.txtHourlyTemp.setText(Integer.toString(hourlyForecast.getTemperature()) + "\u2103");
    }


    @Override
    public int getItemCount() {
        return hourlyForecastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgWeatherIcon;
        TextView txtHourlyTime, txtHourlyTemp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgWeatherIcon = itemView.findViewById(R.id.imgHourlyIcon);
            txtHourlyTemp = itemView.findViewById(R.id.txtHourlyTemp);
            txtHourlyTime = itemView.findViewById(R.id.txtHourlyTime);
        }
    }
}
