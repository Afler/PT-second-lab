import lombok.Data;

@Data
public class Equation {

    private String expression;
    private String result;
    private String error;
    private String continueCheck;

}