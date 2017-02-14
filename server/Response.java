/**
 * Created by sergiopuleri on 2/13/17.
 */

public class Response {
  private String message;
  private Boolean terminate;
  private Boolean bye;
  private int currentValue;

  public Response() {
    this.message = "";
    this.currentValue = -1;
    this.terminate = false;
    this.bye = false;
  }

  // Static constructors to create certain configured instances of this object
  public static Response byeResponse() {
    Response res = new Response();
    res.setMessage("-5");
    res.setBye();
    return res;
  }

  public static Response terminateResponse() {
    Response res = new Response();
    res.setMessage("-5");
    res.setTerminate();
    return res;
  }

  public static Response invalidCommandResponse() {
    Response res = new Response();
    res.setMessage("-1");
    return res;
  }

  public int getCurrentValue() {
    return this.currentValue;
  }
  public String getMessage() {
    if (currentValue != -1) {
      return Integer.toString(currentValue);
    }
    return this.message;
  }

  public Boolean isTerminate() {
    return this.terminate;
  }

  public Boolean isBye() {
    return this.bye;
  }

  public void setCurrentValue(int val) {
    this.currentValue = val;
  }

  public void setTerminate() {
    this.terminate = true;
  }

  public void setBye() {
    this.bye = true;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
