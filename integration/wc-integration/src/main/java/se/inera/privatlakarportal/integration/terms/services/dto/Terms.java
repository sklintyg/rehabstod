package se.inera.privatlakarportal.integration.terms.services.dto;

import org.joda.time.LocalDateTime;

/**
 * Created by pebe on 2015-08-25.
 */
public class Terms {
    private String text;
    private long version;
    private LocalDateTime date;

    public Terms() {
    }

    public Terms(String text, long version, LocalDateTime date) {
        this.text = text;
        this.version = version;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
