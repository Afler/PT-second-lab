import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class History {

    private List<Equation> equations;
    private int lastIndex;

    {
        equations = new ArrayList<>();
    }

}
