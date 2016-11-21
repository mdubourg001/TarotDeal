package TestsJUnit;

import Tarot.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ModelTest {

    @Test
    public void init(){
        Model model = new Model();
        assertTrue(model.getDeckCards().size() == Model.NB_CARDS);
    }
}