package msa.poc.microservices.common.request;

import java.util.Date;

public class BaseRequest {
    private int id;
    private Date timestamp;

    public BaseRequest() {

    }

    public BaseRequest(int id, Date timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
