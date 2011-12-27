package songer.ui.presentationmodel;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class LogObservingPresentationModel  {
    private static final Logger logger = Logger.getLogger("songer");
    private ValueHolder textModel = new ValueHolder("");
    private ValueHolder caretPositionModel = new ValueHolder(0);

    public LogObservingPresentationModel() {
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (isLoggable(record)) {
                    textModel.setValue( textModel.getString() + "-> " + record.getMessage() + "\n" );
                    caretPositionModel.setValue( textModel.getString().length() );
                }
            }

            @Override
            public void flush() { }

            @Override
            public void close() throws SecurityException { }
        });

        logger.info("Handler initialized !");
    }




    public ValueModel getTextModel() {
        return textModel;
    }
    
    public ValueModel getCaretPositionModel() {
       return caretPositionModel;
    }
}
