package com.ngsown.knowyoursky.adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ngsown.knowyoursky.R;
import com.ngsown.knowyoursky.model.HourlyWeather;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {
    private List<HourlyWeather> hourlyWeatherList;
    private Context context;

    public HourlyAdapter(List<HourlyWeather> hourlyWeatherList, Context context) {
        this.hourlyWeatherList = hourlyWeatherList;
        this.context = context;
    }

    public void setHourlyWeatherList(List<HourlyWeather> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
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
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        holder.imgWeatherIcon.setImageDrawable(ContextCompat.getDrawable(context, hourlyWeather.getIconId()));
        holder.txtHourlyTime.setText(hourlyWeather.getDateTime());
        holder.txtHourlyTemp.setText(Integer.toString(hourlyWeather.getTemperature()) + "\u2103");
    }


    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
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
