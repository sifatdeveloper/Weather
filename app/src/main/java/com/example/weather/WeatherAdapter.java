package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weather.R;
import com.example.weather.WeatherForecast;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private List<WeatherForecast> weatherList;
    Context context;

    public WeatherAdapter(List<WeatherForecast> weatherList, Context context) {
        this.weatherList = weatherList;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherForecast weather = weatherList.get(position);
        holder.timeTextView.setText(weather.getTime());
        holder.raintext.setText(String.valueOf(weather.getRainPercentage()) + "%");
        holder.temperatureTextView.setText(String.valueOf(weather.getTemperature()) + "°C");
        Glide.with(holder.itemView.getContext())
                .load(weather.getIconUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, temperatureTextView,raintext;
        ImageView imageView;

        WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.time1);
            raintext=itemView.findViewById(R.id.rain);
            temperatureTextView = itemView.findViewById(R.id.temperature);
            imageView=itemView.findViewById(R.id.imagead);
        }
    }
}
