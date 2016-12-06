package TestsJUnit;

import Tarot.Controller;
import Tarot.ModelPack.Model;
import Tarot.ViewPack.DistributionPack.View;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelTest {
    Model model;
    Controller controller;
    TestView view;

    @Test
    public void constructorTest(){
        model = new Model();
        controller = new Controller(model);
        view = new TestView();
        model.addObserver(view);

        assertTrue(model.getJeu().size() == Model.NB_CARDS);
    }

    @Test
    public void startTest(){
        assertFalse(view.isJeuMixed());
        controller.doNextAction();
        assertTrue(view.isJeuMixed());
    }
}