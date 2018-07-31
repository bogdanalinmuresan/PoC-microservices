package msa.poc.microservices.common.response;

public class BaseResponse {

    private InternalStatus status;
    private String info;

    public BaseResponse (){

    }

    public InternalStatus getStatus() {
        return status;
    }

    public void setStatus(InternalStatus status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setResponse(InternalStatus status, String info) {
        this.status = status;
        this.info = info;
    }
}
