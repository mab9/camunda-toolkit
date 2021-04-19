package ch.mab.camunda.serializer;

import spinjar.com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class Event {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate endDate;
    private String topic;

    public Event() {
    }

    public Event(LocalDate startDate, LocalDate endDate, String topic) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.topic = topic;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
