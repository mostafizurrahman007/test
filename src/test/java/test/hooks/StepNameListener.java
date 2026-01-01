package test.hooks;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.PickleStepTestStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import test.utils.LogColor;

public class StepNameListener implements ConcurrentEventListener {
    private String currentStepName;
    public static final Logger logger = LogManager.getLogger(StepNameListener.class);

    private EventHandler<TestStepStarted> stepHandler = event -> {  //Step handler
        if (event.getTestStep() instanceof PickleStepTestStep) {  //Checks if its cucumbers step like given/when or not
            PickleStepTestStep testStep = (PickleStepTestStep) event.getTestStep();  //Gets the step
            currentStepName = testStep.getStep().getText();  //Get the step name 
            printStepName(currentStepName);  //Printing the step name

        }
    };

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepStarted.class, stepHandler);  //Helps to call the cucumber code to execute
    }
    
    
    private void printStepName(String currentStepName) {  //Step output method call
        System.out.println("  ");
        System.out.println("  ");
        logger.info(LogColor.DarkOrange+"================================================================"+LogColor.RESET);
        logger.warn(LogColor.DarkOrange+"Executing step: " + currentStepName);    //Formatting for colorful visual log output
        logger.info(LogColor.DarkOrange+"================================================================"+LogColor.RESET);
        System.out.println("  ");
    	
    }
}
