package com.example.weather;

public class WeatherForecast {
    private String time;
    private int temperature;
    private String iconUrl;
    private int rainPercentage;

    public WeatherForecast(String time, int temperature, String iconUrl, int rainPercentage) {
        this.time = time;
        this.temperature = temperature;
        this.iconUrl = iconUrl;
        this.rainPercentage = rainPercentage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getRainPercentage() {
        return rainPercentage;
    }

    public void setRainPercentage(int rainPercentage) {
        this.rainPercentage = rainPercentage;
    }

    // Getters and setters for all fields, including rainPercentage
}

