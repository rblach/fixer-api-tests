package com.fixerapi.stepdefs;

import com.fixerapi.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class CommonHooks {
    @Before(order = 0)
    public void getScenario(Scenario scenario) {
        TestContext.remember("scenario", scenario);
    }

    @After(order = 0)
    public void cleanTestContext() {
        TestContext.forgetAll();
    }
    @AfterStep
    public void afterStepLogResponse() {
        Scenario scenario = TestContext.recall("scenario");
        if (scenario != null) {
            String requestLog = TestContext.recallAndForget("request.log");
            String responseLog = TestContext.recallAndForget("response.log");
            if (requestLog != null) {
                scenario.log(requestLog);
            }
            if (responseLog != null) {
                scenario.log(responseLog);
            }
        }
    }
}
