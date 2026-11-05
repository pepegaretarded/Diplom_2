package ru.praktikum.support;

import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

import java.util.Collections;

/** Keep generated passwords and access tokens out of published reports. */
public class ReportParameterFilter implements StepLifecycleListener {
    @Override
    public void beforeStepStop(StepResult result) {
        result.setParameters(Collections.emptyList());
    }
}
