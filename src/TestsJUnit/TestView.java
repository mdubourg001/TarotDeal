package TestsJUnit;

import java.util.Observable;
import java.util.Observer;

public class TestView implements Observer {
    Object arg;

	@Override
    public void update(Observable arg0, Object arg1) {
        arg = arg1;
    }

    public Object getArg(){
        return arg;
    }
}
