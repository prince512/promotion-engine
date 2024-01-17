package com.wasoko.promotion.service.newrelic;

import com.newrelic.api.agent.NewRelic;
import com.wasoko.promotion.service.exception.SokoRuntimeException;

import java.util.Map;

/**
 * Wrapper class over New Relic Agent
 * This will be used to record errors, log metrics and record custom parameters
 * It will use Service Name and Resource name to segregate the metrics and/or errors
 *
 * @author Sanchit
 */
public class WasokoNewRelic {

    private static final String serviceName = "promotion-service";

    /**
     * Static final string for "Errors"
     * This will be used by New Relic to report Errors
     */
    private static final String ERRORS = "Errors";

    /**
     * Static final string for "Web"
     * This will be used by New Relic to name transactions under this category
     */
    private static final String TRANSACTION_CATEGORY = "Web";


    /**
     * Records a metric in New Relic with the given resource name and metric name.
     * Constructs a metric name with the format "serviceName/resourceName/errorMetricName".
     * Uses the NewRelic recordMetric() method to record the metric with a value of 1.
     *
     * @param resourceName The name of the resource for which the error occurred
     * @param metricName   The name of the error metric to record
     * @author Sanchit
     */
    public static void recordMetric(String resourceName, String metricName) {
        String name = WasokoNewRelic.serviceName + "/" + resourceName + "/" + metricName;
        NewRelic.recordMetric(name, 1);
    }

    /**
     * Records a custom metric in New Relic with the given resource name and metric name.
     * Constructs a metric name with the format "serviceName/resourceName/errorMetricName".
     * Uses the NewRelic recordMetric() method to record the metric with the given float value
     *
     * @param resourceName The name of the resource for which the error occurred
     * @param metricName   The name of the error metric to record
     * @param metricValue  The float value of metric to be recorded
     * @author Sanchit
     */
    public static void recordCustomMetric(String resourceName, String metricName, float metricValue) {
        String name = WasokoNewRelic.serviceName + "/" + resourceName + "/" + metricName;
        NewRelic.recordMetric(name, metricValue);
    }

    /**
     * Records a response time metric in New Relic with the given resource name
     * Constructs a metric name with the format "serviceName/resourceName/ResponseTime".
     * Uses the NewRelic recordMetric() method to record the metric with the given float value
     *
     * @param resourceName The name of the resource for which the error occurred
     * @param responseTime The response time in milliseconds of the transaction/metric to be recorded
     * @author Sanchit
     */
    public static void recordResponseTime(String resourceName, float responseTime) {
        String name = WasokoNewRelic.serviceName + "/" + resourceName + "/" + "ResponseTime";
        NewRelic.recordMetric(name, responseTime);
    }

    /**
     * Records an error metric in New Relic with the given resource name and error metric name.
     * Constructs a metric name with the format "serviceName/resourceName/Errors/errorMetricName".
     * Uses the NewRelic recordMetric() method to record the metric with a value of 1.
     *
     * @param resourceName    The name of the resource for which the error occurred
     * @param errorMetricName The name of the error metric to record
     * @author Sanchit
     */
    private static void recordErrorMetric(String resourceName, String errorMetricName) {
        String name = WasokoNewRelic.serviceName + "/" + resourceName + "/" + ERRORS + "/" + errorMetricName;
        NewRelic.recordMetric(name, 1);
    }

    /**
     * Records an error and corresponding error metric with the provided resource name and error metric name.
     *
     * @param throwable       the Throwable object representing the error that occurred
     * @param resourceName    the name of the resource associated with the error
     * @param errorMetricName the name of the error metric associated with the error
     * @author Sanchit
     */
    public static void recordError(Throwable throwable, String resourceName, String errorMetricName) {
        NewRelic.noticeError(throwable);
        recordErrorMetric(resourceName, errorMetricName);
    }

    /**
     * Records an error in New Relic with the provided error message and associated resource name and error metric name.
     * Uses a SokoRuntimeException to encapsulate the error message and pass it to the New Relic API.
     * using a SokoRuntimeException allows us to get a stack trace
     * Also records an error metric with the provided resource name and error metric name.
     *
     * @param errorMessage    the error message to record
     * @param resourceName    the name of the resource associated with the error
     * @param errorMetricName the name of the error metric to record
     * @author Sanchit
     */
    public static void recordError(String errorMessage, String resourceName, String errorMetricName) {
        NewRelic.noticeError(new SokoRuntimeException(errorMessage));
        recordErrorMetric(resourceName, errorMetricName);
    }

    /**
     * Records a custom parameter with the given key and value.
     *
     * @param key   the key to associate with the value
     * @param value the string value to be recorded as a custom parameter
     * @author Sanchit
     */
    public static void recordCustomParameter(String key, String value) {
        NewRelic.addCustomParameter(key, value);
    }

    /**
     * Records a custom parameter with the given key and value.
     *
     * @param key   the key to associate with the value
     * @param value the Number value to be recorded as a custom parameter
     * @author Sanchit
     */
    public static void recordCustomParameter(String key, Number value) {
        NewRelic.addCustomParameter(key, value);
    }

    /**
     * Records a custom parameter with the given key and value.
     *
     * @param key   the key to associate with the value
     * @param value the boolean value to be recorded as a custom parameter
     * @author Sanchit
     */
    public static void recordCustomParameter(String key, boolean value) {
        NewRelic.addCustomParameter(key, value);
    }

    /**
     * Records a map of custom parameters with the given keys and values.
     *
     * @param params A map containing the custom parameter names and their corresponding values.
     * @author Sanchit
     */
    public static void recordCustomParameters(Map<String, Object> params) {
        NewRelic.addCustomParameters(params);
    }

    /**
     * Sets the name of the transaction in New Relic.
     * It groups all transactions under the Web Category.
     *
     * @param transactionName The name of the transaction.
     * @author Sanchit
     */
    public static void setTransactionName(String transactionName) {
        NewRelic.setTransactionName(TRANSACTION_CATEGORY, transactionName);
    }
}
