package TestsJUnit;

import Tarot.ModelPack.TarotAction;
import javafx.util.Pair;

import java.util.Observable;
import java.util.Observer;

public class TestView implements Observer {

    private boolean jeuMixed = false;

    @Override
    public void update(Observable arg0, Object arg1) {
        switch (((Pair<TarotAction, Object>) arg1).getKey()) {
            case JEU_MIXED:
                jeuMixed = true;
                break;
            case JEU_CUT:
                break;
            case CARDS_DISTRIBUTED:
                break;
            case CARD_MOVED_TO_CHIEN:
                break;
            case PLAYER_REVERTED:
                break;
            case PLAYER_ORGANIZED:
                break;
            case PETIT_SEC_DETECTED:
                break;
            case ACTION_CHOSEN:
                break;
            case CHIEN_REVERTED:
                break;
            case CARD_ADDED_GAP:
                break;
            case GAP_DONE:
                break;
            case DISTRIBUTION_DONE:
                break;
        }
    }

    public boolean isJeuMixed() {
        return jeuMixed;
    }
}
