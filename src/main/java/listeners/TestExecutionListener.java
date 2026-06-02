package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import utilities.AllureUtils;

/**
 * Suite-level listener responsible for initializing reporting infrastructure
 * before tests execute and performing cleanup after the suite completes.
 */
public class TestExecutionListener implements ISuiteListener {

    private static final Logger logger = LogManager.getLogger(TestExecutionListener.class);

    @Override
    public void onStart(ISuite suite) {
        logger.info("Starting test suite: {}", suite.getName());
        AllureUtils.initializeReporting();
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.info("Finished test suite: {}", suite.getName());
    }
}
