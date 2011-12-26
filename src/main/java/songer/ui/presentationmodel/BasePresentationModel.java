package songer.ui.presentationmodel;


import songer.ui.PresentationModel;

import java.awt.*;

public class BasePresentationModel implements PresentationModel {
    private Frame ownerFrame;
    
    protected Frame getOwnerFrame() {
        return ownerFrame;
    }
    
    @Override
    public void setFrame(Frame ownerFrame) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
